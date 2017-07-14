
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
        System.out.print("PC: ");
        System.out.format("%3d | ", PC);
          
        System.out.print("FP: ");
        System.out.format("%3d | ", FP);
          
        System.out.print("SP: ");
        System.out.format("%3d | ", SP);
        
        System.out.print("EP: ");
        int length = (int)(Math.log10(max)+1); // Printed length equal to the num digits of MAX
        System.out.format("%"+length+"d | ", EP);
        
        System.out.print("NP: ");
        System.out.format("%3d | Stack: [", NP);
        String stackString = "";
        for (int i=SP; i>0; i--)
            stackString += stack[i] +", ";
        System.out.println(stackString + stack[0]+"]");
    }
     
}
