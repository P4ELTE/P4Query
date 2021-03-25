package p4query.applications.smc.hir;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import p4query.applications.smc.hir.typing.ExternDataType;
import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.hir.typing.Struct;

// note: this is somewhat different from GlobalMemoryLayout, because two parameters may have the same name
//
public class LocalMemoryLayout  {

  private final List<Segment> layout = new LinkedList<>();
  private final LinkedHashMap<String, Segment> index = new LinkedHashMap<>();

  public LocalMemoryLayout(LinkedHashMap<String, IRType> parameters) {
      int currAddr = 0;
      for (Map.Entry<String, IRType> entry : parameters.entrySet()) {
          String parName = entry.getKey();    
          IRType parType = entry.getValue();    

          int size  = 1 ; // reference

     // everything must be size 1 on the function stack. even action parameters will be stored on the global space.
     //     int size;
     //     if(parType instanceof Struct){
     //         size = 1; // reference
     //     } else if(parType instanceof ExternDataType){
     //         size = 1; // reference
     //     } else {
     //         size = parType.getSize();
     //     }

          Segment segment = new Segment(parType, currAddr, "", parName, true);
          layout.add(segment);
          index.put(parName, segment);
          currAddr += size;
      }
  }
  

    public Segment lookupSegmentByName(String firstFieldName) {
        return index.get(firstFieldName);
    }

    @Override
    public String toString() {
        return "LocalMemoryLayout [index=" + index + "]";
    }
}
