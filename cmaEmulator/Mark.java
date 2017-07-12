
public class Mark extends Instruction {

    public void exec(CMA state) {
	state.stack[state.SP+1] = state.EP;
	state.stack[state.SP+2] = state.FP;
	state.SP = state.SP+2;
    }

    
}
