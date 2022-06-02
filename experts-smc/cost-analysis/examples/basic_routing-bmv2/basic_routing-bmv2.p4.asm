;
; Copyright 2020-2022, Dániel Lukács, Eötvös Loránd University.
; All rights reserved.
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;     http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;
; Author: Dániel Lukács, 2022
;


  packet_in=0 (273),
  packet_in.cursor=0 (1),
  packet_in.buffer=1 (272),
  packet_out=273 (0),
  GLOBAL=273 (32),
  GLOBAL.__v1model_version=273 (32),
  standard_metadata_t=305 (326),
  standard_metadata_t.ingress_port=305 (9),
  standard_metadata_t.egress_spec=314 (9),
  standard_metadata_t.egress_port=323 (9),
  standard_metadata_t.instance_type=332 (32),
  standard_metadata_t.packet_length=364 (32),
  standard_metadata_t.enq_timestamp=396 (32),
  standard_metadata_t.enq_qdepth=428 (19),
  standard_metadata_t.deq_timedelta=447 (32),
  standard_metadata_t.deq_qdepth=479 (19),
  standard_metadata_t.ingress_global_timestamp=498 (48),
  standard_metadata_t.egress_global_timestamp=546 (48),
  standard_metadata_t.mcast_grp=594 (16),
  standard_metadata_t.egress_rid=610 (16),
  standard_metadata_t.checksum_error=626 (1),
  standard_metadata_t.parser_error=627 (1),
  standard_metadata_t.priority=628 (3),
  ingress_metadata_t=631 (44),
  ingress_metadata_t.vrf=631 (12),
  ingress_metadata_t.bd=643 (16),
  ingress_metadata_t.nexthop_index=659 (16),
  metadata=675 (44),
  metadata.ingress_metadata=675 (44),
  metadata.ingress_metadata.vrf=675 (12),
  metadata.ingress_metadata.bd=687 (16),
  metadata.ingress_metadata.nexthop_index=703 (16),
  headers=719 (276),
  headers.ethernet=719 (114),
  headers.ethernet.valid=719 (1),
  headers.ethernet.size=720 (1),
  headers.ethernet.srcAddr=721 (48),
  headers.ethernet.dstAddr=769 (48),
  headers.ethernet.etherType=817 (16),
  headers.ipv4=833 (162),
  headers.ipv4.valid=833 (1),
  headers.ipv4.size=834 (1),
  headers.ipv4.version=835 (4),
  headers.ipv4.ihl=839 (4),
  headers.ipv4.diffserv=843 (8),
  headers.ipv4.totalLen=851 (16),
  headers.ipv4.identification=867 (16),
  headers.ipv4.flags=883 (3),
  headers.ipv4.fragOffset=886 (13),
  headers.ipv4.ttl=899 (8),
  headers.ipv4.protocol=907 (8),
  headers.ipv4.hdrChecksum=915 (16),
  headers.ipv4.srcAddr=931 (32),
  headers.ipv4.dstAddr=963 (32),
  rewrite_mac=995 (0),
  rewrite_mac.hit=995 (1),
  ingress=996 (0),
  ingress.temp0_BOOL=996 (1),
  ingress.temp1_bit_9=997 (9),
  computeChecksum=1006 (0),
  computeChecksum.temp0_::update_checksum/condition=1006 (1),
  computeChecksum.temp1_LIST_11=1007 (11),
  computeChecksum.temp2_enum-type=1018 (1),
  set_vrf=1019 (12),
  set_vrf.vrf=1019 (12),
  on_miss=1031 (0),
  fib_hit_nexthop=1031 (16),
  fib_hit_nexthop.nexthop_index=1031 (16),
  fib_hit_nexthop.temp0_bit_8=1047 (8),
  fib_hit_nexthop.temp1_bit_8=1055 (8),
  NoAction=1063 (0),
  set_egress_details=1063 (9),
  set_egress_details.egress_spec=1063 (9),
  set_bd=1072 (16),
  set_bd.bd=1072 (16),
  bd=1088 (0),
  bd.hit=1088 (1),
  bd.ENTRY[0,0]=1089 (16),
  ipv4_fib=1105 (0),
  ipv4_fib.hit=1105 (1),
  ipv4_fib.ENTRY[0,0]=1106 (32),
  ipv4_fib.ENTRY[0,1]=1138 (12),
  ipv4_fib_lpm=1150 (0),
  ipv4_fib_lpm.hit=1150 (1),
  ipv4_fib_lpm.ENTRY[0,0]=1151 (32),
  ipv4_fib_lpm.ENTRY[0,1]=1183 (12),
  nexthop=1195 (0),
  nexthop.hit=1195 (1),
  nexthop.ENTRY[0,0]=1196 (16),
  port_mapping=1212 (0),
  port_mapping.hit=1212 (1),
  port_mapping.ENTRY[0,0]=1213 (9),
  ParserImpl=1222 (0),
  ParserImpl.packet=1222 (0),
  ParserImpl.temp0_LIST_1=1222 (1),
  ParserImpl.temp1_LIST_1=1223 (1),
  ParserImpl.temp2_bit_16=1224 (16),
  ParserImpl.temp3_LIST_1=1240 (1),
  DeparserImpl=1241 (0),
  DeparserImpl.packet=1241 (0),
  verifyChecksum=1241 (0),
  verifyChecksum.temp0_::update_checksum/condition=1241 (1),
  verifyChecksum.temp1_LIST_11=1242 (11),
  verifyChecksum.temp2_enum-type=1253 (1),
  egress=1254 (0),
  rewrite_src_dst_mac=1254 (96),
  rewrite_src_dst_mac.smac=1254 (48),
  rewrite_src_dst_mac.dmac=1302 (48),
  receive_packet=1350 (0),
  fill_tables=1350 (0),
  memcmp=1350 (3),
  memcmp.src=1350 (1),
  memcmp.dst=1351 (1),
  memcmp.length=1352 (1),
  memcpy=1353 (3),
  memcpy.src=1353 (1),
  memcpy.dst=1354 (1),
  memcpy.length=1355 (1),
  subtract=1356 (0),
  main=1356 (0),
  main.temp0_LIST_5=1356 (5),
  main.temp1_LIST_14=1361 (14),
  main.temp2_LIST_2=1375 (2),
  main.temp3_LIST_3=1377 (3),
  main.temp4_LIST_1=1380 (1),
  main.temp5_LIST_16=1381 (16),
  main.temp6_LIST_5=1397 (5),
  main.temp7_LIST_14=1402 (14),
  main.temp8_LIST_2=1416 (2),
  main.temp9_LIST_3=1418 (3),
  main.temp10_LIST_1=1421 (1),
  main.temp11_LIST_16=1422 (16),
  main.temp12_LIST_5=1438 (5),
  main.temp13_LIST_14=1443 (14),
  main.temp14_LIST_2=1457 (2),
  main.temp15_LIST_3=1459 (3),
  main.temp16_LIST_1=1462 (1),
  main.temp17_LIST_5=1463 (5),
  main.temp18_LIST_14=1468 (14),
  main.temp19_LIST_2=1482 (2),
  main.temp20_LIST_3=1484 (3),
  main.temp21_LIST_1=1487 (1),
  main.temp22_LIST_16=1488 (16),
  main.temp23_LIST_5=1504 (5),
  main.temp24_LIST_14=1509 (14),
  main.temp25_LIST_2=1523 (2),
  main.temp26_LIST_3=1525 (3),
  main.temp27_LIST_1=1528 (1),
  main.temp28_LIST_5=1529 (5),
  main.temp29_LIST_14=1534 (14),
  main.temp30_LIST_2=1548 (2),
  isValid=1550 (1),
  isValid.hdr=1550 (1),
  extract=1551 (2),
  extract.packet=1551 (1),
  extract.hdr=1552 (1),
  emit=1553 (2),
  emit.packet=1553 (1),
  emit.hdr=1554 (1),
  update_checksum=1555 (4),
  update_checksum.condition=1555 (1),
  update_checksum.data=1556 (1),
  update_checksum.checksum=1557 (1),
  update_checksum.algo=1558 (1),
  verify_checksum=1559 (4),
  verify_checksum.condition=1559 (1),
  verify_checksum.data=1560 (1),
  verify_checksum.checksum=1561 (1),
  verify_checksum.algo=1562 (1),
  mark_to_drop=1563 (1),
  mark_to_drop.standard_metadata=1563 (1),
  count=1564 (33),
  count.counter=1564 (1),
  count.index=1565 (32),
  setInvalid=1597 (1),
  setInvalid.hdr=1597 (1),
  setValid=1598 (1),
  setValid.hdr=1598 (1),

0:  	alloc 1599		 //1599: size of: global
1:  	const 112		 //112: size of: headers.ethernet without validity bit, size field
2:  	const 720		 //global address of headers.ethernet.size
3:  	putfield
4:  	const 160		 //160: size of: headers.ipv4 without validity bit, size field
5:  	const 834		 //global address of headers.ipv4.size
6:  	putfield
7:  	invoke 1587 0		 // 1587: label to stdlib::fill_tables, 0: size of: 
8:  	invoke 661 0		 // 661: label to stdlib::receive_packet, 0: size of: 
9:  	invoke 1803 0		 // 1803: label to ::main, 0: size of: 
10:  	pop
11:  	exit
// definition of ingress(headers hdr, metadata meta, standard_metadata_t standard_metadata)
// start of block
// if(null::isValid(hdr.ipv4))
// null::isValid(hdr.ipv4)
// hdr.ipv4
12:  	load 0		 // 0: local address of hdr
13:  	const 1		 //1: index of ipv4
14:  	add
15:  	getfield
// end of hdr.ipv4
16:  	invoke 2306 1		 // 2306: label to null::isValid, 1: size of: 
// return value is used
// end of null::isValid(hdr.ipv4)
17:  	ifeq 47		 //47: label to jump if not null::isValid(hdr.ipv4)
// start of block
// ingress::port_mapping(hdr, meta, standard_metadata)
18:  	load 0		 // 0: local address of hdr
19:  	load 1		 // 1: local address of meta
20:  	load 2		 // 2: local address of standard_metadata
21:  	invoke 637 3		 // 637: label to ingress::port_mapping, 3: size of: 
22:  	pop
// end of ingress::port_mapping(hdr, meta, standard_metadata)
// ingress::bd(hdr, meta, standard_metadata)
23:  	load 0		 // 0: local address of hdr
24:  	load 1		 // 1: local address of meta
25:  	load 2		 // 2: local address of standard_metadata
26:  	invoke 507 3		 // 507: label to ingress::bd, 3: size of: 
27:  	pop
// end of ingress::bd(hdr, meta, standard_metadata)
// if(!ingress::ipv4_fib(hdr, meta, standard_metadata).hit)
// !ingress::ipv4_fib(hdr, meta, standard_metadata).hit (size: 1)
// ingress::ipv4_fib(hdr, meta, standard_metadata).hit
// ingress::ipv4_fib(hdr, meta, standard_metadata)
28:  	load 0		 // 0: local address of hdr
29:  	load 1		 // 1: local address of meta
30:  	load 2		 // 2: local address of standard_metadata
31:  	invoke 534 3		 // 534: label to ingress::ipv4_fib, 3: size of: 
32:  	pop
// end of ingress::ipv4_fib(hdr, meta, standard_metadata)
33:  	const 1105		 //global address of ipv4_fib.hit
// end of ingress::ipv4_fib(hdr, meta, standard_metadata).hit
34:  	getfield
35:  	not
// end of !ingress::ipv4_fib(hdr, meta, standard_metadata).hit (size: 1)
36:  	ifeq 42		 //42: label to jump if not !ingress::ipv4_fib(hdr, meta, standard_metadata).hit
// start of block
// ingress::ipv4_fib_lpm(hdr, meta, standard_metadata)
37:  	load 0		 // 0: local address of hdr
38:  	load 1		 // 1: local address of meta
39:  	load 2		 // 2: local address of standard_metadata
40:  	invoke 572 3		 // 572: label to ingress::ipv4_fib_lpm, 3: size of: 
41:  	pop
// end of ingress::ipv4_fib_lpm(hdr, meta, standard_metadata)
// ingress::nexthop(hdr, meta, standard_metadata)
42:  	load 0		 // 0: local address of hdr
43:  	load 1		 // 1: local address of meta
44:  	load 2		 // 2: local address of standard_metadata
45:  	invoke 610 3		 // 610: label to ingress::nexthop, 3: size of: 
46:  	pop
// end of ingress::nexthop(hdr, meta, standard_metadata)
// standard_metadata.egress_spec = 1
// 1
47:  	const 1		 //1: 0th bit of 1
48:  	const 997		 //global address of ingress.temp1_bit_9
49:  	const 8		 //8: size of: suffix of bit_9
50:  	add
51:  	putfield
52:  	const 997		 //global address of ingress.temp1_bit_9
// end of 1
// standard_metadata.egress_spec
53:  	load 2		 // 2: local address of standard_metadata
54:  	const 1		 //1: index of egress_spec
55:  	add
56:  	getfield
// end of standard_metadata.egress_spec
57:  	const 9		 //9: size of: standard_metadata.egress_spec
58:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
59:  	pop
// end of standard_metadata.egress_spec = 1
60:  	const 0		 //0: ingress terminates with status OK
61:  	return 
// end of definition of ingress(headers hdr, metadata meta, standard_metadata_t standard_metadata)
//

// definition of computeChecksum(headers hdr, metadata meta)
// start of block
// null::update_checksum(1, {hdr.ipv4.version,hdr.ipv4.ihl,hdr.ipv4.diffserv,hdr.ipv4.totalLen,hdr.ipv4.identification,hdr.ipv4.flags,hdr.ipv4.fragOffset,hdr.ipv4.ttl,hdr.ipv4.protocol,hdr.ipv4.srcAddr,hdr.ipv4.dstAddr}, hdr.ipv4.hdrChecksum, 1)
// 1
62:  	const 1		 //1: ::update_checksum/condition
63:  	const 1006		 //global address of computeChecksum.temp0_::update_checksum/condition
64:  	putfield
65:  	const 1006		 //global address of computeChecksum.temp0_::update_checksum/condition
// end of 1
// {hdr.ipv4.version,hdr.ipv4.ihl,hdr.ipv4.diffserv,hdr.ipv4.totalLen,hdr.ipv4.identification,hdr.ipv4.flags,hdr.ipv4.fragOffset,hdr.ipv4.ttl,hdr.ipv4.protocol,hdr.ipv4.srcAddr,hdr.ipv4.dstAddr}
// hdr.ipv4.version
66:  	load 0		 // 0: local address of hdr
67:  	const 1		 //1: index of ipv4
68:  	add
69:  	getfield
70:  	const 2		 //2: index of version
71:  	add
72:  	getfield
// end of hdr.ipv4.version
// hdr.ipv4.ihl
73:  	load 0		 // 0: local address of hdr
74:  	const 1		 //1: index of ipv4
75:  	add
76:  	getfield
77:  	const 3		 //3: index of ihl
78:  	add
79:  	getfield
// end of hdr.ipv4.ihl
// hdr.ipv4.diffserv
80:  	load 0		 // 0: local address of hdr
81:  	const 1		 //1: index of ipv4
82:  	add
83:  	getfield
84:  	const 4		 //4: index of diffserv
85:  	add
86:  	getfield
// end of hdr.ipv4.diffserv
// hdr.ipv4.totalLen
87:  	load 0		 // 0: local address of hdr
88:  	const 1		 //1: index of ipv4
89:  	add
90:  	getfield
91:  	const 5		 //5: index of totalLen
92:  	add
93:  	getfield
// end of hdr.ipv4.totalLen
// hdr.ipv4.identification
94:  	load 0		 // 0: local address of hdr
95:  	const 1		 //1: index of ipv4
96:  	add
97:  	getfield
98:  	const 6		 //6: index of identification
99:  	add
100:  	getfield
// end of hdr.ipv4.identification
// hdr.ipv4.flags
101:  	load 0		 // 0: local address of hdr
102:  	const 1		 //1: index of ipv4
103:  	add
104:  	getfield
105:  	const 7		 //7: index of flags
106:  	add
107:  	getfield
// end of hdr.ipv4.flags
// hdr.ipv4.fragOffset
108:  	load 0		 // 0: local address of hdr
109:  	const 1		 //1: index of ipv4
110:  	add
111:  	getfield
112:  	const 8		 //8: index of fragOffset
113:  	add
114:  	getfield
// end of hdr.ipv4.fragOffset
// hdr.ipv4.ttl
115:  	load 0		 // 0: local address of hdr
116:  	const 1		 //1: index of ipv4
117:  	add
118:  	getfield
119:  	const 9		 //9: index of ttl
120:  	add
121:  	getfield
// end of hdr.ipv4.ttl
// hdr.ipv4.protocol
122:  	load 0		 // 0: local address of hdr
123:  	const 1		 //1: index of ipv4
124:  	add
125:  	getfield
126:  	const 10		 //10: index of protocol
127:  	add
128:  	getfield
// end of hdr.ipv4.protocol
// hdr.ipv4.srcAddr
129:  	load 0		 // 0: local address of hdr
130:  	const 1		 //1: index of ipv4
131:  	add
132:  	getfield
133:  	const 12		 //12: index of srcAddr
134:  	add
135:  	getfield
// end of hdr.ipv4.srcAddr
// hdr.ipv4.dstAddr
136:  	load 0		 // 0: local address of hdr
137:  	const 1		 //1: index of ipv4
138:  	add
139:  	getfield
140:  	const 13		 //13: index of dstAddr
141:  	add
142:  	getfield
// end of hdr.ipv4.dstAddr
// memcpy(src,dst,length)
143:  	derefTop
144:  	const 11		 //11: size of: list
145:  	sub
146:  	inc
147:  	const 1007		 //global address of computeChecksum.temp1_LIST_11
148:  	const 11		 //11: size of: list
149:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
150:  	pop
// end of memcpy(src,dst,length)
151:  	popn 11		 //11: size of: list
152:  	const 1007		 //global address of computeChecksum.temp1_LIST_11
// end of {hdr.ipv4.version,hdr.ipv4.ihl,hdr.ipv4.diffserv,hdr.ipv4.totalLen,hdr.ipv4.identification,hdr.ipv4.flags,hdr.ipv4.fragOffset,hdr.ipv4.ttl,hdr.ipv4.protocol,hdr.ipv4.srcAddr,hdr.ipv4.dstAddr}
// hdr.ipv4.hdrChecksum
153:  	load 0		 // 0: local address of hdr
154:  	const 1		 //1: index of ipv4
155:  	add
156:  	getfield
157:  	const 11		 //11: index of hdrChecksum
158:  	add
159:  	getfield
// end of hdr.ipv4.hdrChecksum
// enum field
160:  	const 1		 //1: enum-type
161:  	const 1018		 //global address of computeChecksum.temp2_enum-type
162:  	putfield
163:  	const 1018		 //global address of computeChecksum.temp2_enum-type
// end of field
164:  	invoke 2345 4		 // 2345: label to null::update_checksum, 4: size of: 
165:  	pop
// end of null::update_checksum(1, {hdr.ipv4.version,hdr.ipv4.ihl,hdr.ipv4.diffserv,hdr.ipv4.totalLen,hdr.ipv4.identification,hdr.ipv4.flags,hdr.ipv4.fragOffset,hdr.ipv4.ttl,hdr.ipv4.protocol,hdr.ipv4.srcAddr,hdr.ipv4.dstAddr}, hdr.ipv4.hdrChecksum, 1)
166:  	const 0		 //0: computeChecksum terminates with status OK
167:  	return 
// end of definition of computeChecksum(headers hdr, metadata meta)
//

