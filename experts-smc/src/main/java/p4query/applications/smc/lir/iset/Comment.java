package p4query.applications.smc.lir.iset;

import java.io.PrintStream;

public class Comment implements StackInstruction {

    private String comment;

    @Override
    public String toString() {
        return "Comment('" + comment + "')";
    }

    public Comment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toHumanReadable() {
        if(comment.isEmpty())
            return "";
        return comment ;
    }
}
