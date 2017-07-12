
public class Enter extends Instruction {
    public int q;

    Enter(int q) { this.q = q; }
    
    public void exec(CMA state) throws StackOverflow {
	state.EP = state.SP + q;
	if (state.EP >= state.NP)
	    throw new StackOverflow();
    }

    
}
