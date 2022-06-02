// Copyright 2020-2022, Dániel Lukács, Eötvös Loránd University.
// All rights reserved.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// 
// Author: Dániel Lukács, 2022

<%doc> use 'dtmc' for probabilistic, 'mdp' for non-deterministic </%doc>
${model_type}

// possible values for op, nop
const int NO_OP = -1; 
const int END_OP = 0; 
const int DONE = -2; 
const int ERROR = -3; 

const int I_READ = 1; 
const int I_WRITE = 2; 
const int I_POP = 3; 
const int I_PUSH = 4; 

const int OP_GOTO  = 11; 
const int OP_IFEQ  = 12; 
const int OP_IFEQ_2  = 121; 

const int OP_CONST  = 13; 
const int OP_STORE  = 14; 
const int OP_STORE_2  = 141; 
const int OP_LOAD  = 15; 
const int OP_LOAD_2  = 151; 
const int OP_ADD = 16;
const int OP_ADD_2 = 161; 
const int OP_ADD_3 = 162; 

const int OP_SUB = 17;
const int OP_SUB_2 = 171; 
const int OP_SUB_3 = 172; 

const int OP_MUL = 18;
const int OP_MUL_2 = 181; 
const int OP_MUL_3 = 182; 

const int OP_EQ = 19;
const int OP_EQ_2 = 191; 
const int OP_EQ_3 = 192; 

const int OP_GT = 20;
const int OP_GT_2 = 201; 
const int OP_GT_3 = 202; 

const int OP_MOD = 21;
const int OP_MOD_2 = 211; 
const int OP_MOD_3 = 212; 

const int OP_SQRT = 22;
const int OP_SQRT_2 = 221; 

const int OP_LT = 23;
const int OP_LT_2 = 231; 
const int OP_LT_3 = 232; 

const int OP_NOT = 24;
const int OP_NOT_2 = 241; 

const int I_MEMCPY = 25; 
const int I_MEMCPY_2 = 251; 
const int I_MEMCPY_3 = 252; 
const int I_MEMCPY_4 = 253; 

const int OP_POP = 26; 

const int OP_GETFIELD = 27; 
const int OP_GETFIELD_2 = 271; 
const int OP_GETFIELD_3 = 272; 

const int OP_PUTFIELD = 28; 
const int OP_PUTFIELD_2 = 281; 
const int OP_PUTFIELD_3 = 282; 

const int I_PROLOGUE = 31; 
const int I_PROLOGUE_2 = 311; 
const int I_PROLOGUE_3 = 312; 

const int I_EPILOGUE = 32; 
const int I_EPILOGUE_2 = 321; 
const int I_EPILOGUE_3 = 322; 
const int I_EPILOGUE_4 = 323; 

const int OP_INVOKE = 33; 
const int OP_INVOKE_2 = 331; 
const int OP_INVOKE_3 = 332; 

const int OP_RETURN = 34; 
const int OP_RETURN_2 = 341; 
const int OP_RETURN_3 = 342; 

const int OP_DEREF = 35; 
const int OP_DEREF_TOP = 36; 

const int OP_INC = 37; 
const int OP_INC_2 = 371; 

const int OP_DEC = 38; 
const int OP_DEC_2 = 381; 

const int OP_POPN = 39; 
const int OP_POPN_2 = 391; 

const int OP_ALLOC = 40; 

const int OP_TOP = 41; 
const int OP_TOP_2 = 411; 
const int OP_TOP_3 = 412; 

const int ERR_NO_ERROR = 0;
const int ERR_ACCESS_VIOLATION_ABOVE = 1;
const int ERR_ACCESS_VIOLATION_BELOW = 2;
const int ERR_STACK_OVERFLOW = 3;
const int ERR_STACK_UNDERFLOW = 4;

const int ERR_INVALID_ARGUMENT = 5;

module a 

  esp : int init -1; 
  ebp : int init -1; 

  op : int init   NO_OP;
  nop : int init  NO_OP; 
  nop2 : int init NO_OP; 

  eip : int init 0;

  error : int init ERR_NO_ERROR;

  z0 : int init 0; // return
  z1 : int init 0;
  z2 : int init 0;

  x1 : int init 0;
  x2 : int init 0;
  x3 : int init 0;
  x4 : int init 0; // used by invoke
  x5 : int init 0; // used by invoke


<%doc>  The loop below will generate this for memsize-1
    s0  : int init 0;
    s1  : int init 0;
    s2  : int init 0;
    s3  : int init 0;
    ...      
</%doc>
% for i in range(memsize): 
  s${i} : int init 0;
% endfor




