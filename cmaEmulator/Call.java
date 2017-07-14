
public class Call extends Instruction {

    public void exec(CMA state) {
	int tmp = state.stack[state.SP];
	state.stack[state.SP] = state.PC;
	state.FP = state.SP;
	state.PC = tmp;
    }
 
    @Override
    public String toString()
    {
        return "call";
    }
    
}
