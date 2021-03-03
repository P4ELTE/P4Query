package p4analyser;

public class ExpertHI 
{
    public static void main( String[] args )
    {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String remoteTraversalSourceName =args[2];

        GraphTraversalSource g = AnonymousTraversalSource.traversal()
            .withRemote(DriverRemoteConnection.using(host, port, remoteTraversalSourceName));
        
        analyse(g);

        System.out.println("OK");
    }
	
	public static void analyse(GraphTraversalSource g,
                                String _dstAddr)
    {
        List<Object> assignmentsLeft = new ArrayList<Object>;
        List<Object> assignmentsRight = new ArrayList<Object>;

        // Queries
        assignmentsleft = g.V().out().has('Dom.Syn.V.CLASS', "AssignMentOrMethodCallStatementContext")
            outE(Dom.Syn).has(Dom.Syn.E.RULE, "lvalue")
            .inV().has(Dom.Syn.V.VALUE, _dstAddr)
            .repeat(.out(Dom.SYN))
            .until(.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .values(Dom.Syn.V.LINE).toList();
        
        assignmentsRight = g.V().out().has('Dom.Syn.V.CLASS', "AssignMentOrMethodCallStatementContext")
            .outE(Dom.Syn).has(Dom.Syn.E.RULE, "expression")
            .inV().has(Dom.Syn.V.VALUE, _dstAddr)
            .repeat(.out(Dom.SYN))
            .until(.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .values(Dom.Syn.V.LINE).toList();
    }

    public static void analyse(GraphTraversalSource g,
                                String _hdr,
                                String _ethernet,
                                String _dstAddr)
    {
        List<Object> assignmentsLeft = new ArrayList<Object>;
        List<Object> assignmentsRight = new ArrayList<Object>;

        // Queries
        assignmentsleft = g.V().out().has('Dom.Syn.V.CLASS', "AssignMentOrMethodCallStatementContext")
            outE(Dom.Syn).has(Dom.Syn.E.RULE, "lvalue")
            .inV().has(Dom.Syn.V.VALUE, _hdr)
            .inV().has(Dom.Syn.V.VALUE, _ethernet)
            .inV().has(Dom.Syn.V.VALUE, _dstAddr)
            .repeat(.out(Dom.SYN))
            .until(.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .values(Dom.Syn.V.LINE).toList();
        
        assignmentsRight = g.V().out().has('Dom.Syn.V.CLASS', "AssignMentOrMethodCallStatementContext")
            .outE(Dom.Syn).has(Dom.Syn.E.RULE, "expression")
            .inV().has(Dom.Syn.V.VALUE, _hdr)
            .inV().has(Dom.Syn.V.VALUE, _ethernet)
            .inV().has(Dom.Syn.V.VALUE, _dstAddr)
            .repeat(.out(Dom.SYN))
            .until(.has(Dom.Syn.V.CLASS, "TerminalNodeImpl"))
            .values(Dom.Syn.V.LINE).toList();
    }
}
