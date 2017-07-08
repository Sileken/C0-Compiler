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
import utils.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

// guckst du handout.pdf S. 97 Codegenerierung Funktionen!
// <LabelName>: enter q          // Instruktion muss in die selbe Zeile des Labels
// alloc k

public class CodeGenerator extends SemanticsVisitor {

	protected static final String BOOLEAN_TRUE = "1"; // CMa returns Bool 0 or 1
	protected static final String BOOLEAN_FALSE = "0";
	protected static final String NULL = "0";

	protected File cmaFile = null;
	protected static File startFile = null;
	
	protected List<String> texts = null;            /* list containing the final commands of the executable */

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
		this.cmaFile = null;
		this.texts = new ArrayList<String>();
		this.methodLabel = null;
		this.literalCount = 0;
		this.comparisonCount = 0;
		this.loopCount = 0;
		this.dereferenceVariable = true;
		this.referenceCurrentObject = true;
    }
    
	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		super.willVisit(node);

		if (node instanceof FileUnit) {
            Logger.debug("Initialize CodeGenerator");
			this.initialize();
			String fileName = node.getIdentifier();
			fileName = fileName.substring(0, fileName.indexOf('.'));
			fileName += ".cma";

			this.cmaFile = new File(fileName);

			this.texts.add("enter 4"); // is it always 4 ?
			this.texts.add("alloc 1"); // always 1 ?
			this.texts.add("mark");
			this.texts.add("loadc _main()");
			this.texts.add("call");
			this.texts.add("halt");
			this.texts.add("");

		} else if (node instanceof FunctionDefinition) {           
            this.methodLabel = this.getCurrentScope().getName();
            Logger.debug("Preparing Function " + methodLabel);
			
			int k = ((FunctionDefinition) node).getTotalLocalVariables();
			int max = 150; 		// ToDo: calculate max (simple version: count arithmetic operations in body..)
			int q = max + k;

            this.texts.add("_" + this.methodLabel + ":" + " enter " + q);
            this.texts.add("alloc " + k);
			
		} else if (node instanceof StructDefinition) {
            Logger.log("  [DEBUG]: Preparing Struct");
            // like FunctionDeclaration
        }
    }

	// not all listed
    @Override
	public boolean visit(ASTNode node) throws Exception {
		Logger.debug("Visiting " + node);

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
		return true;
	}


	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
        // leaving FileUnit, write content on disk
		if (node instanceof FileUnit) {
            Logger.debug("writing content to: " + cmaFile);        
			File dir = this.cmaFile.getParentFile();

			Logger.debug("Dir: " + dir);
			if (dir != null) {
				dir.mkdirs();
			}

			try {
				this.cmaFile.createNewFile();
			
				BufferedWriter asmWriter = new BufferedWriter(new FileWriter(this.cmaFile));

				for (int i = 0; i < texts.size(); i++) {
					String line = texts.get(i);
					
					if (line.endsWith(":")) {
						line += " " + texts.get(++i);	
					}
					
					asmWriter.write(line);
					asmWriter.newLine();
				}

				asmWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (node instanceof FunctionDefinition) {
			this.texts.add("return");
            this.texts.add("");

		} else if (node instanceof ReturnStatement) {
			// return to call function
             //this.texts.add("jmp " + this.methodLabel + "_END");
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

    // Currently WhileStatement does not contain the inner-Block statement?
    private void generateWhileStatement(WhileStatement whileStatement) throws Exception {
		Integer loopCount = this.loopCount++;
		String loopName = "__LOOP_Name_" + loopCount;
		String jumpMark = "__LOOP_END_" + loopCount;

		this.texts.add(loopName + ":");
		if(whileStatement.getWhileCondition() != null) {
			whileStatement.getWhileCondition().accept(this);
		} else {
			this.texts.add("loadc " + BOOLEAN_TRUE);
		}

		this.texts.add("jumpz " + jumpMark);

		//whileStatement.getWhileStatement().accept(this);
		
		this.texts.add("jump" + loopName);
		this.texts.add(jumpMark + ":");
	}

    // Only the registers have to be changed <- LOL what a wrong comment
	// Current Problem: the next instruction after ":" has to be in the same row
	// maybe fix this problem for any method when wie print out the ArrayList
	// like "if previous ends with ":", write next one in the same line (with space)"..
	private void generateIfStatement(IfStatement ifStatement) throws Exception {
		Integer conditionCount = this.conditionCount++;
		String jumpMark = "";

		if (ifStatement.getElseStatement() != null) {
			jumpMark = "__ELSE_STATEMENT_" + conditionCount;
		} else {
			jumpMark = "__IF_END_" + conditionCount;
		}

		if(ifStatement.getIfCondition() != null) {
			ifStatement.getIfCondition().accept(this);
		} else {
			this.texts.add("loadc" + BOOLEAN_TRUE);
		}

		this.texts.add("jumpz " + jumpMark);

		ifStatement.getIfStatement().accept(this);

		this.texts.add(jumpMark + ":");

		if (ifStatement.getElseStatement() != null) {
			ifStatement.getElseStatement().accept(this);
		}
    }

    private void generateFieldAccess(FieldAccess fieldAccess) throws Exception {
		// ToDo
    }

    private void generateVariableAccess(Name name) throws Exception {
        // ToDo
	}
}