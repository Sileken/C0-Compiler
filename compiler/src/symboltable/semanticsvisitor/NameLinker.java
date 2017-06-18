package symboltable.semanticsvisitor;

import ast.*;
import symboltable.*;

public class NameLinker extends SemanticsVisitor {
	public NameLinker(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
	}

	public boolean visit(ASTNode node) throws SymbolTableException {
		return true;
	}

	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
	}
}