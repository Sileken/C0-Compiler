
public class Loadr extends Instruction {

    public int j;

    Loadr(int j) { this.j = j; }
    
    public void exec(CMA state) {
	state.SP++;
	state.stack[state.SP] = state.stack[state.FP+j];;
    }

    
}
