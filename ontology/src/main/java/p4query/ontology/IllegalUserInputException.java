package p4query.ontology;

public class IllegalUserInputException extends Exception {
   private boolean error;

   public IllegalUserInputException(Throwable t){
      super(t);
   }

   public IllegalUserInputException(String msg){
        super(msg);
        this.error = error;
   }

   public boolean isError() {
      return error;
   }
    
}
