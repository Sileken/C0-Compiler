
public class CMA {

     public Instruction[] program;
     public int PC;
     public int FP;
     public int SP;
     public int EP;
     public int NP;
     public int max = 1000;
     public int[] stack;
     

     CMA (Instruction[] program) {
	  this.program = program;
	  this.PC = 0;
	  this.NP = this.max;
	  this.FP = 0;
	  this.EP = 0;
	  this.SP = -1;
	  this.stack = new int[max];
     }
     CMA (Instruction[] program, int max) {
	  this.program = program;
	  this.max = max;
	  this.PC = 0;
	  this.NP = this.max;
	  this.FP = 0;
	  this.EP = 0;
	  this.SP = -1;
	  this.stack = new int[max];
     }
     
    public int exec() throws BadInstruction, StackOverflow {
	  Instruction instr;
	  while (true) {
	      this.print();
	      instr = program[PC];
	      PC = PC + 1;
	      if (instr.halt()) break;
	      instr.exec(this);
	  }
	  return stack[SP];
    }

    public void print() {
	String s = "PC: " + PC;
	s = s + " FP: " + FP;
	s = s + " SP: " + SP;
	s = s + " NP: " + NP + " Stack: [";	
	for (int i=SP; i>0; i--)
	    s = s + stack[i] +", ";
	System.out.println(s+stack[0]+"]");
	    
    }
     
}
