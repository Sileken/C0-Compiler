
public class Not extends Instruction {
    
    public void exec(CMA state) {
	state.stack[state.SP] = 1-state.stack[state.SP];
    }

}
