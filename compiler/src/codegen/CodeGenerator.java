package codegen;

import ast.*;
import ast.declaration.*;
import ast.definition.*;
import ast.expression.*;
import ast.expression.primary.*;
import ast.expression.primary.name.*;
import ast.identifier.*;
import ast.statement.*;
import ast.type.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;
import logger.*;

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

	protected File cmaFile;

	protected List<String> texts; /* list containing the final commands of the executable */

	private String methodLabel;
	private Integer loopCount;
	private Integer conditionCount;

	public CodeGenerator(SymbolTable symbolTable) {
		super(symbolTable);
	}

	private void initialize() {
		this.cmaFile = null;
		this.texts = new ArrayList<String>();
		this.methodLabel = null;
		this.loopCount = 0;
		this.conditionCount = 0;
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
			this.texts.add("alloc 1"); // always 1 (for loadc _main)?
			this.texts.add("mark");
			this.texts.add("loadc _main()");
			this.texts.add("call");
			this.texts.add("halt");

		} else if (node instanceof FunctionDefinition) {
			this.methodLabel = this.getCurrentScope().getName();
			Logger.debug("Preparing Function " + methodLabel);

			int k = ((FunctionDefinition) node).getTotalLocalVariables();
			int max = 150; // ToDo: calculate max (simple version: count arithmetic operations in body..)
			int q = max + k;

			this.texts.add("_" + this.methodLabel + ":" + " enter " + q);
			this.texts.add("alloc " + k);

		} else if (node instanceof StructDefinition) {
			Logger.debug("Preparing Struct");
			// like FunctionDeclaration
		}
	}

	// not all listed
	@Override
	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof VariableDeclarationExpression) {
			// do nothing, prevent the generation of the VariableDeclaration
			return false;
		} else if (node instanceof VariableDeclaration) {
			this.generateVariableDeclaration((VariableDeclaration) node);
			return false;
		} else if (node instanceof Name) {
			this.generateVariableAccessRightValue((Name) node);
			return false;
		} else if (node instanceof LiteralPrimary) {
			this.generateLiteral((LiteralPrimary) node);
			return false;
		} else if (node instanceof AssignmentExpression) {
			this.generateAssignmentExpression((AssignmentExpression) node);
			return false;
		} else if (node instanceof UnaryExpression) {
			this.generateUnaryExpression((UnaryExpression) node);
			return false;
		} else if (node instanceof BinaryExpression) {
			this.generateBinaryExpression((BinaryExpression) node);
			return false;
		} else if (node instanceof ExpressionPrimary) {
			ExpressionPrimary expPrimary = (ExpressionPrimary) node;
			if (expPrimary.getExpression() instanceof MethodInvokeExpression) {
				this.generateMethodInvoke(expPrimary);
				return false;
			}
		} else if (node instanceof ReturnStatement) {
			this.generateReturnStatement((ReturnStatement) node);
			return false;
		} else if (node instanceof IfStatement) {
			this.generateIfStatement((IfStatement) node);
			return false;
		} else if (node instanceof WhileStatement) {
			this.generateWhileStatement((WhileStatement) node);
			return false;
		} else if (node instanceof ForStatement) {
			this.generateForLoop((ForStatement) node);
			return false;
		} else if (node instanceof AllocExpression) {
			this.generateAllocExpression((AllocExpression) node);
			return false;
		} else if (node instanceof ArrayAccess) {
			this.generateArrayAccess((ArrayAccess) node);
			return false;
		} else if (node instanceof FieldAccess) {
			this.generateFieldAccess((FieldAccess) node);
			return false;
		} else if (node instanceof FieldDereferenceAccess) {
			this.generateFieldDereferenceAccess((FieldDereferenceAccess) node);
			return false;
		}

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
		} else if (node instanceof ReturnStatement) {
			// return to call function
			//this.texts.add("jmp " + this.methodLabel + "_END");
		} else if (node instanceof StructDefinition) {
			// ...
		}

		super.didVisit(node);
	}

	private void generateVariableDeclaration(VariableDeclaration variableDeclaration) throws Exception {
		Logger.debug("VariableDeclarationExpression of " + variableDeclaration.getIdentifier());
		this.texts.add("loadrc " + variableDeclaration.getIndex());
	}

	private void generateVariableAccessRightValue(Name name) throws Exception {
		Logger.debug("VariableAccessRightValue of " + name.getIdentifier());
		this.texts.add("loadr " + ((Declaration) name.getOriginalDeclaration().getNode()).getIndex());
	}

	private void generateVariableAccessLeftValue(Name name) throws Exception {
		Logger.debug("VariableAccessLeftValue of " + name.getIdentifier());
		this.texts.add("loadrc " + ((Declaration) name.getOriginalDeclaration().getNode()).getIndex());
	}

	private void generateLiteral(LiteralPrimary literal) throws Exception {

		if (literal.getLiteralType() == LiteralPrimary.LiteralType.NULL) {
			// Throw compile error ?
		} else if (literal.getLiteralType() == LiteralPrimary.LiteralType.INTLIT) {
			Logger.debug("Literal " + Integer.parseInt(literal.getValue()));
			this.texts.add("loadc " + Integer.parseInt(literal.getValue()));
		} else if (literal.getLiteralType() == LiteralPrimary.LiteralType.BOOLLIT) {
			if (literal.getValue().equals("true")) {
				Logger.debug("Literal true");
				this.texts.add("loadc " + BOOLEAN_TRUE);
			} else {
				this.texts.add("loadc " + BOOLEAN_FALSE);
			}
		}
	}

	private void generateUnaryExpression(UnaryExpression unaryExpr) throws Exception {
		unaryExpr.getOperand().accept(this);

		switch (unaryExpr.getOperator()) {
		case BANG:
			this.texts.add("not");
			break;
		case TILDE:
			this.texts.add("bnot");
			break;
		case MINUS:
			this.texts.add("neg");
			break;
		case STAR:
			//codel *e p = coder e p
			this.texts.add("load"); //todo: test dereference
			break;
		case INCR:
			this.texts.add("loadc 1");
			this.texts.add("add");
			break;
		case DECR:
			this.texts.add("loadc 1");
			this.texts.add("sub");
			break;
		default:
			// todo: Throw UnsupportedOperationException
			break;
		}

		if (unaryExpr.getOperand() instanceof Name) {
			this.generateVariableAccessLeftValue((Name) unaryExpr.getOperand());
			this.texts.add("store");
			this.texts.add("pop");
		}
	}

	private void generateBinaryExpression(BinaryExpression binaryExpression) throws Exception {
		binaryExpression.getLeftOperand().accept(this);
		binaryExpression.getRightOperand().accept(this);

		switch (binaryExpression.getOperator()) {
		case PLUS:
			this.texts.add("add");
			break;
		case MINUS:
			this.texts.add("sub");
			break;
		case STAR:
			this.texts.add("mul");
			break;
		case SLASH:
			this.texts.add("div");
			break;
		case REM:
			this.texts.add("mod");
			break;
		case EQ:
			this.texts.add("eq");
			break;
		case NEQ:
			this.texts.add("neq");
			break;
		case LT:
			this.texts.add("le");
			break;
		case LEQ:
			this.texts.add("leq");
			break;
		case GT:
			this.texts.add("gr");
			break;
		case GEQ:
			this.texts.add("geq");
			break;
		case AND:
			this.texts.add("and");
			break;
		case OR:
			this.texts.add("or");
			break;
		case BOR:
			this.texts.add("bor");
			break;
		case BXOR:
			this.texts.add("bxor");
			break;
		case BAND:
			this.texts.add("band");
			break;
		default:
			// todo: Throw UnsupportedOperationException
			break;
		}
	}

	private void generateAssignmentExpression(AssignmentExpression assignmentExpression) throws Exception {
		assignmentExpression.getRightValue().accept(this);

		VariableDeclaration variableDeclaration = assignmentExpression.getVariableDeclaration();

		if (variableDeclaration == null) {
			if (assignmentExpression.getOperator().ordinal() != AssignmentExpression.Operator.ASSIGN.ordinal()) {
				if (assignmentExpression.getLeftValue() instanceof Name) {
					this.generateVariableAccessRightValue((Name) assignmentExpression.getLeftValue());
				} else {
					assignmentExpression.getLeftValue().accept(this);
				}

				switch (assignmentExpression.getOperator()) {
				case PLUSASSIGN:
					this.texts.add("add");
					break;
				case MINUSASSIGN:
					this.texts.add("sub");
					break;
				case STARASSIGN:
					this.texts.add("mul");
					break;
				case SLASHASSIGN:
					this.texts.add("div");
					break;
				case REMASSIGN:
					this.texts.add("mod");
					break;
				case ANDASSIGN:
					this.texts.add("and");
					break;
				case ORASSIGN:
					this.texts.add("or");
					break;
				case XORASSIGN:
					this.texts.add("xor");
					break;
				default:
					// todo: Throw UnsupportedOperationException
					break;
				}
			}

			if (assignmentExpression.getLeftValue() instanceof Name) {
				this.generateVariableAccessLeftValue((Name) assignmentExpression.getLeftValue());
			} else {
				assignmentExpression.getLeftValue().accept(this);
			}
		} else {
			variableDeclaration.accept(this);
		}

		this.texts.add("store");
		this.texts.add("pop");
	}

	private void generateMethodInvoke(ExpressionPrimary expressionPrimary) throws Exception {
		MethodInvokeExpression methodInvoke = (MethodInvokeExpression) expressionPrimary.getExpression();
		Name name = (Name) expressionPrimary.getPrefix();

		List<Expression> args = methodInvoke.getArguments();
		for (int i = args.size() - 1; i >= 0; i--) {
			Expression arg = args.get(i);
			arg.accept(this);
		}

		this.texts.add("mark");

		ASTNode decNode = name.getOriginalDeclaration().getNode();
		String functionLabel;
		if (decNode instanceof FunctionDeclaration) {
			functionLabel = "_" + table.getFileUnitScope().getSignatureOfFunction((FunctionDeclaration) decNode);
		} else {
			functionLabel = "_" + table.getFileUnitScope().getSignatureOfFunction((FunctionDefinition) decNode);
		}
		this.texts.add("loadc " + functionLabel);

		this.texts.add("call");
		this.texts.add("slide " + (args.size() - 1));

		if (expressionPrimary.getParent() instanceof ExpressionStatement) {
			this.texts.add("pop"); // method call without assignment
		}
	}

	private void generateReturnStatement(ReturnStatement returnStatement) throws Exception {
		if (returnStatement.getExpression() != null) {
			returnStatement.getExpression().accept(this);
		}
		texts.add("storer -3"); // always -3 ??
	}

	// Only the registers have to be changed <- LOL what a wrong comment
	private void generateIfStatement(IfStatement ifStatement) throws Exception {
		Integer conditionCount = this.conditionCount++;
		String elseMark = "__ELSE_STATEMENT_" + conditionCount;
		String endMark = "__IF_END_" + conditionCount;

		if (ifStatement.getIfCondition() != null) {
			ifStatement.getIfCondition().accept(this);
		} else {
			this.texts.add("loadc" + BOOLEAN_TRUE);
		}

		if (ifStatement.getElseStatement() != null) {
			this.texts.add("jumpz " + elseMark);
		} else {
			this.texts.add("jumpz " + endMark);
		}

		ifStatement.getIfStatement().accept(this);

		if (ifStatement.getElseStatement() != null) {
			this.texts.add("jump " + endMark);
			this.texts.add(elseMark + ":");
			ifStatement.getElseStatement().accept(this);
		}

		this.texts.add(endMark + ":");
	}

	// Currently WhileStatement does not contain the inner-Block statement?
	private void generateWhileStatement(WhileStatement whileStatement) throws Exception {
		Integer loopCount = this.loopCount++;
		String loopName = "__LOOP_Name_" + loopCount;
		String jumpMark = "__LOOP_END_" + loopCount;

		this.texts.add(loopName + ":");
		if (whileStatement.getWhileCondition() != null) {
			whileStatement.getWhileCondition().accept(this);
		} else {
			this.texts.add("loadc " + BOOLEAN_TRUE); // should be never null
		}

		this.texts.add("jumpz " + jumpMark);

		whileStatement.getWhileStatement().accept(this);

		this.texts.add("jump " + loopName);
		this.texts.add(jumpMark + ":");
	}

	private void generateForLoop(ForStatement forStatement) throws Exception {
		if (forStatement.hasInitializer()) {
			forStatement.getInitialization().accept(this);
		}

		Integer loopCount = this.loopCount++;
		String loopStart = "__LOOP_Name_" + loopCount;
		String jumpEnd = "__LOOP_END_" + loopCount;

		this.texts.add(loopStart + ":");
		forStatement.getCondition().accept(this);
		this.texts.add("jumpz " + jumpEnd);
		forStatement.getStatement().accept(this);
		forStatement.getIncrement().accept(this);
		this.texts.add("jump " + loopStart);
		this.texts.add(jumpEnd + ":");

		if (forStatement.hasInitializer()) {
			this.texts.add("pop"); // pop only one possible initializer		
		}
	}

	private void generateAllocExpression(AllocExpression allocExpression) throws Exception {
		// ToDo
		// switch between Alloc Array and alloc Struct 
	}

	private void generateArrayAccess(ArrayAccess arrayAccess) throws Exception {
		// ToDo
	}

	private void generateFieldAccess(FieldAccess fieldAccess) throws Exception {
		// ToDo
	}

	private void generateFieldDereferenceAccess(FieldDereferenceAccess fieldDereference) throws Exception {
		// ToDo
	}
}