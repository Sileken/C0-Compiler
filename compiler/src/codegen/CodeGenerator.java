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

public class CodeGenerator extends SemanticsVisitor {

	protected static final String BOOLEAN_TRUE = "1"; // CMa returns Bool 0 or 1
	protected static final String BOOLEAN_FALSE = "0";
	protected static final String NULL = "0";

	protected File cmaFile;

	protected List<String> code; /* list containing the final commands of the executable */

	private String methodLabel;
	private Integer loopCount;
	private Integer conditionCount;

	public CodeGenerator(SymbolTable symbolTable) {
		super(symbolTable);
	}

	private void initialize() {
		this.cmaFile = null;
		this.code = new ArrayList<String>();
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

			this.code.add("enter 4"); // is it always 4 ?
			this.code.add("alloc 1"); // always 1 (for loadc _main)?
			this.code.add("mark");
			this.code.add("loadc _main()");
			this.code.add("call");
			this.code.add("halt");

		} else if (node instanceof FunctionDefinition) {
			this.methodLabel = this.getCurrentScope().getName();
			Logger.debug("Preparing Function " + methodLabel);

			int k = ((FunctionDefinition) node).getTotalLocalVariables();
			int max = 150; // ToDo: calculate max (simple version: count arithmetic operations in body..)
			int q = max + k;

			this.code.add("_" + this.methodLabel + ":" + " enter " + q);
			this.code.add("alloc " + k);

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
			this.generateArrayAccessRightValue((ArrayAccess) node);
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

				for (int i = 0; i < code.size(); i++) {
					String line = code.get(i);

					if (line.endsWith(":")) {
						line += " " + code.get(++i);
					}

					asmWriter.write(line);
					asmWriter.newLine();
				}

				asmWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (node instanceof FunctionDefinition) {
			this.code.add("return");
		} else if (node instanceof ReturnStatement) {
			// return to call function
			//this.code.add("jmp " + this.methodLabel + "_END");
		} else if (node instanceof StructDefinition) {
			// ...
		}

		super.didVisit(node);
	}

	private void generateVariableDeclaration(VariableDeclaration variableDeclaration) throws Exception {
		Logger.debug("VariableDeclarationExpression of " + variableDeclaration.getIdentifier());
		this.code.add("loadrc " + variableDeclaration.getIndex());
	}

	private void generateVariableAccessRightValue(Name name) throws Exception {
		Logger.debug("VariableAccessRightValue of " + name.getIdentifier());
		this.code.add("loadr " + ((Declaration) name.getOriginalDeclaration().getNode()).getIndex());
	}

	private void generateVariableAccessLeftValue(Name name) throws Exception {
		Logger.debug("VariableAccessLeftValue of " + name.getIdentifier());
		this.code.add("loadrc " + ((Declaration) name.getOriginalDeclaration().getNode()).getIndex());
	}

	private void generateLiteral(LiteralPrimary literal) throws Exception {

		if (literal.getLiteralType() == LiteralPrimary.LiteralType.NULL) {
			// Throw compile error ?
		} else if (literal.getLiteralType() == LiteralPrimary.LiteralType.INTLIT) {
			Logger.debug("Literal " + Integer.parseInt(literal.getValue()));
			this.code.add("loadc " + Integer.parseInt(literal.getValue()));
		} else if (literal.getLiteralType() == LiteralPrimary.LiteralType.BOOLLIT) {
			if (literal.getValue().equals("true")) {
				Logger.debug("Literal true");
				this.code.add("loadc " + BOOLEAN_TRUE);
			} else {
				this.code.add("loadc " + BOOLEAN_FALSE);
			}
		}
	}

	private void generateUnaryExpression(UnaryExpression unaryExpr) throws Exception {
		unaryExpr.getOperand().accept(this);

		switch (unaryExpr.getOperator()) {
		case BANG:
			this.code.add("not");
			break;
		case TILDE:
			this.code.add("bnot");
			break;
		case MINUS:
			this.code.add("neg");
			break;
		case STAR:
			//codel *e p = coder e p
			this.code.add("load"); //todo: test dereference
			break;
		case INCR:
			this.code.add("loadc 1");
			this.code.add("add");
			break;
		case DECR:
			this.code.add("loadc 1");
			this.code.add("sub");
			break;
		default:
			String msg = "Unsupported Operation while generating unary expression code.";
			Logger.error(msg);
			throw new CodeGenerationException(msg);
		}

		if (unaryExpr.getOperand() instanceof Name) {
			this.generateVariableAccessLeftValue((Name) unaryExpr.getOperand());
			this.code.add("store");
			this.code.add("pop");
		}
	}

