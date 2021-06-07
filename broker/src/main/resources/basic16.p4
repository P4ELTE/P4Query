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
    ethernet_t   ethernet5;
    ipv4_t       ipv45;
    ethernet_t   ethernet6;
    ipv4_t       ipv46;
    ethernet_t   ethernet7;
    ipv4_t       ipv47;
    ethernet_t   ethernet8;
    ipv4_t       ipv48;
    ethernet_t   ethernet9;
    ipv4_t       ipv49;
    ethernet_t   ethernet10;
    ipv4_t       ipv410;
    ethernet_t   ethernet11;
    ipv4_t       ipv411;
    ethernet_t   ethernet12;
    ipv4_t       ipv412;
    ethernet_t   ethernet13;
    ipv4_t       ipv413;
    ethernet_t   ethernet14;
    ipv4_t       ipv414;
    ethernet_t   ethernet15;
    ipv4_t       ipv415;
    ethernet_t   ethernet16;
    ipv4_t       ipv416;
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
        transition parse_ethernet5;

    }



    state parse_ethernet5 {
        packet.extract(hdr.ethernet5);
        transition select(hdr.ethernet5.etherType) {
            TYPE_IPV4: parse_ipv45;
            default: accept;
        }
    }

    state parse_ipv45 {
        packet.extract(hdr.ipv45);
        transition parse_ethernet6;
    }
    
    state parse_ethernet6 {
        packet.extract(hdr.ethernet6);
        transition select(hdr.ethernet6.etherType) {
            TYPE_IPV4: parse_ipv46;
            default: accept;
        }
    }

    state parse_ipv46 {
        packet.extract(hdr.ipv46);
        transition parse_ethernet7;

    }

    state parse_ethernet7 {
        packet.extract(hdr.ethernet7);
        transition select(hdr.ethernet7.etherType) {
            TYPE_IPV4: parse_ipv47;
            default: accept;
        }
    }

    state parse_ipv47 {
        packet.extract(hdr.ipv47);
        transition parse_ethernet8;
    }

    state parse_ethernet8 {
        packet.extract(hdr.ethernet8);
        transition select(hdr.ethernet8.etherType) {
            TYPE_IPV4: parse_ipv48;
            default: accept;
        }
    }

    state parse_ipv48 {
        packet.extract(hdr.ipv48);
        transition parse_ethernet9;

    }

    state parse_ethernet9 {
        packet.extract(hdr.ethernet9);
        transition select(hdr.ethernet9.etherType) {
            TYPE_IPV4: parse_ipv49;
            default: accept;
        }
    }



    state parse_ipv49 {
        packet.extract(hdr.ipv49);
        transition parse_ethernet10;
    }
    
    state parse_ethernet10 {
        packet.extract(hdr.ethernet10);
        transition select(hdr.ethernet10.etherType) {
            TYPE_IPV4: parse_ipv410;
            default: accept;
        }
    }

    state parse_ipv410 {
        packet.extract(hdr.ipv410);
        transition parse_ethernet11;

    }

    state parse_ethernet11 {
        packet.extract(hdr.ethernet11);
        transition select(hdr.ethernet11.etherType) {
            TYPE_IPV4: parse_ipv411;
            default: accept;
        }
    }

    state parse_ipv411 {
        packet.extract(hdr.ipv411);
        transition parse_ethernet12;
    }

    state parse_ethernet12 {
        packet.extract(hdr.ethernet12);
        transition select(hdr.ethernet12.etherType) {
            TYPE_IPV4: parse_ipv412;
            default: accept;
        }
    }

    state parse_ipv412 {
        packet.extract(hdr.ipv412);
        transition parse_ethernet13;

    }



    state parse_ethernet13 {
        packet.extract(hdr.ethernet13);
        transition select(hdr.ethernet13.etherType) {
            TYPE_IPV4: parse_ipv413;
            default: accept;
        }
    }

    state parse_ipv413 {
        packet.extract(hdr.ipv413);
        transition parse_ethernet14;
    }
    
    state parse_ethernet14 {
        packet.extract(hdr.ethernet14);
        transition select(hdr.ethernet14.etherType) {
            TYPE_IPV4: parse_ipv414;
            default: accept;
        }
    }

    state parse_ipv414 {
        packet.extract(hdr.ipv414);
        transition parse_ethernet15;

    }

    state parse_ethernet15 {
        packet.extract(hdr.ethernet15);
        transition select(hdr.ethernet15.etherType) {
            TYPE_IPV4: parse_ipv415;
            default: accept;
        }
    }

    state parse_ipv415 {
        packet.extract(hdr.ipv415);
        transition parse_ethernet16;
    }

    state parse_ethernet16 {
        packet.extract(hdr.ethernet16);
        transition select(hdr.ethernet16.etherType) {
            TYPE_IPV4: parse_ipv416;
            default: accept;
        }
    }

    state parse_ipv416 {
        packet.extract(hdr.ipv416);
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

    action ipv45_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet5.srcAddr = hdr.ethernet5.dstAddr;
        hdr.ethernet5.dstAddr = dstAddr;
        hdr.ipv45.ttl = hdr.ipv45.ttl - 1;
    }
    

    action ipv46_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet6.srcAddr = hdr.ethernet6.dstAddr;
        hdr.ethernet6.dstAddr = dstAddr;
        hdr.ipv46.ttl = hdr.ipv46.ttl - 1;
    }


    action ipv47_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet7.srcAddr = hdr.ethernet7.dstAddr;
        hdr.ethernet7.dstAddr = dstAddr;
        hdr.ipv47.ttl = hdr.ipv47.ttl - 1;
    }

    action ipv48_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet8.srcAddr = hdr.ethernet8.dstAddr;
        hdr.ethernet8.dstAddr = dstAddr;
        hdr.ipv48.ttl = hdr.ipv48.ttl - 1;
    }

    action ipv49_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet9.srcAddr = hdr.ethernet9.dstAddr;
        hdr.ethernet9.dstAddr = dstAddr;
        hdr.ipv49.ttl = hdr.ipv49.ttl - 1;
    }
    

    action ipv410_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet10.srcAddr = hdr.ethernet10.dstAddr;
        hdr.ethernet10.dstAddr = dstAddr;
        hdr.ipv410.ttl = hdr.ipv410.ttl - 1;
    }


    action ipv411_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet11.srcAddr = hdr.ethernet11.dstAddr;
        hdr.ethernet11.dstAddr = dstAddr;
        hdr.ipv411.ttl = hdr.ipv411.ttl - 1;
    }

    action ipv412_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet12.srcAddr = hdr.ethernet12.dstAddr;
        hdr.ethernet12.dstAddr = dstAddr;
        hdr.ipv412.ttl = hdr.ipv412.ttl - 1;
    }

    action ipv413_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet13.srcAddr = hdr.ethernet13.dstAddr;
        hdr.ethernet13.dstAddr = dstAddr;
        hdr.ipv413.ttl = hdr.ipv413.ttl - 1;
    }
    

    action ipv414_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet14.srcAddr = hdr.ethernet14.dstAddr;
        hdr.ethernet14.dstAddr = dstAddr;
        hdr.ipv414.ttl = hdr.ipv414.ttl - 1;
    }


    action ipv415_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet15.srcAddr = hdr.ethernet15.dstAddr;
        hdr.ethernet15.dstAddr = dstAddr;
        hdr.ipv415.ttl = hdr.ipv415.ttl - 1;
    }

    action ipv416_forward(macAddr_t dstAddr, egressSpec_t port) {
        standard_metadata.egress_spec = port;
        hdr.ethernet16.srcAddr = hdr.ethernet16.dstAddr;
        hdr.ethernet16.dstAddr = dstAddr;
        hdr.ipv416.ttl = hdr.ipv416.ttl - 1;
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

    table ipv45_lpm {
        key = {
            hdr.ipv45.dstAddr: lpm;
        }
        actions = {
            ipv45_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }

    table ipv46_lpm {
        key = {
            hdr.ipv46.dstAddr: lpm;
        }
        actions = {
            ipv46_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    table ipv47_lpm {
        key = {
            hdr.ipv47.dstAddr: lpm;
        }
        actions = {
            ipv47_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    table ipv48_lpm {
        key = {
            hdr.ipv48.dstAddr: lpm;
        }
        actions = {
            ipv48_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }

    table ipv49_lpm {
        key = {
            hdr.ipv49.dstAddr: lpm;
        }
        actions = {
            ipv49_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }
    
    table ipv410_lpm {
        key = {
            hdr.ipv410.dstAddr: lpm;
        }
        actions = {
            ipv410_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    table ipv411_lpm {
        key = {
            hdr.ipv411.dstAddr: lpm;
        }
        actions = {
            ipv411_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    table ipv412_lpm {
        key = {
            hdr.ipv412.dstAddr: lpm;
        }
        actions = {
            ipv412_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }

    table ipv413_lpm {
        key = {
            hdr.ipv413.dstAddr: lpm;
        }
        actions = {
            ipv413_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }

    table ipv414_lpm {
        key = {
            hdr.ipv414.dstAddr: lpm;
        }
        actions = {
            ipv414_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    table ipv415_lpm {
        key = {
            hdr.ipv415.dstAddr: lpm;
        }
        actions = {
            ipv415_forward;
            drop;
            NoAction;
        }
        size = 1024;
        default_action = drop();
    }


    table ipv416_lpm {
        key = {
            hdr.ipv416.dstAddr: lpm;
        }
        actions = {
            ipv416_forward;
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
                      if (hdr.ipv45.isValid()) {
                         ipv45_lpm.apply();
                         if (hdr.ipv46.isValid()) {
                            ipv46_lpm.apply();
                            if (hdr.ipv47.isValid()) {
                               ipv47_lpm.apply();
                               if (hdr.ipv48.isValid()) {
                                  ipv48_lpm.apply();
                                  if (hdr.ipv49.isValid()) {
                                     ipv49_lpm.apply();
                                      if (hdr.ipv410.isValid()) {
                                         ipv410_lpm.apply();
                                         if (hdr.ipv411.isValid()) {
                                            ipv411_lpm.apply();
                                            if (hdr.ipv412.isValid()) {
                                               ipv412_lpm.apply();
                                               if (hdr.ipv413.isValid()) {
                                                  ipv413_lpm.apply();
                                                  if (hdr.ipv414.isValid()) {
                                                     ipv414_lpm.apply();
                                                     if (hdr.ipv415.isValid()) {
                                                        ipv415_lpm.apply();
                                                        if (hdr.ipv416.isValid()) {
                                                            ipv416_lpm.apply();
                                                        } else {}
                                                    } else {}
                                                  } else {}
                                                } else {}
                                               } else {}
                                           } else {}
                                       } else {}
                                   } else {}
                               } else {}
                            } else {}
                         } else {}
                      } else {}
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
        packet.emit(hdr.ethernet5);
        packet.emit(hdr.ipv45);
        packet.emit(hdr.ethernet6);
        packet.emit(hdr.ipv46);
        packet.emit(hdr.ethernet7);
        packet.emit(hdr.ipv47);
        packet.emit(hdr.ethernet8);
        packet.emit(hdr.ipv48);
        packet.emit(hdr.ethernet9);
        packet.emit(hdr.ipv49);
        packet.emit(hdr.ethernet10);
        packet.emit(hdr.ipv410);
        packet.emit(hdr.ethernet11);
        packet.emit(hdr.ipv411);
        packet.emit(hdr.ethernet12);
        packet.emit(hdr.ipv412);
        packet.emit(hdr.ethernet13);
        packet.emit(hdr.ipv413);
        packet.emit(hdr.ethernet14);
        packet.emit(hdr.ipv414);
        packet.emit(hdr.ethernet15);
        packet.emit(hdr.ipv415);
        packet.emit(hdr.ethernet16);
        packet.emit(hdr.ipv416);

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
