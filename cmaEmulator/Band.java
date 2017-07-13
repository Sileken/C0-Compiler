
public class Band extends Instruction {
    
    public void exec(CMA state) {
	state.SP--;
	state.stack[state.SP] = state.stack[state.SP] & state.stack[state.SP+1];
    }

}