	private void generateBinaryExpression(BinaryExpression binaryExpression) throws Exception {
		binaryExpression.getLeftOperand().accept(this);
		binaryExpression.getRightOperand().accept(this);

		switch (binaryExpression.getOperator()) {
		case PLUS:
			this.code.add("add");
			break;
		case MINUS:
			this.code.add("sub");
			break;
		case STAR:
			this.code.add("mul");
			break;
		case SLASH:
			this.code.add("div");
			break;
		case REM:
			this.code.add("mod");
			break;
		case EQ:
			this.code.add("eq");
			break;
		case NEQ:
			this.code.add("neq");
			break;
		case LT:
			this.code.add("le");
			break;
		case LEQ:
			this.code.add("leq");
			break;
		case GT:
			this.code.add("gr");
			break;
		case GEQ:
			this.code.add("geq");
			break;
		case AND:
			this.code.add("and");
			break;
		case OR:
			this.code.add("or");
			break;
		case BOR:
			this.code.add("bor");
			break;
		case BXOR:
			this.code.add("bxor");
			break;
		case BAND:
			this.code.add("band");
			break;
		default:
			String msg = "Unsupported Operation while generating binary expression code.";
			Logger.error(msg);
			throw new CodeGenerationException(msg);
		}
	}

	private void generateAssignmentExpression(AssignmentExpression assignmentExpression) throws Exception {
		VariableDeclaration variableDeclaration = assignmentExpression.getVariableDeclaration();

		if (variableDeclaration == null) {
			if (assignmentExpression.getOperator().ordinal() != AssignmentExpression.Operator.ASSIGN.ordinal()) {
				if (assignmentExpression.getLeftValue() instanceof Name) {
					this.generateVariableAccessRightValue((Name) assignmentExpression.getLeftValue());
				} else {
					assignmentExpression.getLeftValue().accept(this);
				}

				assignmentExpression.getRightValue().accept(this);

				switch (assignmentExpression.getOperator()) {
				case PLUSASSIGN:
					this.code.add("add");
					break;
				case MINUSASSIGN:
					this.code.add("sub");
					break;
				case STARASSIGN:
					this.code.add("mul");
					break;
				case SLASHASSIGN:
					this.code.add("div");
					break;
				case REMASSIGN:
					this.code.add("mod");
					break;
				case ANDASSIGN:
					this.code.add("and");
					break;
				case ORASSIGN:
					this.code.add("or");
					break;
				case XORASSIGN:
					this.code.add("xor");
					break;
				default:
					String msg = "Unsupported Operation while generating assignment operator expression code.";
					Logger.error(msg);
					throw new CodeGenerationException(msg);
				}
			} else {
				assignmentExpression.getRightValue().accept(this);
			}

			if (assignmentExpression.getLeftValue() instanceof ArrayAccess) {
				this.generateArrayAccessLeftValue((ArrayAccess) assignmentExpression.getLeftValue());
			} else if (assignmentExpression.getLeftValue() instanceof Name) {
				this.generateVariableAccessLeftValue((Name) assignmentExpression.getLeftValue());
			} else {
				assignmentExpression.getLeftValue().accept(this);
			}
		} else {
			assignmentExpression.getRightValue().accept(this);
			variableDeclaration.accept(this);
		}

		this.code.add("store");
		this.code.add("pop");
	}

	private void generateMethodInvoke(ExpressionPrimary expressionPrimary) throws Exception {
		MethodInvokeExpression methodInvoke = (MethodInvokeExpression) expressionPrimary.getExpression();
		Name name = (Name) expressionPrimary.getPrefix();

		List<Expression> args = methodInvoke.getArguments();
		for (int i = args.size() - 1; i >= 0; i--) {
			Expression arg = args.get(i);
			arg.accept(this);
		}

		this.code.add("mark");

		ASTNode decNode = name.getOriginalDeclaration().getNode();
		String functionLabel;
		if (decNode instanceof FunctionDeclaration) {
			functionLabel = "_" + table.getFileUnitScope().getSignatureOfFunction((FunctionDeclaration) decNode);
		} else {
			functionLabel = "_" + table.getFileUnitScope().getSignatureOfFunction((FunctionDefinition) decNode);
		}
		this.code.add("loadc " + functionLabel);

		this.code.add("call");
		this.code.add("slide " + (args.size() - 1));

		if (expressionPrimary.getParent() instanceof ExpressionStatement) {
			this.code.add("pop"); // method call without assignment
		}
	}

	private void generateReturnStatement(ReturnStatement returnStatement) throws Exception {
		if (returnStatement.getExpression() != null) {
			returnStatement.getExpression().accept(this);
		}
		code.add("storer -3");
	}

