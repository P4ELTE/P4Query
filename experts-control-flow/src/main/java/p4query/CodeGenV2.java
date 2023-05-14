package p4query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import p4query.ontology.Dom;

// A CodeGenV2 osztály az id alapú bejárást alkalmazza alap folyamatok bemutatására.
// RecursiveTask-ból származik, mivel a compute-nak van visszatérési értéke (nem void).
public class CodeGenV2 extends RecursiveTask<List<String>> {

    private final GraphTraversalSource graphTraversalSource;
    private Object currentId;
    private final Object endOfInclude;
    private final Integer threshold;

    

    public CodeGenV2(GraphTraversalSource graphTraversalSource, Object currentId,Object endOfInclude, Integer threshold) {
        this.graphTraversalSource = graphTraversalSource;
        this.currentId = currentId;
        this.endOfInclude = endOfInclude;
        this.threshold = threshold;
    }

    // Ezt a metódust hívom meg kívülről, ezzel kezdem a bejárást. Itt létrehozza a taskot, amit submit-elek, végül összeszedi az outputList-be az eredményt.
    public List<String> getCode(){
        List<String> outputList = new ArrayList<>();       

        CodeGenV2 task = new CodeGenV2(graphTraversalSource, currentId, endOfInclude, threshold);

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

    // Ha még van gyereke az adott csúcsnak (ahol tart), akkor végigiterál a gyerekein. 
    // Amennyiben az isSubTreeBigEnough segédfüggvény szerint elég nagy a részfa, akkor fork-olni kell ezen gyerek részfájának bejárására. 
    // Ha nem, akkor egy szálon történik a maradék részfa számolása. 
	// Ezek eredményei rendre a taskList és simpleRecursiveList listában vannak annyi csavarral, hogy ha egy szálon lesz a futás, 
    // akkor a taskList-be null kerül, ezzel jelezve a listák összefűzésénél, hogy itt a következő lista a simpleRecursiveList-ből kerüljön be.
    @Override
    protected List<String> compute() {
        List<String> outputList = new ArrayList<>();
        try{
            if(graphTraversalSource.V(currentId).outE(Dom.SYN).order().by(Dom.Syn.E.ORD).inV().toList().size() > 0){
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
                        }catch(Throwable t){
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
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            } else{
                List<Object> classList = graphTraversalSource.V(currentId).values(Dom.Syn.V.VALUE).toList();
                if(classList.size()>0){
                    outputList.add(classList.get(0).toString().replace("\\",""));
                }
            }
        }catch(Throwable t){
            return outputList;
        }

        return outputList;
    }

    // Az egyszálú rekurzív gráfbejárás itt megy végbe. Hasonló elveket használ, mint a recursiveWriteV1
    private List<String> simpleRecursiveGetCode(GraphTraversalSource g, Object cId){
        List<String> outputList = new ArrayList<>();
        try{
            if(g.V(cId).outE(Dom.SYN).order().by(Dom.Syn.E.ORD).inV().toList().size() > 0){
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
                        }catch(Throwable t){
                            t.printStackTrace();
                        }
                    }
                }
            } else{
                System.out.println( g.V(cId).values(Dom.Syn.V.VALUE).toList().get(0));
                List<Object> classList = g.V(cId).values(Dom.Syn.V.VALUE).toList();
                if(classList.size()>0){
                    outputList.add(classList.get(0).toString().replace("\\",""));
                }
            }
        }catch(Throwable t){
            return outputList;
        }

        return outputList;
    }

    // Segédfüggvény, ami vizsgálja, hogy az adott gyökerű részfa elég nagy-e ahhoz, hogy fork-oljunk rá.
    private boolean isSubTreeBigEnough(GraphTraversalSource gts, Object id, int threshold){
        try{
            List<Object> idList = gts.V(id).outE(Dom.SYN).order().by(Dom.Syn.E.ORD).inV().id().toList();
            if(threshold <= 1){
                return true;
            }else{
                boolean isBigEnough = false;
                for (Object i : idList) {                    
                    isBigEnough = isBigEnough || isSubTreeBigEnough(gts, i, threshold-1);
                }
                return isBigEnough;
            }
            
        }catch(Throwable t){
            return false;
        }
    }
}
