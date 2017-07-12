package symboltable.semanticsvisitor;

import ast.*;
import ast.declaration.*;
import ast.definition.*;
import ast.statement.*;
import ast.type.*;
import symboltable.*;

/** This Visitor creates a global FileUnit Scope 
 * and adds Struct- and Function-Declaration/Defintion to this Scope 
 * and add types to Struct Declaration/Declaration Symbol and Function Declaration/Declaration Symbol */
public class GlobalDeclarationAndDefinitionVisitor extends SemanticsVisitor {
	public GlobalDeclarationAndDefinitionVisitor(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FileUnit) { // Add file unit scope to symboltable
			FileUnitScope scope = this.table.addFileUnitScope((FileUnit) node);
			this.pushScope(scope);
		}
	}

	public boolean visit(ASTNode node) throws SymbolTableException {
		if (node instanceof StructDeclaration) { // Add struct declaration as symbol to file unit scope
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			currentScope.addStructDeclaration((StructDeclaration) node);
		} else if (node instanceof FunctionDeclaration) { // Add function declaration as symbol to file unit scope
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			currentScope.addFunctionDeclaration((FunctionDeclaration) node);
		} else if (node instanceof StructDefinition) { // Add struct definition as symbol to file unit scope
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			currentScope.addStructDefinition((StructDefinition) node);
		} else if (node instanceof FunctionDefinition) { // Add function definition as symbol to file unit scope
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			currentScope.addFunctionDefinition((FunctionDefinition) node);
		}

		return !(node instanceof StructDeclaration || node instanceof FunctionDeclaration // Prevent child node processing
				|| node instanceof StructDefinition || node instanceof FunctionDefinition);
	}

	public void didVisit(ASTNode node) throws SymbolTableException {
		super.didVisit(node);

		if (node instanceof StructDeclaration) { // Add type to struct declaration
			Type type = ((StructDeclaration) node).getType();
			if (type == null) {
				return;
			}

			FileUnitScope enclosingScope = (FileUnitScope) this.getCurrentScope();
			Symbol symbol = enclosingScope.resolveStructSymbol(
					enclosingScope.getDeclarationPrefix() + ((StructDeclaration) node).getName().getName());
			symbol.setType(type);
		} else if (node instanceof FunctionDeclaration) { // Add type to function declaration
			Type type = ((FunctionDeclaration) node).getType();
			if (type == null) {
				return;
			}

			FileUnitScope enclosingScope = (FileUnitScope) this.getCurrentScope();
			Symbol symbol = enclosingScope.resolveFunctionSymbol(enclosingScope.getDeclarationPrefix()
					+ enclosingScope.getSignatureOfFunction((FunctionDeclaration) node));
			symbol.setType(type);
		} else if (node instanceof FunctionDefinition) { // Add type to function definition
			Type type = ((FunctionDefinition) node).getType();
			if (type == null) {
				return;
			}

			FileUnitScope enclosingScope = (FileUnitScope) this.getCurrentScope();
			Symbol symbol = enclosingScope.resolveFunctionSymbol(enclosingScope.getDefinitionPrefix()
					+ enclosingScope.getSignatureOfFunction((FunctionDefinition) node));
			symbol.setType(type);
		} else if (node instanceof StructDefinition) { // Add type to struct definition
			Type type = ((StructDefinition) node).getType();
			if (type == null) {
				return;
			}

			FileUnitScope enclosingScope = (FileUnitScope) this.getCurrentScope();
			Symbol symbol = enclosingScope.resolveStructSymbol(
					enclosingScope.getDefinitionPrefix() + ((StructDefinition) node).getName().getName());
			symbol.setType(type);
		}
	}
}