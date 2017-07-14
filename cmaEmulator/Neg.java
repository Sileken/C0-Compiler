
public class Neg extends Instruction {
    
    public void exec(CMA state) {
	state.stack[state.SP] = -state.stack[state.SP];
    }

      @Override
    public String toString()
    {
        return "neg";
    }
}
