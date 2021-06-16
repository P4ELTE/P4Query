/* -*- P4_16 -*- */
#include <core.p4>
#include <v1model.p4>

const bit<16> TYPE_IPV4 = 0x800;

/*************************************************************************
*********************** H E A D E R S  ***********************************
*************************************************************************/

typedef bit<9>  egressSpec_t;
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

struct metadata {
    /* empty */
}

struct headers {
    ethernet_t   ethernet;
    ipv4_t       ipv4;
    ethernet_t   ethernet2;
    ipv4_t       ipv42;
    ethernet_t   ethernet3;
    ipv4_t       ipv43;
    ethernet_t   ethernet4;
    ipv4_t       ipv44;
}

/*************************************************************************
*********************** P A R S E R  ***********************************
*************************************************************************/

parser MyParser(packet_in packet,
                out headers hdr,
                inout metadata meta,
                inout standard_metadata_t standard_metadata) {

    state start {
        transition parse_ethernet;
    }

    state parse_ethernet {
        packet.extract(hdr.ethernet);
        transition select(hdr.ethernet.etherType) {
            TYPE_IPV4: parse_ipv4;
            default: accept;
        }
    }

    state parse_ipv4 {
        packet.extract(hdr.ipv4);
        transition parse_ethernet2;
    }
    
    state parse_ethernet2 {
        packet.extract(hdr.ethernet2);
        transition select(hdr.ethernet2.etherType) {
            TYPE_IPV4: parse_ipv42;
            default: accept;
        }
    }

    state parse_ipv42 {
        packet.extract(hdr.ipv42);
        transition parse_ethernet3;

    }

    state parse_ethernet3 {
        packet.extract(hdr.ethernet3);
        transition select(hdr.ethernet3.etherType) {
            TYPE_IPV4: parse_ipv43;
            default: accept;
        }
    }

    state parse_ipv43 {
        packet.extract(hdr.ipv43);
        transition parse_ethernet4;
    }

    state parse_ethernet4 {
        packet.extract(hdr.ethernet4);
        transition select(hdr.ethernet4.etherType) {
            TYPE_IPV4: parse_ipv44;
            default: accept;
        }
    }

    state parse_ipv44 {
        packet.extract(hdr.ipv44);
        transition accept;

    }






}

/*************************************************************************
************   C H E C K S U M    V E R I F I C A T I O N   *************
*************************************************************************/

control MyVerifyChecksum(inout headers hdr, inout metadata meta) {   
    apply {  }
}


/*************************************************************************
**************  I N G R E S S   P R O C E S S I N G   *******************
*************************************************************************/

control MyIngress(inout headers hdr,
                  inout metadata meta,
                  inout standard_metadata_t standard_metadata) {
    action drop() {
        mark_to_drop(standard_metadata);
    }
    
    action ipv4_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet.srcAddr = hdr.ethernet.dstAddr;
        hdr.ethernet.dstAddr = dstAddr;
        hdr.ipv4.ttl = hdr.ipv4.ttl - 1;
    }
    

    action ipv42_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet2.srcAddr = hdr.ethernet2.dstAddr;
        hdr.ethernet2.dstAddr = dstAddr;
        hdr.ipv42.ttl = hdr.ipv42.ttl - 1;
    }


    action ipv43_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet3.srcAddr = hdr.ethernet3.dstAddr;
        hdr.ethernet3.dstAddr = dstAddr;
        hdr.ipv43.ttl = hdr.ipv43.ttl - 1;
    }

    action ipv44_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet4.srcAddr = hdr.ethernet4.dstAddr;
        hdr.ethernet4.dstAddr = dstAddr;
        hdr.ipv44.ttl = hdr.ipv44.ttl - 1;
    }




    table ipv4_lpm {
        key = {
            hdr.ipv4.dstAddr: lpm;
        }
        actions = {
            ipv4_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }
    
    table ipv42_lpm {
        key = {
            hdr.ipv42.dstAddr: lpm;
        }
        actions = {
            ipv42_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    table ipv43_lpm {
        key = {
            hdr.ipv43.dstAddr: lpm;
        }
        actions = {
            ipv43_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    table ipv44_lpm {
        key = {
            hdr.ipv44.dstAddr: lpm;
        }
        actions = {
            ipv44_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    apply {
        if (hdr.ipv4.isValid()) {
            ipv4_lpm.apply();
            if (hdr.ipv42.isValid()) {
               ipv42_lpm.apply();
               if (hdr.ipv43.isValid()) {
                   ipv43_lpm.apply();
                   if (hdr.ipv44.isValid()) {
                      ipv44_lpm.apply();
                   } else {}
               } else {}
            } else {}
        } else {}
    }
}

/*************************************************************************
****************  E G R E S S   P R O C E S S I N G   *******************
*************************************************************************/

control MyEgress(inout headers hdr,
                 inout metadata meta,
                 inout standard_metadata_t standard_metadata) {
    apply {  }
}

/*************************************************************************
*************   C H E C K S U M    C O M P U T A T I O N   **************
*************************************************************************/

control MyComputeChecksum(inout headers  hdr, inout metadata meta) {
     apply {
	update_checksum(
	    hdr.ipv4.isValid(),
            { hdr.ipv4.version,
	      hdr.ipv4.ihl,
              hdr.ipv4.diffserv,
              hdr.ipv4.totalLen,
              hdr.ipv4.identification,
              hdr.ipv4.flags,
              hdr.ipv4.fragOffset,
              hdr.ipv4.ttl,
              hdr.ipv4.protocol,
              hdr.ipv4.srcAddr,
              hdr.ipv4.dstAddr },
            hdr.ipv4.hdrChecksum,
            HashAlgorithm.csum16);
    }
}

/*************************************************************************
***********************  D E P A R S E R  *******************************
*************************************************************************/

control MyDeparser(packet_out packet, in headers hdr) {
    apply {
        packet.emit(hdr.ethernet);
        packet.emit(hdr.ipv4);
        packet.emit(hdr.ethernet2);
        packet.emit(hdr.ipv42);
        packet.emit(hdr.ethernet3);
        packet.emit(hdr.ipv43);
        packet.emit(hdr.ethernet4);
        packet.emit(hdr.ipv44);
    }
}

/*************************************************************************
***********************  S W I T C H  *******************************
*************************************************************************/

V1Switch(
MyParser(),
MyVerifyChecksum(),
MyIngress(),
MyEgress(),
MyComputeChecksum(),
MyDeparser()
) main;