// definition of set_vrf(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_12 vrf)
// start of block
// meta.ingress_metadata.vrf = vrf
168:  	load 3		 // 3: local address of vrf
// meta.ingress_metadata.vrf
169:  	load 1		 // 1: local address of meta
170:  	const 0		 //0: index of ingress_metadata
171:  	add
172:  	getfield
173:  	const 0		 //0: index of vrf
174:  	add
175:  	getfield
// end of meta.ingress_metadata.vrf
176:  	const 12		 //12: size of: meta.ingress_metadata.vrf
177:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
178:  	pop
// end of meta.ingress_metadata.vrf = vrf
179:  	const 0		 //0: set_vrf terminates with status OK
180:  	return 
// end of definition of set_vrf(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_12 vrf)
//

// definition of on_miss(headers hdr, metadata meta, standard_metadata_t standard_metadata)
// start of block
181:  	const 0		 //0: on_miss terminates with status OK
182:  	return 
// end of definition of on_miss(headers hdr, metadata meta, standard_metadata_t standard_metadata)
//

// definition of fib_hit_nexthop(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_16 nexthop_index)
// start of block
// meta.ingress_metadata.nexthop_index = nexthop_index
183:  	load 3		 // 3: local address of nexthop_index
// meta.ingress_metadata.nexthop_index
184:  	load 1		 // 1: local address of meta
185:  	const 0		 //0: index of ingress_metadata
186:  	add
187:  	getfield
188:  	const 2		 //2: index of nexthop_index
189:  	add
190:  	getfield
// end of meta.ingress_metadata.nexthop_index
191:  	const 16		 //16: size of: meta.ingress_metadata.nexthop_index
192:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
193:  	pop
// end of meta.ingress_metadata.nexthop_index = nexthop_index
// hdr.ipv4.ttl = hdr.ipv4.ttl-8w1
// hdr.ipv4.ttl-8w1 (size: 8)
// hdr.ipv4.ttl
194:  	load 0		 // 0: local address of hdr
195:  	const 1		 //1: index of ipv4
196:  	add
197:  	getfield
198:  	const 9		 //9: index of ttl
199:  	add
200:  	getfield
// end of hdr.ipv4.ttl
// 8w1
201:  	const 1		 //1: 0th bit of 8w1
202:  	const 1047		 //global address of fib_hit_nexthop.temp0_bit_8
203:  	const 7		 //7: size of: suffix of bit_8
204:  	add
205:  	putfield
206:  	const 1047		 //global address of fib_hit_nexthop.temp0_bit_8
// end of 8w1
207:  	const 8		 //8: size of: hdr.ipv4.ttl
208:  	const 1055		 //global address of fib_hit_nexthop.temp1_bit_8
209:  	invoke 1801 4		 // 1801: label to stdlib::subtract, 4: size of: left, right, target, length
// end of hdr.ipv4.ttl-8w1 (size: 8)
// hdr.ipv4.ttl
210:  	load 0		 // 0: local address of hdr
211:  	const 1		 //1: index of ipv4
212:  	add
213:  	getfield
214:  	const 9		 //9: index of ttl
215:  	add
216:  	getfield
// end of hdr.ipv4.ttl
217:  	const 8		 //8: size of: hdr.ipv4.ttl
218:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
219:  	pop
// end of hdr.ipv4.ttl = hdr.ipv4.ttl-8w1
220:  	const 0		 //0: fib_hit_nexthop terminates with status OK
221:  	return 
// end of definition of fib_hit_nexthop(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_16 nexthop_index)
//

// definition of NoAction()
// start of block
222:  	const 0		 //0: NoAction terminates with status OK
223:  	return 
// end of definition of NoAction()
//

// definition of set_egress_details(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_9 egress_spec)
// start of block
224:  	const 0		 //0: set_egress_details terminates with status OK
225:  	return 
// end of definition of set_egress_details(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_9 egress_spec)
//

// definition of set_bd(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_16 bd)
// start of block
// meta.ingress_metadata.bd = bd
226:  	load 3		 // 3: local address of bd
// meta.ingress_metadata.bd
227:  	load 1		 // 1: local address of meta
228:  	const 0		 //0: index of ingress_metadata
229:  	add
230:  	getfield
231:  	const 1		 //1: index of bd
232:  	add
233:  	getfield
// end of meta.ingress_metadata.bd
234:  	const 16		 //16: size of: meta.ingress_metadata.bd
235:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
236:  	pop
// end of meta.ingress_metadata.bd = bd
237:  	const 0		 //0: set_bd terminates with status OK
238:  	return 
// end of definition of set_bd(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_16 bd)
//

// definition of ParserImpl(packet_in packet, headers hdr, metadata meta, standard_metadata_t standard_metadata)
// state start
// state parse_ethernet
// packet_in::extract(packet, hdr.ethernet)
239:  	load 0		 // 0: local address of packet
// hdr.ethernet
240:  	load 1		 // 1: local address of hdr
241:  	const 0		 //0: index of ethernet
242:  	add
243:  	getfield
// end of hdr.ethernet
244:  	invoke 2310 2		 // 2310: label to packet_in::extract, 2: size of: 
245:  	pop
// end of packet_in::extract(packet, hdr.ethernet)
// select {hdr.ethernet.etherType}
// {hdr.ethernet.etherType}
// hdr.ethernet.etherType
246:  	load 1		 // 1: local address of hdr
247:  	const 0		 //0: index of ethernet
248:  	add
249:  	getfield
250:  	const 4		 //4: index of etherType
251:  	add
252:  	getfield
// end of hdr.ethernet.etherType
// memcpy(src,dst,length)
253:  	derefTop
254:  	const 1		 //1: size of: list
255:  	sub
256:  	inc
257:  	const 1222		 //global address of ParserImpl.temp0_LIST_1
258:  	const 1		 //1: size of: list
259:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
260:  	pop
// end of memcpy(src,dst,length)
261:  	popn 1		 //1: size of: list
262:  	const 1222		 //global address of ParserImpl.temp0_LIST_1
// end of {hdr.ethernet.etherType}
// HEAD ~= 16w0x800
263:  	top
264:  	const 0		 //0: index of head expression
265:  	add
266:  	getfield
// 16w0x800
267:  	const 0		 //0: 11th bit of 16w0x800
268:  	const 1224		 //global address of ParserImpl.temp2_bit_16
269:  	const 15		 //15: size of: suffix of bit_16
270:  	add
271:  	putfield
272:  	const 0		 //0: 10th bit of 16w0x800
273:  	const 1224		 //global address of ParserImpl.temp2_bit_16
274:  	const 14		 //14: size of: suffix of bit_16
275:  	add
276:  	putfield
277:  	const 0		 //0: 9th bit of 16w0x800
278:  	const 1224		 //global address of ParserImpl.temp2_bit_16
279:  	const 13		 //13: size of: suffix of bit_16
280:  	add
281:  	putfield
282:  	const 0		 //0: 8th bit of 16w0x800
283:  	const 1224		 //global address of ParserImpl.temp2_bit_16
284:  	const 12		 //12: size of: suffix of bit_16
285:  	add
286:  	putfield
287:  	const 0		 //0: 7th bit of 16w0x800
288:  	const 1224		 //global address of ParserImpl.temp2_bit_16
289:  	const 11		 //11: size of: suffix of bit_16
290:  	add
291:  	putfield
292:  	const 0		 //0: 6th bit of 16w0x800
293:  	const 1224		 //global address of ParserImpl.temp2_bit_16
294:  	const 10		 //10: size of: suffix of bit_16
295:  	add
296:  	putfield
297:  	const 0		 //0: 5th bit of 16w0x800
298:  	const 1224		 //global address of ParserImpl.temp2_bit_16
299:  	const 9		 //9: size of: suffix of bit_16
300:  	add
301:  	putfield
302:  	const 0		 //0: 4th bit of 16w0x800
303:  	const 1224		 //global address of ParserImpl.temp2_bit_16
304:  	const 8		 //8: size of: suffix of bit_16
305:  	add
306:  	putfield
307:  	const 0		 //0: 3th bit of 16w0x800
308:  	const 1224		 //global address of ParserImpl.temp2_bit_16
309:  	const 7		 //7: size of: suffix of bit_16
310:  	add
311:  	putfield
312:  	const 0		 //0: 2th bit of 16w0x800
313:  	const 1224		 //global address of ParserImpl.temp2_bit_16
314:  	const 6		 //6: size of: suffix of bit_16
315:  	add
316:  	putfield
317:  	const 0		 //0: 1th bit of 16w0x800
318:  	const 1224		 //global address of ParserImpl.temp2_bit_16
319:  	const 5		 //5: size of: suffix of bit_16
320:  	add
321:  	putfield
322:  	const 1		 //1: 0th bit of 16w0x800
323:  	const 1224		 //global address of ParserImpl.temp2_bit_16
324:  	const 4		 //4: size of: suffix of bit_16
325:  	add
326:  	putfield
327:  	const 1224		 //global address of ParserImpl.temp2_bit_16
// end of 16w0x800
328:  	const 16		 //16: size of: 16w0x800
329:  	invoke 1760 3		 // 1760: label to stdlib::memcmp, 3: size of: left, right, length
// return value is used
330:  	ifeq 342		 //342: label to jump to next case if not HEAD ~= 16w0x800
331:  	goto 332		 /* 332: label to match */
// match
332:  	pop
// state parse_ipv4
// packet_in::extract(packet, hdr.ipv4)
333:  	load 0		 // 0: local address of packet
// hdr.ipv4
334:  	load 1		 // 1: local address of hdr
335:  	const 1		 //1: index of ipv4
336:  	add
337:  	getfield
// end of hdr.ipv4
338:  	invoke 2310 2		 // 2310: label to packet_in::extract, 2: size of: 
339:  	pop
// end of packet_in::extract(packet, hdr.ipv4)
340:  	const 0		 //0: ParserImpl terminates with status OK
341:  	return 
// HEAD ~= DEFAULT
342:  	goto 343		 /* 343: label to match */
// match
343:  	pop
344:  	const 0		 //0: ParserImpl terminates with status OK
345:  	return 
// end of definition of ParserImpl(packet_in packet, headers hdr, metadata meta, standard_metadata_t standard_metadata)
//

// definition of DeparserImpl(packet_out packet, headers hdr)
// start of block
// packet_out::emit(packet, hdr.ethernet)
346:  	load 0		 // 0: local address of packet
// hdr.ethernet
347:  	load 1		 // 1: local address of hdr
348:  	const 0		 //0: index of ethernet
349:  	add
350:  	getfield
// end of hdr.ethernet
351:  	invoke 2343 2		 // 2343: label to packet_out::emit, 2: size of: 
352:  	pop
// end of packet_out::emit(packet, hdr.ethernet)
// packet_out::emit(packet, hdr.ipv4)
353:  	load 0		 // 0: local address of packet
// hdr.ipv4
354:  	load 1		 // 1: local address of hdr
355:  	const 1		 //1: index of ipv4
356:  	add
357:  	getfield
// end of hdr.ipv4
358:  	invoke 2343 2		 // 2343: label to packet_out::emit, 2: size of: 
359:  	pop
// end of packet_out::emit(packet, hdr.ipv4)
360:  	const 0		 //0: DeparserImpl terminates with status OK
361:  	return 
// end of definition of DeparserImpl(packet_out packet, headers hdr)
//

// definition of verifyChecksum(headers hdr, metadata meta)
// start of block
// null::verify_checksum(1, {hdr.ipv4.version,hdr.ipv4.ihl,hdr.ipv4.diffserv,hdr.ipv4.totalLen,hdr.ipv4.identification,hdr.ipv4.flags,hdr.ipv4.fragOffset,hdr.ipv4.ttl,hdr.ipv4.protocol,hdr.ipv4.srcAddr,hdr.ipv4.dstAddr}, hdr.ipv4.hdrChecksum, 1)
// 1
362:  	const 1		 //1: ::update_checksum/condition
363:  	const 1241		 //global address of verifyChecksum.temp0_::update_checksum/condition
364:  	putfield
365:  	const 1241		 //global address of verifyChecksum.temp0_::update_checksum/condition
// end of 1
// {hdr.ipv4.version,hdr.ipv4.ihl,hdr.ipv4.diffserv,hdr.ipv4.totalLen,hdr.ipv4.identification,hdr.ipv4.flags,hdr.ipv4.fragOffset,hdr.ipv4.ttl,hdr.ipv4.protocol,hdr.ipv4.srcAddr,hdr.ipv4.dstAddr}
// hdr.ipv4.version
366:  	load 0		 // 0: local address of hdr
367:  	const 1		 //1: index of ipv4
368:  	add
369:  	getfield
370:  	const 2		 //2: index of version
371:  	add
372:  	getfield
// end of hdr.ipv4.version
// hdr.ipv4.ihl
373:  	load 0		 // 0: local address of hdr
374:  	const 1		 //1: index of ipv4
375:  	add
376:  	getfield
377:  	const 3		 //3: index of ihl
378:  	add
379:  	getfield
// end of hdr.ipv4.ihl
// hdr.ipv4.diffserv
380:  	load 0		 // 0: local address of hdr
381:  	const 1		 //1: index of ipv4
382:  	add
383:  	getfield
384:  	const 4		 //4: index of diffserv
385:  	add
386:  	getfield
// end of hdr.ipv4.diffserv
// hdr.ipv4.totalLen
387:  	load 0		 // 0: local address of hdr
388:  	const 1		 //1: index of ipv4
389:  	add
390:  	getfield
391:  	const 5		 //5: index of totalLen
392:  	add
393:  	getfield
// end of hdr.ipv4.totalLen
// hdr.ipv4.identification
394:  	load 0		 // 0: local address of hdr
395:  	const 1		 //1: index of ipv4
396:  	add
397:  	getfield
398:  	const 6		 //6: index of identification
399:  	add
400:  	getfield
// end of hdr.ipv4.identification
// hdr.ipv4.flags
401:  	load 0		 // 0: local address of hdr
402:  	const 1		 //1: index of ipv4
403:  	add
404:  	getfield
405:  	const 7		 //7: index of flags
406:  	add
407:  	getfield
// end of hdr.ipv4.flags
// hdr.ipv4.fragOffset
408:  	load 0		 // 0: local address of hdr
409:  	const 1		 //1: index of ipv4
410:  	add
411:  	getfield
412:  	const 8		 //8: index of fragOffset
413:  	add
414:  	getfield
// end of hdr.ipv4.fragOffset
// hdr.ipv4.ttl
415:  	load 0		 // 0: local address of hdr
416:  	const 1		 //1: index of ipv4
417:  	add
418:  	getfield
419:  	const 9		 //9: index of ttl
420:  	add
421:  	getfield
// end of hdr.ipv4.ttl
// hdr.ipv4.protocol
422:  	load 0		 // 0: local address of hdr
423:  	const 1		 //1: index of ipv4
424:  	add
425:  	getfield
426:  	const 10		 //10: index of protocol
427:  	add
428:  	getfield
// end of hdr.ipv4.protocol
// hdr.ipv4.srcAddr
429:  	load 0		 // 0: local address of hdr
430:  	const 1		 //1: index of ipv4
431:  	add
432:  	getfield
433:  	const 12		 //12: index of srcAddr
434:  	add
435:  	getfield
// end of hdr.ipv4.srcAddr
// hdr.ipv4.dstAddr
436:  	load 0		 // 0: local address of hdr
437:  	const 1		 //1: index of ipv4
438:  	add
439:  	getfield
440:  	const 13		 //13: index of dstAddr
441:  	add
442:  	getfield
// end of hdr.ipv4.dstAddr
// memcpy(src,dst,length)
443:  	derefTop
444:  	const 11		 //11: size of: list
445:  	sub
446:  	inc
447:  	const 1242		 //global address of verifyChecksum.temp1_LIST_11
448:  	const 11		 //11: size of: list
449:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
450:  	pop
// end of memcpy(src,dst,length)
451:  	popn 11		 //11: size of: list
452:  	const 1242		 //global address of verifyChecksum.temp1_LIST_11
// end of {hdr.ipv4.version,hdr.ipv4.ihl,hdr.ipv4.diffserv,hdr.ipv4.totalLen,hdr.ipv4.identification,hdr.ipv4.flags,hdr.ipv4.fragOffset,hdr.ipv4.ttl,hdr.ipv4.protocol,hdr.ipv4.srcAddr,hdr.ipv4.dstAddr}
// hdr.ipv4.hdrChecksum
453:  	load 0		 // 0: local address of hdr
454:  	const 1		 //1: index of ipv4
455:  	add
456:  	getfield
457:  	const 11		 //11: index of hdrChecksum
458:  	add
459:  	getfield
// end of hdr.ipv4.hdrChecksum
// enum field
460:  	const 1		 //1: enum-type
461:  	const 1253		 //global address of verifyChecksum.temp2_enum-type
462:  	putfield
463:  	const 1253		 //global address of verifyChecksum.temp2_enum-type
// end of field
464:  	invoke 2347 4		 // 2347: label to null::verify_checksum, 4: size of: 
465:  	pop
// end of null::verify_checksum(1, {hdr.ipv4.version,hdr.ipv4.ihl,hdr.ipv4.diffserv,hdr.ipv4.totalLen,hdr.ipv4.identification,hdr.ipv4.flags,hdr.ipv4.fragOffset,hdr.ipv4.ttl,hdr.ipv4.protocol,hdr.ipv4.srcAddr,hdr.ipv4.dstAddr}, hdr.ipv4.hdrChecksum, 1)
466:  	const 0		 //0: verifyChecksum terminates with status OK
467:  	return 
// end of definition of verifyChecksum(headers hdr, metadata meta)
//

