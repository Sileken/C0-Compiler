package symboltable.semanticsvisitor;

import ast.*;
import ast.declaration.*;
import ast.definition.*;
import ast.statement.*;
import symboltable.*;

/** This Visitor creates a global FileUnit Scope 
 * and adds Struct- and Function-Defintion to this Scope */
public class GlobalDeclarationVisitor extends SemanticsVisitor {
	public GlobalDeclarationVisitor(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FileUnit) {
			FileUnitScope scope = this.table.addFileUnitScope((FileUnit)node);
			this.pushScope(scope);
		}
	}

	public boolean visit(ASTNode node) throws SymbolTableException {
		if (node instanceof StructDefinition) {
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			currentScope.addStructDefinition((StructDefinition) node);
		} else if (node instanceof FunctionDefinition) {
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			currentScope.addFunctionDefinition((FunctionDefinition) node);
		}

		return !(node instanceof StructDefinition || node instanceof FunctionDefinition);
	}
}