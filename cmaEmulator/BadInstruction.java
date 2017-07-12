
public class BadInstruction extends Exception {
    BadInstruction(String cmd, int lineno) {
	super("Bad instruction "+cmd+" at line number " + lineno);
    }
    
}
