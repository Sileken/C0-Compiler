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

import java.util.List;
import java.util.ArrayList;

public class CodeGenerator extends SemanticsVisitor {
	protected static final String BOOLEAN_TRUE = "1";
	protected static final String BOOLEAN_FALSE = "0";
	protected static final String NULL = "0";
	public static final String JUMP_START = "__START__";
	public static final String JUMP_END = "__END__";

	protected List<String> code;
	private Integer loopCount;
	private Integer conditionCount;

	public CodeGenerator(SymbolTable symbolTable) {
		super(symbolTable);
	}

	private void initialize() {
		this.code = new ArrayList<String>();
		this.loopCount = 0;
		this.conditionCount = 0;
	}

	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		super.willVisit(node);

		if (node instanceof FileUnit) {
			Logger.debug("Initialize CodeGenerator");
			this.initialize();

			this.code.add("enter 4");
			this.code.add("alloc 1");
			this.code.add("mark");
			this.code.add("loadc _main()");
			this.code.add("call");
			this.code.add("halt");
		} else if (node instanceof FunctionDefinition) {
			String methodLabel = this.getCurrentScope().getName();
			Logger.debug("Preparing Function " + methodLabel);

			int k = ((FunctionDefinition) node).getTotalLocalVariables();

			int max = node.countArithmeticOps();
			//System.out.println("ARITHM OPS: " + max);

			int q = max + k;
			//System.out.println(">>>> Q: " + q);

			this.code.add("_" + methodLabel + ":" + " enter " + q);
			this.code.add("alloc " + k);
		}
	}

	@Override
	public boolean visit(ASTNode node) throws SymbolTableException, CodeGenerationException, Exception {
		// Generate "complex" statement
		if (node instanceof ForStatement) {
			this.generateForLoop((ForStatement) node);
			return false;
		} else if (node instanceof IfStatement) { // if statement generation includes else code generation
			this.generateIfStatement((IfStatement) node);
			return false;
		} else if (node instanceof ReturnStatement) {
			this.generateReturnStatement((ReturnStatement) node);
			return false;
		} else if (node instanceof WhileStatement) {
			this.generateWhileStatement((WhileStatement) node);
			return false;
		}
		// Generate expression statement
		else if (node instanceof AllocExpression) {
			this.generateAllocExpression((AllocExpression) node);
			return false;
		} else if (node instanceof AssignmentExpression) {
			this.generateAssignmentExpression((AssignmentExpression) node);
			return false;
		} else if (node instanceof BinaryExpression) {
			this.generateBinaryExpression((BinaryExpression) node);
			return false;
		} else if (node instanceof ConditionalExpression) {
			this.generateConditionalExpression((ConditionalExpression) node);
			return false;
		} else if (node instanceof UnaryExpression) {
			this.generateUnaryExpression((UnaryExpression) node);
			return false;
		} else if (node instanceof VariableDeclarationExpression) {
			/* do nothing, prevent the generation of the VariableDeclaration 
			*  VariableDeclation will be generated in MethodInvokation */
			return false;
		} else if (node instanceof VariableDeclaration) {
			this.generateVariableDeclaration((VariableDeclaration) node);
			return false;
		} // Generate Primary Expressions		 
		else if (node instanceof Name) {
			this.generateVariableAccessRightValue((Name) node);
			return false;
		} else if (node instanceof ArrayAccess) {
			this.generateArrayAccessRightValue((ArrayAccess) node);
			return false;
		} else if (node instanceof ExpressionPrimary) {
			// Primary Expressions are method invikation or expression in brace
			ExpressionPrimary expPrimary = (ExpressionPrimary) node;
			if (expPrimary.getExpression() instanceof MethodInvokeExpression) {
				this.generateMethodInvoke(expPrimary);
				return false;
			}
		} else if (node instanceof FieldAccess) {
			this.generateFieldAccessRightValue((FieldAccess) node);
			return false;
		} else if (node instanceof FieldDereferenceAccess) {
			this.generateFieldDereferenceAccessRightValue((FieldDereferenceAccess) node);
			return false;
		} else if (node instanceof LiteralPrimary) {
			this.generateLiteral((LiteralPrimary) node);
			return false;
		}

		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FileUnit) {
			((FileUnit) node).setGeneratedCode(code);
		}
		super.didVisit(node);
	}

	private void generateVariableDeclaration(VariableDeclaration variableDeclaration) throws Exception {
		this.code.add("loadrc " + variableDeclaration.getIndex());
	}

	private void generateVariableAccessRightValue(Name name) throws Exception {
		this.code.add("loadr " + ((Declaration) name.getOriginalDeclaration().getNode()).getIndex());
	}

	private void generateVariableAccessLeftValue(Name name) throws Exception {
		this.code.add("loadrc " + ((Declaration) name.getOriginalDeclaration().getNode()).getIndex());
	}

	private void generateLiteral(LiteralPrimary literal) {
		if (literal.getLiteralType() == LiteralPrimary.LiteralType.NULL) {
			this.code.add("loadc " + NULL);
		} else if (literal.getLiteralType() == LiteralPrimary.LiteralType.INTLIT) {
			this.code.add("loadc " + Integer.parseInt(literal.getValue()));
		} else if (literal.getLiteralType() == LiteralPrimary.LiteralType.BOOLLIT) {
			if (literal.getValue().equals("true")) {
				this.code.add("loadc " + BOOLEAN_TRUE);
			} else {
				this.code.add("loadc " + BOOLEAN_FALSE);
			}
		}
	}

	private void generateUnaryExpression(UnaryExpression unaryExpr)
			throws Exception, SymbolTableException, CodeGenerationException {
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
			if (!this.isDereferenceForLeftValue(unaryExpr)) {
				this.code.add("load");
			}
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
			throw new CodeGenerationException("Code generation internal error: Unsupported unary operation while generating unary expression code.");
		}

		if (unaryExpr.getOperand() instanceof Name
				&& (unaryExpr.getOperator().ordinal() == UnaryExpression.Operator.INCR.ordinal()
						|| unaryExpr.getOperator().ordinal() == UnaryExpression.Operator.DECR.ordinal())) {
			this.generateVariableAccessLeftValue((Name) unaryExpr.getOperand());
			this.code.add("store");
			this.code.add("pop");
		}
	}

	private void generateBinaryExpression(BinaryExpression binaryExpression)
			throws SymbolTableException, CodeGenerationException, Exception {
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
			throw new CodeGenerationException("Code generation internal error: Unsupported binary operation while generating binary expression code.");
		}
	}

	private void generateConditionalExpression(ConditionalExpression conditionalExpression) throws Exception {
		Integer conditionCount = this.conditionCount++;
		String elseMark = JUMP_START + "ELSE_STATEMENT_" + conditionCount;
		String ifEndMark = JUMP_END + "IF_" + conditionCount;

		conditionalExpression.getCondition().accept(this);
		this.code.add("jumpz " + elseMark);
		conditionalExpression.getTrueExpression().accept(this);
		this.code.add("jump " + ifEndMark);
		this.code.add(elseMark + ":");
		conditionalExpression.getFalseExpression().accept(this);
		this.code.add(ifEndMark + ":");
	}

	private void generateAssignmentExpression(AssignmentExpression assignmentExpression)
			throws SymbolTableException, CodeGenerationException, Exception {
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
					throw new CodeGenerationException( "Code generation internal error: Unsupported assignment operation while generating assignment expression code.");
				}
			} else {
				assignmentExpression.getRightValue().accept(this);
			}

			if (assignmentExpression.getLeftValue() instanceof ArrayAccess) {
				this.generateArrayAccessLeftValue((ArrayAccess) assignmentExpression.getLeftValue());
			} else if (assignmentExpression.getLeftValue() instanceof FieldDereferenceAccess) {
				this.generateFieldDereferenceAccessLeftValue(
						(FieldDereferenceAccess) assignmentExpression.getLeftValue());
			} else if (assignmentExpression.getLeftValue() instanceof FieldAccess) {
				this.generateFieldAccessLeftValue((FieldAccess) assignmentExpression.getLeftValue());
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

	private void generateMethodInvoke(ExpressionPrimary expressionPrimary) throws SymbolTableException, Exception {
		MethodInvokeExpression methodInvoke = (MethodInvokeExpression) expressionPrimary.getExpression();
		Name name = (Name) expressionPrimary.getPrefix();

		List<Expression> args = methodInvoke.getArguments();
		if (args.size() > 0) {
			for (int i = args.size() - 1; i >= 0; i--) {
				Expression arg = args.get(i);
				arg.accept(this);
			}
		} else {
			this.code.add("alloc 1");
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
		if ((args.size() - 1) > 0) {
			this.code.add("slide " + (args.size() - 1));
		}

		if (expressionPrimary.getParent() instanceof ExpressionStatement) {
			this.code.add("pop"); // method call without assignment
		}
	}

	private void generateReturnStatement(ReturnStatement returnStatement) throws Exception {
		if (returnStatement.getExpression() != null) {
			returnStatement.getExpression().accept(this);
		}

		code.add("storer -3");
		code.add("pop");
		code.add("return");
	}

	private void generateIfStatement(IfStatement ifStatement) throws Exception {
		Integer conditionCount = this.conditionCount++;
		String elseMark = JUMP_START + "ELSE_STATEMENT_" + conditionCount;
		String ifEndMark = JUMP_START + "IF_END_" + conditionCount;

		if (ifStatement.getIfCondition() != null) {
			ifStatement.getIfCondition().accept(this);
		} else {
			this.code.add("loadc" + BOOLEAN_TRUE);
		}

		if (ifStatement.getElseStatement() != null) {
			this.code.add("jumpz " + elseMark);
		} else {
			this.code.add("jumpz " + ifEndMark);
		}

		ifStatement.getIfStatement().accept(this);

		if (ifStatement.getElseStatement() != null) {
			this.code.add("jump " + ifEndMark);
			this.code.add(elseMark + ":");
			ifStatement.getElseStatement().accept(this);
		}

		this.code.add(ifEndMark + ":");
	}

	// Currently WhileStatement does not contain the inner-Block statement?
	private void generateWhileStatement(WhileStatement whileStatement) throws Exception {
		Integer loopCount = this.loopCount++;
		String loopStartMark = JUMP_START + "WHILE_LOOP_" + loopCount;
		String loopEndMark = JUMP_END + "WHILE_LOOP_" + loopCount;

		this.code.add(loopStartMark + ":");
		if (whileStatement.getWhileCondition() != null) {
			whileStatement.getWhileCondition().accept(this);
		} else {
			this.code.add("loadc " + BOOLEAN_TRUE);
		}

		this.code.add("jumpz " + loopEndMark);

		whileStatement.getWhileStatement().accept(this);

		this.code.add("jump " + loopStartMark);
		this.code.add(loopEndMark + ":");
	}

	private void generateForLoop(ForStatement forStatement) throws Exception {
		if (forStatement.hasInitializer()) {
			forStatement.getInitialization().accept(this);
		}

		Integer loopCount = this.loopCount++;
		String loopStartMark = JUMP_START + "FOR_LOOP_" + loopCount;
		String loopEndMark = JUMP_END + "FOR_LOOP_" + loopCount;

		this.code.add(loopStartMark + ":");
		forStatement.getCondition().accept(this);
		this.code.add("jumpz " + loopEndMark);
		forStatement.getStatement().accept(this);
		forStatement.getIncrement().accept(this);
		this.code.add("jump " + loopStartMark);
		this.code.add(loopEndMark + ":");
	}

	private void generateAllocExpression(AllocExpression allocExpression) throws Exception {
		if (allocExpression.isArrayAlloc()) {
			allocExpression.getArrayAllocationSize().accept(this);
			this.code.add("loadc " + this.getSizeOfType(allocExpression.getAllocationType()));
			this.code.add("mul");
			this.code.add("new");
		} else {
			this.code.add("loadc " + this.getSizeOfType(allocExpression.getAllocationType()));
			this.code.add("new");
		}

		if (allocExpression.getParent() instanceof Statement
				&& !(allocExpression.getParent() instanceof ReturnStatement)) {
			this.code.add("pop"); // not assigned allocation address or returned
		}
	}

	private void generateArrayAccessRightValue(ArrayAccess arrayAccess) throws SymbolTableException, Exception {
		this.generateArrayAccessLeftValue(arrayAccess);
		this.code.add("load");

		if (arrayAccess.getParent() instanceof Statement && !(arrayAccess.getParent() instanceof ReturnStatement)) {
			this.code.add("pop"); // not assigned allocation address or returned
		}
	}

	private void generateArrayAccessLeftValue(ArrayAccess arrayAccess) throws SymbolTableException, Exception {
		arrayAccess.getPrefix().accept(this);
		arrayAccess.getIndexExpression().accept(this);
		this.code.add("loadc " + this.getSizeOfType(arrayAccess.getType()));
		this.code.add("mul");
		this.code.add("add");
	}

	private void generateFieldAccessRightValue(FieldAccess fieldAccess)
			throws CodeGenerationException, SymbolTableException, Exception {
		this.generateFieldAccessLeftValue(fieldAccess);
		this.code.add("load");

		if (fieldAccess.getParent() instanceof Statement && !(fieldAccess.getParent() instanceof ReturnStatement)) {
			this.code.add("pop"); // not assigned allocation address or returned
		}
	}

	private void generateFieldAccessLeftValue(FieldAccess fieldAccess)
			throws CodeGenerationException, SymbolTableException, Exception {
		fieldAccess.getPrefix().accept(this);
		this.code.add("loadc " + this.getFieldAccesFieldIndex(fieldAccess));
		this.code.add("add");
	}

	private void generateFieldDereferenceAccessRightValue(FieldDereferenceAccess fieldDereferenceAccess)
			throws CodeGenerationException, SymbolTableException, Exception {
		this.generateFieldDereferenceAccessLeftValue(fieldDereferenceAccess);
		this.code.add("load");

		if (fieldDereferenceAccess.getParent() instanceof Statement
				&& !(fieldDereferenceAccess.getParent() instanceof ReturnStatement)) {
			this.code.add("pop"); // not assigned allocation address or returned
		}
	}

	private void generateFieldDereferenceAccessLeftValue(FieldDereferenceAccess fieldDereferenceAccess)
			throws CodeGenerationException, SymbolTableException, Exception {
		fieldDereferenceAccess.getPrefix().accept(this);

		this.code.add("loadc " + this.getFieldDereferenceFieldIndex(fieldDereferenceAccess));
		this.code.add("add");
	}

	private int getSizeOfType(Type type) throws SymbolTableException {
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

		return typeSize;
	}

	private int getFieldAccesFieldIndex(FieldAccess fieldAccess) throws CodeGenerationException, SymbolTableException {
		StructType structType = (StructType) fieldAccess.getPrefix().getType();
		return this.getStructFieldIndex(structType, fieldAccess.getFieldIdentifier().getName());
	}

	private int getFieldDereferenceFieldIndex(FieldDereferenceAccess fieldDereferenceAccess)
			throws CodeGenerationException, SymbolTableException {
		StructType structType = (StructType) fieldDereferenceAccess.getPrefix().getType();
		return this.getStructFieldIndex(structType, fieldDereferenceAccess.getFieldIdentifier().getName());
	}

	private int getStructFieldIndex(StructType structType, String fieldName)
			throws CodeGenerationException, SymbolTableException {
		StructTypeScope structScope = this.table.getStructTypeScope(structType.getScopeName());

		int fieldIndex = -1;

		for (Symbol symbol : structScope.getSymbols()) {
			FieldDefinition fieldDef = (FieldDefinition) symbol.getNode();
			if (((FieldDefinition) symbol.getNode()).getName().getName().equals(fieldName)) {
				fieldIndex = fieldDef.getIndex();
				break;
			}
		}

		if (fieldIndex == -1) {
			throw new CodeGenerationException("Not exisiting field \"" + fieldName + "\" in struct type \""
					+ structType.getFullyQualifiedName() + "\" while generating field access code.");
		}

		return fieldIndex;
	}

	private boolean isDereferenceForLeftValue(UnaryExpression unaryExpr) {
		boolean isDereferenceForLeftValue = false;
		ASTNode currentParent = unaryExpr.getParent();

		while (!(currentParent instanceof FileUnit)) {
			if (currentParent instanceof AssignmentExpression || currentParent instanceof FieldAccess) {
				isDereferenceForLeftValue = true;
				break;
			} else if (currentParent instanceof UnaryExpression && ((UnaryExpression) currentParent).getOperator()
					.ordinal() == UnaryExpression.Operator.STAR.ordinal()) {
				break;
			}

			currentParent = currentParent.getParent();
		}

		return isDereferenceForLeftValue;
	} 
}