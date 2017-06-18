package symboltable.semanticsvisitor;

import ast.*;
import symboltable.*;

public class NameLinker extends SemanticsVisitor {
	public NameLinker(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws Exception {
	}

	@Override
	public boolean visit(ASTNode node) throws Exception {
		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
	}
}