package symboltable.semanticsvisitor;

import ast.*;
import symboltable.*;

public class TypeChecker extends SemanticsVisitor {
	public TypeChecker(SymbolTable table) {
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