// definition of egress(headers hdr, metadata meta, standard_metadata_t standard_metadata)
// start of block
// egress::rewrite_mac(hdr, meta, standard_metadata)
468:  	load 0		 // 0: local address of hdr
469:  	load 1		 // 1: local address of meta
470:  	load 2		 // 2: local address of standard_metadata
471:  	invoke 501 3		 // 501: label to egress::rewrite_mac, 3: size of: 
472:  	pop
// end of egress::rewrite_mac(hdr, meta, standard_metadata)
473:  	const 0		 //0: egress terminates with status OK
474:  	return 
// end of definition of egress(headers hdr, metadata meta, standard_metadata_t standard_metadata)
//

// definition of on_miss(headers hdr, metadata meta, standard_metadata_t standard_metadata)
// start of block
475:  	const 0		 //0: on_miss terminates with status OK
476:  	return 
// end of definition of on_miss(headers hdr, metadata meta, standard_metadata_t standard_metadata)
//

// definition of rewrite_src_dst_mac(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_48 smac, bit_48 dmac)
// start of block
// hdr.ethernet.srcAddr = smac
477:  	load 3		 // 3: local address of smac
// hdr.ethernet.srcAddr
478:  	load 0		 // 0: local address of hdr
479:  	const 0		 //0: index of ethernet
480:  	add
481:  	getfield
482:  	const 2		 //2: index of srcAddr
483:  	add
484:  	getfield
// end of hdr.ethernet.srcAddr
485:  	const 48		 //48: size of: hdr.ethernet.srcAddr
486:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
487:  	pop
// end of hdr.ethernet.srcAddr = smac
// hdr.ethernet.dstAddr = dmac
488:  	load 4		 // 4: local address of dmac
// hdr.ethernet.dstAddr
489:  	load 0		 // 0: local address of hdr
490:  	const 0		 //0: index of ethernet
491:  	add
492:  	getfield
493:  	const 3		 //3: index of dstAddr
494:  	add
495:  	getfield
// end of hdr.ethernet.dstAddr
496:  	const 48		 //48: size of: hdr.ethernet.dstAddr
497:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
498:  	pop
// end of hdr.ethernet.dstAddr = dmac
499:  	const 0		 //0: rewrite_src_dst_mac terminates with status OK
500:  	return 
// end of definition of rewrite_src_dst_mac(headers hdr, metadata meta, standard_metadata_t standard_metadata, bit_48 smac, bit_48 dmac)
//

// definition of rewrite_mac()
// keys
// failure: no entries matched, action performed, leave hit bit at 0
501:  	goto 505		 /* 505: label to last entry failed, go to junction */
// success: switch on the hit bit
502:  	const 1		 //1: new value of hit bit
503:  	const 995		 //global address of rewrite_mac.hit
504:  	putfield
505:  	const 0		 //0: rewrite_mac terminates with status OK
506:  	return 
// end of definition of rewrite_mac()
//

// definition of bd()
// keys
// Match entry 0 of bd
507:  	const 1		 //1: initial true, every key will binary-and it with the results
// Match key meta.ingress_metadata.bd entry 0 of bd
// meta.ingress_metadata.bd
508:  	load 1		 // 1: local address of meta
509:  	const 0		 //0: index of ingress_metadata
510:  	add
511:  	getfield
512:  	const 1		 //1: index of bd
513:  	add
514:  	getfield
// end of meta.ingress_metadata.bd
515:  	const 1089		 //global address of bd.ENTRY[0,0]
516:  	const 16		 //16: size of: bd.ENTRY[0,0]
517:  	invoke 1760 3		 // 1760: label to stdlib::memcmp, 3: size of: src, dst, length
518:  	mul
519:  	not
520:  	ifeq 522		 //522: label to if all memcmp returned 1, i.e. if entry 0 matched, jump to action set_vrf
// failure: no entries matched, action performed, leave hit bit at 0
521:  	goto 532		 /* 532: label to last entry failed, go to junction */
// set_vrf(hdr, meta, standard_metadata, vrf)
522:  	load 0		 // 0: local address of hdr
523:  	load 1		 // 1: local address of meta
524:  	load 2		 // 2: local address of standard_metadata
525:  	const 1019		 //global address of vrf
526:  	invoke 168 4		 // 168: label to ::set_vrf, 4: size of: hdr, meta, standard_metadata, vrf
527:  	pop
528:  	goto 529		 /* 529: label to success case */
// success: switch on the hit bit
529:  	const 1		 //1: new value of hit bit
530:  	const 1088		 //global address of bd.hit
531:  	putfield
532:  	const 0		 //0: bd terminates with status OK
533:  	return 
// end of definition of bd()
//

// definition of ipv4_fib()
// keys
// Match entry 0 of ipv4_fib
534:  	const 1		 //1: initial true, every key will binary-and it with the results
// Match key hdr.ipv4.dstAddr entry 0 of ipv4_fib
// hdr.ipv4.dstAddr
535:  	load 0		 // 0: local address of hdr
536:  	const 1		 //1: index of ipv4
537:  	add
538:  	getfield
539:  	const 13		 //13: index of dstAddr
540:  	add
541:  	getfield
// end of hdr.ipv4.dstAddr
542:  	const 1106		 //global address of ipv4_fib.ENTRY[0,0]
543:  	const 32		 //32: size of: ipv4_fib.ENTRY[0,0]
544:  	invoke 1760 3		 // 1760: label to stdlib::memcmp, 3: size of: src, dst, length
545:  	mul
// Match key meta.ingress_metadata.vrf entry 0 of ipv4_fib
// meta.ingress_metadata.vrf
546:  	load 1		 // 1: local address of meta
547:  	const 0		 //0: index of ingress_metadata
548:  	add
549:  	getfield
550:  	const 0		 //0: index of vrf
551:  	add
552:  	getfield
// end of meta.ingress_metadata.vrf
553:  	const 1138		 //global address of ipv4_fib.ENTRY[0,1]
554:  	const 12		 //12: size of: ipv4_fib.ENTRY[0,1]
555:  	invoke 1760 3		 // 1760: label to stdlib::memcmp, 3: size of: src, dst, length
556:  	mul
557:  	not
558:  	ifeq 560		 //560: label to if all memcmp returned 1, i.e. if entry 0 matched, jump to action fib_hit_nexthop
// failure: no entries matched, action performed, leave hit bit at 0
559:  	goto 570		 /* 570: label to last entry failed, go to junction */
// fib_hit_nexthop(hdr, meta, standard_metadata, nexthop_index)
560:  	load 0		 // 0: local address of hdr
561:  	load 1		 // 1: local address of meta
562:  	load 2		 // 2: local address of standard_metadata
563:  	const 1031		 //global address of nexthop_index
564:  	invoke 183 4		 // 183: label to ::fib_hit_nexthop, 4: size of: hdr, meta, standard_metadata, nexthop_index
565:  	pop
566:  	goto 567		 /* 567: label to success case */
// success: switch on the hit bit
567:  	const 1		 //1: new value of hit bit
568:  	const 1105		 //global address of ipv4_fib.hit
569:  	putfield
570:  	const 0		 //0: ipv4_fib terminates with status OK
571:  	return 
// end of definition of ipv4_fib()
//

// definition of ipv4_fib_lpm()
// keys
// Match entry 0 of ipv4_fib_lpm
572:  	const 1		 //1: initial true, every key will binary-and it with the results
// Match key hdr.ipv4.dstAddr entry 0 of ipv4_fib_lpm
// hdr.ipv4.dstAddr
573:  	load 0		 // 0: local address of hdr
574:  	const 1		 //1: index of ipv4
575:  	add
576:  	getfield
577:  	const 13		 //13: index of dstAddr
578:  	add
579:  	getfield
// end of hdr.ipv4.dstAddr
580:  	const 1151		 //global address of ipv4_fib_lpm.ENTRY[0,0]
581:  	const 32		 //32: size of: ipv4_fib_lpm.ENTRY[0,0]
582:  	invoke 1760 3		 // 1760: label to stdlib::memcmp, 3: size of: src, dst, length
583:  	mul
// Match key meta.ingress_metadata.vrf entry 0 of ipv4_fib_lpm
// meta.ingress_metadata.vrf
584:  	load 1		 // 1: local address of meta
585:  	const 0		 //0: index of ingress_metadata
586:  	add
587:  	getfield
588:  	const 0		 //0: index of vrf
589:  	add
590:  	getfield
// end of meta.ingress_metadata.vrf
591:  	const 1183		 //global address of ipv4_fib_lpm.ENTRY[0,1]
592:  	const 12		 //12: size of: ipv4_fib_lpm.ENTRY[0,1]
593:  	invoke 1760 3		 // 1760: label to stdlib::memcmp, 3: size of: src, dst, length
594:  	mul
595:  	not
596:  	ifeq 598		 //598: label to if all memcmp returned 1, i.e. if entry 0 matched, jump to action fib_hit_nexthop
// failure: no entries matched, action performed, leave hit bit at 0
597:  	goto 608		 /* 608: label to last entry failed, go to junction */
// fib_hit_nexthop(hdr, meta, standard_metadata, nexthop_index)
598:  	load 0		 // 0: local address of hdr
599:  	load 1		 // 1: local address of meta
600:  	load 2		 // 2: local address of standard_metadata
601:  	const 1031		 //global address of nexthop_index
602:  	invoke 183 4		 // 183: label to ::fib_hit_nexthop, 4: size of: hdr, meta, standard_metadata, nexthop_index
603:  	pop
604:  	goto 605		 /* 605: label to success case */
// success: switch on the hit bit
605:  	const 1		 //1: new value of hit bit
606:  	const 1150		 //global address of ipv4_fib_lpm.hit
607:  	putfield
608:  	const 0		 //0: ipv4_fib_lpm terminates with status OK
609:  	return 
// end of definition of ipv4_fib_lpm()
//

// definition of nexthop()
// keys
// Match entry 0 of nexthop
610:  	const 1		 //1: initial true, every key will binary-and it with the results
// Match key meta.ingress_metadata.nexthop_index entry 0 of nexthop
// meta.ingress_metadata.nexthop_index
611:  	load 1		 // 1: local address of meta
612:  	const 0		 //0: index of ingress_metadata
613:  	add
614:  	getfield
615:  	const 2		 //2: index of nexthop_index
616:  	add
617:  	getfield
// end of meta.ingress_metadata.nexthop_index
618:  	const 1196		 //global address of nexthop.ENTRY[0,0]
619:  	const 16		 //16: size of: nexthop.ENTRY[0,0]
620:  	invoke 1760 3		 // 1760: label to stdlib::memcmp, 3: size of: src, dst, length
621:  	mul
622:  	not
623:  	ifeq 625		 //625: label to if all memcmp returned 1, i.e. if entry 0 matched, jump to action set_egress_details
// failure: no entries matched, action performed, leave hit bit at 0
624:  	goto 635		 /* 635: label to last entry failed, go to junction */
// set_egress_details(hdr, meta, standard_metadata, egress_spec)
625:  	load 0		 // 0: local address of hdr
626:  	load 1		 // 1: local address of meta
627:  	load 2		 // 2: local address of standard_metadata
628:  	const 1063		 //global address of egress_spec
629:  	invoke 224 4		 // 224: label to ::set_egress_details, 4: size of: hdr, meta, standard_metadata, egress_spec
630:  	pop
631:  	goto 632		 /* 632: label to success case */
// success: switch on the hit bit
632:  	const 1		 //1: new value of hit bit
633:  	const 1195		 //global address of nexthop.hit
634:  	putfield
635:  	const 0		 //0: nexthop terminates with status OK
636:  	return 
// end of definition of nexthop()
//

// definition of port_mapping()
// keys
// Match entry 0 of port_mapping
637:  	const 1		 //1: initial true, every key will binary-and it with the results
// Match key standard_metadata.ingress_port entry 0 of port_mapping
// standard_metadata.ingress_port
638:  	load 2		 // 2: local address of standard_metadata
639:  	const 0		 //0: index of ingress_port
640:  	add
641:  	getfield
// end of standard_metadata.ingress_port
642:  	const 1213		 //global address of port_mapping.ENTRY[0,0]
643:  	const 9		 //9: size of: port_mapping.ENTRY[0,0]
644:  	invoke 1760 3		 // 1760: label to stdlib::memcmp, 3: size of: src, dst, length
645:  	mul
646:  	not
647:  	ifeq 649		 //649: label to if all memcmp returned 1, i.e. if entry 0 matched, jump to action set_bd
// failure: no entries matched, action performed, leave hit bit at 0
648:  	goto 659		 /* 659: label to last entry failed, go to junction */
// set_bd(hdr, meta, standard_metadata, bd)
649:  	load 0		 // 0: local address of hdr
650:  	load 1		 // 1: local address of meta
651:  	load 2		 // 2: local address of standard_metadata
652:  	const 1072		 //global address of bd
653:  	invoke 226 4		 // 226: label to ::set_bd, 4: size of: hdr, meta, standard_metadata, bd
654:  	pop
655:  	goto 656		 /* 656: label to success case */
// success: switch on the hit bit
656:  	const 1		 //1: new value of hit bit
657:  	const 1212		 //global address of port_mapping.hit
658:  	putfield
659:  	const 0		 //0: port_mapping terminates with status OK
660:  	return 
// end of definition of port_mapping()
//

