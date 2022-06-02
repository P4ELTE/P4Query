/**
 * Copyright 2020-2022, Dániel Lukács, Eötvös Loránd University.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Dániel Lukács, 2022
 */
package p4query.applications.smc.hir.externs;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import p4query.applications.smc.IPacket;
import p4query.applications.smc.Packet;
import p4query.applications.smc.TableEntry;
import p4query.applications.smc.hir.CompilerState;
import p4query.applications.smc.hir.externs.implem.*;
import p4query.applications.smc.hir.p4api.Declaration;
import p4query.applications.smc.hir.p4api.Definition;
import p4query.applications.smc.hir.p4api.TableDeclaration;

public class V1ModelUseCase implements IUseCase {

    private final IPacket packet;
    private final Map<String, TableEntry[]> tableContents;

    public V1ModelUseCase(final IPacket packet, final Map<String, TableEntry[]> tableContents) {
        this.packet = packet;
        this.tableContents = tableContents;
    }

    @Override
    public void addMemory(final CompilerState state) {
      int bufferSize = getBufferSize();
      state.getGlobals().addFirst(new PacketOut(bufferSize, state.getTypeFactory()));
      state.getGlobals().addFirst(new PacketIn(bufferSize, state.getTypeFactory()));
    }

    @Override
    public LinkedHashMap<Declaration, Definition> linkDefinitions(final Collection<Declaration> decls, final CompilerState st) {

      final LinkedHashMap<Declaration, Definition> map = new LinkedHashMap<>();

      for (final Declaration decl : decls) {

        if(decl instanceof TableDeclaration){
            TableEntry[] contents = tableContents.get(decl.getName());
            if (contents == null) {
                throw new IllegalStateException(
                    String.format("No content-definition found for table %s.",
                                  decl.getName()));
            }
            try {

              final TableApplicationImpl tapp = new TableApplicationImpl(decl, toEntryActionNameMapping(contents));
              tapp.init(st);
              map.put(decl, tapp);
            } catch (final UnableToLinkDeclaration e) {
              throw new IllegalStateException(e);
            }
            continue;
        }
        Definition def = null;

        for (int i = 0; i < 15; i++) {
          try {
            switch(i){
              case 0: def = new IsValidImpl(decl); break;
              case 1: def = new MarkToDropImpl(decl); break;
              case 2: def = new MemCmpImpl(decl); break;
              case 3: def = new MemCpyImpl(decl); break;
              case 4: def = new PacketInExtractImpl(decl); break;
              case 5: def = new PacketOutEmitImpl(decl); break;
              case 6: def = new ReceivePacketImpl(decl, packet); break;
              case 7: def = new UpdateChecksum(decl); break;
              case 8: def = new VerifyChecksum(decl); break;
              case 9: def = new V1ModelMain(decl); break;
              case 10: def = new SubtractImpl(decl); break;
              case 11: def = new CounterCountImpl(decl); break;
              case 12: def = new SetInvalidImpl(decl); break;
              case 13: def = new SetValidImpl(decl); break;
              case 14: def = new FillTablesImpl(decl, tableContents); break;
            }
          } catch(final UnableToLinkDeclaration e){
            continue;
          }
        }

        if(def != null){
          def.init(st);
          map.put(decl, def);
        }
      }

      return map;

    }



    @Override
    public int getBufferSize() {
        return packet.getSize();
    }

    private static String[] toEntryActionNameMapping(TableEntry[] contents){
       return Arrays.stream(contents)
                    .map(te -> te.getActionName())
                    .toArray(n -> new String[n]);
    }

}
    
