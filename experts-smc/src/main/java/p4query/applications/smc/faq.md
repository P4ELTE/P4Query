
Q: How are packet buffers (extract, emit) implemented?

- Usually there are no separate stores for receiving packets and sending out packets.
- There is only one packet puffer: this is where it arrives to and where it departs from. 
- The (big) advantage is that the payload does not have to be moved from one store to another. 
- The (small) disadvantage is that in general, modifying (e.g. swapping) the headers requires a temporary store and associated memcpy operations to and from the temp. (Otherwise, only 1 memcpy would be needed between the receive-buffer and the send-buffer.)
- P4C BM moves the headers to the temporary store during parsing (extract). (See OCS20 for pseudocode.)
- T4P4S DPDK moves the headers to the temporary store during deparsing (emit).

Q: How large the stack should be?

The sufficiently large stack size is: 'global state' + 'maximum weight path in call graph',
  where the weight of a path is 'its length' * 'the size of the functions on the path'.
The size of a function is size_eip + size_ebp + size_op + 'size of function parameters and local variables'. 
- size_eip, size_ebp, size_op are IR specific parameters.
- size_op tells how much stack the most size-expensive instruction primitive needs. (For example OP_ADD has size two, because it needs two parameters on the stack. OP_INVOKE has 0, because we already calculate it with size_eip, size_ebp and size of function parameters)
It's probably easier to just prepare a very large stack.

Q: What is the global state?

The global state is the header-buffer and the temporary stores (and the table contents). 
Since the payload is not touched, it can be omitted from the IR.
Note that:     
- The buffer is only used during parsing and deparsing.
- The required buffer size is a parameter (it is the maximum sum size of the headers in the packet traffic). 

Q: How are lookup tables implemented?

Lookup tables are implemented inside extern function definitions. 
I still have to experiment with lookup tables: The faithful approach is to put it in the memory, and use a few rules to implement a general lookup algorithm. Another option is to pre-compile each table into nested ternary expressions. A third is to pre-compile each table row into a separate rule. 
- I speculate that pre-compiled ternaries result in the smallest model: there is only one transition per table, and the lookup result is calculated during model building.
- On the other hand, PRISM cannot model check inside the ternary, so the cost (correctness, etc.) of the lookup has to be calculated elsewhere. (I already have plug-and-play formulas for lookups.)  This makes some sense, since the table contents are (usually) not part of the P4 code.
- We can only experiment with this after everything else is done, so for now I pre-compile to ternaries, since it is simpler.

Q: How is 'main' (package instantiation) compiled?

- The "instantiation" syntax in the specifiation is weird. 
  * It expresses that 'main' is a template method, and the P4 functions in the list implement its hooks.  
  * This can be handled **entirely at compile-time**, and so in practice it simply instructs the linker on how to link together the package with the program.
  * The signature and methods of 'main' (i.e. the interface) are declared by the package, the implementation of 'main' itself is external, and the implementation of the methods are in the P4 program.
  * The reason it is weird, is because linking implementation with interface is a problem that already had a working solution in C. The only thing gained here is that users can now rename the implementing functions. 
  * Moreover, the syntax makes it look like a runtime thing, but since it is on the implementation-side, one interface can have only one implementation, and so it would be useless to perform this during runtime. It also looks weird that parametric functions are "called" without paremeters. (And syntactically these really appear as expressions calling a function.)    
- So we simply link the 'main' implementation of v1model/v1switch at compile-time with the user functions in the P4 code, replacing P,C,D, etc. with the implementations. At runtime, only 'main' is called, the call-like things inside the generic parameter-like thing are not.
- Linking MyP to P: the P template name in v1model is translated to a function, that simply calls MyP.
- Currently, we simply translate "V1Model(MyP(),MyC(),MyD()) main" as if it was the sequence "MyP(...);  MyC(...); MyD(...);".

Q: How is the program entry compiled?
 
- The first few steps of execution are: 
    * Global data layout is created on the stack.
    * The buffer is filled with the packet header. (Either with a concrete package, or by a Markov-chain packet generator)
    * 'main' is invoked. 
    * 'main' invokes everyone else.

