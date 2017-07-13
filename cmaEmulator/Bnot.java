
public class Bnot extends Instruction {
    
    public void exec(CMA state) {
	state.stack[state.SP] = ~ (state.stack[state.SP]);
    }

}