// definition of receive_packet()
661:  	{ 33/100 -> goto 662		 /* 662: label to receive packet Packet [headers=[Header [name=ethernet, schema={src=48, dst=48, etherType=16}, data={src=000000000000000000000000000000000000000000000000, dst=000000000000000000000000000000000000000000000000, etherType=0000100000000000}], Header [name=ipv4, schema={version=4, ihl=4, diffserv=8, totalLen=16, identification=16, flags=3, fragOffset=13, ttl=8, protocol=8, hdrChecksum=16, srcAddr=32, dstAddr=32}, data={version=0000, ihl=0000, diffserv=00000000, totalLen=0000000000000000, identification=0000000000000000, flags=000, fragOffset=0000000000000, ttl=00000000, protocol=00000000, hdrChecksum=0000000000000000, srcAddr=00000000000000000000000000000000, dstAddr=00000000000000000000000000000000}]]] */, 17/50 -> goto 1055		 /* 1055: label to receive packet Packet [headers=[Header [name=ethernet, schema={src=48, dst=48, etherType=16}, data={src=000000000000000000000000000000000000000000000000, dst=000000000000000000000000000000000000000000000000, etherType=0000100000000000}], Header [name=ipv4, schema={version=4, ihl=4, diffserv=8, totalLen=16, identification=16, flags=3, fragOffset=13, ttl=8, protocol=8, hdrChecksum=16, srcAddr=32, dstAddr=32}, data={version=0000, ihl=0000, diffserv=00000000, totalLen=0000000000000000, identification=0000000000000000, flags=000, fragOffset=0000000000000, ttl=00000000, protocol=00000000, hdrChecksum=0000000000000000, srcAddr=00000000000000000000000000000000, dstAddr=11111111111111111111111111111111}]]] */, 33/100 -> goto 1448		 /* 1448: label to receive packet Packet [headers=[Header [name=ethernet, schema={src=48, dst=48, etherType=16}, data={src=000000000000000000000000000000000000000000000000, dst=000000000000000000000000000000000000000000000000, etherType=0000000000000000}]]] */ }
// start of header ethernet
// start of field ethernet.src (48)
662:  	const 0		 //0: 0th bit
663:  	const 0		 //0: 1th bit
664:  	const 0		 //0: 2th bit
665:  	const 0		 //0: 3th bit
666:  	const 0		 //0: 4th bit
667:  	const 0		 //0: 5th bit
668:  	const 0		 //0: 6th bit
669:  	const 0		 //0: 7th bit
670:  	const 0		 //0: 8th bit
671:  	const 0		 //0: 9th bit
672:  	const 0		 //0: 10th bit
673:  	const 0		 //0: 11th bit
674:  	const 0		 //0: 12th bit
675:  	const 0		 //0: 13th bit
676:  	const 0		 //0: 14th bit
677:  	const 0		 //0: 15th bit
678:  	const 0		 //0: 16th bit
679:  	const 0		 //0: 17th bit
680:  	const 0		 //0: 18th bit
681:  	const 0		 //0: 19th bit
682:  	const 0		 //0: 20th bit
683:  	const 0		 //0: 21th bit
684:  	const 0		 //0: 22th bit
685:  	const 0		 //0: 23th bit
686:  	const 0		 //0: 24th bit
687:  	const 0		 //0: 25th bit
688:  	const 0		 //0: 26th bit
689:  	const 0		 //0: 27th bit
690:  	const 0		 //0: 28th bit
691:  	const 0		 //0: 29th bit
692:  	const 0		 //0: 30th bit
693:  	const 0		 //0: 31th bit
694:  	const 0		 //0: 32th bit
695:  	const 0		 //0: 33th bit
696:  	const 0		 //0: 34th bit
697:  	const 0		 //0: 35th bit
698:  	const 0		 //0: 36th bit
699:  	const 0		 //0: 37th bit
700:  	const 0		 //0: 38th bit
701:  	const 0		 //0: 39th bit
702:  	const 0		 //0: 40th bit
703:  	const 0		 //0: 41th bit
704:  	const 0		 //0: 42th bit
705:  	const 0		 //0: 43th bit
706:  	const 0		 //0: 44th bit
707:  	const 0		 //0: 45th bit
708:  	const 0		 //0: 46th bit
709:  	const 0		 //0: 47th bit
710:  	derefTop
711:  	const 47		 //47: size of: ethernet.src - 1
712:  	sub
713:  	const 1		 //global address of target addr of field ethernet.src in packet_in
714:  	const 48		 //48: size of: ethernet.src
715:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
716:  	pop
717:  	popn 48		 //48: size of: ethernet.src
// end of field ethernet.src (48)
// start of field ethernet.dst (48)
718:  	const 0		 //0: 0th bit
719:  	const 0		 //0: 1th bit
720:  	const 0		 //0: 2th bit
721:  	const 0		 //0: 3th bit
722:  	const 0		 //0: 4th bit
723:  	const 0		 //0: 5th bit
724:  	const 0		 //0: 6th bit
725:  	const 0		 //0: 7th bit
726:  	const 0		 //0: 8th bit
727:  	const 0		 //0: 9th bit
728:  	const 0		 //0: 10th bit
729:  	const 0		 //0: 11th bit
730:  	const 0		 //0: 12th bit
731:  	const 0		 //0: 13th bit
732:  	const 0		 //0: 14th bit
733:  	const 0		 //0: 15th bit
734:  	const 0		 //0: 16th bit
735:  	const 0		 //0: 17th bit
736:  	const 0		 //0: 18th bit
737:  	const 0		 //0: 19th bit
738:  	const 0		 //0: 20th bit
739:  	const 0		 //0: 21th bit
740:  	const 0		 //0: 22th bit
741:  	const 0		 //0: 23th bit
742:  	const 0		 //0: 24th bit
743:  	const 0		 //0: 25th bit
744:  	const 0		 //0: 26th bit
745:  	const 0		 //0: 27th bit
746:  	const 0		 //0: 28th bit
747:  	const 0		 //0: 29th bit
748:  	const 0		 //0: 30th bit
749:  	const 0		 //0: 31th bit
750:  	const 0		 //0: 32th bit
751:  	const 0		 //0: 33th bit
752:  	const 0		 //0: 34th bit
753:  	const 0		 //0: 35th bit
754:  	const 0		 //0: 36th bit
755:  	const 0		 //0: 37th bit
756:  	const 0		 //0: 38th bit
757:  	const 0		 //0: 39th bit
758:  	const 0		 //0: 40th bit
759:  	const 0		 //0: 41th bit
760:  	const 0		 //0: 42th bit
761:  	const 0		 //0: 43th bit
762:  	const 0		 //0: 44th bit
763:  	const 0		 //0: 45th bit
764:  	const 0		 //0: 46th bit
765:  	const 0		 //0: 47th bit
766:  	derefTop
767:  	const 47		 //47: size of: ethernet.dst - 1
768:  	sub
769:  	const 49		 //global address of target addr of field ethernet.dst in packet_in
770:  	const 48		 //48: size of: ethernet.dst
771:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
772:  	pop
773:  	popn 48		 //48: size of: ethernet.dst
// end of field ethernet.dst (48)
// start of field ethernet.etherType (16)
774:  	const 0		 //0: 0th bit
775:  	const 0		 //0: 1th bit
776:  	const 0		 //0: 2th bit
777:  	const 0		 //0: 3th bit
778:  	const 1		 //1: 4th bit
779:  	const 0		 //0: 5th bit
780:  	const 0		 //0: 6th bit
781:  	const 0		 //0: 7th bit
782:  	const 0		 //0: 8th bit
783:  	const 0		 //0: 9th bit
784:  	const 0		 //0: 10th bit
785:  	const 0		 //0: 11th bit
786:  	const 0		 //0: 12th bit
787:  	const 0		 //0: 13th bit
788:  	const 0		 //0: 14th bit
789:  	const 0		 //0: 15th bit
790:  	derefTop
791:  	const 15		 //15: size of: ethernet.etherType - 1
792:  	sub
793:  	const 97		 //global address of target addr of field ethernet.etherType in packet_in
794:  	const 16		 //16: size of: ethernet.etherType
795:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
796:  	pop
797:  	popn 16		 //16: size of: ethernet.etherType
// end of field ethernet.etherType (16)
// end of header ethernet
// start of header ipv4
// start of field ipv4.version (4)
798:  	const 0		 //0: 0th bit
799:  	const 0		 //0: 1th bit
800:  	const 0		 //0: 2th bit
801:  	const 0		 //0: 3th bit
802:  	derefTop
803:  	const 3		 //3: size of: ipv4.version - 1
804:  	sub
805:  	const 113		 //global address of target addr of field ipv4.version in packet_in
806:  	const 4		 //4: size of: ipv4.version
807:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
808:  	pop
809:  	popn 4		 //4: size of: ipv4.version
// end of field ipv4.version (4)
// start of field ipv4.ihl (4)
810:  	const 0		 //0: 0th bit
811:  	const 0		 //0: 1th bit
812:  	const 0		 //0: 2th bit
813:  	const 0		 //0: 3th bit
814:  	derefTop
815:  	const 3		 //3: size of: ipv4.ihl - 1
816:  	sub
817:  	const 117		 //global address of target addr of field ipv4.ihl in packet_in
818:  	const 4		 //4: size of: ipv4.ihl
819:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
820:  	pop
821:  	popn 4		 //4: size of: ipv4.ihl
// end of field ipv4.ihl (4)
// start of field ipv4.diffserv (8)
822:  	const 0		 //0: 0th bit
823:  	const 0		 //0: 1th bit
824:  	const 0		 //0: 2th bit
825:  	const 0		 //0: 3th bit
826:  	const 0		 //0: 4th bit
827:  	const 0		 //0: 5th bit
828:  	const 0		 //0: 6th bit
829:  	const 0		 //0: 7th bit
830:  	derefTop
831:  	const 7		 //7: size of: ipv4.diffserv - 1
832:  	sub
833:  	const 121		 //global address of target addr of field ipv4.diffserv in packet_in
834:  	const 8		 //8: size of: ipv4.diffserv
835:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
836:  	pop
837:  	popn 8		 //8: size of: ipv4.diffserv
// end of field ipv4.diffserv (8)
// start of field ipv4.totalLen (16)
838:  	const 0		 //0: 0th bit
839:  	const 0		 //0: 1th bit
840:  	const 0		 //0: 2th bit
841:  	const 0		 //0: 3th bit
842:  	const 0		 //0: 4th bit
843:  	const 0		 //0: 5th bit
844:  	const 0		 //0: 6th bit
845:  	const 0		 //0: 7th bit
846:  	const 0		 //0: 8th bit
847:  	const 0		 //0: 9th bit
848:  	const 0		 //0: 10th bit
849:  	const 0		 //0: 11th bit
850:  	const 0		 //0: 12th bit
851:  	const 0		 //0: 13th bit
852:  	const 0		 //0: 14th bit
853:  	const 0		 //0: 15th bit
854:  	derefTop
855:  	const 15		 //15: size of: ipv4.totalLen - 1
856:  	sub
857:  	const 129		 //global address of target addr of field ipv4.totalLen in packet_in
858:  	const 16		 //16: size of: ipv4.totalLen
859:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
860:  	pop
861:  	popn 16		 //16: size of: ipv4.totalLen
// end of field ipv4.totalLen (16)
// start of field ipv4.identification (16)
862:  	const 0		 //0: 0th bit
863:  	const 0		 //0: 1th bit
864:  	const 0		 //0: 2th bit
865:  	const 0		 //0: 3th bit
866:  	const 0		 //0: 4th bit
867:  	const 0		 //0: 5th bit
868:  	const 0		 //0: 6th bit
869:  	const 0		 //0: 7th bit
870:  	const 0		 //0: 8th bit
871:  	const 0		 //0: 9th bit
872:  	const 0		 //0: 10th bit
873:  	const 0		 //0: 11th bit
874:  	const 0		 //0: 12th bit
875:  	const 0		 //0: 13th bit
876:  	const 0		 //0: 14th bit
877:  	const 0		 //0: 15th bit
878:  	derefTop
879:  	const 15		 //15: size of: ipv4.identification - 1
880:  	sub
881:  	const 145		 //global address of target addr of field ipv4.identification in packet_in
882:  	const 16		 //16: size of: ipv4.identification
883:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
884:  	pop
885:  	popn 16		 //16: size of: ipv4.identification
// end of field ipv4.identification (16)
// start of field ipv4.flags (3)
886:  	const 0		 //0: 0th bit
887:  	const 0		 //0: 1th bit
888:  	const 0		 //0: 2th bit
889:  	derefTop
890:  	const 2		 //2: size of: ipv4.flags - 1
891:  	sub
892:  	const 161		 //global address of target addr of field ipv4.flags in packet_in
893:  	const 3		 //3: size of: ipv4.flags
894:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
895:  	pop
896:  	popn 3		 //3: size of: ipv4.flags
// end of field ipv4.flags (3)
// start of field ipv4.fragOffset (13)
897:  	const 0		 //0: 0th bit
898:  	const 0		 //0: 1th bit
899:  	const 0		 //0: 2th bit
900:  	const 0		 //0: 3th bit
901:  	const 0		 //0: 4th bit
902:  	const 0		 //0: 5th bit
903:  	const 0		 //0: 6th bit
904:  	const 0		 //0: 7th bit
905:  	const 0		 //0: 8th bit
906:  	const 0		 //0: 9th bit
907:  	const 0		 //0: 10th bit
908:  	const 0		 //0: 11th bit
909:  	const 0		 //0: 12th bit
910:  	derefTop
911:  	const 12		 //12: size of: ipv4.fragOffset - 1
912:  	sub
913:  	const 164		 //global address of target addr of field ipv4.fragOffset in packet_in
914:  	const 13		 //13: size of: ipv4.fragOffset
915:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
916:  	pop
917:  	popn 13		 //13: size of: ipv4.fragOffset
// end of field ipv4.fragOffset (13)
// start of field ipv4.ttl (8)
918:  	const 0		 //0: 0th bit
919:  	const 0		 //0: 1th bit
920:  	const 0		 //0: 2th bit
921:  	const 0		 //0: 3th bit
922:  	const 0		 //0: 4th bit
923:  	const 0		 //0: 5th bit
924:  	const 0		 //0: 6th bit
925:  	const 0		 //0: 7th bit
926:  	derefTop
927:  	const 7		 //7: size of: ipv4.ttl - 1
928:  	sub
929:  	const 177		 //global address of target addr of field ipv4.ttl in packet_in
930:  	const 8		 //8: size of: ipv4.ttl
931:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
932:  	pop
933:  	popn 8		 //8: size of: ipv4.ttl
// end of field ipv4.ttl (8)
// start of field ipv4.protocol (8)
934:  	const 0		 //0: 0th bit
935:  	const 0		 //0: 1th bit
936:  	const 0		 //0: 2th bit
937:  	const 0		 //0: 3th bit
938:  	const 0		 //0: 4th bit
939:  	const 0		 //0: 5th bit
940:  	const 0		 //0: 6th bit
941:  	const 0		 //0: 7th bit
942:  	derefTop
943:  	const 7		 //7: size of: ipv4.protocol - 1
944:  	sub
945:  	const 185		 //global address of target addr of field ipv4.protocol in packet_in
946:  	const 8		 //8: size of: ipv4.protocol
947:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
948:  	pop
949:  	popn 8		 //8: size of: ipv4.protocol
// end of field ipv4.protocol (8)
// start of field ipv4.hdrChecksum (16)
950:  	const 0		 //0: 0th bit
951:  	const 0		 //0: 1th bit
952:  	const 0		 //0: 2th bit
953:  	const 0		 //0: 3th bit
954:  	const 0		 //0: 4th bit
955:  	const 0		 //0: 5th bit
956:  	const 0		 //0: 6th bit
957:  	const 0		 //0: 7th bit
958:  	const 0		 //0: 8th bit
959:  	const 0		 //0: 9th bit
960:  	const 0		 //0: 10th bit
961:  	const 0		 //0: 11th bit
962:  	const 0		 //0: 12th bit
963:  	const 0		 //0: 13th bit
964:  	const 0		 //0: 14th bit
965:  	const 0		 //0: 15th bit
966:  	derefTop
967:  	const 15		 //15: size of: ipv4.hdrChecksum - 1
968:  	sub
969:  	const 193		 //global address of target addr of field ipv4.hdrChecksum in packet_in
970:  	const 16		 //16: size of: ipv4.hdrChecksum
971:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
972:  	pop
973:  	popn 16		 //16: size of: ipv4.hdrChecksum
// end of field ipv4.hdrChecksum (16)
// start of field ipv4.srcAddr (32)
974:  	const 0		 //0: 0th bit
975:  	const 0		 //0: 1th bit
976:  	const 0		 //0: 2th bit
977:  	const 0		 //0: 3th bit
978:  	const 0		 //0: 4th bit
979:  	const 0		 //0: 5th bit
980:  	const 0		 //0: 6th bit
981:  	const 0		 //0: 7th bit
982:  	const 0		 //0: 8th bit
983:  	const 0		 //0: 9th bit
984:  	const 0		 //0: 10th bit
985:  	const 0		 //0: 11th bit
986:  	const 0		 //0: 12th bit
987:  	const 0		 //0: 13th bit
988:  	const 0		 //0: 14th bit
989:  	const 0		 //0: 15th bit
990:  	const 0		 //0: 16th bit
991:  	const 0		 //0: 17th bit
992:  	const 0		 //0: 18th bit
993:  	const 0		 //0: 19th bit
994:  	const 0		 //0: 20th bit
995:  	const 0		 //0: 21th bit
996:  	const 0		 //0: 22th bit
997:  	const 0		 //0: 23th bit
998:  	const 0		 //0: 24th bit
999:  	const 0		 //0: 25th bit
1000:  	const 0		 //0: 26th bit
1001:  	const 0		 //0: 27th bit
1002:  	const 0		 //0: 28th bit
1003:  	const 0		 //0: 29th bit
1004:  	const 0		 //0: 30th bit
1005:  	const 0		 //0: 31th bit
1006:  	derefTop
1007:  	const 31		 //31: size of: ipv4.srcAddr - 1
1008:  	sub
1009:  	const 209		 //global address of target addr of field ipv4.srcAddr in packet_in
1010:  	const 32		 //32: size of: ipv4.srcAddr
1011:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1012:  	pop
1013:  	popn 32		 //32: size of: ipv4.srcAddr
// end of field ipv4.srcAddr (32)
// start of field ipv4.dstAddr (32)
1014:  	const 0		 //0: 0th bit
1015:  	const 0		 //0: 1th bit
1016:  	const 0		 //0: 2th bit
1017:  	const 0		 //0: 3th bit
1018:  	const 0		 //0: 4th bit
1019:  	const 0		 //0: 5th bit
1020:  	const 0		 //0: 6th bit
1021:  	const 0		 //0: 7th bit
1022:  	const 0		 //0: 8th bit
1023:  	const 0		 //0: 9th bit
1024:  	const 0		 //0: 10th bit
1025:  	const 0		 //0: 11th bit
1026:  	const 0		 //0: 12th bit
1027:  	const 0		 //0: 13th bit
1028:  	const 0		 //0: 14th bit
1029:  	const 0		 //0: 15th bit
1030:  	const 0		 //0: 16th bit
1031:  	const 0		 //0: 17th bit
1032:  	const 0		 //0: 18th bit
1033:  	const 0		 //0: 19th bit
1034:  	const 0		 //0: 20th bit
1035:  	const 0		 //0: 21th bit
1036:  	const 0		 //0: 22th bit
1037:  	const 0		 //0: 23th bit
1038:  	const 0		 //0: 24th bit
1039:  	const 0		 //0: 25th bit
1040:  	const 0		 //0: 26th bit
1041:  	const 0		 //0: 27th bit
1042:  	const 0		 //0: 28th bit
1043:  	const 0		 //0: 29th bit
1044:  	const 0		 //0: 30th bit
1045:  	const 0		 //0: 31th bit
1046:  	derefTop
1047:  	const 31		 //31: size of: ipv4.dstAddr - 1
1048:  	sub
1049:  	const 241		 //global address of target addr of field ipv4.dstAddr in packet_in
1050:  	const 32		 //32: size of: ipv4.dstAddr
1051:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1052:  	pop
1053:  	popn 32		 //32: size of: ipv4.dstAddr
// end of field ipv4.dstAddr (32)
// end of header ipv4
1054:  	goto 1585		 /* 1585: label to packet received, go to junction */
// start of header ethernet
// start of field ethernet.src (48)
1055:  	const 0		 //0: 0th bit
1056:  	const 0		 //0: 1th bit
1057:  	const 0		 //0: 2th bit
1058:  	const 0		 //0: 3th bit
1059:  	const 0		 //0: 4th bit
1060:  	const 0		 //0: 5th bit
1061:  	const 0		 //0: 6th bit
1062:  	const 0		 //0: 7th bit
1063:  	const 0		 //0: 8th bit
1064:  	const 0		 //0: 9th bit
1065:  	const 0		 //0: 10th bit
1066:  	const 0		 //0: 11th bit
1067:  	const 0		 //0: 12th bit
1068:  	const 0		 //0: 13th bit
1069:  	const 0		 //0: 14th bit
1070:  	const 0		 //0: 15th bit
1071:  	const 0		 //0: 16th bit
1072:  	const 0		 //0: 17th bit
1073:  	const 0		 //0: 18th bit
1074:  	const 0		 //0: 19th bit
1075:  	const 0		 //0: 20th bit
1076:  	const 0		 //0: 21th bit
1077:  	const 0		 //0: 22th bit
1078:  	const 0		 //0: 23th bit
1079:  	const 0		 //0: 24th bit
1080:  	const 0		 //0: 25th bit
1081:  	const 0		 //0: 26th bit
1082:  	const 0		 //0: 27th bit
1083:  	const 0		 //0: 28th bit
1084:  	const 0		 //0: 29th bit
1085:  	const 0		 //0: 30th bit
1086:  	const 0		 //0: 31th bit
1087:  	const 0		 //0: 32th bit
1088:  	const 0		 //0: 33th bit
1089:  	const 0		 //0: 34th bit
1090:  	const 0		 //0: 35th bit
1091:  	const 0		 //0: 36th bit
1092:  	const 0		 //0: 37th bit
1093:  	const 0		 //0: 38th bit
1094:  	const 0		 //0: 39th bit
1095:  	const 0		 //0: 40th bit
1096:  	const 0		 //0: 41th bit
1097:  	const 0		 //0: 42th bit
1098:  	const 0		 //0: 43th bit
1099:  	const 0		 //0: 44th bit
1100:  	const 0		 //0: 45th bit
1101:  	const 0		 //0: 46th bit
1102:  	const 0		 //0: 47th bit
1103:  	derefTop
1104:  	const 47		 //47: size of: ethernet.src - 1
1105:  	sub
1106:  	const 1		 //global address of target addr of field ethernet.src in packet_in
1107:  	const 48		 //48: size of: ethernet.src
1108:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1109:  	pop
1110:  	popn 48		 //48: size of: ethernet.src
// end of field ethernet.src (48)
// start of field ethernet.dst (48)
1111:  	const 0		 //0: 0th bit
1112:  	const 0		 //0: 1th bit
1113:  	const 0		 //0: 2th bit
1114:  	const 0		 //0: 3th bit
1115:  	const 0		 //0: 4th bit
1116:  	const 0		 //0: 5th bit
1117:  	const 0		 //0: 6th bit
1118:  	const 0		 //0: 7th bit
1119:  	const 0		 //0: 8th bit
1120:  	const 0		 //0: 9th bit
1121:  	const 0		 //0: 10th bit
1122:  	const 0		 //0: 11th bit
1123:  	const 0		 //0: 12th bit
1124:  	const 0		 //0: 13th bit
1125:  	const 0		 //0: 14th bit
1126:  	const 0		 //0: 15th bit
1127:  	const 0		 //0: 16th bit
1128:  	const 0		 //0: 17th bit
1129:  	const 0		 //0: 18th bit
1130:  	const 0		 //0: 19th bit
1131:  	const 0		 //0: 20th bit
1132:  	const 0		 //0: 21th bit
1133:  	const 0		 //0: 22th bit
1134:  	const 0		 //0: 23th bit
1135:  	const 0		 //0: 24th bit
1136:  	const 0		 //0: 25th bit
1137:  	const 0		 //0: 26th bit
1138:  	const 0		 //0: 27th bit
1139:  	const 0		 //0: 28th bit
1140:  	const 0		 //0: 29th bit
1141:  	const 0		 //0: 30th bit
1142:  	const 0		 //0: 31th bit
1143:  	const 0		 //0: 32th bit
1144:  	const 0		 //0: 33th bit
1145:  	const 0		 //0: 34th bit
1146:  	const 0		 //0: 35th bit
1147:  	const 0		 //0: 36th bit
1148:  	const 0		 //0: 37th bit
1149:  	const 0		 //0: 38th bit
1150:  	const 0		 //0: 39th bit
1151:  	const 0		 //0: 40th bit
1152:  	const 0		 //0: 41th bit
1153:  	const 0		 //0: 42th bit
1154:  	const 0		 //0: 43th bit
1155:  	const 0		 //0: 44th bit
1156:  	const 0		 //0: 45th bit
1157:  	const 0		 //0: 46th bit
1158:  	const 0		 //0: 47th bit
1159:  	derefTop
1160:  	const 47		 //47: size of: ethernet.dst - 1
1161:  	sub
1162:  	const 49		 //global address of target addr of field ethernet.dst in packet_in
1163:  	const 48		 //48: size of: ethernet.dst
1164:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1165:  	pop
1166:  	popn 48		 //48: size of: ethernet.dst
// end of field ethernet.dst (48)
// start of field ethernet.etherType (16)
1167:  	const 0		 //0: 0th bit
1168:  	const 0		 //0: 1th bit
1169:  	const 0		 //0: 2th bit
1170:  	const 0		 //0: 3th bit
1171:  	const 1		 //1: 4th bit
1172:  	const 0		 //0: 5th bit
1173:  	const 0		 //0: 6th bit
1174:  	const 0		 //0: 7th bit
1175:  	const 0		 //0: 8th bit
1176:  	const 0		 //0: 9th bit
1177:  	const 0		 //0: 10th bit
1178:  	const 0		 //0: 11th bit
1179:  	const 0		 //0: 12th bit
1180:  	const 0		 //0: 13th bit
1181:  	const 0		 //0: 14th bit
1182:  	const 0		 //0: 15th bit
1183:  	derefTop
1184:  	const 15		 //15: size of: ethernet.etherType - 1
1185:  	sub
1186:  	const 97		 //global address of target addr of field ethernet.etherType in packet_in
1187:  	const 16		 //16: size of: ethernet.etherType
1188:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1189:  	pop
1190:  	popn 16		 //16: size of: ethernet.etherType
// end of field ethernet.etherType (16)
// end of header ethernet
// start of header ipv4
// start of field ipv4.version (4)
1191:  	const 0		 //0: 0th bit
1192:  	const 0		 //0: 1th bit
1193:  	const 0		 //0: 2th bit
1194:  	const 0		 //0: 3th bit
1195:  	derefTop
1196:  	const 3		 //3: size of: ipv4.version - 1
1197:  	sub
1198:  	const 113		 //global address of target addr of field ipv4.version in packet_in
1199:  	const 4		 //4: size of: ipv4.version
1200:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1201:  	pop
1202:  	popn 4		 //4: size of: ipv4.version
// end of field ipv4.version (4)
// start of field ipv4.ihl (4)
1203:  	const 0		 //0: 0th bit
1204:  	const 0		 //0: 1th bit
1205:  	const 0		 //0: 2th bit
1206:  	const 0		 //0: 3th bit
1207:  	derefTop
1208:  	const 3		 //3: size of: ipv4.ihl - 1
1209:  	sub
1210:  	const 117		 //global address of target addr of field ipv4.ihl in packet_in
1211:  	const 4		 //4: size of: ipv4.ihl
1212:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1213:  	pop
1214:  	popn 4		 //4: size of: ipv4.ihl
// end of field ipv4.ihl (4)
// start of field ipv4.diffserv (8)
1215:  	const 0		 //0: 0th bit
1216:  	const 0		 //0: 1th bit
1217:  	const 0		 //0: 2th bit
1218:  	const 0		 //0: 3th bit
1219:  	const 0		 //0: 4th bit
1220:  	const 0		 //0: 5th bit
1221:  	const 0		 //0: 6th bit
1222:  	const 0		 //0: 7th bit
1223:  	derefTop
1224:  	const 7		 //7: size of: ipv4.diffserv - 1
1225:  	sub
1226:  	const 121		 //global address of target addr of field ipv4.diffserv in packet_in
1227:  	const 8		 //8: size of: ipv4.diffserv
1228:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1229:  	pop
1230:  	popn 8		 //8: size of: ipv4.diffserv
// end of field ipv4.diffserv (8)
// start of field ipv4.totalLen (16)
1231:  	const 0		 //0: 0th bit
1232:  	const 0		 //0: 1th bit
1233:  	const 0		 //0: 2th bit
1234:  	const 0		 //0: 3th bit
1235:  	const 0		 //0: 4th bit
1236:  	const 0		 //0: 5th bit
1237:  	const 0		 //0: 6th bit
1238:  	const 0		 //0: 7th bit
1239:  	const 0		 //0: 8th bit
1240:  	const 0		 //0: 9th bit
1241:  	const 0		 //0: 10th bit
1242:  	const 0		 //0: 11th bit
1243:  	const 0		 //0: 12th bit
1244:  	const 0		 //0: 13th bit
1245:  	const 0		 //0: 14th bit
1246:  	const 0		 //0: 15th bit
1247:  	derefTop
1248:  	const 15		 //15: size of: ipv4.totalLen - 1
1249:  	sub
1250:  	const 129		 //global address of target addr of field ipv4.totalLen in packet_in
1251:  	const 16		 //16: size of: ipv4.totalLen
1252:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1253:  	pop
1254:  	popn 16		 //16: size of: ipv4.totalLen
// end of field ipv4.totalLen (16)
// start of field ipv4.identification (16)
1255:  	const 0		 //0: 0th bit
1256:  	const 0		 //0: 1th bit
1257:  	const 0		 //0: 2th bit
1258:  	const 0		 //0: 3th bit
1259:  	const 0		 //0: 4th bit
1260:  	const 0		 //0: 5th bit
1261:  	const 0		 //0: 6th bit
1262:  	const 0		 //0: 7th bit
1263:  	const 0		 //0: 8th bit
1264:  	const 0		 //0: 9th bit
1265:  	const 0		 //0: 10th bit
1266:  	const 0		 //0: 11th bit
1267:  	const 0		 //0: 12th bit
1268:  	const 0		 //0: 13th bit
1269:  	const 0		 //0: 14th bit
1270:  	const 0		 //0: 15th bit
1271:  	derefTop
1272:  	const 15		 //15: size of: ipv4.identification - 1
1273:  	sub
1274:  	const 145		 //global address of target addr of field ipv4.identification in packet_in
1275:  	const 16		 //16: size of: ipv4.identification
1276:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1277:  	pop
1278:  	popn 16		 //16: size of: ipv4.identification
// end of field ipv4.identification (16)
// start of field ipv4.flags (3)
1279:  	const 0		 //0: 0th bit
1280:  	const 0		 //0: 1th bit
1281:  	const 0		 //0: 2th bit
1282:  	derefTop
1283:  	const 2		 //2: size of: ipv4.flags - 1
1284:  	sub
1285:  	const 161		 //global address of target addr of field ipv4.flags in packet_in
1286:  	const 3		 //3: size of: ipv4.flags
1287:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1288:  	pop
1289:  	popn 3		 //3: size of: ipv4.flags
// end of field ipv4.flags (3)
// start of field ipv4.fragOffset (13)
1290:  	const 0		 //0: 0th bit
1291:  	const 0		 //0: 1th bit
1292:  	const 0		 //0: 2th bit
1293:  	const 0		 //0: 3th bit
1294:  	const 0		 //0: 4th bit
1295:  	const 0		 //0: 5th bit
1296:  	const 0		 //0: 6th bit
1297:  	const 0		 //0: 7th bit
1298:  	const 0		 //0: 8th bit
1299:  	const 0		 //0: 9th bit
1300:  	const 0		 //0: 10th bit
1301:  	const 0		 //0: 11th bit
1302:  	const 0		 //0: 12th bit
1303:  	derefTop
1304:  	const 12		 //12: size of: ipv4.fragOffset - 1
1305:  	sub
1306:  	const 164		 //global address of target addr of field ipv4.fragOffset in packet_in
1307:  	const 13		 //13: size of: ipv4.fragOffset
1308:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1309:  	pop
1310:  	popn 13		 //13: size of: ipv4.fragOffset
// end of field ipv4.fragOffset (13)
// start of field ipv4.ttl (8)
1311:  	const 0		 //0: 0th bit
1312:  	const 0		 //0: 1th bit
1313:  	const 0		 //0: 2th bit
1314:  	const 0		 //0: 3th bit
1315:  	const 0		 //0: 4th bit
1316:  	const 0		 //0: 5th bit
1317:  	const 0		 //0: 6th bit
1318:  	const 0		 //0: 7th bit
1319:  	derefTop
1320:  	const 7		 //7: size of: ipv4.ttl - 1
1321:  	sub
1322:  	const 177		 //global address of target addr of field ipv4.ttl in packet_in
1323:  	const 8		 //8: size of: ipv4.ttl
1324:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1325:  	pop
1326:  	popn 8		 //8: size of: ipv4.ttl
// end of field ipv4.ttl (8)
// start of field ipv4.protocol (8)
1327:  	const 0		 //0: 0th bit
1328:  	const 0		 //0: 1th bit
1329:  	const 0		 //0: 2th bit
1330:  	const 0		 //0: 3th bit
1331:  	const 0		 //0: 4th bit
1332:  	const 0		 //0: 5th bit
1333:  	const 0		 //0: 6th bit
1334:  	const 0		 //0: 7th bit
1335:  	derefTop
1336:  	const 7		 //7: size of: ipv4.protocol - 1
1337:  	sub
1338:  	const 185		 //global address of target addr of field ipv4.protocol in packet_in
1339:  	const 8		 //8: size of: ipv4.protocol
1340:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1341:  	pop
1342:  	popn 8		 //8: size of: ipv4.protocol
// end of field ipv4.protocol (8)
// start of field ipv4.hdrChecksum (16)
1343:  	const 0		 //0: 0th bit
1344:  	const 0		 //0: 1th bit
1345:  	const 0		 //0: 2th bit
1346:  	const 0		 //0: 3th bit
1347:  	const 0		 //0: 4th bit
1348:  	const 0		 //0: 5th bit
1349:  	const 0		 //0: 6th bit
1350:  	const 0		 //0: 7th bit
1351:  	const 0		 //0: 8th bit
1352:  	const 0		 //0: 9th bit
1353:  	const 0		 //0: 10th bit
1354:  	const 0		 //0: 11th bit
1355:  	const 0		 //0: 12th bit
1356:  	const 0		 //0: 13th bit
1357:  	const 0		 //0: 14th bit
1358:  	const 0		 //0: 15th bit
1359:  	derefTop
1360:  	const 15		 //15: size of: ipv4.hdrChecksum - 1
1361:  	sub
1362:  	const 193		 //global address of target addr of field ipv4.hdrChecksum in packet_in
1363:  	const 16		 //16: size of: ipv4.hdrChecksum
1364:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1365:  	pop
1366:  	popn 16		 //16: size of: ipv4.hdrChecksum
// end of field ipv4.hdrChecksum (16)
// start of field ipv4.srcAddr (32)
1367:  	const 0		 //0: 0th bit
1368:  	const 0		 //0: 1th bit
1369:  	const 0		 //0: 2th bit
1370:  	const 0		 //0: 3th bit
1371:  	const 0		 //0: 4th bit
1372:  	const 0		 //0: 5th bit
1373:  	const 0		 //0: 6th bit
1374:  	const 0		 //0: 7th bit
1375:  	const 0		 //0: 8th bit
1376:  	const 0		 //0: 9th bit
1377:  	const 0		 //0: 10th bit
1378:  	const 0		 //0: 11th bit
1379:  	const 0		 //0: 12th bit
1380:  	const 0		 //0: 13th bit
1381:  	const 0		 //0: 14th bit
1382:  	const 0		 //0: 15th bit
1383:  	const 0		 //0: 16th bit
1384:  	const 0		 //0: 17th bit
1385:  	const 0		 //0: 18th bit
1386:  	const 0		 //0: 19th bit
1387:  	const 0		 //0: 20th bit
1388:  	const 0		 //0: 21th bit
1389:  	const 0		 //0: 22th bit
1390:  	const 0		 //0: 23th bit
1391:  	const 0		 //0: 24th bit
1392:  	const 0		 //0: 25th bit
1393:  	const 0		 //0: 26th bit
1394:  	const 0		 //0: 27th bit
1395:  	const 0		 //0: 28th bit
1396:  	const 0		 //0: 29th bit
1397:  	const 0		 //0: 30th bit
1398:  	const 0		 //0: 31th bit
1399:  	derefTop
1400:  	const 31		 //31: size of: ipv4.srcAddr - 1
1401:  	sub
1402:  	const 209		 //global address of target addr of field ipv4.srcAddr in packet_in
1403:  	const 32		 //32: size of: ipv4.srcAddr
1404:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1405:  	pop
1406:  	popn 32		 //32: size of: ipv4.srcAddr
// end of field ipv4.srcAddr (32)
// start of field ipv4.dstAddr (32)
1407:  	const 1		 //1: 0th bit
1408:  	const 1		 //1: 1th bit
1409:  	const 1		 //1: 2th bit
1410:  	const 1		 //1: 3th bit
1411:  	const 1		 //1: 4th bit
1412:  	const 1		 //1: 5th bit
1413:  	const 1		 //1: 6th bit
1414:  	const 1		 //1: 7th bit
1415:  	const 1		 //1: 8th bit
1416:  	const 1		 //1: 9th bit
1417:  	const 1		 //1: 10th bit
1418:  	const 1		 //1: 11th bit
1419:  	const 1		 //1: 12th bit
1420:  	const 1		 //1: 13th bit
1421:  	const 1		 //1: 14th bit
1422:  	const 1		 //1: 15th bit
1423:  	const 1		 //1: 16th bit
1424:  	const 1		 //1: 17th bit
1425:  	const 1		 //1: 18th bit
1426:  	const 1		 //1: 19th bit
1427:  	const 1		 //1: 20th bit
1428:  	const 1		 //1: 21th bit
1429:  	const 1		 //1: 22th bit
1430:  	const 1		 //1: 23th bit
1431:  	const 1		 //1: 24th bit
1432:  	const 1		 //1: 25th bit
1433:  	const 1		 //1: 26th bit
1434:  	const 1		 //1: 27th bit
1435:  	const 1		 //1: 28th bit
1436:  	const 1		 //1: 29th bit
1437:  	const 1		 //1: 30th bit
1438:  	const 1		 //1: 31th bit
1439:  	derefTop
1440:  	const 31		 //31: size of: ipv4.dstAddr - 1
1441:  	sub
1442:  	const 241		 //global address of target addr of field ipv4.dstAddr in packet_in
1443:  	const 32		 //32: size of: ipv4.dstAddr
1444:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1445:  	pop
1446:  	popn 32		 //32: size of: ipv4.dstAddr
// end of field ipv4.dstAddr (32)
// end of header ipv4
1447:  	goto 1585		 /* 1585: label to packet received, go to junction */
// start of header ethernet
// start of field ethernet.src (48)
1448:  	const 0		 //0: 0th bit
1449:  	const 0		 //0: 1th bit
1450:  	const 0		 //0: 2th bit
1451:  	const 0		 //0: 3th bit
1452:  	const 0		 //0: 4th bit
1453:  	const 0		 //0: 5th bit
1454:  	const 0		 //0: 6th bit
1455:  	const 0		 //0: 7th bit
1456:  	const 0		 //0: 8th bit
1457:  	const 0		 //0: 9th bit
1458:  	const 0		 //0: 10th bit
1459:  	const 0		 //0: 11th bit
1460:  	const 0		 //0: 12th bit
1461:  	const 0		 //0: 13th bit
1462:  	const 0		 //0: 14th bit
1463:  	const 0		 //0: 15th bit
1464:  	const 0		 //0: 16th bit
1465:  	const 0		 //0: 17th bit
1466:  	const 0		 //0: 18th bit
1467:  	const 0		 //0: 19th bit
1468:  	const 0		 //0: 20th bit
1469:  	const 0		 //0: 21th bit
1470:  	const 0		 //0: 22th bit
1471:  	const 0		 //0: 23th bit
1472:  	const 0		 //0: 24th bit
1473:  	const 0		 //0: 25th bit
1474:  	const 0		 //0: 26th bit
1475:  	const 0		 //0: 27th bit
1476:  	const 0		 //0: 28th bit
1477:  	const 0		 //0: 29th bit
1478:  	const 0		 //0: 30th bit
1479:  	const 0		 //0: 31th bit
1480:  	const 0		 //0: 32th bit
1481:  	const 0		 //0: 33th bit
1482:  	const 0		 //0: 34th bit
1483:  	const 0		 //0: 35th bit
1484:  	const 0		 //0: 36th bit
1485:  	const 0		 //0: 37th bit
1486:  	const 0		 //0: 38th bit
1487:  	const 0		 //0: 39th bit
1488:  	const 0		 //0: 40th bit
1489:  	const 0		 //0: 41th bit
1490:  	const 0		 //0: 42th bit
1491:  	const 0		 //0: 43th bit
1492:  	const 0		 //0: 44th bit
1493:  	const 0		 //0: 45th bit
1494:  	const 0		 //0: 46th bit
1495:  	const 0		 //0: 47th bit
1496:  	derefTop
1497:  	const 47		 //47: size of: ethernet.src - 1
1498:  	sub
1499:  	const 1		 //global address of target addr of field ethernet.src in packet_in
1500:  	const 48		 //48: size of: ethernet.src
1501:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1502:  	pop
1503:  	popn 48		 //48: size of: ethernet.src
// end of field ethernet.src (48)
// start of field ethernet.dst (48)
1504:  	const 0		 //0: 0th bit
1505:  	const 0		 //0: 1th bit
1506:  	const 0		 //0: 2th bit
1507:  	const 0		 //0: 3th bit
1508:  	const 0		 //0: 4th bit
1509:  	const 0		 //0: 5th bit
1510:  	const 0		 //0: 6th bit
1511:  	const 0		 //0: 7th bit
1512:  	const 0		 //0: 8th bit
1513:  	const 0		 //0: 9th bit
1514:  	const 0		 //0: 10th bit
1515:  	const 0		 //0: 11th bit
1516:  	const 0		 //0: 12th bit
1517:  	const 0		 //0: 13th bit
1518:  	const 0		 //0: 14th bit
1519:  	const 0		 //0: 15th bit
1520:  	const 0		 //0: 16th bit
1521:  	const 0		 //0: 17th bit
1522:  	const 0		 //0: 18th bit
1523:  	const 0		 //0: 19th bit
1524:  	const 0		 //0: 20th bit
1525:  	const 0		 //0: 21th bit
1526:  	const 0		 //0: 22th bit
1527:  	const 0		 //0: 23th bit
1528:  	const 0		 //0: 24th bit
1529:  	const 0		 //0: 25th bit
1530:  	const 0		 //0: 26th bit
1531:  	const 0		 //0: 27th bit
1532:  	const 0		 //0: 28th bit
1533:  	const 0		 //0: 29th bit
1534:  	const 0		 //0: 30th bit
1535:  	const 0		 //0: 31th bit
1536:  	const 0		 //0: 32th bit
1537:  	const 0		 //0: 33th bit
1538:  	const 0		 //0: 34th bit
1539:  	const 0		 //0: 35th bit
1540:  	const 0		 //0: 36th bit
1541:  	const 0		 //0: 37th bit
1542:  	const 0		 //0: 38th bit
1543:  	const 0		 //0: 39th bit
1544:  	const 0		 //0: 40th bit
1545:  	const 0		 //0: 41th bit
1546:  	const 0		 //0: 42th bit
1547:  	const 0		 //0: 43th bit
1548:  	const 0		 //0: 44th bit
1549:  	const 0		 //0: 45th bit
1550:  	const 0		 //0: 46th bit
1551:  	const 0		 //0: 47th bit
1552:  	derefTop
1553:  	const 47		 //47: size of: ethernet.dst - 1
1554:  	sub
1555:  	const 49		 //global address of target addr of field ethernet.dst in packet_in
1556:  	const 48		 //48: size of: ethernet.dst
1557:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1558:  	pop
1559:  	popn 48		 //48: size of: ethernet.dst
// end of field ethernet.dst (48)
// start of field ethernet.etherType (16)
1560:  	const 0		 //0: 0th bit
1561:  	const 0		 //0: 1th bit
1562:  	const 0		 //0: 2th bit
1563:  	const 0		 //0: 3th bit
1564:  	const 0		 //0: 4th bit
1565:  	const 0		 //0: 5th bit
1566:  	const 0		 //0: 6th bit
1567:  	const 0		 //0: 7th bit
1568:  	const 0		 //0: 8th bit
1569:  	const 0		 //0: 9th bit
1570:  	const 0		 //0: 10th bit
1571:  	const 0		 //0: 11th bit
1572:  	const 0		 //0: 12th bit
1573:  	const 0		 //0: 13th bit
1574:  	const 0		 //0: 14th bit
1575:  	const 0		 //0: 15th bit
1576:  	derefTop
1577:  	const 15		 //15: size of: ethernet.etherType - 1
1578:  	sub
1579:  	const 97		 //global address of target addr of field ethernet.etherType in packet_in
1580:  	const 16		 //16: size of: ethernet.etherType
1581:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1582:  	pop
1583:  	popn 16		 //16: size of: ethernet.etherType
// end of field ethernet.etherType (16)
// end of header ethernet
1584:  	goto 1585		 /* 1585: label to packet received, go to junction */
1585:  	const 0		 //0: receive_packet terminates with status OK
1586:  	return 
//  
// end of definition of receive_packet()
//