Q: How are fields (e.g. 'hdr.ethernet.srcAddr') resolved?

- In P4, structs are just hierarchical address spaces. (This is because P4 has no pointers, no -> operation.)
    * This means 'hdr.ethernet srcAddr' is the i-th element relative to 'ethernet', and in turn 'hdr.ethernet' is the j-th element relative to 'hdr'.
- The absolute address is calculated by resolving the local address and adding it to the parent address.
- For example, if 'hdr' has absolute address k, 'ethernet' has local address j, and 'srcAddr' has local address i, then:
    * The absolute address of 'hdr.ethernet' is k+j
    * The absolute address of 'hdr.ethernet.srcAddr' is k+j+i
- This assumes structs are stored in a continuous manner. 
    * So assuming 0-indexing, the address of the n-th struct will be sizeof(first n-1 struct).
    * The address of the n-th field of struct at absolute address k, will be  k + sizeof(first n-1 field).
- IMPORTANT: call-by-reference makes it impossible to resolve 'hdr' at compile-time (it points to a parameter storing an address). 
    * For this reason, the resolution is performed partially at runtime (using type information 'headers' and 'ethernet_t').
    * The operations are: 
      1. push 'hdr' address to stack, 
      2. increment top with sizeof(fields before 'ethernet' in 'headers')
      3. increment top with sizeof(fields before 'srcAddr' in 'ethernet_t')
      4. top now contains the absolute address of 'hdr.ethernet.src'
    * (It would be possible to avoid using type information, but this would make things more complicated.)

Q: How are assignments implemented?

- A memcpy is used, on the top three elements (src,dst,length) of the stack. The length is figured out at compile-time using type information.
- There are two cases. Case 1: src is a name. Case 2: src is a literal or an arithmetic expression. 
- Case 1 is simple: names either refer to global or local storages. We just pass this address to memcpy.
- Case 2: these expressions (and subexpressions) require local variables on the stack. Before the assignment, each expression is calculated and its result is stored to the store variable. Then, the address of the local store is passed to memcpy.
- This is now quite wasteful, since temps are stored on the stack permanently, but later it can be optimized to reuse the same space for temps.

Q: What function call semantics are used?
 
- Call-by-reference for structs and headers (usually found in controls and parsers). Note that this does not require resolution, just push the reference to the stack. 
- Call-by-value for field values (usually found in tables and actions). Note that this means pushing all bits on the stack (but does not require resolution).
- Note that this is a divergence from the specification which prescribes copy-in/copy-out

Q: How method dispatch (e.g. 'packet.extract(...)') works?

