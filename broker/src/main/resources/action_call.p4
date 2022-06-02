typedef bit<48> macAddr_t;
typedef bit<32> ip4Addr_t;

header ethernet_t {
    macAddr_t dstAddr;
    macAddr_t srcAddr;
    bit<16>   etherType;
}

header ipv4_t {
    bit<4>    version;
    bit<4>    ihl;
    bit<8>    diffserv;
    bit<16>   totalLen;
    bit<16>   identification;
    bit<3>    flags;
    bit<13>   fragOffset;
    bit<8>    ttl;
    bit<8>    protocol;
    bit<16>   hdrChecksum;
    ip4Addr_t srcAddr;
    ip4Addr_t dstAddr;
}

struct headers {
    ethernet_t   ethernet;
    ipv4_t       ipv4;
}

control MyControl(inout headers hdr) {   

    action myAction(headers hdr2, macAddr_t x) {
        hdr.ethernet.srcAddr = x - 1;
    }

    apply {  
       myAction({{1,2,3,4,5}, {1,2,3,4,5,6,7,8,9,10,11,12,13,14}}, 99); 
    }
}

V1Switch(MyControl()) main;
