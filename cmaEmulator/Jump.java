
public class Jump extends Instruction {

    public int A;
    public String arg;  // only used in first pass, when addresses are not yet resolved 
    
    Jump(int A) { this.A = A; }

    Jump(String arg) { this.arg = arg; }

    public boolean jump() {
	return true;
    }
    public void exec(CMA state) {
	state.PC = A;
    }

}
