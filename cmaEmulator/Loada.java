
public class Loada extends Instruction {

    public int q;

    Loada(int q) { this.q = q; }
    
    public void exec(CMA state) {
	state.SP++;
	state.stack[state.SP] = state.stack[q];
    }

    
}
