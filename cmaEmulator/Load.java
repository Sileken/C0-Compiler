
public class Load extends Instruction {

    
    public void exec(CMA state) {
	state.stack[state.SP] = state.stack[state.stack[state.SP]];
    }

      @Override
    public String toString()
    {
        return "load";
    }
    
}
