package symboltable.semanticsvisitor;

import ast.*;
import symboltable.*;
import ast.expression.*;
import ast.expression.primary.*;
import ast.type.*;
import ast.declaration.*;
import ast.expression.primary.*;
import ast.expression.primary.name.*;
import ast.statement.*;
import java.util.Stack;

// Some Notes:
// - it can parse "void b;" => NEED A CHECK THAT A VARIABLE IS NOT VOID 
//      => I've put that into DeepDeclarationVisitor.java when the type is set
// - divison by zero check?
// - infinite function recursion check?
// - which operators are defined for which types? e.g. bool b = true; b %= false;
// -------------------------------------------------------------------
// Examples:
// x + y  		| "x" and "y" must have numeric types
// x = a  		| types must match
// if(exp) 		| "exp" must be boolean
// a[exp] 		| "a" must be type array, exp must be int
// r.f 			| "r" must be struct
// foo(a,b,c) 	| "args" must have right types + right amount of args
//  => check if return statement is present if not [VOID]
//  => check that return type match function return type
// p* 			| "p" must be a pointer
//  => convert "p->a" to (*p).a, then check type? 

public class TypeChecker extends SemanticsVisitor {
         
	// This is from the Joos-Compiler, but i never figured it out why he needs several type-stacks
	// At the moment there is only one type-stack used (pushed in the constructor)
	// Indent: Types are pushed onto the stack if encountered and popped if something 
	// has to be checked (e.g. a binary expression). The parsing is bottom-up, which means
	// that types will be pushed onto the stack from LEAVES first.
	private Stack<Stack<Type>> typeStacks;

