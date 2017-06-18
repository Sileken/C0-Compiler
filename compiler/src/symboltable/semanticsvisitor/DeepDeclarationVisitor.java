package symboltable.semanticsvisitor;

import ast.*;
import symboltable.*;

public class DeepDeclarationVisitor extends SemanticsVisitor {
	public DeepDeclarationVisitor(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
	}

	public boolean visit(ASTNode node) throws Exception {
		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
	}
}