// definition of fill_tables()
// Filling table rewrite_mac
// Filling table bd
// bd / Entry 0
// bd / Entry 0 / Key meta.ingress_metadata.bd
1587:  	const 0		 //0: 
1588:  	const 0		 //0: 
1589:  	const 0		 //0: 
1590:  	const 0		 //0: 
1591:  	const 0		 //0: 
1592:  	const 0		 //0: 
1593:  	const 0		 //0: 
1594:  	const 0		 //0: 
1595:  	const 0		 //0: 
1596:  	const 0		 //0: 
1597:  	const 0		 //0: 
1598:  	const 0		 //0: 
1599:  	const 0		 //0: 
1600:  	const 0		 //0: 
1601:  	const 0		 //0: 
1602:  	const 1		 //1: 
1603:  	derefTop
1604:  	const 15		 //15: size of: bd.ENTRY[0,0]  - 1
1605:  	sub
1606:  	const 1089		 //global address of bd.ENTRY[0,0]
1607:  	const 16		 //16: size of: bd.ENTRY[0,0]
1608:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
// Filling table ipv4_fib
// ipv4_fib / Entry 0
// ipv4_fib / Entry 0 / Key hdr.ipv4.dstAddr
1609:  	const 0		 //0: 
1610:  	const 0		 //0: 
1611:  	const 0		 //0: 
1612:  	const 0		 //0: 
1613:  	const 0		 //0: 
1614:  	const 0		 //0: 
1615:  	const 0		 //0: 
1616:  	const 0		 //0: 
1617:  	const 0		 //0: 
1618:  	const 0		 //0: 
1619:  	const 0		 //0: 
1620:  	const 0		 //0: 
1621:  	const 0		 //0: 
1622:  	const 0		 //0: 
1623:  	const 0		 //0: 
1624:  	const 0		 //0: 
1625:  	const 0		 //0: 
1626:  	const 0		 //0: 
1627:  	const 0		 //0: 
1628:  	const 0		 //0: 
1629:  	const 0		 //0: 
1630:  	const 0		 //0: 
1631:  	const 0		 //0: 
1632:  	const 0		 //0: 
1633:  	const 0		 //0: 
1634:  	const 0		 //0: 
1635:  	const 0		 //0: 
1636:  	const 0		 //0: 
1637:  	const 0		 //0: 
1638:  	const 0		 //0: 
1639:  	const 0		 //0: 
1640:  	const 0		 //0: 
1641:  	derefTop
1642:  	const 31		 //31: size of: ipv4_fib.ENTRY[0,0]  - 1
1643:  	sub
1644:  	const 1106		 //global address of ipv4_fib.ENTRY[0,0]
1645:  	const 32		 //32: size of: ipv4_fib.ENTRY[0,0]
1646:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
// ipv4_fib / Entry 0 / Key meta.ingress_metadata.vrf
1647:  	const 0		 //0: 
1648:  	const 0		 //0: 
1649:  	const 0		 //0: 
1650:  	const 0		 //0: 
1651:  	const 0		 //0: 
1652:  	const 0		 //0: 
1653:  	const 0		 //0: 
1654:  	const 0		 //0: 
1655:  	const 0		 //0: 
1656:  	const 0		 //0: 
1657:  	const 0		 //0: 
1658:  	const 0		 //0: 
1659:  	derefTop
1660:  	const 11		 //11: size of: ipv4_fib.ENTRY[0,1]  - 1
1661:  	sub
1662:  	const 1138		 //global address of ipv4_fib.ENTRY[0,1]
1663:  	const 12		 //12: size of: ipv4_fib.ENTRY[0,1]
1664:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
// Filling table ipv4_fib_lpm
// ipv4_fib_lpm / Entry 0
// ipv4_fib_lpm / Entry 0 / Key hdr.ipv4.dstAddr
1665:  	const 0		 //0: 
1666:  	const 0		 //0: 
1667:  	const 0		 //0: 
1668:  	const 0		 //0: 
1669:  	const 0		 //0: 
1670:  	const 0		 //0: 
1671:  	const 0		 //0: 
1672:  	const 0		 //0: 
1673:  	const 0		 //0: 
1674:  	const 0		 //0: 
1675:  	const 0		 //0: 
1676:  	const 0		 //0: 
1677:  	const 0		 //0: 
1678:  	const 0		 //0: 
1679:  	const 0		 //0: 
1680:  	const 0		 //0: 
1681:  	const 0		 //0: 
1682:  	const 0		 //0: 
1683:  	const 0		 //0: 
1684:  	const 0		 //0: 
1685:  	const 0		 //0: 
1686:  	const 0		 //0: 
1687:  	const 0		 //0: 
1688:  	const 0		 //0: 
1689:  	const 0		 //0: 
1690:  	const 0		 //0: 
1691:  	const 0		 //0: 
1692:  	const 0		 //0: 
1693:  	const 0		 //0: 
1694:  	const 0		 //0: 
1695:  	const 0		 //0: 
1696:  	const 1		 //1: 
1697:  	derefTop
1698:  	const 31		 //31: size of: ipv4_fib_lpm.ENTRY[0,0]  - 1
1699:  	sub
1700:  	const 1151		 //global address of ipv4_fib_lpm.ENTRY[0,0]
1701:  	const 32		 //32: size of: ipv4_fib_lpm.ENTRY[0,0]
1702:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
// ipv4_fib_lpm / Entry 0 / Key meta.ingress_metadata.vrf
1703:  	const 0		 //0: 
1704:  	const 0		 //0: 
1705:  	const 0		 //0: 
1706:  	const 0		 //0: 
1707:  	const 0		 //0: 
1708:  	const 0		 //0: 
1709:  	const 0		 //0: 
1710:  	const 0		 //0: 
1711:  	const 0		 //0: 
1712:  	const 0		 //0: 
1713:  	const 0		 //0: 
1714:  	const 1		 //1: 
1715:  	derefTop
1716:  	const 11		 //11: size of: ipv4_fib_lpm.ENTRY[0,1]  - 1
1717:  	sub
1718:  	const 1183		 //global address of ipv4_fib_lpm.ENTRY[0,1]
1719:  	const 12		 //12: size of: ipv4_fib_lpm.ENTRY[0,1]
1720:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
// Filling table nexthop
// nexthop / Entry 0
// nexthop / Entry 0 / Key meta.ingress_metadata.nexthop_index
1721:  	const 0		 //0: 
1722:  	const 0		 //0: 
1723:  	const 0		 //0: 
1724:  	const 0		 //0: 
1725:  	const 0		 //0: 
1726:  	const 0		 //0: 
1727:  	const 0		 //0: 
1728:  	const 0		 //0: 
1729:  	const 0		 //0: 
1730:  	const 0		 //0: 
1731:  	const 0		 //0: 
1732:  	const 0		 //0: 
1733:  	const 0		 //0: 
1734:  	const 0		 //0: 
1735:  	const 0		 //0: 
1736:  	const 1		 //1: 
1737:  	derefTop
1738:  	const 15		 //15: size of: nexthop.ENTRY[0,0]  - 1
1739:  	sub
1740:  	const 1196		 //global address of nexthop.ENTRY[0,0]
1741:  	const 16		 //16: size of: nexthop.ENTRY[0,0]
1742:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
// Filling table port_mapping
// port_mapping / Entry 0
// port_mapping / Entry 0 / Key standard_metadata.ingress_port
1743:  	const 0		 //0: 
1744:  	const 0		 //0: 
1745:  	const 0		 //0: 
1746:  	const 0		 //0: 
1747:  	const 0		 //0: 
1748:  	const 0		 //0: 
1749:  	const 0		 //0: 
1750:  	const 0		 //0: 
1751:  	const 1		 //1: 
1752:  	derefTop
1753:  	const 8		 //8: size of: port_mapping.ENTRY[0,0]  - 1
1754:  	sub
1755:  	const 1213		 //global address of port_mapping.ENTRY[0,0]
1756:  	const 9		 //9: size of: port_mapping.ENTRY[0,0]
1757:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1758:  	const 0		 //0: fill_tables terminates with status OK
1759:  	return 
//  
// end of definition of fill_tables()
//

