package symboltable.semanticsvisitor;

import ast.*;
import symboltable.*;
import ast.expression.*;
import ast.expression.primary.*;
import ast.type.*;
import ast.declaration.*;
import ast.expression.primary.*;
import ast.expression.primary.name.*;
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
		}else if (node instanceof Name) {
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

			// Add type information to AST
			((AssignmentExpression)node).setType(assignType);
		}
		else if(node instanceof BinaryExpression)
		{
			Type rightType = popType();
			Type leftType = popType();

			Type resultType = binaryExpression(leftType, rightType);
			if(resultType == null)
			{
				String leftString = leftType.getFullyQualifiedName();
				String rightString = rightType.getFullyQualifiedName();
				throw new SymbolTableException("Type error: '" + leftString + "' and '" + rightString + "' does not match");
			}

			pushType(resultType);

			// Add type information to AST
			((BinaryExpression)node).setType(resultType);
		}

		super.didVisit(node);
	}


	private Type binaryExpression(Type lType, Type rType)
	{
		// For now assume that the types has to be equal, so just return the first type if thats the case
		if(lType.getFullyQualifiedName() == rType.getFullyQualifiedName())
			return lType;

		return null;
	}

	// TODO: check which operator is allowed for which type
	private boolean isAssignable(Type varType, Type assignType, AssignmentExpression.Operator op) {
		System.out.println("VarType: " + varType + " " + op +  " assignType: " + assignType);
		
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