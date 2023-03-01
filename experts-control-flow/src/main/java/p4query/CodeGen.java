package p4query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import p4query.ontology.Dom;

public class CodeGen extends RecursiveTask<List<String>> {

    private GraphTraversalSource graphTraversalSource;
    private Object currentId;
    private Object endOfInclude;

    

    public CodeGen(GraphTraversalSource graphTraversalSource, Object currentId,Object endOfInclude) {
        this.graphTraversalSource = graphTraversalSource;
        this.currentId = currentId;
        this.endOfInclude = endOfInclude;
    }

    public List<String> getCode(){
        List<String> outputList = new ArrayList<>();       

        CodeGen task = new CodeGen(graphTraversalSource, currentId, endOfInclude);

        ForkJoinPool fjPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
	    Future<List<String>> f = fjPool.submit(task);
        try{
			outputList.addAll(f.get());
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		fjPool.shutdown();
		return outputList;
        
    }

    @Override
    protected List<String> compute() {
        List<String> outputList = new ArrayList<>();

        List<Object> children = graphTraversalSource.V().has(Dom.Syn.V.NODE_ID, currentId).outE(Dom.SYN)
            .order().by(Dom.Syn.E.ORD).inV().values(Dom.Syn.V.NODE_ID).toList();  

        if(children.contains(endOfInclude)){
            children = Arrays.asList(endOfInclude);
        }

        if(children.size() == 0) {
            List<Object> classList = graphTraversalSource.V().has(Dom.Syn.V.NODE_ID, currentId).values(Dom.Syn.V.VALUE).toList();
            if(classList.size()>0){
                outputList.add(classList.get(0).toString().replace("\\",""));
            }
        }else{
            List<ForkJoinTask<List<String>>> taskList = new ArrayList<>();
            List<List<String>> simpleRecursiveList = new ArrayList<>();
            for(int i = 0; i < children.size(); ++i){
                Object childId = children.get(i);
                try{                        
                    if(isSubTreeBigEnoughNodeID(graphTraversalSource, childId, 5)){
                        ForkJoinTask<List<String>> task = new CodeGen(graphTraversalSource,childId,endOfInclude).fork();
                        taskList.add(task);                            
                    }else{
                        simpleRecursiveList.add(simpleRecursiveGetCodeNodeID(graphTraversalSource, childId));
                        taskList.add(null);
                    }
                }catch(Exception e){
                    System.out.println("in children");
                    e.printStackTrace();
                }
                
            }
            taskList.forEach(task -> {
                try {
                    if(task != null){
                        outputList.addAll(task.get());
                    }else{                        
                        outputList.addAll(simpleRecursiveList.remove(0));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
        return outputList;
    }

    //Full fjp
    // @Override
    // protected List<String> compute() {
    //     List<String> outputList = new ArrayList<>();

    //     //Object currentNodeId = graphTraversalSource.V(currentId).values(Dom.Syn.V.NODE_ID).toList().get(0);


    //     List<Object> children = graphTraversalSource.V(currentId).outE(Dom.SYN)
    //         .order().by(Dom.Syn.E.ORD).inV().id().toList();  

    //     if(children.contains(endOfInclude)){
    //         children = graphTraversalSource.V(endOfInclude).id().toList();
    //     }
    //     if(children.size() == 0) {
    //         List<Object> classList = graphTraversalSource.V(currentId).values(Dom.Syn.V.VALUE).toList();
    //         if(classList.size()>0){
    //             outputList.add(classList.get(0).toString().replace("\\",""));
    //         }
    //     }else{
    //         ArrayList<Object> checked = new ArrayList<>();
    //         List<ForkJoinTask<List<String>>> taskList = new ArrayList<>();
    //         for(int i = 0; i < children.size(); ++i){
    //             Object childId = children.get(i);
    //             if(!checked.contains(childId)){
    //                 checked.add(childId);
    //                 try{
    //                     ForkJoinTask<List<String>> task = new CodeGen(graphTraversalSource,childId,endOfInclude).fork();
    //                     taskList.add(task);
    //                 }catch(Exception e){
    //                     System.out.println("in children");
    //                     e.printStackTrace();
    //                 }
    //             }
    //         }
    //         taskList.forEach(task -> {
    //             try {
    //                 outputList.addAll(task.get());
    //             } catch (InterruptedException | ExecutionException e) {
    //                 e.printStackTrace();
    //             }
    //         });
    //     }

    //     return outputList;
    // }

    private List<String> simpleRecursiveGetCodeNodeID(GraphTraversalSource g, Object cId){
        List<String> outputList = new ArrayList<>();

        List<Object> children = g.V().has(Dom.Syn.V.NODE_ID, cId).outE(Dom.SYN)
            .order().by(Dom.Syn.E.ORD).inV().values(Dom.Syn.V.NODE_ID).toList();

        if(children.contains(endOfInclude)){
            children = Arrays.asList(endOfInclude);
        }

        if(children.size() == 0) {
            List<Object> classList = g.V().has(Dom.Syn.V.NODE_ID, cId).values(Dom.Syn.V.VALUE).toList();
            if(classList.size()>0){
                outputList.add(classList.get(0).toString().replace("\\",""));
            }
        }else{
            for(int i = 0; i < children.size(); ++i){
                Object childId = children.get(i);
                try{                        
                    outputList.addAll(simpleRecursiveGetCodeNodeID(g, childId));
                }catch(Exception e){
                    System.out.println("in children");
                    e.printStackTrace();
                }                
            }
        }
        return outputList;
    }

    private boolean isSubTreeBigEnoughNodeID(GraphTraversalSource gts, Object id, int threshold){
        List<Object> idList = gts.V().has(Dom.Syn.V.NODE_ID, id).outE(Dom.SYN).order().by(Dom.Syn.E.ORD).inV().values(Dom.Syn.V.NODE_ID).toList();
        try{
            if(threshold == 1){
                return true;
            }else{
                boolean isBigEnough = false;
                for (Object i : idList) {                    
                    isBigEnough = isBigEnough || isSubTreeBigEnough(gts, i, threshold-1);
                }
                return isBigEnough;
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean isSubTreeBigEnough(GraphTraversalSource gts, Object id, int threshold){
        List<Object> idList = gts.V(id).outE(Dom.SYN).order().by(Dom.Syn.E.ORD).inV().id().toList();
        try{
            if(threshold == 1){
                return true;
            }else{
                boolean isBigEnough = false;
                for (Object i : idList) {                    
                    isBigEnough = isBigEnough || isSubTreeBigEnough(gts, i, threshold-1);
                }
                return isBigEnough;
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }    
}