// definition of memcmp()
1760:  	load 2		 // 2: local address of length
1761:  	top
1762:  	ifeq 1780		 //1780: label to jump if length is 0
1763:  	load 0		 // 0: local address of src
1764:  	top
1765:  	inc
1766:  	store 0		 //0: local address of src
1767:  	getfield
1768:  	load 1		 // 1: local address of dst
1769:  	top
1770:  	inc
1771:  	store 1		 //1: local address of dst
1772:  	getfield
1773:  	eq
1774:  	ifeq 1777		 //1777: label to jump if not equal
1775:  	dec
1776:  	goto 1761		 /* 1761: label to loop */
1777:  	pop
1778:  	const 0		 //0: fail
1779:  	goto 1782		 /* 1782: label to return */
1780:  	pop
1781:  	const 1		 //1: success
1782:  	return 
// end of definition of memcmp()
//

// definition of memcpy()
1783:  	load 2		 // 2: local address of length
1784:  	top
1785:  	ifeq 1798		 //1798: label to jump if length is 0
1786:  	load 0		 // 0: local address of src
1787:  	top
1788:  	inc
1789:  	store 0		 //0: local address of src
1790:  	getfield
1791:  	load 1		 // 1: local address of dst
1792:  	top
1793:  	inc
1794:  	store 1		 //1: local address of dst
1795:  	putfield
1796:  	dec
1797:  	goto 1784		 /* 1784: label to loop */
1798:  	pop
1799:  	const 0		 //0: memcpy terminates with status OK
1800:  	return 
//  
// end of definition of memcpy()
//

