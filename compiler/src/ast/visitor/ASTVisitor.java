package ast.visitor;

import ast.ASTNode;

public abstract class ASTVisitor {

	public ASTVisitor() {
	}
	
	public abstract void willVisit(ASTNode node) throws Exception;

	public boolean visit(ASTNode node) throws Exception {
		return true;
	}
	
	public abstract void didVisit(ASTNode node) throws Exception;
}