	public TypeChecker(SymbolTable table) {
		super(table);
		this.typeStacks = new Stack<Stack<Type>>();
		typeStacks.push(new Stack<Type>());
	}


	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		
		super.willVisit(node);
	}

	public boolean visit(ASTNode node) throws Exception {

		return super.visit(node);
	}

	// This function is called first for the LEAVES => BOTTOM-UP TYPE CHECKING
	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {

		if(node instanceof PrimitiveType)
		{
			pushType((PrimitiveType)node);
		}	
		else if(node instanceof VariableDeclarationExpression)
		{
			// consume type -> e.g. bool a; (PrimitiveType pushes "bool" and VariableDeclarationExpression pops it)
			popType();
		}
		else if(node instanceof ExpressionStatement)
		{
			// consume type after ";"
			popType();
		}
		else if (node instanceof Name) {
			// If the node is just a variable, get the type of it and push it onto the stack
			Symbol symbol = ((Name) node).getOriginalDeclaration();
			pushType(symbol.getType());
		}
		else if(node instanceof LiteralPrimary)
		{
			LiteralPrimary.LiteralType litType = ((LiteralPrimary)node).getLiteralType();
			try
			{
				Type type = ((LiteralPrimary)node).getType();
				pushType(type);
			} catch(Exception e){ e.printStackTrace(); }
		}
		else if(node instanceof AssignmentExpression)
		{
			Type assignType = popType();
			Type varType = popType();

			AssignmentExpression.Operator op = ((AssignmentExpression)node).getOperator();

			if(!isAssignable(varType, assignType, op))
			{
				// This is a bit hacky because the class AssignmentExpression contains either a 
				// VariableDeclaration OR an Expression (which is ugly by the way :D)
				VariableDeclaration var = ((AssignmentExpression)node).getVariableDeclaration();
				String lValueString = "";
				if(var != null)	{
					lValueString = varType.getFullyQualifiedName() + " " + var.getIdentifier();
				} else {
					Expression exp = ((AssignmentExpression)node).getLeftValue();
					lValueString = exp.getIdentifier();
				}
				
				String assignString = assignType.getFullyQualifiedName();
				throw new SymbolTableException("Can not assign type '" + assignString + "' to variable '" + lValueString + "'");
			}

			// add var-type to the stack because assignments can be further processed e.g. (a = 5) * 10;
			pushType(varType);

			// Add type information to AST
			((AssignmentExpression)node).setType(assignType);
		}
		else if(node instanceof BinaryExpression)
		{
			Type rightType = popType();
			Type leftType = popType();

			BinaryExpression.Operator op = ((BinaryExpression)node).getOperator();

			Type resultType = checkBinaryExpression(leftType, op, rightType);
			if(resultType == null)
			{
				throw new SymbolTableException("Type error: '" + leftType + "' and '" + rightType + 
				                               "' does not match with operator '" + op + "'");
			}

			pushType(resultType);

			// Add type information to AST
			((BinaryExpression)node).setType(resultType);
		}
		else if(node instanceof UnaryExpression)
		{
			Type exprType = popType();
			UnaryExpression.Operator op = ((UnaryExpression)node).getOperator();

			// TODO: is the result-type always the same? "Dereference" is a unary-exp -> result-type could change
			Type resultType = checkUnaryExpression(exprType, op);
			if(resultType == null)
			{
				throw new SymbolTableException("Unary operator '" + op + "' is not compatible with type '" + exprType + "'");
			}

			pushType(resultType);
		}
		else if(node instanceof IfStatement)
		{
			Type exprType = popType();

			// This is another way of getting the type, without the stack
			// try{
			// 	Expression exp = ((IfStatement)node).getCondition();
			// 	Type exprType = exp.getType();
			// }catch(Exception e){e.printStackTrace();}

			if(exprType.getFullyQualifiedName() != "BOOL")
			{
				throw new SymbolTableException("Type error in if-statement: Expected [BOOL] but type was: " + exprType);
			}
		}
		else if(node instanceof ForStatement)
		{
			ForStatement forStmt = (ForStatement)node;

			Expression increment = forStmt.getIncrement();
			if(increment != null)
				popType();

			Type conditionType = popType();
			if(conditionType.getFullyQualifiedName() != "BOOL")
			{
				throw new SymbolTableException("Type error in for-statement: "
				                             + "Expected [BOOL] from condition but type was [" + conditionType + "]");
			}

			Expression init = forStmt.getInitialization();
			if(init != null)
				popType();
		}

		super.didVisit(node);
	}

	// Return the type for a given type and unary operator
	// TODO: check if that is all correct
	// TODO: add dereference support
	private Type checkUnaryExpression(Type type, UnaryExpression.Operator op)
	{
		String typeName = type.getFullyQualifiedName();
		switch(op)
		{
			// "!" => type must be [BOOL], result is [BOOL]
			case BANG:
			return typeName == "BOOL" ? type : null;
			
			// "~" : bitwise not => type must be [INT], result is [INT]
			case TILDE:
			return typeName == "INT" ? type : null;

			// "-" => type must be [INT], result is [INT]
			case MINUS:
			return typeName == "INT" ? type : null;

			// "*" => dereferencing, type must be [POINTER], result is [POINTER-TYPE]
			case STAR:
			return null;

			// "++", "--" => type must be [INT], result is [INT]
			case INCR:
			case DECR:
			return typeName == "INT" ? type : null;
		}
		return null;
	}

	// Return the result type for a binary expression for two given types and an operator
	private Type checkBinaryExpression(Type lType, BinaryExpression.Operator op, Type rType)
	{
		String typeName1 = lType.getFullyQualifiedName();
		String typeName2 = rType.getFullyQualifiedName();

		// Both types HAS TO BE EQUAL in all cases
		if(typeName1 != typeName2)
			return null;

		switch(op)
		{
			// "||", "&&" => types MUST be [BOOL], result is [BOOL]
			case OR: case AND: 
			return typeName1 == "BOOL" ? lType : null;

			// "|", "^", "&" => types MUST be [INT], result is [INT]
			case BOR: case BXOR: case BAND: 
			return typeName1 == "INT" ? lType : null;

			// "==", "!=" => type is wayne, result is [BOOL]
			case EQ: case NEQ:		
			return new PrimitiveType(PrimitiveType.Primitive.BOOL);

			// "<", ">", "<=", ">=" => types must be [INT], result is [BOOL] (TODO: check if types MUST BE INT???)
			case LT: case GT: case LEQ: case GEQ:
			return typeName1 == "INT" ? new PrimitiveType(PrimitiveType.Primitive.BOOL) : null;

			// "+", "-", "*", "/", "%" => types must be [INT], result is [INT]
			case PLUS: case MINUS: case STAR: case SLASH: case REM: 
			return typeName1 == "INT" ? lType : null;
		}
		return null;
	}

	// TODO: check which operator is allowed for which type (e.g. "+=" is not allowed for BOOLEANS)
	private boolean isAssignable(Type varType, Type assignType, AssignmentExpression.Operator op) {
		//System.out.println("VarType: " + varType + " " + op +  " assignType: " + assignType);
		
		// same types are assignable
		if(varType.getFullyQualifiedName() == assignType.getFullyQualifiedName())
			return true;

		// Joos Compiler has this but it does not work here but why??
		//if(varType.equals(assignType)) 
		//	return true;

		return false;
	}

	
	private Stack<Type> getCurrentTypeStack() {
		if (!this.typeStacks.isEmpty())
			return this.typeStacks.peek();
		return null;
	}

	private void pushType(Type type) {
		System.out.println("> Pushing Type " + type.getFullyQualifiedName());
		this.getCurrentTypeStack().push(type);
	}

	private Type popType() {
		Type type = this.getCurrentTypeStack().pop();
		System.out.println("Popping Type " + type.getFullyQualifiedName());
		return type;
	}
}