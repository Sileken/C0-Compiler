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
	protected static final String BOOLEAN_TRUE = "1";
	protected static final String BOOLEAN_FALSE = "0";
	protected static final String NULL = "0";

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
			int max = 150; // ToDo: calculate max (simple version: count arithmetic operations in body..)
			int q = max + k;

			this.code.add("_" + methodLabel + ":" + " enter " + q);
			this.code.add("alloc " + k);
		}
	}

	// todo: find other positible code generation nodes
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
			this.generateFieldAccessRightValue((FieldAccess) node);
			return false;
		} else if (node instanceof FieldDereferenceAccess) {
			this.generateFieldDereferenceAccess((FieldDereferenceAccess) node);
			return false;
		}

		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FileUnit) {
			this.writeCodeIntoFile((FileUnit) node);
		} else if (node instanceof FunctionDefinition) {
			this.code.add("return");
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
			throws SymbolTableException, CodeGenerationException {
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
			this.code.add("load");
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

	private void generateBinaryExpression(BinaryExpression binaryExpression)
			throws SymbolTableException, CodeGenerationException {
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
		if((args.size() - 1) > 0){
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
			this.code.add("loadc " + BOOLEAN_TRUE);
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

	private void generateArrayAccessRightValue(ArrayAccess arrayAccess) throws Exception {
		this.generateArrayAccessLeftValue(arrayAccess);
		this.code.add("load");

		if (arrayAccess.getParent() instanceof Statement && !(arrayAccess.getParent() instanceof ReturnStatement)) {
			this.code.add("pop"); // not assigned allocation address or returned
		}
	}

	private void generateArrayAccessLeftValue(ArrayAccess arrayAccess) throws Exception {
		arrayAccess.getPrefix().accept(this);
		arrayAccess.getIndexExpression().accept(this);
		this.code.add("loadc " + this.getSizeOfType(arrayAccess.getType()));
		this.code.add("mul");
		this.code.add("add");
	}

	private void generateFieldAccessRightValue(FieldAccess fieldAccess) throws Exception {
		this.generateFieldAccessLeftValue(fieldAccess);
		this.code.add("load");

		if (fieldAccess.getParent() instanceof Statement && !(fieldAccess.getParent() instanceof ReturnStatement)) {
			this.code.add("pop"); // not assigned allocation address or returned
		}
	}

	private void generateFieldAccessLeftValue(FieldAccess fieldAccess) throws Exception {
		boolean isPrefDeref = false;
		if (fieldAccess.getPrefix() instanceof ExpressionPrimary) {
			ExpressionPrimary primExp = (ExpressionPrimary) fieldAccess.getPrefix();
			if (primExp.getExpression() instanceof ExpressionPrimary) {
				ExpressionPrimary primExp2 = (ExpressionPrimary) primExp.getExpression();
				if (primExp2.getExpression() instanceof UnaryExpression) {
					UnaryExpression unExp = (UnaryExpression) primExp2.getExpression();
					if (unExp.getOperator().ordinal() == UnaryExpression.Operator.STAR.ordinal()) {
						isPrefDeref = true;
						unExp.getOperand().accept(this); // (*p).field: prevent dereferencing p
					}
				}
			}
		}

		if (!isPrefDeref) {
			fieldAccess.getPrefix().accept(this);
		}

		this.code.add("loadc " + this.getFieldAccesFieldIndex(fieldAccess));
		this.code.add("add");
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

		return typeSize;
	}

	private int getFieldAccesFieldIndex(FieldAccess fieldAccess) throws CodeGenerationException, SymbolTableException {
		StructType structType = (StructType) fieldAccess.getPrefix().getType();
		StructTypeScope structScope = this.table.getStructTypeScope(structType.getScopeName());

		int fieldIndex = -1;

		for (Symbol symbol : structScope.getSymbols()) {
			FieldDefinition fieldDef = (FieldDefinition) symbol.getNode();
			if (((FieldDefinition) symbol.getNode()).getName().getName()
					.equals(fieldAccess.getFieldIdentifier().getName())) {
				fieldIndex = fieldDef.getIndex();
				break;
			}
		}

		if (fieldIndex == -1) {
			String msg = "Not exisiting Field \"" + fieldAccess.getFieldIdentifier().getName() + "\" in struct type \""
					+ structType.getFullyQualifiedName() + "\" while field access code.";
			Logger.error(msg);
			throw new CodeGenerationException(msg);
		}

		return fieldIndex;
	}

	private void writeCodeIntoFile(FileUnit fileUnit) {
		String fileName = fileUnit.getIdentifier();
		fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		fileName += ".cma";

		File cmaFile = new File(fileName);

		Logger.debug("Writing content to: " + cmaFile.getAbsoluteFile());

		File dir = cmaFile.getAbsoluteFile().getParentFile();
		if (dir != null) {
			dir.mkdirs();
		}

		try {
			cmaFile.createNewFile();
			try (FileWriter fw = new FileWriter(cmaFile); BufferedWriter bw = new BufferedWriter(fw);) {
				for (int i = 0; i < code.size(); i++) {
					String line = code.get(i);

					if (line.endsWith(":")) {
						line += " " + code.get(++i);
					}

					bw.write(line);
					bw.newLine();
				}

				bw.close();
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}