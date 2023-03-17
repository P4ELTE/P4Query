package p4query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import p4query.ontology.Dom;

public class CodeGenV2 extends RecursiveTask<List<String>> {

    private GraphTraversalSource graphTraversalSource;
    private Object currentId;
    private Object endOfInclude;
    private Integer threshold;

    

    public CodeGenV2(GraphTraversalSource graphTraversalSource, Object currentId,Object endOfInclude, Integer threshold) {
        this.graphTraversalSource = graphTraversalSource;
        this.currentId = currentId;
        this.endOfInclude = endOfInclude;
        this.threshold = threshold;
    }

    public List<String> getCode(){
        List<String> outputList = new ArrayList<>();       

        CodeGenV2 task = new CodeGenV2(graphTraversalSource, currentId, endOfInclude, threshold);

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

        List<Object> children = graphTraversalSource.V(currentId).outE(Dom.SYN)
            .order().by(Dom.Syn.E.ORD).inV().id().toList();  

        if(children.contains(endOfInclude)){
            children = graphTraversalSource.V(endOfInclude).id().toList();
        }
        if(children.size() == 0) {
            List<Object> classList = graphTraversalSource.V(currentId).values(Dom.Syn.V.VALUE).toList();
            if(classList.size()>0){
                outputList.add(classList.get(0).toString().replace("\\",""));
            }
        }else{
            List<ForkJoinTask<List<String>>> taskList = new ArrayList<>();
            List<List<String>> simpleRecursiveList = new ArrayList<>();
            for(int i = 0; i < children.size(); ++i){
                Object childId = children.get(i);                
                try{                        
                    if(isSubTreeBigEnough(graphTraversalSource, childId, threshold)){
                        ForkJoinTask<List<String>> task = new CodeGenV2(graphTraversalSource,childId,endOfInclude, threshold).fork();
                        taskList.add(task);                            
                    }else{
                        simpleRecursiveList.add(simpleRecursiveGetCode(graphTraversalSource, childId));
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

    private List<String> simpleRecursiveGetCode(GraphTraversalSource g, Object cId){
        List<String> outputList = new ArrayList<>();
        try{
            List<Object> children = g.V(cId).outE(Dom.SYN)
                .order().by(Dom.Syn.E.ORD).inV().id().toList();

            if(children.contains(endOfInclude)){
                children = g.V(endOfInclude).id().toList();
            }
            if(children.size() == 0) {
                List<Object> classList = g.V(cId).values(Dom.Syn.V.VALUE).toList();
                if(classList.size()>0){
                    outputList.add(classList.get(0).toString().replace("\\",""));
                }
            }else{
                for(int i = 0; i < children.size(); ++i){
                    Object childId = children.get(i);
                    try{                        
                        outputList.addAll(simpleRecursiveGetCode(g, childId));
                    }catch(Exception e){
                        System.out.println("in children");
                        e.printStackTrace();
                    }
                }
            }
        }catch(Exception e){
            System.out.println("in children");
            e.printStackTrace();
        }

        return outputList;
    }

    private boolean isSubTreeBigEnough(GraphTraversalSource gts, Object id, int threshold){
        try{
            List<Object> idList = gts.V(id).outE(Dom.SYN).order().by(Dom.Syn.E.ORD).inV().id().toList();
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
            return false;
        }
    }
}
