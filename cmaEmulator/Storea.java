
public class Storea extends Instruction {

    public int q;

    Storea(int q) { this.q = q; }
    
    public void exec(CMA state) {
	state.stack[q] = state.stack[state.SP];
    }

    
}
