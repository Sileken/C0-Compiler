
public class Storer extends Instruction {

    public int j;

    Storer(int j) { this.j = j; }
    
    public void exec(CMA state) {
	state.stack[state.FP + j] = state.stack[state.SP];
    }

      @Override
    public String toString()
    {
        return "storer " + j;
    }
    
}
