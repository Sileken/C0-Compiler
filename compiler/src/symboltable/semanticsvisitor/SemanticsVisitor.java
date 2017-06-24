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

	protected void pushScope(Scope scope) {
		System.out.println("Pushing scope " + scope.toString());
		this.viewStack.push(scope);
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
			String blockName = currentScope.getSignatureOfFunction((FunctionDefinition) node);
			Scope scope = this.table.getBlockScope(blockName);

			this.blockCount = 0;
			this.pushScope(scope);
		} else if (node instanceof StructDefinition) {
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			String structTypeScopeName = this.table.getStructTypeScopeName((StructDefinition) node);

			Scope scope = this.table.getStructTypeScope(structTypeScopeName);
			this.pushScope(scope);
		} else if (node instanceof Block && !(((Block) node).getParent() instanceof FunctionDefinition)) {
			BlockScope currentScope = (BlockScope) this.getCurrentScope();
			String blockName = this.table.getBlockScopeName(currentScope, this.blockCount);
			Scope scope = this.table.getBlockScope(blockName);

			this.blockCount++;
			this.pushScope(scope);
		}
	}

	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FileUnit) {
			this.viewStack.clear();
		} else if (node instanceof FunctionDefinition) { // leaving Function Definition
			while (this.getCurrentScope() instanceof BlockScope) { // leaving Blocks in Functions
				this.popScope();
			}
		} else if (node instanceof StructDefinition) {
			if (this.getCurrentScope() instanceof StructTypeScope) {
				this.popScope();
			}
		} else if (node instanceof Block) { // leaving Block or Struct Type Scope
			this.popScope();
		}
	}
}