// definition of subtract()
// TODO
1801:  	const 0		 //0: subtract terminates with status OK
1802:  	return 
// end of definition of subtract()
//

// definition of main()
// ParserImpl(packet_in, {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}, {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority})
1803:  	const 0		 //global address of packet_in
// {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
1804:  	const 719		 //global address of headers.ethernet.valid
1805:  	const 720		 //global address of headers.ethernet.size
1806:  	const 721		 //global address of headers.ethernet.srcAddr
1807:  	const 769		 //global address of headers.ethernet.dstAddr
1808:  	const 817		 //global address of headers.ethernet.etherType
// memcpy(src,dst,length)
1809:  	derefTop
1810:  	const 5		 //5: size of: list
1811:  	sub
1812:  	inc
1813:  	const 1356		 //global address of main.temp0_LIST_5
1814:  	const 5		 //5: size of: list
1815:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1816:  	pop
// end of memcpy(src,dst,length)
1817:  	popn 5		 //5: size of: list
1818:  	const 1356		 //global address of main.temp0_LIST_5
// end of {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
// {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
1819:  	const 833		 //global address of headers.ipv4.valid
1820:  	const 834		 //global address of headers.ipv4.size
1821:  	const 835		 //global address of headers.ipv4.version
1822:  	const 839		 //global address of headers.ipv4.ihl
1823:  	const 843		 //global address of headers.ipv4.diffserv
1824:  	const 851		 //global address of headers.ipv4.totalLen
1825:  	const 867		 //global address of headers.ipv4.identification
1826:  	const 883		 //global address of headers.ipv4.flags
1827:  	const 886		 //global address of headers.ipv4.fragOffset
1828:  	const 899		 //global address of headers.ipv4.ttl
1829:  	const 907		 //global address of headers.ipv4.protocol
1830:  	const 915		 //global address of headers.ipv4.hdrChecksum
1831:  	const 931		 //global address of headers.ipv4.srcAddr
1832:  	const 963		 //global address of headers.ipv4.dstAddr
// memcpy(src,dst,length)
1833:  	derefTop
1834:  	const 14		 //14: size of: list
1835:  	sub
1836:  	inc
1837:  	const 1361		 //global address of main.temp1_LIST_14
1838:  	const 14		 //14: size of: list
1839:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1840:  	pop
// end of memcpy(src,dst,length)
1841:  	popn 14		 //14: size of: list
1842:  	const 1361		 //global address of main.temp1_LIST_14
// end of {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
// memcpy(src,dst,length)
1843:  	derefTop
1844:  	const 2		 //2: size of: list
1845:  	sub
1846:  	inc
1847:  	const 1375		 //global address of main.temp2_LIST_2
1848:  	const 2		 //2: size of: list
1849:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1850:  	pop
// end of memcpy(src,dst,length)
1851:  	popn 2		 //2: size of: list
1852:  	const 1375		 //global address of main.temp2_LIST_2
// end of {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
// {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
1853:  	const 675		 //global address of metadata.ingress_metadata.vrf
1854:  	const 687		 //global address of metadata.ingress_metadata.bd
1855:  	const 703		 //global address of metadata.ingress_metadata.nexthop_index
// memcpy(src,dst,length)
1856:  	derefTop
1857:  	const 3		 //3: size of: list
1858:  	sub
1859:  	inc
1860:  	const 1377		 //global address of main.temp3_LIST_3
1861:  	const 3		 //3: size of: list
1862:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1863:  	pop
// end of memcpy(src,dst,length)
1864:  	popn 3		 //3: size of: list
1865:  	const 1377		 //global address of main.temp3_LIST_3
// end of {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
// memcpy(src,dst,length)
1866:  	derefTop
1867:  	const 1		 //1: size of: list
1868:  	sub
1869:  	inc
1870:  	const 1380		 //global address of main.temp4_LIST_1
1871:  	const 1		 //1: size of: list
1872:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1873:  	pop
// end of memcpy(src,dst,length)
1874:  	popn 1		 //1: size of: list
1875:  	const 1380		 //global address of main.temp4_LIST_1
// end of {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
// {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
1876:  	const 305		 //global address of standard_metadata_t.ingress_port
1877:  	const 314		 //global address of standard_metadata_t.egress_spec
1878:  	const 323		 //global address of standard_metadata_t.egress_port
1879:  	const 332		 //global address of standard_metadata_t.instance_type
1880:  	const 364		 //global address of standard_metadata_t.packet_length
1881:  	const 396		 //global address of standard_metadata_t.enq_timestamp
1882:  	const 428		 //global address of standard_metadata_t.enq_qdepth
1883:  	const 447		 //global address of standard_metadata_t.deq_timedelta
1884:  	const 479		 //global address of standard_metadata_t.deq_qdepth
1885:  	const 498		 //global address of standard_metadata_t.ingress_global_timestamp
1886:  	const 546		 //global address of standard_metadata_t.egress_global_timestamp
1887:  	const 594		 //global address of standard_metadata_t.mcast_grp
1888:  	const 610		 //global address of standard_metadata_t.egress_rid
1889:  	const 626		 //global address of standard_metadata_t.checksum_error
1890:  	const 627		 //global address of standard_metadata_t.parser_error
1891:  	const 628		 //global address of standard_metadata_t.priority
// memcpy(src,dst,length)
1892:  	derefTop
1893:  	const 16		 //16: size of: list
1894:  	sub
1895:  	inc
1896:  	const 1381		 //global address of main.temp5_LIST_16
1897:  	const 16		 //16: size of: list
1898:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1899:  	pop
// end of memcpy(src,dst,length)
1900:  	popn 16		 //16: size of: list
1901:  	const 1381		 //global address of main.temp5_LIST_16
// end of {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
1902:  	invoke 239 4		 // 239: label to ::ParserImpl, 4: size of: packet_in, {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}, {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
1903:  	pop
// ingress({{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}, {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority})
// {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
1904:  	const 719		 //global address of headers.ethernet.valid
1905:  	const 720		 //global address of headers.ethernet.size
1906:  	const 721		 //global address of headers.ethernet.srcAddr
1907:  	const 769		 //global address of headers.ethernet.dstAddr
1908:  	const 817		 //global address of headers.ethernet.etherType
// memcpy(src,dst,length)
1909:  	derefTop
1910:  	const 5		 //5: size of: list
1911:  	sub
1912:  	inc
1913:  	const 1397		 //global address of main.temp6_LIST_5
1914:  	const 5		 //5: size of: list
1915:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1916:  	pop
// end of memcpy(src,dst,length)
1917:  	popn 5		 //5: size of: list
1918:  	const 1397		 //global address of main.temp6_LIST_5
// end of {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
// {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
1919:  	const 833		 //global address of headers.ipv4.valid
1920:  	const 834		 //global address of headers.ipv4.size
1921:  	const 835		 //global address of headers.ipv4.version
1922:  	const 839		 //global address of headers.ipv4.ihl
1923:  	const 843		 //global address of headers.ipv4.diffserv
1924:  	const 851		 //global address of headers.ipv4.totalLen
1925:  	const 867		 //global address of headers.ipv4.identification
1926:  	const 883		 //global address of headers.ipv4.flags
1927:  	const 886		 //global address of headers.ipv4.fragOffset
1928:  	const 899		 //global address of headers.ipv4.ttl
1929:  	const 907		 //global address of headers.ipv4.protocol
1930:  	const 915		 //global address of headers.ipv4.hdrChecksum
1931:  	const 931		 //global address of headers.ipv4.srcAddr
1932:  	const 963		 //global address of headers.ipv4.dstAddr
// memcpy(src,dst,length)
1933:  	derefTop
1934:  	const 14		 //14: size of: list
1935:  	sub
1936:  	inc
1937:  	const 1402		 //global address of main.temp7_LIST_14
1938:  	const 14		 //14: size of: list
1939:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1940:  	pop
// end of memcpy(src,dst,length)
1941:  	popn 14		 //14: size of: list
1942:  	const 1402		 //global address of main.temp7_LIST_14
// end of {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
// memcpy(src,dst,length)
1943:  	derefTop
1944:  	const 2		 //2: size of: list
1945:  	sub
1946:  	inc
1947:  	const 1416		 //global address of main.temp8_LIST_2
1948:  	const 2		 //2: size of: list
1949:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1950:  	pop
// end of memcpy(src,dst,length)
1951:  	popn 2		 //2: size of: list
1952:  	const 1416		 //global address of main.temp8_LIST_2
// end of {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
// {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
1953:  	const 675		 //global address of metadata.ingress_metadata.vrf
1954:  	const 687		 //global address of metadata.ingress_metadata.bd
1955:  	const 703		 //global address of metadata.ingress_metadata.nexthop_index
// memcpy(src,dst,length)
1956:  	derefTop
1957:  	const 3		 //3: size of: list
1958:  	sub
1959:  	inc
1960:  	const 1418		 //global address of main.temp9_LIST_3
1961:  	const 3		 //3: size of: list
1962:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1963:  	pop
// end of memcpy(src,dst,length)
1964:  	popn 3		 //3: size of: list
1965:  	const 1418		 //global address of main.temp9_LIST_3
// end of {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
// memcpy(src,dst,length)
1966:  	derefTop
1967:  	const 1		 //1: size of: list
1968:  	sub
1969:  	inc
1970:  	const 1421		 //global address of main.temp10_LIST_1
1971:  	const 1		 //1: size of: list
1972:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1973:  	pop
// end of memcpy(src,dst,length)
1974:  	popn 1		 //1: size of: list
1975:  	const 1421		 //global address of main.temp10_LIST_1
// end of {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
// {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
1976:  	const 305		 //global address of standard_metadata_t.ingress_port
1977:  	const 314		 //global address of standard_metadata_t.egress_spec
1978:  	const 323		 //global address of standard_metadata_t.egress_port
1979:  	const 332		 //global address of standard_metadata_t.instance_type
1980:  	const 364		 //global address of standard_metadata_t.packet_length
1981:  	const 396		 //global address of standard_metadata_t.enq_timestamp
1982:  	const 428		 //global address of standard_metadata_t.enq_qdepth
1983:  	const 447		 //global address of standard_metadata_t.deq_timedelta
1984:  	const 479		 //global address of standard_metadata_t.deq_qdepth
1985:  	const 498		 //global address of standard_metadata_t.ingress_global_timestamp
1986:  	const 546		 //global address of standard_metadata_t.egress_global_timestamp
1987:  	const 594		 //global address of standard_metadata_t.mcast_grp
1988:  	const 610		 //global address of standard_metadata_t.egress_rid
1989:  	const 626		 //global address of standard_metadata_t.checksum_error
1990:  	const 627		 //global address of standard_metadata_t.parser_error
1991:  	const 628		 //global address of standard_metadata_t.priority
// memcpy(src,dst,length)
1992:  	derefTop
1993:  	const 16		 //16: size of: list
1994:  	sub
1995:  	inc
1996:  	const 1422		 //global address of main.temp11_LIST_16
1997:  	const 16		 //16: size of: list
1998:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
1999:  	pop
// end of memcpy(src,dst,length)
2000:  	popn 16		 //16: size of: list
2001:  	const 1422		 //global address of main.temp11_LIST_16
// end of {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
2002:  	invoke 12 3		 // 12: label to ::ingress, 3: size of: {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}, {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
2003:  	pop
// verifyChecksum({{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}})
// {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
2004:  	const 719		 //global address of headers.ethernet.valid
2005:  	const 720		 //global address of headers.ethernet.size
2006:  	const 721		 //global address of headers.ethernet.srcAddr
2007:  	const 769		 //global address of headers.ethernet.dstAddr
2008:  	const 817		 //global address of headers.ethernet.etherType
// memcpy(src,dst,length)
2009:  	derefTop
2010:  	const 5		 //5: size of: list
2011:  	sub
2012:  	inc
2013:  	const 1438		 //global address of main.temp12_LIST_5
2014:  	const 5		 //5: size of: list
2015:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2016:  	pop
// end of memcpy(src,dst,length)
2017:  	popn 5		 //5: size of: list
2018:  	const 1438		 //global address of main.temp12_LIST_5
// end of {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
// {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
2019:  	const 833		 //global address of headers.ipv4.valid
2020:  	const 834		 //global address of headers.ipv4.size
2021:  	const 835		 //global address of headers.ipv4.version
2022:  	const 839		 //global address of headers.ipv4.ihl
2023:  	const 843		 //global address of headers.ipv4.diffserv
2024:  	const 851		 //global address of headers.ipv4.totalLen
2025:  	const 867		 //global address of headers.ipv4.identification
2026:  	const 883		 //global address of headers.ipv4.flags
2027:  	const 886		 //global address of headers.ipv4.fragOffset
2028:  	const 899		 //global address of headers.ipv4.ttl
2029:  	const 907		 //global address of headers.ipv4.protocol
2030:  	const 915		 //global address of headers.ipv4.hdrChecksum
2031:  	const 931		 //global address of headers.ipv4.srcAddr
2032:  	const 963		 //global address of headers.ipv4.dstAddr
// memcpy(src,dst,length)
2033:  	derefTop
2034:  	const 14		 //14: size of: list
2035:  	sub
2036:  	inc
2037:  	const 1443		 //global address of main.temp13_LIST_14
2038:  	const 14		 //14: size of: list
2039:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2040:  	pop
// end of memcpy(src,dst,length)
2041:  	popn 14		 //14: size of: list
2042:  	const 1443		 //global address of main.temp13_LIST_14
// end of {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
// memcpy(src,dst,length)
2043:  	derefTop
2044:  	const 2		 //2: size of: list
2045:  	sub
2046:  	inc
2047:  	const 1457		 //global address of main.temp14_LIST_2
2048:  	const 2		 //2: size of: list
2049:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2050:  	pop
// end of memcpy(src,dst,length)
2051:  	popn 2		 //2: size of: list
2052:  	const 1457		 //global address of main.temp14_LIST_2
// end of {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
// {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
2053:  	const 675		 //global address of metadata.ingress_metadata.vrf
2054:  	const 687		 //global address of metadata.ingress_metadata.bd
2055:  	const 703		 //global address of metadata.ingress_metadata.nexthop_index
// memcpy(src,dst,length)
2056:  	derefTop
2057:  	const 3		 //3: size of: list
2058:  	sub
2059:  	inc
2060:  	const 1459		 //global address of main.temp15_LIST_3
2061:  	const 3		 //3: size of: list
2062:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2063:  	pop
// end of memcpy(src,dst,length)
2064:  	popn 3		 //3: size of: list
2065:  	const 1459		 //global address of main.temp15_LIST_3
// end of {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
// memcpy(src,dst,length)
2066:  	derefTop
2067:  	const 1		 //1: size of: list
2068:  	sub
2069:  	inc
2070:  	const 1462		 //global address of main.temp16_LIST_1
2071:  	const 1		 //1: size of: list
2072:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2073:  	pop
// end of memcpy(src,dst,length)
2074:  	popn 1		 //1: size of: list
2075:  	const 1462		 //global address of main.temp16_LIST_1
// end of {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
2076:  	invoke 362 2		 // 362: label to ::verifyChecksum, 2: size of: {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
2077:  	pop
// egress({{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}, {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority})
// {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
2078:  	const 719		 //global address of headers.ethernet.valid
2079:  	const 720		 //global address of headers.ethernet.size
2080:  	const 721		 //global address of headers.ethernet.srcAddr
2081:  	const 769		 //global address of headers.ethernet.dstAddr
2082:  	const 817		 //global address of headers.ethernet.etherType
// memcpy(src,dst,length)
2083:  	derefTop
2084:  	const 5		 //5: size of: list
2085:  	sub
2086:  	inc
2087:  	const 1463		 //global address of main.temp17_LIST_5
2088:  	const 5		 //5: size of: list
2089:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2090:  	pop
// end of memcpy(src,dst,length)
2091:  	popn 5		 //5: size of: list
2092:  	const 1463		 //global address of main.temp17_LIST_5
// end of {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
// {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
2093:  	const 833		 //global address of headers.ipv4.valid
2094:  	const 834		 //global address of headers.ipv4.size
2095:  	const 835		 //global address of headers.ipv4.version
2096:  	const 839		 //global address of headers.ipv4.ihl
2097:  	const 843		 //global address of headers.ipv4.diffserv
2098:  	const 851		 //global address of headers.ipv4.totalLen
2099:  	const 867		 //global address of headers.ipv4.identification
2100:  	const 883		 //global address of headers.ipv4.flags
2101:  	const 886		 //global address of headers.ipv4.fragOffset
2102:  	const 899		 //global address of headers.ipv4.ttl
2103:  	const 907		 //global address of headers.ipv4.protocol
2104:  	const 915		 //global address of headers.ipv4.hdrChecksum
2105:  	const 931		 //global address of headers.ipv4.srcAddr
2106:  	const 963		 //global address of headers.ipv4.dstAddr
// memcpy(src,dst,length)
2107:  	derefTop
2108:  	const 14		 //14: size of: list
2109:  	sub
2110:  	inc
2111:  	const 1468		 //global address of main.temp18_LIST_14
2112:  	const 14		 //14: size of: list
2113:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2114:  	pop
// end of memcpy(src,dst,length)
2115:  	popn 14		 //14: size of: list
2116:  	const 1468		 //global address of main.temp18_LIST_14
// end of {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
// memcpy(src,dst,length)
2117:  	derefTop
2118:  	const 2		 //2: size of: list
2119:  	sub
2120:  	inc
2121:  	const 1482		 //global address of main.temp19_LIST_2
2122:  	const 2		 //2: size of: list
2123:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2124:  	pop
// end of memcpy(src,dst,length)
2125:  	popn 2		 //2: size of: list
2126:  	const 1482		 //global address of main.temp19_LIST_2
// end of {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
// {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
2127:  	const 675		 //global address of metadata.ingress_metadata.vrf
2128:  	const 687		 //global address of metadata.ingress_metadata.bd
2129:  	const 703		 //global address of metadata.ingress_metadata.nexthop_index
// memcpy(src,dst,length)
2130:  	derefTop
2131:  	const 3		 //3: size of: list
2132:  	sub
2133:  	inc
2134:  	const 1484		 //global address of main.temp20_LIST_3
2135:  	const 3		 //3: size of: list
2136:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2137:  	pop
// end of memcpy(src,dst,length)
2138:  	popn 3		 //3: size of: list
2139:  	const 1484		 //global address of main.temp20_LIST_3
// end of {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
// memcpy(src,dst,length)
2140:  	derefTop
2141:  	const 1		 //1: size of: list
2142:  	sub
2143:  	inc
2144:  	const 1487		 //global address of main.temp21_LIST_1
2145:  	const 1		 //1: size of: list
2146:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2147:  	pop
// end of memcpy(src,dst,length)
2148:  	popn 1		 //1: size of: list
2149:  	const 1487		 //global address of main.temp21_LIST_1
// end of {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
// {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
2150:  	const 305		 //global address of standard_metadata_t.ingress_port
2151:  	const 314		 //global address of standard_metadata_t.egress_spec
2152:  	const 323		 //global address of standard_metadata_t.egress_port
2153:  	const 332		 //global address of standard_metadata_t.instance_type
2154:  	const 364		 //global address of standard_metadata_t.packet_length
2155:  	const 396		 //global address of standard_metadata_t.enq_timestamp
2156:  	const 428		 //global address of standard_metadata_t.enq_qdepth
2157:  	const 447		 //global address of standard_metadata_t.deq_timedelta
2158:  	const 479		 //global address of standard_metadata_t.deq_qdepth
2159:  	const 498		 //global address of standard_metadata_t.ingress_global_timestamp
2160:  	const 546		 //global address of standard_metadata_t.egress_global_timestamp
2161:  	const 594		 //global address of standard_metadata_t.mcast_grp
2162:  	const 610		 //global address of standard_metadata_t.egress_rid
2163:  	const 626		 //global address of standard_metadata_t.checksum_error
2164:  	const 627		 //global address of standard_metadata_t.parser_error
2165:  	const 628		 //global address of standard_metadata_t.priority
// memcpy(src,dst,length)
2166:  	derefTop
2167:  	const 16		 //16: size of: list
2168:  	sub
2169:  	inc
2170:  	const 1488		 //global address of main.temp22_LIST_16
2171:  	const 16		 //16: size of: list
2172:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2173:  	pop
// end of memcpy(src,dst,length)
2174:  	popn 16		 //16: size of: list
2175:  	const 1488		 //global address of main.temp22_LIST_16
// end of {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
2176:  	invoke 468 3		 // 468: label to ::egress, 3: size of: {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}, {standard_metadata_t.ingress_port,standard_metadata_t.egress_spec,standard_metadata_t.egress_port,standard_metadata_t.instance_type,standard_metadata_t.packet_length,standard_metadata_t.enq_timestamp,standard_metadata_t.enq_qdepth,standard_metadata_t.deq_timedelta,standard_metadata_t.deq_qdepth,standard_metadata_t.ingress_global_timestamp,standard_metadata_t.egress_global_timestamp,standard_metadata_t.mcast_grp,standard_metadata_t.egress_rid,standard_metadata_t.checksum_error,standard_metadata_t.parser_error,standard_metadata_t.priority}
2177:  	pop
// computeChecksum({{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}})
// {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
2178:  	const 719		 //global address of headers.ethernet.valid
2179:  	const 720		 //global address of headers.ethernet.size
2180:  	const 721		 //global address of headers.ethernet.srcAddr
2181:  	const 769		 //global address of headers.ethernet.dstAddr
2182:  	const 817		 //global address of headers.ethernet.etherType
// memcpy(src,dst,length)
2183:  	derefTop
2184:  	const 5		 //5: size of: list
2185:  	sub
2186:  	inc
2187:  	const 1504		 //global address of main.temp23_LIST_5
2188:  	const 5		 //5: size of: list
2189:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2190:  	pop
// end of memcpy(src,dst,length)
2191:  	popn 5		 //5: size of: list
2192:  	const 1504		 //global address of main.temp23_LIST_5
// end of {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
// {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
2193:  	const 833		 //global address of headers.ipv4.valid
2194:  	const 834		 //global address of headers.ipv4.size
2195:  	const 835		 //global address of headers.ipv4.version
2196:  	const 839		 //global address of headers.ipv4.ihl
2197:  	const 843		 //global address of headers.ipv4.diffserv
2198:  	const 851		 //global address of headers.ipv4.totalLen
2199:  	const 867		 //global address of headers.ipv4.identification
2200:  	const 883		 //global address of headers.ipv4.flags
2201:  	const 886		 //global address of headers.ipv4.fragOffset
2202:  	const 899		 //global address of headers.ipv4.ttl
2203:  	const 907		 //global address of headers.ipv4.protocol
2204:  	const 915		 //global address of headers.ipv4.hdrChecksum
2205:  	const 931		 //global address of headers.ipv4.srcAddr
2206:  	const 963		 //global address of headers.ipv4.dstAddr
// memcpy(src,dst,length)
2207:  	derefTop
2208:  	const 14		 //14: size of: list
2209:  	sub
2210:  	inc
2211:  	const 1509		 //global address of main.temp24_LIST_14
2212:  	const 14		 //14: size of: list
2213:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2214:  	pop
// end of memcpy(src,dst,length)
2215:  	popn 14		 //14: size of: list
2216:  	const 1509		 //global address of main.temp24_LIST_14
// end of {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
// memcpy(src,dst,length)
2217:  	derefTop
2218:  	const 2		 //2: size of: list
2219:  	sub
2220:  	inc
2221:  	const 1523		 //global address of main.temp25_LIST_2
2222:  	const 2		 //2: size of: list
2223:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2224:  	pop
// end of memcpy(src,dst,length)
2225:  	popn 2		 //2: size of: list
2226:  	const 1523		 //global address of main.temp25_LIST_2
// end of {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
// {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
2227:  	const 675		 //global address of metadata.ingress_metadata.vrf
2228:  	const 687		 //global address of metadata.ingress_metadata.bd
2229:  	const 703		 //global address of metadata.ingress_metadata.nexthop_index
// memcpy(src,dst,length)
2230:  	derefTop
2231:  	const 3		 //3: size of: list
2232:  	sub
2233:  	inc
2234:  	const 1525		 //global address of main.temp26_LIST_3
2235:  	const 3		 //3: size of: list
2236:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2237:  	pop
// end of memcpy(src,dst,length)
2238:  	popn 3		 //3: size of: list
2239:  	const 1525		 //global address of main.temp26_LIST_3
// end of {metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}
// memcpy(src,dst,length)
2240:  	derefTop
2241:  	const 1		 //1: size of: list
2242:  	sub
2243:  	inc
2244:  	const 1528		 //global address of main.temp27_LIST_1
2245:  	const 1		 //1: size of: list
2246:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2247:  	pop
// end of memcpy(src,dst,length)
2248:  	popn 1		 //1: size of: list
2249:  	const 1528		 //global address of main.temp27_LIST_1
// end of {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
2250:  	invoke 62 2		 // 62: label to ::computeChecksum, 2: size of: {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}, {{metadata.ingress_metadata.vrf,metadata.ingress_metadata.bd,metadata.ingress_metadata.nexthop_index}}
2251:  	pop
// DeparserImpl(packet_out, {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}})
2252:  	const 273		 //global address of packet_out
// {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
// {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
2253:  	const 719		 //global address of headers.ethernet.valid
2254:  	const 720		 //global address of headers.ethernet.size
2255:  	const 721		 //global address of headers.ethernet.srcAddr
2256:  	const 769		 //global address of headers.ethernet.dstAddr
2257:  	const 817		 //global address of headers.ethernet.etherType
// memcpy(src,dst,length)
2258:  	derefTop
2259:  	const 5		 //5: size of: list
2260:  	sub
2261:  	inc
2262:  	const 1529		 //global address of main.temp28_LIST_5
2263:  	const 5		 //5: size of: list
2264:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2265:  	pop
// end of memcpy(src,dst,length)
2266:  	popn 5		 //5: size of: list
2267:  	const 1529		 //global address of main.temp28_LIST_5
// end of {headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType}
// {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
2268:  	const 833		 //global address of headers.ipv4.valid
2269:  	const 834		 //global address of headers.ipv4.size
2270:  	const 835		 //global address of headers.ipv4.version
2271:  	const 839		 //global address of headers.ipv4.ihl
2272:  	const 843		 //global address of headers.ipv4.diffserv
2273:  	const 851		 //global address of headers.ipv4.totalLen
2274:  	const 867		 //global address of headers.ipv4.identification
2275:  	const 883		 //global address of headers.ipv4.flags
2276:  	const 886		 //global address of headers.ipv4.fragOffset
2277:  	const 899		 //global address of headers.ipv4.ttl
2278:  	const 907		 //global address of headers.ipv4.protocol
2279:  	const 915		 //global address of headers.ipv4.hdrChecksum
2280:  	const 931		 //global address of headers.ipv4.srcAddr
2281:  	const 963		 //global address of headers.ipv4.dstAddr
// memcpy(src,dst,length)
2282:  	derefTop
2283:  	const 14		 //14: size of: list
2284:  	sub
2285:  	inc
2286:  	const 1534		 //global address of main.temp29_LIST_14
2287:  	const 14		 //14: size of: list
2288:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2289:  	pop
// end of memcpy(src,dst,length)
2290:  	popn 14		 //14: size of: list
2291:  	const 1534		 //global address of main.temp29_LIST_14
// end of {headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}
// memcpy(src,dst,length)
2292:  	derefTop
2293:  	const 2		 //2: size of: list
2294:  	sub
2295:  	inc
2296:  	const 1548		 //global address of main.temp30_LIST_2
2297:  	const 2		 //2: size of: list
2298:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2299:  	pop
// end of memcpy(src,dst,length)
2300:  	popn 2		 //2: size of: list
2301:  	const 1548		 //global address of main.temp30_LIST_2
// end of {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
2302:  	invoke 346 2		 // 346: label to ::DeparserImpl, 2: size of: packet_out, {{headers.ethernet.valid,headers.ethernet.size,headers.ethernet.srcAddr,headers.ethernet.dstAddr,headers.ethernet.etherType},{headers.ipv4.valid,headers.ipv4.size,headers.ipv4.version,headers.ipv4.ihl,headers.ipv4.diffserv,headers.ipv4.totalLen,headers.ipv4.identification,headers.ipv4.flags,headers.ipv4.fragOffset,headers.ipv4.ttl,headers.ipv4.protocol,headers.ipv4.hdrChecksum,headers.ipv4.srcAddr,headers.ipv4.dstAddr}}
2303:  	pop
2304:  	const 0		 //0: main terminates with status OK
2305:  	return 
// end of definition of main()
//

