package p4query.applications.smc.hir;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import groovyjarjarantlr.collections.Stack;
import p4query.applications.smc.hir.iset.Instruction;
import p4query.applications.smc.lir.iset.Comment;
import p4query.applications.smc.lir.iset.Exit;
import p4query.applications.smc.lir.iset.InterProcJumping;
import p4query.applications.smc.lir.iset.IntraProcJumping;
import p4query.applications.smc.lir.iset.ProbabilisticInstruction;
import p4query.applications.smc.lir.LabelledStackInstruction;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.lir.typing.Label;
import p4query.applications.smc.lir.typing.UnresolvedInstructionLabel;
import p4query.applications.smc.lir.typing.UnresolvedNameLabel;
import p4query.applications.smc.lir.typing.UnresolvedVertexLabel;

public class InstructionLayout {

    private List<LabelledStackInstruction> insts;

    private InstructionLayout(List<LabelledStackInstruction> insts) {
        this.insts = insts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        for (LabelledStackInstruction inst : insts) {
           sb.append("  ");
           if(inst.getLabel() == null){
           } else {
             sb.append(inst.getLabel()) ;
             sb.append(" : ");
           }
           sb.append(inst.getInst()); 
           sb.append(System.lineSeparator());
        }
        return "InstructionLayout [insts={" + sb.toString() + "}]";
    }

    public static class Builder {

        private Map<Vertex, List<StackInstruction>> origins = new HashMap<>();  

        private Map<Vertex, Vertex> cjmps = new HashMap<>();
        private Map<Vertex, Vertex> jmps = new HashMap<>();

        private LinkedHashMap<StackInstruction, Integer> index = new LinkedHashMap<>();

        private Map<String, StackInstruction> procStarts = new HashMap<>();

        public void register(Vertex v, StackInstruction inst){
            List<StackInstruction> insts = origins.get(v);
            if(insts == null){
            insts = new LinkedList<>();
            origins.put(v, insts);
            }
            insts.add(inst);
        }

        public void registerAllCondJumps(Map<Vertex, Vertex> cjmps){
            this.cjmps.putAll(cjmps);
        }
        public void registerAllJumps(Map<Vertex, Vertex> jmps){
            this.jmps.putAll(jmps);
        }

        public void registerAll(Vertex v, List<StackInstruction> inst){
            List<StackInstruction> insts = origins.get(v);
            if(insts == null){
            insts = new LinkedList<>();
            origins.put(v, insts);
            }
            insts.addAll(inst);
        }

        // TODO namespace
		public void registerProc(Definition procDef, StackInstruction inst) {
            procStarts.put(procDef.getName(), inst);
		}

        // TODO this should make a deep copy
        public InstructionLayout build(List<StackInstruction> insts){
            indexInstructions(insts);
            resolveIntraProcJumps(insts);
            resolveInterProcJumps(insts);

            List<LabelledStackInstruction> insts2 = new LinkedList<>();
            for (StackInstruction inst : insts) {
                insts2.add(new LabelledStackInstruction(index.get(inst), inst));
            }
            return new InstructionLayout(insts2);
        }

        private void indexInstructions(List<StackInstruction> insts){
            int idx = 0;
            for (StackInstruction inst : insts) {
                if(inst instanceof Comment) 
                    continue;

                index.put(inst, idx);
                idx += 1;
            }
        }

        private void resolveIntraProcJumps(List<StackInstruction> insts){
            for (StackInstruction inst : insts) {
                resolveIntraProcJumps(insts, inst);
            }
        }

        private void resolveIntraProcJumps(List<StackInstruction> insts, StackInstruction inst) {
            // NOTE: this is where it would be nice to have multiple dispatch in JAVA

            if(inst instanceof ProbabilisticInstruction){
                ProbabilisticInstruction pinst = (ProbabilisticInstruction) inst;
                for (StackInstruction subInst : pinst.getInstructions()) {
                    resolveIntraProcJumps(insts, subInst);
                }
                return;
            }

            if(!(inst instanceof IntraProcJumping))
                return;

            IntraProcJumping jmp = (IntraProcJumping) inst;
            if(jmp.getDest() instanceof UnresolvedVertexLabel){
                UnresolvedVertexLabel dest = (UnresolvedVertexLabel) jmp.getDest();
                Vertex dstVert = findDstVert(dest);

    //            System.out.println(origins);
    //            System.out.println(jmp);
    //            System.out.println(cjmps);

                StackInstruction targetInst = getFirst(dstVert);
                int labNo = findLabNo(insts, targetInst);
                jmp.setDest(new Label(labNo, dest.getComment()));
            } else if (jmp.getDest() instanceof UnresolvedInstructionLabel){
                UnresolvedInstructionLabel dest = (UnresolvedInstructionLabel) jmp.getDest();
                StackInstruction targetInst = dest.getTarget();

                int labNo = findLabNo(insts, targetInst);
                jmp.setDest(new Label(labNo, dest.getComment()));
            }
        }

        private Vertex findDstVert(UnresolvedVertexLabel dest) {
                Vertex srcVert = (Vertex) dest.getVertex();

                Vertex dstVert = this.cjmps.get(srcVert);

                if(dstVert == null)
                    dstVert = this.jmps.get(srcVert);

                if(dstVert == null)
                    throw new IllegalStateException("Unable to find jump target for vertex " + srcVert);

                return dstVert;
        }

        private int findLabNo(List<StackInstruction> insts, StackInstruction targetInst){
                if(targetInst instanceof Comment){
                    Iterator<StackInstruction> it = insts.iterator();

                    // find that comment
                    while(!it.next().equals(targetInst)){}

                    // find the first non-comment instruction after the comment
                    while(targetInst instanceof Comment){
                        targetInst = it.next();
                    }
                }

                if(!index.containsKey(targetInst)){
                    throw new IllegalArgumentException("insruction not indexed before: " + targetInst);
                }
                int labNo = index.get(targetInst);
                return labNo;
        }


        private StackInstruction getFirst(Vertex v){
            List<StackInstruction> insts = origins.get(v);
            if(insts == null)
                throw new IllegalStateException("No instructions registered for vertex " + v);
            return insts.get(0);
        }

        private void resolveInterProcJumps(List<StackInstruction> insts){
            for (StackInstruction inst : insts) {
                if(!(inst instanceof InterProcJumping))
                    continue;

                InterProcJumping jmp = (InterProcJumping) inst;

                if(!(jmp.getDest() instanceof UnresolvedNameLabel)){
                    continue;
                }
                UnresolvedNameLabel dest = (UnresolvedNameLabel) jmp.getDest();

                StackInstruction destInst = procStarts.get(dest.getName());
                if(destInst == null){
                    System.err.println("Warning: no declaration registered for procedure " + dest.getName());
                    continue;
                }

                int labNo = findLabNo(insts, destInst);
                jmp.setDest(new Label(labNo, dest.getNamespace() + "::" + dest.getName()));
            }
        }



    }

    public List<String> toHumanReadable(){
        LinkedList<String> strs = new LinkedList<>();
        for (LabelledStackInstruction inst : insts) {
            strs.add(inst.toHumanReadable());
        }
        return strs;
    }
}
