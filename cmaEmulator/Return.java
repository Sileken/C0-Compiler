
public class Return extends Instruction {

    public void exec(CMA state) throws StackOverflow {
	state.PC = state.stack[state.FP];
	state.EP = state.stack[state.FP-2];
	if (state.EP >= state.NP) throw new StackOverflow();
	state.SP = state.FP-3;
	state.FP = state.stack[state.SP+2];
    }

    
}
