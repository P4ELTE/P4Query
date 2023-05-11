package p4query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import p4query.ontology.Dom;

public class CodeGen extends RecursiveTask<List<String>> {

    private final GraphTraversalSource graphTraversalSource;
    private Object currentId;
    private final Object endOfInclude;
    private final Integer threshold;

    

    public CodeGen(GraphTraversalSource graphTraversalSource, Object currentId,Object endOfInclude, Integer threshold) {
        this.graphTraversalSource = graphTraversalSource;
        this.currentId = currentId;
        this.endOfInclude = endOfInclude;
        this.threshold = threshold;
    }

    public List<String> getCode(){
        List<String> outputList = new ArrayList<>();       

        CodeGen task = new CodeGen(graphTraversalSource, currentId, endOfInclude, threshold);

        ForkJoinPool fjPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
	    Future<List<String>> f = fjPool.submit(task);
        try{
			outputList.addAll(f.get());
		}catch(Exception e){
            e.printStackTrace();
		}
		fjPool.shutdown();
		return outputList;
        
    }

    @Override
    protected List<String> compute() {
        List<String> outputList = new ArrayList<>();
        try{
            if(graphTraversalSource.V().has(Dom.Syn.V.NODE_ID, currentId).outE(Dom.SYN).order().by(Dom.Syn.E.ORD).inV().toList().size() > 0){
                List<Object> childrenIds = graphTraversalSource.V().has(Dom.Syn.V.NODE_ID, currentId).outE(Dom.SYN)
                    .order().by(Dom.Syn.E.ORD).inV().id().toList(); 

                List<Object> children = new ArrayList<>(); 
                for(Object id : childrenIds){
                    children.add(graphTraversalSource.V(id).values(Dom.Syn.V.NODE_ID).toList().get(0));  
                }                

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
                            if(isSubTreeBigEnough(graphTraversalSource, childrenIds.get(i), threshold)){
                                ForkJoinTask<List<String>> task = new CodeGen(graphTraversalSource,childId,endOfInclude, threshold).fork();
                                taskList.add(task);                            
                            }else{
                                simpleRecursiveList.add(simpleRecursiveGetCodeNodeID(graphTraversalSource, childId));
                                taskList.add(null);
                            }
                        }catch(Throwable e){
                            simpleRecursiveList.add(new ArrayList<String>());
                            taskList.add(null);
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
            } else{
                List<Object> classList = graphTraversalSource.V().has(Dom.Syn.V.NODE_ID, currentId).values(Dom.Syn.V.VALUE).toList();
                if(classList.size()>0){
                    outputList.add(classList.get(0).toString().replace("\\",""));
                }
            }
        } catch(Throwable t){
            return outputList;
        }
        return outputList;
    }

    private List<String> simpleRecursiveGetCodeNodeID(GraphTraversalSource g, Object cId){
        List<String> outputList = new ArrayList<>();
        try{
            int size = 0;
            try{
                Object actualId = g.V().has(Dom.Syn.V.NODE_ID, cId).id().toList().get(0);
                
                size = g.V(actualId).outE(Dom.SYN).order().by(Dom.Syn.E.ORD).inV().toList().size();
            }catch (Throwable t){
                size = 0;
            }
            if(size > 0){
                List<Object> childrenIds = g.V().has(Dom.Syn.V.NODE_ID, cId).outE(Dom.SYN)
                    .order().by(Dom.Syn.E.ORD).inV().id().toList(); 

                List<Object> children = new ArrayList<>(); 
                for(Object id : childrenIds){
                    children.add(graphTraversalSource.V(id).values(Dom.Syn.V.NODE_ID).toList().get(0));  
                }

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
                        }catch(Throwable e){
                            e.printStackTrace();
                        }                
                    }
                }
            }else{
                List<Object> classList = g.V().has(Dom.Syn.V.NODE_ID, cId).values(Dom.Syn.V.VALUE).toList();
                if(classList.size()>0){
                    outputList.add(classList.get(0).toString().replace("\\",""));
                }
            }
        } catch(Throwable e ){
            return outputList;
        }
        return outputList;
    }

    private boolean isSubTreeBigEnough(GraphTraversalSource gts, Object id, int threshold){        
        try{
            List<Object> idList = gts.V(id).outE(Dom.SYN).inV().id().toList();
            if(threshold <= 1){
                return true;
            }else{
                boolean isBigEnough = false;
                for (Object i : idList) {                    
                    isBigEnough = isBigEnough || isSubTreeBigEnough(gts, i, threshold-1);
                }
                return isBigEnough;
            }
        }catch(Throwable e){
            return false;
        }
    }    
}
