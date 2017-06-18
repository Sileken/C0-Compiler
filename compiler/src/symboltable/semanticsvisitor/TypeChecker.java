package symboltable.semanticsvisitor;

import ast.*;
import symboltable.*;

public class TypeChecker extends SemanticsVisitor {
	public TypeChecker(SymbolTable table) {
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