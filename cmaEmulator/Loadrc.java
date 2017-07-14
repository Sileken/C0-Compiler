
public class Loadrc extends Instruction {
    public int j;

    Loadrc(int j) { this.j = j; }
    
    public void exec(CMA state) {
	state.SP++;
	state.stack[state.SP] = state.FP + j;
    }

      @Override
    public String toString()
    {
        return "loadrc " + j;
    }
}
