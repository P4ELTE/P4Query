package p4analyser;

/**
 * Hello world!
 *
 */
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

    public static void analyse(GraphTraversalSource g)
    {
        List<Object> assignmentsLeft = new ArrayList<Object>;
        List<Object> assignmentsRight = new ArrayList<Object>;

        // Queries
        assignmentsLeft = g.V().until(has(Dom.Syn.V.CLASS, "AssignMentOrMethodCallStatementContext"))
            .repeat().until(has(Dom.Syn.V.CLASS, "LValueContext"))
            .repeat().until(has(Dom.Syn.V.VALUE, "dstAddr"))
            .properties(Dom.Syn.V.NODEID).ToList();

        assignmentsRight = g.V().until(has(Dom.Syn.V.CLASS, "AssignMentOrMethodCallStatementContext"))
        .repeat().until(has(Dom.Syn.V.CLASS, "ExpressionContext"))
        .repeat().until(has(Dom.Syn.V.VALUE, "dstAddr"))
        .properties(Dom.Syn.V.NODEID).ToList();

        // Left assignments output
        if(assignmentsLeft.size() > 0)
        {
            for(int i=0; i<nodes.size(); i++)
            {
                System.out.println("Left assignment " + i+1 + ": " + assignmentsLeft.get(i));
            }
        }
        else
        {
            System.out.println("No left assignment found.");
        }

        // Right assignments output
        if(assignmentsRight.size() > 0)
        {
            for(int i=0; i<nodes.size(); i++)
            {
                System.out.println("Right assignment " + i+1 + ": " + assignmentsRight.get(i));
            }
        }
        else
        {
            System.out.println("No right assignment found.");
        }

        // Completed
        System.out.println("Task finished.");
    }
}
