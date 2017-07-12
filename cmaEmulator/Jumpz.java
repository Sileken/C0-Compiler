
public class Jumpz extends Instruction {

    public int A;
    public String arg;  // only used in first pass, when addresses are not yet resolved

    Jumpz(String arg) { this.arg = arg; }

    Jumpz(int A) { this.A = A;}
    
    public boolean jump() {
	return true;
    }
    public void exec(CMA state) {
	if (state.stack[state.SP]==0) state.PC = A;
	state.SP--;
    }

}
