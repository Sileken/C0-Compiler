package symboltable.semanticsvisitor;

import java.util.Stack;

import ast.*;
import ast.visitor.*;
import symboltable.*;

public abstract class SemanticsVisitor extends ASTVisitor {
    protected SymbolTable table;
	protected int blockCount;
	private Stack<Scope> viewStack;

	public SemanticsVisitor(SymbolTable table) {
		this.viewStack = new Stack<Scope>();
		this.table = table;
		this.blockCount = 0;
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


