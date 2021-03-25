package p4query.applications.smc.hir.iset;

import java.util.LinkedList;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import p4query.applications.smc.hir.typing.IRType;
import p4query.applications.smc.lir.iset.StackInstruction;
import p4query.applications.smc.hir.GlobalMemoryLayout;
import p4query.applications.smc.hir.LocalMemoryLayout;
import p4query.applications.smc.hir.ProcedureDefinition;
import p4query.applications.smc.hir.exprs.Expression;

public class ProcedureCall implements Instruction {

   private Expression expression;
   private Vertex src;

   ProcedureCall(GraphTraversalSource g, Vertex src, String vClass, IRType.SingletonFactory typeFactory, ProcedureDefinition parDef) {
      this.src = src;
      this.expression = Expression.Factory.create(g, src, typeFactory, parDef, -1);
   }

   @Override
   public String toString() {
      return "ProcedureCall [expression=" + expression + "]";
   }

   @Override
   public List<StackInstruction> compileToLIR(LocalMemoryLayout local, GlobalMemoryLayout global) {
      return expression.compileToLIR(local, global);
   }

   @Override
   public Vertex getOrigin() {
      return src;
   }

   
}