// KERNEL: 
// - dynamic read and write (PRISM workaround)
// - pop, push
// - instruction epilogue

// INSTRUCTION EPILOGUE
// increments instruction pointer

// note: nop2 enables instructions to call another instruction. 
//   it is a quick hack made for invoke and should not be relied upon

[] (op=END_OP & nop2 = NO_OP) -> 
  (nop' = NO_OP) &
  (op' = NO_OP) &
  (eip' = eip + 1) ;

[] (op=END_OP & nop2 != NO_OP) -> 
  (nop' = NO_OP) &
  (nop2' = NO_OP) &
  (op' = nop2) ;


// READ 
// Look up value at the address stored in z1 and copy it to z0.

<%doc> The loop below will generate this for memsize-1:
   [] (op=I_READ & z1 = 0) -> 
      (z0' = s0) & 
      (op' = nop) &
      (nop' = -1);

   [] (op=I_READ & z1 = 1) -> 
     (z0' = s1) & 
     (op' = nop) & 
     (nop' = -1);
   ...      
</%doc>

% for i in range(memsize): 
[] (op=I_READ & z1 = ${i}) -> 
  (z0' = s${i}) & 
  (op' = nop) &
  (nop' = -1);
% endfor 

[] (op=I_READ & z1 > ${memsize - 1}) -> 
  (op' = ERROR) & 
  (error' = ERR_ACCESS_VIOLATION_ABOVE);

[] (op=I_READ & z1 < 0) -> 
  (op' = ERROR) & 
  (error' = ERR_ACCESS_VIOLATION_BELOW);

// WRITE 
// Copy the contents of z1 to the address stored in z2.

<%doc>
 The loop below will generate this for memsize-1:
     [] (op=I_WRITE & z2 = 0) -> 
       (s0' = z1) & 
       (op' = nop) & 
       (nop' = -1);
     
     [] (op=I_WRITE & z2 = 1) -> 
       (s1' = z1) & 
       (op' = nop) & 
       (nop' = -1);
     ...      
</%doc>

% for i in range(memsize):
[] (op=I_WRITE & z2 = ${i}) -> 
  (s${i}' = z1) & 
  (op' = nop) &
  (nop' = -1);
% endfor 

[] (op=I_WRITE & z2 > ${memsize - 1}) -> 
  (op' = ERROR) & 
  (error' = ERR_STACK_OVERFLOW);

[] (op=I_WRITE & z2 < 0) -> 
  (op' = ERROR) & 
  (error' = ERR_STACK_UNDERFLOW);


// POP
// Pop the top of the stack and store it in z0. 

[] (op=I_POP) ->
  (op'=I_READ) & 
  (z1'=esp) & 
  (esp' = esp - 1) & 
  (nop' = nop);

// PUSH
// Push the contents of z1 onto the stack.

[] (op=I_PUSH) ->
  (op'=I_WRITE) & 
  (z2'= esp + 1) & 
  (esp'= esp + 1) & 
  (nop' = nop);


// INSTRUCTION SET

// Note: a concise intro to JVM bytecode can be found at https://courses.cs.ut.ee/MTAT.05.085/2014_spring/uploads/Main/JVM%20Bytecode.pdf

// GOTO
// Jump to the value in x1

[] (op=OP_GOTO) -> 
  (eip'= x1 - 1) & // +(-1) because END_OP will increment 
  (op' = END_OP); 

// IFEQ
// Pop top element. If it is 0 (i.e. false), jump to x1. Otherwise do no-op (i.e. continue).

[] (op=OP_IFEQ) -> 
  (op'=I_POP) & 
  (nop'=OP_IFEQ_2) ; 

[] (op=OP_IFEQ_2 & z0 = 0) -> 
  (eip'= x1 - 1) & // +(-1) because END_OP will increment 
  (op' = END_OP); 

[] (op=OP_IFEQ_2 & z0 > 0) -> 
  (op' = END_OP);

// CONST
// Push the value in x1 

[] (op=OP_CONST) -> 
  (op'=I_PUSH) & 
  (z1'= x1) & 
  (nop' = END_OP);

// STORE
// Pop a value and write it to address x1 relative to the current stack frame. (First address is 0.)

[] (op=OP_STORE) -> 
  (op'=I_POP) & 
  (nop' = OP_STORE_2);

[] (op=OP_STORE_2) -> 
  (op'=I_WRITE) & 
  (z1'= z0) & 
  (z2'= ebp + 1 + x1) & 
  (nop' = END_OP);

// LOAD
// Read a value from address x1 relative to the current stack frame and push it. (First address is 0.)

[] (op=OP_LOAD) -> 
  (op'=I_READ) & 
  (z1'= ebp + 1 + x1) & 
  (nop' = OP_LOAD_2);

[] (op=OP_LOAD_2) -> 
  (op'=I_PUSH) & 
  (z1'= z0) & 
  (nop' = END_OP);

// ADD
// Pop two values and push their sum 

[] (op=OP_ADD) ->
  (op'=I_POP) & 
  (nop' = OP_ADD_2);

[] (op=OP_ADD_2) ->
  (x1'=z0) &
  (op'=I_POP) & 
  (nop' = OP_ADD_3);

[] (op=OP_ADD_3) ->
  (op'=I_PUSH) & 
  (z1'=x1 + z0) &
  (nop' = END_OP);

// SUB
// Pop two values, subtract the younger element from the older element (o-y), and push the result. 

[] (op=OP_SUB) ->
  (op'=I_POP) & 
  (nop' = OP_SUB_2);

[] (op=OP_SUB_2) ->
  (x1'=z0) &
  (op'=I_POP) & 
  (nop' = OP_SUB_3);

[] (op=OP_SUB_3) ->
  (op'=I_PUSH) & 
  (z1'=z0 - x1) &
  (nop' = END_OP);


// MUL
// Pop two values and push their product.

[] (op=OP_MUL) ->
  (op'=I_POP) & 
  (nop' = OP_MUL_2);

[] (op=OP_MUL_2) ->
  (x1'=z0) &
  (op'=I_POP) & 
  (nop' = OP_MUL_3);

[] (op=OP_MUL_3) ->
  (op'=I_PUSH) & 
  (z1'=x1 * z0) &
  (nop' = END_OP);

// MOD
// Pop two values, and push  o mod y. 

[] (op=OP_MOD) ->
  (op'=I_POP) & 
  (nop' = OP_MOD_2);

[] (op=OP_MOD_2) ->
  (x1'=z0) &
  (op'=I_POP) & 
  (nop' = OP_MOD_3);

[] (op=OP_MOD_3) ->
  (op'=I_PUSH) & 
  (z1'=mod(z0, x1)) &
  (nop' = END_OP);

// SQRT
// Pop a value n, and push ceil(sqrt(n))

[] (op=OP_SQRT) ->
  (op'=I_POP) & 
  (nop' = OP_SQRT_2);

[] (op=OP_SQRT_2) ->
  (op'=I_PUSH) & 
  (z1'=ceil(pow(z0, 0.5))) &
  (nop' = END_OP);

// EQ
// Pop two values and push 1 if they are equal, otherwise push 0. 

[] (op=OP_EQ) ->
  (op'=I_POP) & 
  (nop' = OP_EQ_2);

[] (op=OP_EQ_2) ->
  (x1'=z0) &
  (op'=I_POP) & 
  (nop' = OP_EQ_3);

[] (op=OP_EQ_3 & z0=x1) ->
  (op'=I_PUSH) & 
  (z1'= 1) &
  (nop' = END_OP);

[] (op=OP_EQ_3 & z0!=x1) ->
  (op'=I_PUSH) & 
  (z1'= 0) &
  (nop' = END_OP);

// GT
// Pop two values and push 1 if the older element is greater than the younger element (o>y), otherwise push 0. 

[] (op=OP_GT) ->
  (op'=I_POP) & 
  (nop' = OP_GT_2);

[] (op=OP_GT_2) ->
  (x1'=z0) &
  (op'=I_POP) & 
  (nop' = OP_GT_3);

[] (op=OP_GT_3 & z0 > x1) ->
  (op'=I_PUSH) & 
  (z1'= 1) &
  (nop' = END_OP);

[] (op=OP_GT_3 & z0 <= x1) ->
  (op'=I_PUSH) & 
  (z1'= 0) &
  (nop' = END_OP);

// LT
// Pop two values and push 1 if the older element is lesser than the younger element (o<y), otherwise push 0. 

[] (op=OP_LT) ->
  (op'=I_POP) & 
  (nop' = OP_LT_2);

[] (op=OP_LT_2) ->
  (x1'=z0) &
  (op'=I_POP) & 
  (nop' = OP_LT_3);

[] (op=OP_LT_3 & z0 < x1) ->
  (op'=I_PUSH) & 
  (z1'= 1) &
  (nop' = END_OP);

[] (op=OP_LT_3 & z0 >= x1) ->
  (op'=I_PUSH) & 
  (z1'= 0) &
  (nop' = END_OP);


[] (op=OP_NOT) ->
  (op'=I_POP) & 
  (nop' = OP_NOT_2);

[] (op=OP_NOT_2 & z0 > 0) ->
  (op'=I_PUSH) & 
  (z1'= 0) &
  (nop' = END_OP);

[] (op=OP_NOT_2 & z0 = 0) ->
  (op'=I_PUSH) & 
  (z1'= 1) &
  (nop' = END_OP);

// MEMCPY
// Copies x3 items from address x1 to address x2 on the stack. x1 should point the bottommost element you want to copy.
// Undefined if the destination precedes the source and their boundaries overlap (this would lose information by overwriting the start of the source before it was copied), that is: 
// * x2 <= x1 & x1 < x2 + x3.
// Reading garbage (data outside of the stack) is allowed: 
// * esp < x1 + x3.
// Note that overlap is allowed, if the source precedes the destination and they overlap (this overwrites the source AFTER it was copied, thus preserving it):
// * x1 <= x2 & x2 < x1 + x3. 
// If x2 + x3 hangs out from the current stack, copying will place new elements on the stack. (Beware of stack overflow.)

[] (op=I_MEMCPY & ( // x1 <= x2  & x2 < x1 + x3 |
                     x2 <= x1  & x1 < x2 + x3 // | 
                     // esp < x1 + x3 - 1
                     )) ->
  (op' = ERROR) &
  (error' = ERR_INVALID_ARGUMENT);

[] (op=I_MEMCPY & !( // x1 <= x2  & x2 < x1 + x3 |
                      x2 <= x1  & x1 < x2 + x3 // | 
                      // esp < x1 + x3 - 1
                      )) ->
  (op' = I_MEMCPY_2) ;

[] (op=I_MEMCPY_2 ) ->
  (esp' = max(x2 + x3 - 1, esp)) &
  (op' = I_MEMCPY_3);

[] (op=I_MEMCPY_3 & x3=0) ->
  (op' = END_OP);

[] (op=I_MEMCPY_3 & x3>0) ->
  (op' = I_READ) &
  (z1' = x1 + x3 - 1) &
  (nop' = I_MEMCPY_4);

[] (op=I_MEMCPY_4) ->
  (op' = I_WRITE) &
  (z1' = z0) &
  (z2' = x2 + x3 -1) &
  (x3' = x3 - 1) &
  (nop' = I_MEMCPY_3);


// POP
[] (op=OP_POP) -> 
  (op'=I_POP) &  
  (nop' = END_OP);

// GETFIELD pops a global address, and pushes its contents on the stack

// TODO this works. delete it, and uncomment the other!!!
// [] (op=OP_GETFIELD) -> 
//   (op'=END_OP) ; 

[] (op=OP_GETFIELD) -> 
  (op'=I_POP) & 
  (nop' = OP_GETFIELD_2);

[] (op=OP_GETFIELD_2) -> 
  (op'=I_READ) & 
  (z1'= z0) & 
  (nop' = OP_GETFIELD_3);

[] (op=OP_GETFIELD_3) -> 
  (op'=I_PUSH) & 
  (z1'= z0) & 
  (nop' = END_OP);

// PUTFIELD pops a global address, and then a value, and writes the value to the address

[] (op=OP_PUTFIELD) -> 
  (op'=I_POP) & 
  (nop' = OP_PUTFIELD_2);

[] (op=OP_PUTFIELD_2) -> 
  (z2'= z0) & 
  (op'=I_POP) & 
  (nop' = OP_PUTFIELD_3);

[] (op=OP_PUTFIELD_3) -> 
  (op'=I_WRITE) & 
  (z1'= z0) & 
  (z2'= z2) & 
  (nop' = END_OP);

// FUNCTION PROLOGUE AND EPILOGUE 
// As described e.g. here: http://www.sm.luth.se/csee/courses/smd/163/lecture9.pdf
// - before the call, ebp points to the bottom of top stack frame, while esp points to the top of the top stack frame
// - in the prologue, we push ebp to the stack, and store the current esp into ebp. (ebp now points to the bottom of the new frame.) we also save the pointer to the next insrtuction. and jump to the label in x1. 
// - in the epilogue, we reset esp to the bottom of the frame (by storing ebp back into esp). then set ebp to the previous frame bottom (we pop the pointer there and store it into ebp). we also restore the stored instruction pointer. 

[] (op=I_PROLOGUE) ->
  (op'=I_PUSH) & 
  (z1' = eip + 1) & 
  (nop' = I_PROLOGUE_2);

[] (op=I_PROLOGUE_2) ->
  (op'=I_PUSH) & 
  (z1' = ebp) & 
  (nop' = I_PROLOGUE_3);

[] (op=I_PROLOGUE_3) ->
  (ebp'=esp) & 
  (eip'=x1 - 1) & // +(-1) because END_OP will increment
  (op' = END_OP) ;


[] (op=I_EPILOGUE) ->
  (esp'=ebp) & 
  (op' = I_EPILOGUE_2);

[] (op=I_EPILOGUE_2) ->
  (op'=I_POP) & 
  (nop' = I_EPILOGUE_3);

[] (op=I_EPILOGUE_3) ->
  (ebp' = z0) & 
  (op'=I_POP) & 
  (nop' = I_EPILOGUE_4);

[] (op=I_EPILOGUE_4) ->
  (eip' = z0 - 1) & // +(-1) because END_OP will increment 
  (op' = END_OP) ;
  
// INVOKE
// Simulates a function call by creating a stack frame and putting the parameters into it. Expects parameters to be already put on top of the stack. Expects the function label in x4, and the number (size) of parameters in x5.
// Steps:
// 1. Makes place to the stack frame (old eip end ebp) by 
//    shifting the top x5 elements (the parameters) of the stack by 2 places towards the top.
// 2. Inserts a stack frame (see I_PROLOGUE) below the parameters.
// 3. Restores esp so that it points to the top parameter (now residing in the top stack frame).

[] (op=OP_INVOKE) -> 
  (op'=I_MEMCPY) &
  (x1' = esp - x5 + 1) &
  (x2' = esp - x5 + 1 + 2) &
  (x3' = x5) &
  (nop2' = OP_INVOKE_2);

[] (op=OP_INVOKE_2) -> 
  (esp' = esp - x5 - 2) &
  (op'=I_PROLOGUE) &
  (x1' = x4) &
  (nop2' = OP_INVOKE_3);

[] (op=OP_INVOKE_3) -> 
  (esp' = esp + x5) &
  (op' = END_OP);

// TODO this will not work if the return value is longer than the size between the ebp and the return value. in this case, we need a memcpy that copies in the other direction (or better yet, a memcpy that uses a temporary store)

// RETURN
// Simulates a function return. It returns the top value on the stack.
// Steps:
// 1. Pops the top stack frame and restores eip (see I_EPILOGUE).
// 2. Copies the return value to the top of the stack

[] (op=OP_RETURN) -> 
  (x4' = esp) &
  (op'=I_EPILOGUE) &
  (nop2' = OP_RETURN_2);

[] (op=OP_RETURN_2) -> 
  (op'=I_MEMCPY) &
  (x1' = x4) &
  (x2' = esp + 1) &
  (x3' = 1) &
  (nop2' = NO_OP);

// DEREF
// Resolves the local address in x1 to its global address (i.e. its absolute position in the stack) and pushes that global address. Note that local addressing starts with index 0, so DEREF 0 will push ebp+1.
[] (op=OP_DEREF) -> 
  (op'=I_PUSH) & 
  (z1'= ebp + 1 + x1) & 
  (nop' = END_OP);

// DEREF_TOP
// Returns the global address of the top element on the stack. 
[] (op=OP_DEREF_TOP) -> 
  (op'=I_PUSH) & 
  (z1'= esp) & 
  (nop' = END_OP);

// INC
// Increment top value by 1.
[] (op=OP_INC) ->
  (op'=I_POP) & 
  (nop' = OP_INC_2);

[] (op=OP_INC_2) ->
  (op'=I_PUSH) & 
  (z1'= z0 + 1) &
  (nop' = END_OP);

// DEC
// Decrement top value by 1.
[] (op=OP_DEC) ->
  (op'=I_POP) & 
  (nop' = OP_DEC_2);

[] (op=OP_DEC_2) ->
  (op'=I_PUSH) & 
  (z1'= z0 - 1) &
  (nop' = END_OP);

// POPN
// Pops x1 element. 
[] (op=OP_POPN & x1=0) ->
  (op' = END_OP);

[] (op=OP_POPN & x1>0) ->
  (op'=I_POP) & 
  (x1' = x1 - 1) & 
  (nop' = OP_POPN);

[] (op=OP_TOP) ->
  (op'=I_READ) & 
  (z1'= esp) & 
  (nop' = OP_TOP_2);

[] (op=OP_TOP_2) ->
  (op'=I_PUSH) & 
  (z1'= z0) &
  (nop' = END_OP);

// ALLOC
// Increment esp with n. (if n=0, you stay where you are. if n=0, you move one step forward)

[] (op=OP_ALLOC) -> 
  (esp'= esp + x1 ) & 
  (op' = END_OP);

<%include file="${program_source}"/>

endmodule

<%include file="${operator_cost_model}"/>

