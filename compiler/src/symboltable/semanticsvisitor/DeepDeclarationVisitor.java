package symboltable.semanticsvisitor;

import ast.*;
import ast.definition.*;
import ast.declaration.*;
import ast.statement.*;
import ast.type.*;
import symboltable.*;

/** This Visitor creates Block-Scopes and add Varaible Declarations to this Scope */
public class DeepDeclarationVisitor extends SemanticsVisitor {
	public DeepDeclarationVisitor(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FunctionDefinition) {
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			String name = currentScope.signatureOfFunction((FunctionDefinition) node);
			Scope scope = this.table.addBlockScope(name, currentScope, node);
			this.blockCount = 0;
			this.pushScope(scope);
		} else if (node instanceof Block && !(((Block) node).getParent() instanceof FunctionDefinition)) {
			BlockScope currentScope = (BlockScope) this.getCurrentScope();
			String name = currentScope.getName() + ".block" + this.blockCount;
			Scope scope = this.table.addBlockScope(name, currentScope, node);
			
			this.blockCount++;
			this.pushScope(scope);
		} else {
			super.willVisit(node);
		}		
	}

	public boolean visit(ASTNode node) throws SymbolTableException {
		if (node instanceof VariableDeclaration) {
			VariableDeclaration varDecl = (VariableDeclaration) node;
			BlockScope currentScope = (BlockScope) this.getCurrentScope();
			currentScope.addVariableDeclaration(varDecl);
		}

		return !(node instanceof VariableDeclaration);
	}

	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
		super.didVisit(node);

		if (node instanceof VariableDeclaration) {
			Type type = ((VariableDeclaration) node).getType();
			if (type == null) {
				return;
			}

			BlockScope enclosingScope = (BlockScope) this.getCurrentScope();
			Symbol symbol = enclosingScope.getVariableDeclaration((VariableDeclaration) node);
			symbol.setType(type);

			if (type instanceof ArrayType) {
				type = ((ArrayType) type).getType();
			}
		}
	}
}