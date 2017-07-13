
public class Main {

    public static void main(String[] args) {
	java.util.Scanner s = null;

	// Open CMA file
	try {
	    if (args.length == 0) {
		System.out.println("No file specified");
		return;
	    } else {
		s = new java.util.Scanner(new java.io.File(args[0]));
	    }
	} catch (Exception e) {
	    System.out.println("File not found");
	    return;
	}

	// Parse input, one command per line
	// CMA program will be in internal format 

	String line = "";
	int lineno = 0;  // line number counter
	java.util.LinkedList<Instruction> instrs = new java.util.LinkedList<Instruction>();
	java.util.HashMap<String, Integer> addr = new java.util.HashMap<String, Integer>();
	try {
	    while (s.hasNext()) {
		line = s.nextLine();
		if (!line.equals("")) {
		    instrs.addLast(parseLine(line,lineno,addr));
		    lineno++;
		}
	    }
	} catch (BadInstruction e) {
	    System.out.println("Error: " + e.getMessage());
	    return;
	}

	// Transform the list into an array such that position
	// corresponds to labels/adresses
	// Replace labels with actual addresses in jump statements
	
	Instruction[] program = instrs.toArray(new Instruction[lineno]);
	for (int i=0; i<lineno; i++) {
	    if (program[i].jump()) {
		if (program[i] instanceof Jump) {
		    Jump jump = (Jump)program[i];
		    Integer j = addr.get(jump.arg);
		    if (j!=null) jump.A = j;
		    else {
			System.out.println("Error: unknown label at line number " + i);
			return;
		    }
		} else {
		    Jumpz jump = (Jumpz)program[i];
		    Integer j = addr.get(jump.arg);
		    if (j!=null) jump.A = j;
		    else {
			System.out.println("Error: unknown label at line number " + i);
			return;
		    }
		}
	    }
	    if (program[i].loadc()) {
		Loadc l = (Loadc)program[i];
		if (l.arg!=null) {
		    Integer j = addr.get(l.arg);
		    if (j!=null) l.q = j;
		    else {
			System.out.println("Error: unknown label at line number " + i);
			return;
		    }
		}
	    }
	}

	// Execute program
	CMA cma = new CMA(program);
	try {
	    int result = cma.exec();
	    System.out.println("Result: " + result);
	} catch (BadInstruction e) {
	    System.out.println("Error: " + e.getMessage());
	} catch (StackOverflow e) {
	    System.out.println("Error: " + e.getMessage());
	}
    }

    public static Instruction parseLine(String l, int lineno, java.util.HashMap<String,Integer> addr)
	throws BadInstruction {
	Instruction instr = null;
	java.util.Scanner s;
	String cmd, arg;
	arg = "";
	// check for labels
	// associate labels with line numbers
	// command: cmd, arguments: args
	if (l.indexOf(':')!=-1) {
	    s = new java.util.Scanner(l).useDelimiter("\\s*:\\s*");
	    addr.put(s.next(),lineno);
	    l = s.next();
	    s.close();
	}
	s = new java.util.Scanner(l).useDelimiter("\\s+");
	cmd = s.next();
	if (s.hasNext()) {
	    arg = s.next();
	}
	s.close();

	// create Instruction objects
	switch (cmd) {
	case "load":
	    instr = new Load();
	    break;
	case "loadrc":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Loadrc(j);
	    } catch (NumberFormatException e) {
		throw new BadInstruction("Non integer argument",lineno);
	    }
	    break;
	case "loadc":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Loadc(j);
	    } catch (NumberFormatException e) {
		instr = new Loadc(arg);
	    }
	    break;
	case "loada":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Loada(j);
	    } catch (NumberFormatException e) {
		throw new BadInstruction("Non integer argument",lineno);
	    }
	    break;
	case "loadr":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Loadr(j);
	    } catch (NumberFormatException e) {
		throw new BadInstruction("Non integer argument",lineno);
	    }
	    break;
	case "store":
	    instr = new Store();
	    break;
	case "storea":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Storea(j);
	    } catch (NumberFormatException e) {
		throw new BadInstruction("Non integer argument",lineno);
	    }
	    break;
	case "storer":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Storer(j);
	    } catch (NumberFormatException e) {
		throw new BadInstruction("Non integer argument",lineno);
	    }
	    break;
	case "call":
	    instr = new Call();
	    break;
	case "enter":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Enter(j);
	    } catch (NumberFormatException e) {
		throw new BadInstruction("Non integer argument",lineno);
	    }
	    break;
	case "mark":
	    instr = new Mark();
	    break;
	case "return":
	    instr = new Return();
	    break;
	case "add":
	    instr = new Add();
	    break;
	case "sub":
	    instr = new Sub();
	    break;
	case "mul":
	    instr = new Mul();
	    break;
	case "div":
	    instr = new Div();
	    break;
	case "mod":
	    instr = new Mod();
	    break;
	case "eq":
	    instr = new Eq();
	    break;
	case "neq":
	    instr = new Neq();
	    break;
	case "neg":
	    instr = new Neg();
	    break;
	case "le":
	    instr = new Le();
	    break;
	case "leq":
	    instr = new Leq();
	    break;
	case "gr":
	    instr = new Gr();
	    break;
	case "geq":
	    instr = new Geq();
	    break;
	case "halt":
	    instr = new Halt();
	    break;
	case "alloc":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Alloc(j);
	    } catch (NumberFormatException e) {
		throw new BadInstruction("Non integer argument",lineno);
	    }
	    break;
	case "and":
	    instr = new And();
	    break;
	case "or":
	    instr = new Or();
	    break;
	case "xor":
	    instr = new Xor();
	    break;
	case "bnot":
	    instr = new Bnot();
	    break;
	case "band":
	    instr = new Band();
	    break;
	case "bor":
	    instr = new Bor();
	    break;
	case "bxor":
	    instr = new Bxor();
	    break;
	case "new":
	    instr = new New();
	    break;
	case "pop":
	    instr = new Pop();
	    break;
	case "slide":
	    try {
		int j = Integer.parseInt(arg);
		instr = new Slide(j);
	    } catch (NumberFormatException e) {
		throw new BadInstruction("Non integer argument",lineno);
	    }
	    break;
	case "jump":
	    instr = new Jump(arg);
	    break;
	case "jumpz":
	    instr = new Jumpz(arg);
	    break;
	default:
	    throw new BadInstruction(cmd,lineno);
	}
	return instr;
    }

}