	private void generateIfStatement(IfStatement ifStatement) throws Exception {
		Integer conditionCount = this.conditionCount++;
		String elseMark = "__ELSE_STATEMENT_" + conditionCount;
		String endMark = "__IF_END_" + conditionCount;

		if (ifStatement.getIfCondition() != null) {
			ifStatement.getIfCondition().accept(this);
		} else {
			this.code.add("loadc" + BOOLEAN_TRUE);
		}

		if (ifStatement.getElseStatement() != null) {
			this.code.add("jumpz " + elseMark);
		} else {
			this.code.add("jumpz " + endMark);
		}

		ifStatement.getIfStatement().accept(this);

		if (ifStatement.getElseStatement() != null) {
			this.code.add("jump " + endMark);
			this.code.add(elseMark + ":");
			ifStatement.getElseStatement().accept(this);
		}

		this.code.add(endMark + ":");
	}

	// Currently WhileStatement does not contain the inner-Block statement?
	private void generateWhileStatement(WhileStatement whileStatement) throws Exception {
		Integer loopCount = this.loopCount++;
		String loopName = "__LOOP_Name_" + loopCount;
		String jumpMark = "__LOOP_END_" + loopCount;

		this.code.add(loopName + ":");
		if (whileStatement.getWhileCondition() != null) {
			whileStatement.getWhileCondition().accept(this);
		} else {
			this.code.add("loadc " + BOOLEAN_TRUE); // should be never null
		}

		this.code.add("jumpz " + jumpMark);

		whileStatement.getWhileStatement().accept(this);

		this.code.add("jump " + loopName);
		this.code.add(jumpMark + ":");
	}

	private void generateForLoop(ForStatement forStatement) throws Exception {
		if (forStatement.hasInitializer()) {
			forStatement.getInitialization().accept(this);
		}

		Integer loopCount = this.loopCount++;
		String loopStart = "__LOOP_Name_" + loopCount;
		String jumpEnd = "__LOOP_END_" + loopCount;

		this.code.add(loopStart + ":");
		forStatement.getCondition().accept(this);
		this.code.add("jumpz " + jumpEnd);
		forStatement.getStatement().accept(this);
		forStatement.getIncrement().accept(this);
		this.code.add("jump " + loopStart);
		this.code.add(jumpEnd + ":");
	}

	private void generateAllocExpression(AllocExpression allocExpression) throws Exception {
		if (allocExpression.isArrayAlloc()) {
			allocExpression.getArrayAllocationSize().accept(this);
			this.code.add("loadc " + getSizeOfType(allocExpression.getAllocationType()));
			this.code.add("mul");
			this.code.add("new");

		} else {
			this.code.add("loadc " + getSizeOfType(allocExpression.getAllocationType()));
			this.code.add("new");
		}

		if (allocExpression.getParent() instanceof Statement
				&& !(allocExpression.getParent() instanceof ReturnStatement)) {
			this.code.add("pop"); // not assigned allocation address or returned
		}
	}

	private void generateArrayAccessRightValue(ArrayAccess arrayAccess) throws Exception {
		this.generateArrayAccessLeftValue(arrayAccess);
		this.code.add("load");

		if (arrayAccess.getParent() instanceof Statement && !(arrayAccess.getParent() instanceof ReturnStatement)) {
			this.code.add("pop"); // not assigned allocation address or returned
		}
	}

	private void generateArrayAccessLeftValue(ArrayAccess arrayAccess) throws Exception {
		Logger.log(arrayAccess.getParent().printPretty("", true));

		arrayAccess.getPrefix().accept(this);
		arrayAccess.getIndexExpression().accept(this);
		this.code.add("loadc " + getSizeOfType(arrayAccess.getType()));
		this.code.add("mul");
		this.code.add("add");
	}

	private void generateFieldAccess(FieldAccess fieldAccess) throws Exception {
		// ToDo
	}

	private void generateFieldDereferenceAccess(FieldDereferenceAccess fieldDereference) throws Exception {
		// ToDo
	}

	private int getSizeOfType(Type type) throws Exception {
		int typeSize = 1;

		if (type instanceof StructType) {
			typeSize = 0;
			StructType structType = (StructType) type;
			StructTypeScope structScope = this.table.getStructTypeScope(structType.getScopeName());
			for (Symbol symbol : structScope.getSymbols()) {
				Logger.debug("Field " + symbol.getName() + " has size " + getSizeOfType(symbol.getType()));
				typeSize += getSizeOfType(symbol.getType());
			}
		}

		Logger.debug("Type " + (type.getFullyQualifiedName() == null ? "yes" : "no") + " has size: " + typeSize);
		return typeSize;
	}
}