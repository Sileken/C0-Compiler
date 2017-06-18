package symboltable.semanticsvisitor;

import java.util.Stack;

import ast.*;
import ast.declaration.*;
import ast.definition.*;
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
		System.out.println("Pushing scope " + table.toString());
		this.viewStack.push(table);
	}

	protected Scope popScope() {
		Scope scope = this.viewStack.pop();
		System.out.println("Popping scope " + scope);
		return scope;
	}

	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FileUnit) {
			FileUnitScope scope = this.table.getFileUnitScope((FileUnit) node);
			this.pushScope(scope);
		} else if (node instanceof FunctionDefinition) {
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			String blockName = currentScope.signatureOfFunction((FunctionDefinition) node);
			Scope scope = this.table.getBlockScope(blockName);

			this.blockCount = 0;
			this.pushScope(scope);
		} else if (node instanceof Block) {
			BlockScope currentScope = (BlockScope) this.getCurrentScope();
			String blockName = currentScope.getName() + ".block" + this.blockCount;
			Scope scope = this.table.getBlockScope(blockName);
			
			this.blockCount++;
			this.pushScope(scope);
		}
	}

	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FileUnit) {
			this.viewStack.clear();
		} if (node instanceof FunctionDefinition) { // leaving Function Definition
			while (this.getCurrentScope() instanceof BlockScope) { // leaving Blocks in Functions
				this.popScope();
			}
		} else if (node instanceof Block) { // leaving Block
			this.popScope();
		}
	}
}
