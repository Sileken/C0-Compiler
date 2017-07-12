
public class Store extends Instruction {
    
    public void exec(CMA state) {
	state.stack[state.stack[state.SP]] = state.stack[state.SP-1];
	state.SP--;
    }

    
}
