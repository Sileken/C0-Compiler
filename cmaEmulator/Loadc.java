
public class Loadc extends Instruction {
    public int q;
    public String arg; // for function label resolution
    
    Loadc(int q) { this.q = q; }
    Loadc(String arg) { this.arg = arg; }
    
    public boolean loadc() {
	return true;
    }
    public void exec(CMA state) {	
	state.SP++;
	state.stack[state.SP] = q;
    }

    
}
