package codegen;

import ast.*;
import ast.expression.*;
import ast.expression.primary.*;
import ast.type.*;
import ast.declaration.*;
import ast.expression.primary.*;
import ast.expression.primary.name.*;
import ast.statement.*;
import ast.definition.*;
import ast.identifier.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


public class CodeGenerator extends SemanticsVisitor {

    private static final boolean DEBUG = true;

	protected static final String BOOLEAN_TRUE = "0xffffffff";
	protected static final String BOOLEAN_FALSE = "0x0";
	protected static final String NULL = "0x0";

	protected File asmFile = null;
	protected static File startFile = null;
	
	protected List<String> texts = null;            /* list containing the final commands of the executable */
	protected List<String> data = null;

	protected static List<String> staticInit = new ArrayList<String>();

	private String methodLabel = null;
	private Integer literalCount = 0;
	private Integer comparisonCount = 0;
	private Integer loopCount = 0;
	private Integer conditionCount = 0;
	private Boolean dereferenceVariable = true;
    private Boolean referenceCurrentObject = true;

    public CodeGenerator(SymbolTable symbolTable) {
        super(symbolTable);
    }


	private void initialize() {
		this.asmFile = null;
		this.texts = new ArrayList<String>();
		this.data = new ArrayList<String>();
		this.methodLabel = null;
		this.literalCount = 0;
		this.comparisonCount = 0;
		this.loopCount = 0;
		this.dereferenceVariable = true;
		this.referenceCurrentObject = true;

		this.texts.add("");
		this.texts.add("section .text");
		this.texts.add("");

		this.data.add("");
		this.data.add("section .data");
		this.data.add("");
    }
    
	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		super.willVisit(node);