- P4 has no concept of type inheritance. This means 'packet' has an exact type and it is known at compile-time ('packet_in'). We know at compile-time which function we need to call: 'extract' in 'packet_in' with parameter 'packet' and whatever else was originally given. 
- The definition of that function will come from outside (since it's an extern), but this not a problem until it uses the same naming scheme we do in the calls.
- Other methods work similarly: in 'hdr.ipv4.isValid()', there is only one definition of 'isValid()', so this is actually the call 'isValid(hdr.ipv4)'.

Q: How are local variables handled?
 
- Space is pre-allocated on the stack for each declared name. 
- Note that one name can be declared by more than one declaration:
  * A name cannot be declared twice on the same CFG path.
  * It's legal to declare the same name twice in sibling branches, but they have to have the same type. 
  * In this case, only one segment is allocated (since both declaration declares the same name). 
  * Usages (reads and writes) of the ambiguous name will also refer to this segment.
- Declarations and initializations are considered as two separate steps
- TODO: currently the space is pre-allocated on the bottom stack-frame, and it lives through the whole lifecycle. Instead, it should allocated on the bottom of the active stack-frame and discarded when done. (It cannot be allocated on the top of the active stack-frame, because it messes up parameter passing to 'invoke' which works with the topmost elements.)   

Q: How are validity bits handled?

- An extra bit is allocated to every header.
- isValid(), setValid(), setInvalid() are extern functions in 'stdlib'

Q: How are expression lists implemented?

- The problem with expression lists is that they can denote any of tuple, struct or header. As such, the callee does not know if they received an expression list, or a pre-allocated header/struct/tuple. 
- Fortunately, lists are never lvalues, so it is always possible to know their memory layout (from the lvalue they are assigned to)
- The spec allows for nested lists as well. So for example nested structs can be assigned a nested list. 
- Other than that, they are fairly straightforward and work similarly to other expressions:
  * First, the list is pre-allocated some sufficiently large space to hold the "result" (the complete flat list)
  * Then, the elements are processed and their result is copied to their respective space. The offset is inferred from the layout.
  * Inner lists aren't problematic either, they are just big.
- Note on in-line headers: these are problematic, since headers store implicit info like validity or size. (This info could be stored elsewhere, so this is not P4 specific.)
  * Implicit info can be handled based on the inferred layout, so that's not a problem.
  * On the other hand, the P4 spec doesn't say what the validity should be in case headers are defined in-line: (a) invalid since they were never parsed, or (b) valid since it wouldn't make sense to define them if we don't want to use them.

Q: How are parsers implemented
- In parsers, state transitions are translated to 'goto's (not function calls): this is because the state blocks have to have access to the parameters of the parser function, and it would be extra work (with no benefits) to do this using function calls.

Notes:

- Everything is a reference. Reasons:
  * Expression lists are very costly otherwise (requires deep copies of structs).
  * There are no primitives, because the most often used "basic" type is bit<n>, which is a composite type (of bits) in the abstract machine so we cannot directly pass these as parameters. 
- Expression list: {p,q,r}, where p,q,r are references. These either point to other expression lists, or point to the flat memory. 
- The resolution is driven by the LHS. For example:
  * Let hdr.eth.ethType be a reference to flat memory, and hdr.eth be a reference to an expression list (of pointers).
  * hdr.eth.ethType = 1; will memcpy from address storing 1 to the ethType address
  * x = hdr.eth.ethType; will memcpy from the ethType address to the x address
  * hdr.eth = {1,2,3}; will memcpy from addresses in the list to the addresses in hdr.eth. this is equivalent to three consecutive assignments (one for each element).
  * hdr = {{1,2,3}, {5,6}}; will memcpy from addresses in the list to the addresses in hdr. this is equivalent to two list assignments.
  * hdr = hdr; is the same as above (list to list), except the compiler doesn't have to generate a temp for storing the RHS beforehand (since the rhs is already a reference).  
- Note that for nested lists, you only want to copy the values, not the lists themselves, since these only store pointers to local data (and you don't want pointers to local data).
- Representing expression lists like that is still fairly space efficient. For example when main calls a control and passes headers, it will create a list with the respective pointers and pass the list reference to the control. When the control passes the same header to its callees, it won't have to recreate the list again, it just passes the reference of the existing list. It only has to create a new list if the programmer explicitly asks for it (by using the {1,2,3} syntax). Here, it is okay to pass local pointers to callees, since they won't change until the callee is active.
- Address resolution of hdr.eth.ethType goes like: 
   resolve(resolve(resolve(hdr) + n) + m), where:
  * hdr stores the address of a list
  * n is the index (!) of eth field in the hdr struct
  * m is the index (!) of ethType field in the eth struct
- Note: given that eth is the fist field of hdr and ethType is the first field of eth, then the first bit of ethType is the first bit eth, and it thus it is also the first bit of hdr. in this case n==m==0, and resolve(resolve(resolve(hdr))) will point to the first bit of ethType (which is also the first bit of hdr).
 
currently, local memory layout stores the parameters, everything else is stored in the global memory layout. the parameters are filled during function calls. StorageReference is compiled differently based on whether the reference points to local or global memory layout: in the local layout, a "LocalAddress" is created (basically the index of the parameter), in the global layout, a "GlobalAddress" is created, which is an absolute address to the global memory. reference parameters should always also store absolute addresses to global memory so they can be resolved in place.
