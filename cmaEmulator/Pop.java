
public class Pop extends Instruction {
    
    public void exec(CMA state) {
	state.SP--;
    }

      @Override
    public String toString()
    {
        return "pop";
    }
}
