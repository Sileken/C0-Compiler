package symboltable.semanticsvisitor;

import java.util.HashSet;
import java.util.Set;

import ast.*;
import ast.statement.*;
import ast.expression.*;
import ast.expression.primary.*;
import ast.expression.primary.name.*;
import ast.type.*;
import ast.identifier.*;
import symboltable.*;

/** The Name Linker Visitor links declaration nodes only to names from variables */
public class NameLinker extends SemanticsVisitor {

	public NameLinker(SymbolTable table) {
		super(table);
	}

	public boolean visit(ASTNode node) throws SymbolTableException, Exception {
		if (node instanceof Type) {
			return false; // Type child nodes can be ignored
		} else if (node instanceof ExpressionPrimary) { // Check first ExpressionPrimary then MethodInvokeExpression to prevent visiting Name from MethodInvokeExpression
			ExpressionPrimary expPrimary = (ExpressionPrimary) node;
			Expression exp = expPrimary.getExpression();
			if (exp instanceof MethodInvokeExpression) {
				MethodInvokeExpression methodInv = (MethodInvokeExpression) exp;
				Primary prefix = methodInv.getPrefix();
				if (prefix != null && !(prefix instanceof Name)) { // declaration node from method name can't be resolved from name linker, because argument types are unknown
					prefix.accept(this); // Check prefix childs -> check name
				}
				for (Expression expr : methodInv.getArguments()) {
					expr.accept(this); // Check arguments -> check arguments name
				}
				return false; // other childs can be ignored
			}
			// ExpressionPrimary childs like Pointer (UnaryExp.* -> Name ) can be processed 
		} else if (node instanceof FieldAccess || node instanceof FieldDereferenceAccess) {
			((Primary) node).getPrefix().accept(this); // Check prefix childs -> check name

			return false;
		} else if (node instanceof Name) {
			String name = ((Name) node).getName();
			BlockScope currentScope = (BlockScope) this.getCurrentScope();

			Symbol resolvedDeclaration = currentScope.resolveVariableDeclaration((Name) node);

			if (resolvedDeclaration != null) {
				((Name) node).setOriginalDeclaration(resolvedDeclaration);
				System.out.println(
						"Resolved " + name + " => " + resolvedDeclaration.getName() + "\tParent: " + node.getParent());
			} else {
				throw new SymbolTableException("Fail to resolve \"" + name + "\" in scope \"" + currentScope + "\"");
				//System.out.println("Fail to resolve \"" + name + "\" in scope \"" + currentScope + "\"");
			}
		} 
		return true;
	}
}