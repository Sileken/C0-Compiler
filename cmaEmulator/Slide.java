
public class Slide extends Instruction {
    public int k;

    Slide(int k) { this.k = k; }
    
    public void exec(CMA state) {
	int tmp = state.stack[state.SP];
	state.SP = state.SP - k;
	state.stack[state.SP] = tmp;
    }

    
}
