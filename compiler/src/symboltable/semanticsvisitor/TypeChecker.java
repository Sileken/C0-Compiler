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
import ast.definition.*;
import ast.identifier.*;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;

// Some Notes:
// - it can parse "void b;" => NEED A CHECK THAT A VARIABLE IS NOT VOID 
//      => I've put that into DeepDeclarationVisitor.java when the type is set
//      => VOID ARRAY STILL POSSIBLE
// - divison by zero check?
// - infinite function recursion check?
// - added Type exprType in Expression-Class => MIGHT NOT BE SET HERE ALWAYS
// - it does not check that a struct-type exists e.g. struct list* as = alloc(struct l);
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

	final boolean DEBUG_PRINT = true;
         
	// This is from the Joos-Compiler, but i never figured it out why he needs several type-stacks
	// At the moment there is only one type-stack used (pushed in the constructor)
	// Indent: Types are pushed onto the stack if encountered and popped if something 
	// has to be checked (e.g. a binary expression). The parsing is bottom-up, which means
	// that types will be pushed onto the stack from LEAVES first.
	private Stack<Stack<Type>> typeStacks;

	// The return type of the current function we are in
	// Used to check if a "return" statement matches the function return type
	private Type currentFunctionReturnType = null;

	public TypeChecker(SymbolTable table) {
		super(table);
		this.typeStacks = new Stack<Stack<Type>>();
		typeStacks.push(new Stack<Type>());
	}


	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {

		if(node instanceof FunctionDefinition)
		{
			currentFunctionReturnType = ((FunctionDefinition)node).getType();
		}
		
		super.willVisit(node);
	}

	public boolean visit(ASTNode node) throws Exception {

		return super.visit(node);
	}

	// This function is called first for the LEAVES => BOTTOM-UP TYPE CHECKING
	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {

		if(node instanceof FileUnit)
		{
			// Check that Type-Stack is empty (it should)
			if(!getCurrentTypeStack().isEmpty())
			{
				while(!getCurrentTypeStack().isEmpty())
				{
					Type type = popType();
					System.out.println(">>>>> @FileUnit <<<<<< Type still in stack: " + type);
				}
				throw new TypeException("Type-Stack is not empty, something might be wrong.");
			}
		}
	    else if(node instanceof PrimitiveType)
		{
			if(DEBUG_PRINT) System.out.print("@PrimitiveType ");
			pushType((PrimitiveType)node);
		}	
		else if(node instanceof ArrayType)
		{
			// Pop the primitive-type and push the corresponding array-type
			Type arrType = popType();
			if(DEBUG_PRINT) System.out.print("@ArrayType ");
			pushType((ArrayType)node);
		}
		else if(node instanceof ReferenceType)
		{
			// pop primitive type and push corresponding reference-type
			Type primType = popType();
			if(DEBUG_PRINT) System.out.print("@ReferenceType ");
			pushType((ReferenceType)node);
		}	
		else if(node instanceof ExpressionStatement)
		{
			// consume type after ";"
			popType();
		}
		else if(node instanceof ConditionalExpression)
		{
			Type firstExpr = popType();
			Type secondExpr = popType();
			Type condition = popType();

			if(!condition.getFullyQualifiedName().equals("BOOL"))
			 	throw new TypeException("@ConditionExpression: Expected condition of type 'BOOL' but got '" + condition + "'");

			if(!firstExpr.getFullyQualifiedName().equals(secondExpr.getFullyQualifiedName()))
				throw new TypeException("@ConditionExpression: Both alternatives have to be the same type, but have "
				                        + "'" + firstExpr + "' and '" + secondExpr + "'");
			
			// Both alternative have to be the same type => the return type is also that type
			pushType(firstExpr);
		}
		else if(node instanceof FieldDefinition)
		{
			// Pop primitive-type for a struct-field
			Type type = popType();
		}
		else if(node instanceof StructDefinition || node instanceof StructDeclaration)
		{
			// Pop struct type
			popType();
		}
		else if(node instanceof StructType)
		{
			if(DEBUG_PRINT) System.out.print("@StructType ");
			pushType((StructType)node);
		}
		else if(node instanceof FieldAccess)
		{
			FieldIdentifier identifier = ((FieldAccess)node).getFieldIdentifier();

			Type poppedType = popType();
			if(!(poppedType instanceof StructType))
				throw new TypeException("FieldAccess: Expected 'Struct' but got '" + poppedType + "'");

			// Get the struct-scope from the symbol-table
			StructType structType = (StructType)poppedType;
			StructTypeScope structScope = table.getStructTypeScope(structType.getScopeName());

			// Get the symbol from the struct-scope
			Symbol symbol = structScope.getFieldDefinition(identifier);
			Type fieldType = symbol.getType();

			if(DEBUG_PRINT) System.out.print("@FieldAccess ");
			pushType(fieldType);

			// Add information to the AST
			identifier.setType(fieldType);
		}
		else if(node instanceof FieldDereferenceAccess)
		{
			FieldIdentifier identifier = ((FieldDereferenceAccess)node).getFieldIdentifier();
			Type poppedType = popType();
			
			if(!(poppedType instanceof ReferenceType))
				throw new TypeException("FieldDereferenceAccess: Expected 'Struct*' but got '" + poppedType + "'");

			ReferenceType structRefType = (ReferenceType)poppedType;

			// Get the struct-scope from the symbol-table
			Type innerType = structRefType.getInnerType();
			if(!(innerType instanceof StructType))
				throw new TypeException("FieldDereferenceAccess: Expected 'Struct*' but got '" + poppedType + "'");

			StructType structType = (StructType) innerType;
			StructTypeScope structScope = table.getStructTypeScope(structType.getScopeName());

			// Get the symbol from the struct-scope
			Symbol symbol = structScope.getFieldDefinition(identifier);
			Type fieldType = symbol.getType();

			if(DEBUG_PRINT) System.out.print("@FieldDereferenceAccess ");
			pushType(fieldType);

			// Add information to the AST
			identifier.setType(fieldType);
		}
		else if(node instanceof VariableDeclarationExpression)
		{
			// consume type -> e.g. bool a; (PrimitiveType pushes "bool" and VariableDeclarationExpression pops it)
			// NOT ANYMORE -> ExpressionStatement pops this
			// TODO: SOMETHING TO DO HERE??
			//Type type = popType();
			//((VariableDeclarationExpression)node).setType(type);
			//pushType(type);
		}
		else if(node instanceof VariableDeclaration)
		{
			// TODO: is there a better way?
			// This pop is for function parameters (normal variable decl. get popped through ExpressionStatement)
			if(node.getParent() instanceof FunctionDefinition)
				popType();
		}
		else if(node instanceof FunctionDeclaration)
		{
			// Pop all argument types and function return type
			List<Type> args = ((FunctionDeclaration)node).getParameterTypes();
			for(Type arg : args)
				popType();

			popType();	// return type
		}
		else if(node instanceof FunctionDefinition)
		{
			// just pop the return type and set the current function return type to null
			Type returnType = popType();
			currentFunctionReturnType = null;
		}		
		else if(node instanceof ReturnStatement)
		{
			// Check if return type match function type
			if(((ReturnStatement)node).hasExpression())
			{
				Type returnType = popType();

				if(currentFunctionReturnType instanceof ReferenceType && returnType instanceof NullType)
				{ /* NULL can be returned for pointers */ }
				else if(!returnType.getFullyQualifiedName().equals(currentFunctionReturnType.getFullyQualifiedName()))
					throw new TypeException("@ReturnStatement: Return type '" + returnType 
											+ "' does not match function type '" + currentFunctionReturnType + "'");
			} else { // empty return statement e.g. return;
				if(currentFunctionReturnType.getFullyQualifiedName() != "VOID")
					throw new TypeException("@ReturnStatement: Function must return '" + currentFunctionReturnType + "' but is 'VOID'");
			}
		}
		else if (node instanceof Name) {
			// Can be a VARIABLE or a FUNCTION name

			// If the node has a reference to the declaration, get the type of it and push it onto the stack
			Symbol symbol = ((Name) node).getOriginalDeclaration();
			
			// Find the symbol manually
			if(symbol == null)
			{
				System.out.println("[Warning]@Name: Could not get symbol '" + node.getIdentifier() + "' from the node itself."
				                   + " Try to manually acquire it from the current scope.");

				BlockScope currentScope = (BlockScope) this.getCurrentScope();
				symbol = currentScope.resolveVariableDeclaration((Name) node);
				if(symbol == null)
				{
					// If we get to here, then the node is a FunctionIdentifier
					// We ignore it and check the correct function types in MethodInvokeExpression
					//throw new TypeException("@Name: Could not find symbol of '" + ((Name) node).getName() + "'");
				} else {
					// TODO: CAN THIS HAPPEN?
					((Name) node).setOriginalDeclaration(symbol);
				}
			} 

			if(symbol != null)
			{
				if(DEBUG_PRINT) System.out.print("@Name: " + symbol.getName() + " ");
				pushType(symbol.getType());
			}
		}
		else if(node instanceof MethodInvokeExpression)
		{
			MethodInvokeExpression method = (MethodInvokeExpression) node;

			Primary prefix = method.getPrefix();			
			String funcName = prefix.getIdentifier(); // TODO: IS THIS ALWAYS THE FUNCTION NAME?

			List<Expression> args = method.getArguments();

			// Pop the amount of argument-types from the stack
			List<Type> argumentTypes = new ArrayList<Type>();
			for(int i = 0; i < args.size(); i++)
			{
				Type argType = popType();
				argumentTypes.add(0, argType);
			}

			// Check if a function exists with the arguments and name
			FileUnitScope fileUnitScope = table.getFileUnitScope();
			String functionSignature = fileUnitScope.getSignatureOfFunction(funcName, argumentTypes);
			if(DEBUG_PRINT) System.out.println(">>>>>> Function Signature: " + functionSignature);

			Symbol symbol = fileUnitScope.resolveFunctionSymbol(functionSignature);

			// If symbol was not found, function with those arguments does not exist
			if(symbol == null)
			{
				String argString = "";
				for(int i = 0; i < argumentTypes.size() - 1; i++){
					argString += argumentTypes.get(i) + ", " ;
				}
				argString += argumentTypes.get(argumentTypes.size() - 1);
				throw new TypeException("@MethodInvokeExpression: Could not resolve function '" + funcName + "("+ argString +")'");
			}

			// Push function type on stack
			if(DEBUG_PRINT) System.out.print("@MethodInvokeExpression: ");
			Type functionType = symbol.getType();
			pushType(functionType);	

			// Add information to ast
			if(!(prefix instanceof Name))
				throw new TypeException("Ooops @MethodInvokeExpression, thought every Prefix is a NAME but it wasn't!");
			
			((Name)prefix).setOriginalDeclaration(symbol);
			method.setType(functionType);
		}
		else if(node instanceof LiteralPrimary)
		{
			LiteralPrimary.LiteralType litType = ((LiteralPrimary)node).getLiteralType();
			try
			{
				Type type = ((LiteralPrimary)node).getType();
				if(DEBUG_PRINT) System.out.print("@LiteralPrimary ");
				pushType(type);
			} catch(Exception e){ e.printStackTrace(); }
		}
		else if(node instanceof ArrayAccess)
		{
			Type indexType = popType();
			if(indexType.getFullyQualifiedName() != "INT")
				throw new TypeException("Type error: Array index type must be 'INT' but is '" + indexType + "'");

			ArrayType arrayType = (ArrayType)popType();
			if(DEBUG_PRINT) System.out.print("@ArrayAccess ");
			pushType(arrayType.getType());
		}
		else if(node instanceof AssignmentExpression)
		{
			Type assignType = popType();
			Type varType = popType();

			AssignmentExpression.Operator op = ((AssignmentExpression)node).getOperator();

			if(!checkAssignmentExpression(varType, assignType, op))
			{
				// This is a bit hacky because the class AssignmentExpression contains either a 
				// VariableDeclaration OR an Expression (which is ugly by the way :D)
				VariableDeclaration var = ((AssignmentExpression)node).getVariableDeclaration();
				if(var != null)	{
					String lValueString = varType + " " + var.getIdentifier();

					throw new TypeException("AssignmentExpression: Can not " + op + " type '"
											+ assignType + "' to variable '" + lValueString + "'");
				} else {
					Expression exp = ((AssignmentExpression)node).getLeftValue();
					String lValueString = varType.getFullyQualifiedName();
					// TODO: There might be a better way for this
					if(!exp.getIdentifier().isEmpty())
						lValueString += " " + exp.getIdentifier();

					throw new TypeException("AssignmentExpression: Can not " + op + " type '"
											+ assignType + "' to lValue type '" + lValueString + "'");
				}
				
			}

			// add var-type to the stack because assignments can be further processed e.g. (a = 5) * 10;
			if(DEBUG_PRINT) System.out.print("@AssignmentExpression ");
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
				throw new TypeException("Type error: '" + leftType + "' and '" + rightType + 
				                        "' does not match with operator '" + op + "'");
			}

			if(DEBUG_PRINT) System.out.print("@BinaryExpression ");
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
				throw new TypeException("Unary operator '" + op + "' is not compatible with type '" + exprType + "'");
			}

			if(DEBUG_PRINT) System.out.print("@UnaryExpression ");
			pushType(resultType);

			// Add type information to AST
			((UnaryExpression)node).setType(resultType);
		}
		else if(node instanceof IfStatement)
		{
			Type exprType = popType();

			if(exprType.getFullyQualifiedName() != "BOOL")
			{
				throw new TypeException("Type error in if-statement: Expected 'BOOL' but type was: " + exprType);
			}
		}		
		else if(node instanceof WhileStatement)
		{
			Type exprType = popType();

			if(exprType.getFullyQualifiedName() != "BOOL")
			{
				throw new TypeException("Type error in while-statement: Expected 'BOOL' but type was: " + exprType);
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
				throw new TypeException("Type error in for-statement: "
				                      + "Expected 'BOOL' from condition but type was '" + conditionType + "'");
			}

			Expression init = forStmt.getInitialization();
			if(init != null)
				popType();
		}
		else if(node instanceof AllocExpression)
		{
			if(((AllocExpression)node).isArrayAlloc())
			{
				// alloc_array(type, amount)
				Type amtType = popType();
				Type allocType = popType();

				if(amtType.getFullyQualifiedName() != "INT")
					throw new TypeException("2. argument of alloc_array must be 'INT' but is '" + amtType + "'");
				
				// push a new array type to the stack
				if(DEBUG_PRINT) System.out.print("@AllocExpression ");
				pushType(new ArrayType(allocType));
			}else{
				// alloc(type)
				Type allocType = popType();

				// push a new pointer type to the stack
				if(DEBUG_PRINT) System.out.print("@AllocExpression ");
				pushType(new ReferenceType(allocType));
			}
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
			if(type instanceof ReferenceType)
			{
				return ((ReferenceType)type).getInnerType();
			} else {
				return null;
			}

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
		if(!typeName1.equals(typeName2))
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

	// TODO: Are the types correct?
	private boolean checkAssignmentExpression(Type varType, Type assignType, AssignmentExpression.Operator op) {
		String varTypeName = varType.getFullyQualifiedName();
		String assignTypeName = assignType.getFullyQualifiedName();

		// NULL can be assigned to a pointer
		if(varType instanceof ReferenceType && assignType instanceof NullType)
		{
			// To nothing here, check which op is valid for this assignment below
		}
		// Both types HAS TO BE EQUAL in all remaining cases
		else if(!varTypeName.equals(assignTypeName))
			return false;		

		switch(op)
		{
			// "="  => every type is allowed
			case ASSIGN:
			return true;

			// "+=", "-=", "*=", "/=", "%=" => types must be [INT]
			case PLUSASSIGN:
			case MINUSASSIGN:
			case STARASSIGN:
			case SLASHASSIGN:
			case REMASSIGN:
			return varTypeName == "INT" ? true : false;

			// "&=", "^=", "|=" => types must be [INT]
			case ANDASSIGN:
			case XORASSIGN:
			case ORASSIGN:
			return varTypeName == "INT" ? true : false;
		}
		return false;
	}
	
	private Stack<Type> getCurrentTypeStack() {
		if (!this.typeStacks.isEmpty())
			return this.typeStacks.peek();
		return null;
	}

	private void pushType(Type type) {
		if(DEBUG_PRINT) System.out.println("> Pushing Type " + type.getFullyQualifiedName());
		this.getCurrentTypeStack().push(type);
	}

	private Type popType() {
		Type type = this.getCurrentTypeStack().pop();
		if(DEBUG_PRINT) System.out.println("Popping Type " + type.getFullyQualifiedName());
		return type;
	}
}