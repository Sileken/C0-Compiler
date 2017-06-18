package symboltable.semanticsvisitor;

import ast.*;
import ast.declaration.*;
import ast.statement.*;
import symboltable.*;

public class GlobalDeclarationVisitor extends SemanticsVisitor {
	public GlobalDeclarationVisitor(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
	/*	if (node instanceof TypeDeclaration) {
			PackageScope currentScope = (PackageScope) this.getCurrentScope();
			String name = ((TypeDeclaration) node).getIdentifier();
			name = currentScope.getName() + "." + name;
			((TypeDeclaration) node).fullyQualifiedName = name;

			TypeScope scope = this.table.addType(name, currentScope, node);

			// Add type declaration as package member
			currentScope.addType((TypeDeclaration) node);

			// Push current scope into the view stack
			this.pushScope(scope);
		}*/
	}

	public boolean visit(ASTNode node) throws Exception {
		if (node instanceof StructDeclaration) {
			/*		TypeScope currentScope = (TypeScope) this.getCurrentScope();
					currentScope.addFieldDecl((FieldDeclaration) node);*/
		} else if (node instanceof FunctionDeclaration) {
			/*			TypeScope currentScope = (TypeScope) this.getCurrentScope();
						currentScope.addMethod((MethodDeclaration) node);*/
		}

		return !(node instanceof Block);
	}
}