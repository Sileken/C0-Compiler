package symboltable.semanticsvisitor;

import java.util.Stack;

import ast.*;
import ast.declaration.*;
import ast.statement.*;
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

	protected Scope getCurrentScope() {
		return this.viewStack.peek();
	}

	protected void pushScope(Scope table) {
		//System.out.println("Pushing scope " + table.toString());
		this.viewStack.push(table);
	}

	protected Scope popScope() {
		Scope scope = this.viewStack.pop();
		//System.out.println("Popping scope " + scope);
		return scope;
	}

	@Override
	public void willVisit(ASTNode node) throws Exception { // entering ...
	}

	@Override
	public void didVisit(ASTNode node) throws Exception {
		if (node instanceof FunctionDeclaration) { // leaving Function Decklaration
			while (this.getCurrentScope() instanceof BlockScope) {
				this.popScope();
			}
		} else if (node instanceof Block) { // leaving Block
			this.popScope();
		}
	}
}