		if (node instanceof FileUnit) {
            if (DEBUG) System.out.println("  [DEBUG]: Initialize CodeGenerator");
			this.initialize();
		} else if (node instanceof FunctionDefinition) {
            if (DEBUG) System.out.println("  [DEBUG]: Preparing Function");
			/* This is the relevant part from joos-compiler
                commands have to be changed to our CMa 

            
            this.methodLabel = methodLabel(this.getCurrentScope().getName());

            this.texts.add("global " + this.methodLabel);
            this.texts.add(this.methodLabel + ":");

            // Preamble
            this.texts.add("push ebp\t\t\t; Preamble");
            this.texts.add("mov ebp, esp");

            // Allocate space for local variables
            // Multiply by 4 because of the 32bit architecture
            this.texts.add("sub esp, " + (((MethodDeclaration) node).totalLocalVariables * 4));

            // Push registers
            // this.texts.add("push eax"); // Leave eax as return value
            this.texts.add("push ebx");
            this.texts.add("push ecx");
            this.texts.add("push edx");
            this.texts.add("");


            */
		} else if (node instanceof StructDefinition) {
            if (DEBUG) System.out.println("  [DEBUG]: Preparing Struct");
            // like FunctionDeclaration
        }
    }

	// not all listed
    @Override
	public boolean visit(ASTNode node) throws Exception {
		if (DEBUG) System.out.println("  [DEBUG]: Visiting " + node);

		if (node instanceof MethodInvokeExpression) {
			this.generateMethodInvoke((MethodInvokeExpression) node);
			return false;
        } /* else if (node instanceof InfixExpression) {
			this.generateInfixExpression((InfixExpression) node);
			return false;
		} else if (node instanceof ArrayCreate) {
			this.generateArrayCreate((ArrayCreate) node);
			return false;
		} */else if (node instanceof ArrayAccess) {
			this.generateArrayAccess((ArrayAccess) node);
			return false;
		} else if (node instanceof LiteralPrimary) {
			this.generateLiteral((LiteralPrimary) node);
			return false;
		} else if (node instanceof UnaryExpression) {
			this.generateUnaryExpression((UnaryExpression) node);
			return false;
		} else if (node instanceof VariableDeclaration) {
			this.generateVariableDeclaration((VariableDeclaration) node);
			return false;
		} else if (node instanceof AssignmentExpression) {
			this.generateAssignmentExpression((AssignmentExpression) node);
			return false;
		} else if (node instanceof ForStatement) {
			this.generateForLoop((ForStatement) node);
			return false;
		} else if (node instanceof WhileStatement) {
			this.generateWhileStatement((WhileStatement) node);
			return false;
		} else if (node instanceof IfStatement) {
			this.generateIfStatement((IfStatement) node);
			return false;
		} else if (node instanceof FieldAccess) {
			this.generateFieldAccess((FieldAccess) node);
			return false;
		} else if (node instanceof Name) {
			this.generateVariableAccess((Name) node);
			return false;
		}

		//return !this.complexNodes.contains(node.getClass());
		return false;
	}


	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
        // leaving FileUnit, write content on disk
		if (node instanceof FileUnit) {
            if (DEBUG) System.out.println("  [DEBUG]: writing content to: " + asmFile);        
			File dir = this.asmFile.getParentFile();

			if (dir != null) {
				dir.mkdirs();
			}

			try {
				this.asmFile.createNewFile();
			
				BufferedWriter asmWriter = new BufferedWriter(new FileWriter(this.asmFile));

				for (String line : this.texts) {
					if (!line.startsWith("global") && !line.startsWith("section")) {
						line = "\t" + line;
						if (!line.endsWith(":")) {
							line = "\t" + line;
						}
					}
					asmWriter.write(line);
					asmWriter.newLine();
				}

				for (String line : this.data) {
					asmWriter.write(line);
					asmWriter.newLine();
				}

				asmWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (node instanceof FunctionDefinition) {
			
            /*
            pop registers

            // Postamble
            this.texts.add(this.methodLabel + "_END:");
            // Pop registers
            this.texts.add("pop edx\t\t\t\t; Postamble");
            this.texts.add("pop ecx");
            this.texts.add("pop ebx");
            // this.texts.add("pop eax"); // Leave eax as return value

            // Deallocate space for local variables
            this.texts.add("add esp, " + (((MethodDeclaration) node).totalLocalVariables * 4));

            // Restore frame pointer
            this.texts.add("pop ebp");

            if (this.methodLabel.equals("_start")) {
                this.texts.add("call __debexit");
            } else {
                this.texts.add("ret");
            }
            this.texts.add("");
            
            */
		} else if (node instanceof ReturnStatement) {
			// return to call function
             this.texts.add("jmp " + this.methodLabel + "_END");
		} else if (node instanceof StructDefinition) {
            // ...
        }
		super.didVisit(node);
    }

    private void generateMethodInvoke(MethodInvokeExpression methodInvoke) throws Exception {

		// Push parameters to stack
		List<Expression> args = methodInvoke.getArguments();
		
		for (int i = args.size() - 1; i >= 0; i--) {
			Expression arg = args.get(i);
			// Generate code for arg
			arg.accept(this);
            // push parameter
			//this.texts.add("push eax\t\t\t; Push parameter #" + (i + 1) + " to stack");
		}

        // ... snipped (check joose-compiler)
	}


	private void generateArrayAccess(ArrayAccess arrayAccess) throws Exception {
		// ToDo
    }

    private void generateLiteral(LiteralPrimary literal) throws Exception {
        // ToDo
    }

	private void generateUnaryExpression(UnaryExpression unaryExpr) throws Exception {
        // ToDo
    }

    private void generateVariableDeclaration(VariableDeclaration decl) throws Exception {
        // ToDo
    }

    private void generateAssignmentExpression(AssignmentExpression assignExpr) throws Exception {
        // ToDo
	}

	private void generateForLoop(ForStatement forStatement) throws Exception {
		// ToDo
	}

    private void generateWhileStatement(WhileStatement whileStatement) throws Exception {
		Integer loopCount = this.loopCount++;
		this.texts.add("__LOOP_CONDITION_" + loopCount + ":");

		if(whileStatement.getWhileCondition() != null) {
			whileStatement.getWhileCondition().accept(this);
		} else {
			this.texts.add("mov eax, " + BOOLEAN_TRUE);
		}

		this.texts.add("cmp eax, " + BOOLEAN_FALSE);
		this.texts.add("je __LOOP_END_" + loopCount);
        /* Currently WhileStatement does not contain the inner-Block statement?

		this.texts.add("__LOOP_STATEMENT_" + loopCount + ":");
		whileStatement.getWhileStatement().accept(this);
		this.texts.add("jmp __LOOP_CONDITION_" + loopCount);
        
        */ 
		this.texts.add("__LOOP_END_" + loopCount + ":");
	}

    // Only the registers have to be changed
	private void generateIfStatement(IfStatement ifStatement) throws Exception {
		Integer conditionCount = this.conditionCount++;

		this.texts.add("__IF_CONDITION_" + conditionCount + ":");
		
		if(ifStatement.getIfCondition() != null) {
			ifStatement.getIfCondition().accept(this);
		} else {
			this.texts.add("mov eax, " + BOOLEAN_TRUE);
		}

		this.texts.add("cmp eax, " + BOOLEAN_FALSE);
		this.texts.add("je __ELSE_STATEMENT_" + conditionCount);

		this.texts.add("__IF_STATEMENT_" + conditionCount + ":");
		ifStatement.getIfStatement().accept(this);
		this.texts.add("jmp __IF_END_" + conditionCount);

		this.texts.add("__ELSE_STATEMENT_" + conditionCount + ":");
		if (ifStatement.getElseStatement() != null) {
			ifStatement.getElseStatement().accept(this);
		}
		this.texts.add("__IF_END_" + conditionCount + ":");
    }

    private void generateFieldAccess(FieldAccess fieldAccess) throws Exception {
		// ToDo
    }

    private void generateVariableAccess(Name name) throws Exception {
        // ToDo
	}
}