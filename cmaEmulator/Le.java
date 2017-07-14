
public class Le extends Instruction {
    
    public void exec(CMA state) {
	state.SP--;
	state.stack[state.SP] =
	    state.stack[state.SP]<state.stack[state.SP+1] ? 1 : 0;
    }

      @Override
    public String toString()
    {
        return "le";
    }
}
