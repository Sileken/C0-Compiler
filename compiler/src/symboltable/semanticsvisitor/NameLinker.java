package symboltable.semanticsvisitor;

import java.util.HashSet;
import java.util.Set;

import ast.*;
import ast.statement.*;
import ast.expression.*;
import ast.expression.primary.*;
import ast.expression.primary.name.*;
import ast.type.*;
import symboltable.*;

public class NameLinker extends SemanticsVisitor {

/*	private int linkName = 0;
	private int checkForwardRef = -1;*/
	private Set<String> visitedFields;

	public NameLinker(SymbolTable table) {
		super(table);
/*		this.linkName = 0;
		this.checkForwardRef = -1;*/
		this.visitedFields = new HashSet<String>();
	}

	@Override
	public void willVisit(ASTNode node) throws SymbolTableException{
/*		if (node instanceof Statement || node instanceof Expression || node instanceof FieldDeclaration) {
			if (!(node instanceof Name))
				linkName++;
		}

		if (node instanceof FieldDeclaration) {
			this.checkForwardRef = 0;
		}*/

		super.willVisit(node);
	}

	public boolean visit(ASTNode node) throws SymbolTableException, Exception  {
		if (node instanceof Type) {
			return false; // Type child nodes can be ignored
		} else if (node instanceof MethodInvokeExpression) {
			Primary prefix = ((MethodInvokeExpression) node).getPrefix();
			if (prefix != null) {
				prefix.accept(this); // Check prefix childs -> check name
			}
			for (Expression expr : ((MethodInvokeExpression) node).getArguments()) {
				expr.accept(this); // Check arguments -> check arguments name
			}
			return false; // other childs can be ignored
		} else if (node instanceof FieldAccess || node instanceof FieldDereferenceAccess || node instanceof ArrayAccess) {
			((Primary) node).getPrefix().accept(this); // Check check prefix childs -> check name
			return false;
		} else if ( /*this.linkName > 0 && */ node instanceof Name) {
			String name = ((Name) node).getName();
			Scope currentScope = this.getCurrentScope();

			// Try to resolve as local/field variable access
			Symbol resolvedSymbol = currentScope.resolveVariableDeclaration((Name) node);


/*			// Check Forward Referencing
			if (this.checkForwardRef == 1 && result != null
					&& result.getWithinScope() == this.getCurrentScope().getParentTypeScope()
					&& !this.visitedFields.contains(result.getName())) {
				ASTNode parentNode = node.getParent();
				if (parentNode instanceof AssignmentExpression
						&& node == ((AssignmentExpression) parentNode).getLeftHand()) {

				} else {
					throw new Exception(
							"Forward referencing " + result.getName() + " in scope " + this.getCurrentScope());
				}
			}*/

			if (resolvedSymbol != null) {
				((Name) node).setOriginalDeclaration(resolvedSymbol);
				System.out.println("Resolved " + name + " => " + resolvedSymbol.getName() + "\tParent: " + node.getParent());
			} else {
				//throw new SymbolTableException("Fail to resolve " + name + " in scope " + currentScope);
				System.out.println("Fail to resolve " + name + " in scope " + currentScope);
			}
		}
		
		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
/*		if (node instanceof Statement || node instanceof Expression || node instanceof FieldDeclaration) {
			if (!(node instanceof Name))
				linkName--;
		}

		if (node instanceof FieldDeclaration) {
			String fieldName = this.getCurrentScope().getParentTypeScope()
					.resolveVariableToDecl(((FieldDeclaration) node).getName()).getName();
			this.visitedFields.add(fieldName);
			this.checkForwardRef = -1;
		} else if (this.checkForwardRef == 0 && node instanceof Name && node.getParent() instanceof FieldDeclaration) {
			this.checkForwardRef = 1;
		}*/
		super.didVisit(node);
	}
}