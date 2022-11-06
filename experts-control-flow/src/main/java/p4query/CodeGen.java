package p4query;

import java.util.ArrayList;
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

        CodeGen task = new CodeGen(graphTraversalSource, 0, endOfInclude);

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

        //Object currentNodeId = graphTraversalSource.V(currentId).values(Dom.Syn.V.NODE_ID).toList().get(0);


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
            ArrayList<Object> checked = new ArrayList<>();
            List<ForkJoinTask<List<String>>> taskList = new ArrayList<>();
            for(int i = 0; i < children.size(); ++i){
                Object childId = children.get(i);
                if(!checked.contains(childId)){
                    checked.add(childId);
                    try{
                        ForkJoinTask<List<String>> task = new CodeGen(graphTraversalSource,childId,endOfInclude).fork();
                        taskList.add(task);
                    }catch(Exception e){
                        System.out.println("in children");
                        e.printStackTrace();
                    }
                }
            }
            taskList.forEach(task -> {
                try {
                    outputList.addAll(task.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }

        return outputList;
    }

    public GraphTraversalSource getGraphTraversalSource() {
        return graphTraversalSource;
    }

    public void setGraphTraversalSource(GraphTraversalSource graphTraversalSource) {
        this.graphTraversalSource = graphTraversalSource;
    }

    public Object getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Object currentId) {
        this.currentId = currentId;
    }

    public Object getEndOfInclude() {
        return endOfInclude;
    }

    public void setEndOfInclude(Object endOfInclude) {
        this.endOfInclude = endOfInclude;
    }

    
}