// definition of isValid(hdr)
2306:  	load 0		 // 0: local address of hdr.valid
2307:  	getfield
2308:  	getfield
2309:  	return 
// end of definition of isValid(::isValid/hdr hdr)
//

// definition of extract()
// memcpy(packet.buffer, hdr, hdr.size)
// packet.buffer + packet.cursor
2310:  	load 0		 // 0: local address of packet
2311:  	const 1		 //1: size of: offset: cursor
2312:  	add
2313:  	load 0		 // 0: local address of packet
2314:  	getfield
2315:  	add
// contents of hdr
2316:  	load 1		 // 1: local address of hdr
2317:  	getfield
2318:  	const 2		 //2: size of: offset: validity bit, size
2319:  	add
// hdr.size
2320:  	load 1		 // 1: local address of hdr
2321:  	getfield
2322:  	const 1		 //1: size of: offset: validity bit
2323:  	add
2324:  	getfield
2325:  	invoke 1783 3		 // 1783: label to stdlib::memcpy, 3: size of: src, dst, length
2326:  	pop
// set validity to 1
2327:  	const 1		 //1: valid
2328:  	load 1		 // 1: local address of hdr
2329:  	getfield
2330:  	putfield
// packet.cursor = packet.cursor + 1
2331:  	load 0		 // 0: local address of packet.cursor
2332:  	getfield
2333:  	load 1		 // 1: local address of hdr
2334:  	getfield
2335:  	const 1		 //1: size of: offset: validity bit
2336:  	add
2337:  	getfield
2338:  	add
2339:  	load 0		 // 0: local address of packet.cursor
2340:  	putfield
2341:  	const 0		 //0: extract terminates with status OK
2342:  	return 
// end of definition of extract()
//

// definition of emit()
// TODO
2343:  	const 0		 //0: emit terminates with status OK
2344:  	return 
// end of definition of emit()
//

// definition of update_checksum()
// TODO
2345:  	const 0		 //0: update_checksum terminates with status OK
2346:  	return 
// end of definition of update_checksum()
//

// definition of verify_checksum()
// TODO
2347:  	const 0		 //0: verify_checksum terminates with status OK
2348:  	return 
// end of definition of verify_checksum()
//

// definition of mark_to_drop()
// TODO
2349:  	const 0		 //0: mark_to_drop terminates with status OK
2350:  	return 
// end of definition of mark_to_drop()
//

// definition of count()
// TODO
2351:  	const 0		 //0: count terminates with status OK
2352:  	return 
//  
// end of definition of count()
//

// definition of setInvalid()
// TODO
2353:  	const 0		 //0: setInvalid terminates with status OK
2354:  	return 
//  
// end of definition of setInvalid()
//

// definition of setValid()
// TODO
2355:  	const 0		 //0: setValid terminates with status OK
2356:  	return 
//  
// end of definition of setValid()
//

