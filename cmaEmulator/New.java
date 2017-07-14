
public class New extends Instruction {

    public void exec(CMA state) {
	if (state.NP - state.stack[state.SP] <= state.EP) {
	    state.stack[state.SP] = 0;
	} else {
	    state.NP = state.NP - state.stack[state.SP];
	    state.stack[state.SP] = state.NP;
	}
    }

      @Override
    public String toString()
    {
        return "new";
    }
    
}
