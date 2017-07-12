
public class Instruction {

    public boolean halt() {
	return false;
    }
    public boolean jump() {
	return false;
    }
    public boolean loadc() {
	return false;
    }
    public void exec(CMA state) throws BadInstruction, StackOverflow {
    }
}
