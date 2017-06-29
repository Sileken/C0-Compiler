package symboltable.semanticsvisitor;

import ast.*;
import ast.definition.*;
import ast.declaration.*;
import ast.statement.*;
import ast.type.*;
import symboltable.*;

/** This Visitor creates 
 * Function-Block-Scopes, Block-Scopes in Functions, StructType-Scopes, 
 * adds Varaible Declarations or Field Definitions to this Scope 
 * and add types to Varaible Declaration Symbol and Field Definition Symbol */
public class DeepDeclarationVisitor extends SemanticsVisitor {
	public DeepDeclarationVisitor(SymbolTable table) {
		super(table);
	}

	@Override
	public void willVisit(ASTNode node) throws SymbolTableException {
		if (node instanceof FunctionDefinition) { // Add function block scope to symboltable
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			String blockName = currentScope.getSignatureOfFunction((FunctionDefinition) node);
			Scope scope = this.table.addBlockScope(blockName, currentScope, node);
			
			this.blockCount = 0;
			this.pushScope(scope);
		} else if (node instanceof StructDefinition) { // Add struct type scope to symboltable
			FileUnitScope currentScope = (FileUnitScope) this.getCurrentScope();
			String structTypeScopeName = this.table.getStructTypeScopeName((StructDefinition) node);
			Scope scope = this.table.addStructTypeScope(structTypeScopeName, currentScope, node);

			this.blockCount = 0;
			this.pushScope(scope);
		} else if (node instanceof Block && !(((Block) node).getParent() instanceof FunctionDefinition)) { 
			// Add block scope to symboltable
			BlockScope currentScope = (BlockScope) this.getCurrentScope();
			String blockName = this.table.getBlockScopeName(currentScope, this.blockCount);
			Scope scope = this.table.addBlockScope(blockName, currentScope, node);

			this.blockCount++;
			this.pushScope(scope);
		} else {
			super.willVisit(node);
		}
	}

	public boolean visit(ASTNode node) throws SymbolTableException {
		if (node instanceof VariableDeclaration) { // Add variable declaration as symbol to block scope
			VariableDeclaration varDecl = (VariableDeclaration) node;
			BlockScope currentScope = (BlockScope) this.getCurrentScope();
			currentScope.addVariableDeclaration(varDecl);
		}
		else if (node instanceof FieldDefinition) { // Add field definition as symbol to struct type scope
			FieldDefinition fieldDefinition = (FieldDefinition) node;
			StructTypeScope currentScope = (StructTypeScope) this.getCurrentScope();
			currentScope.addFieldDefinition(fieldDefinition);
		}

		return !(node instanceof VariableDeclaration || node instanceof FieldDefinition || node instanceof FunctionDeclaration); // Prevent child node processing
	}

	@Override
	public void didVisit(ASTNode node) throws SymbolTableException {
		super.didVisit(node);
	
		if (node instanceof VariableDeclaration) { 	// Add type to vairable declaration
			Type type = ((VariableDeclaration) node).getType();
			if (type == null) {
				return;
			}
			BlockScope enclosingScope = (BlockScope) this.getCurrentScope();
			Symbol symbol = enclosingScope.getVariableDeclaration((VariableDeclaration) node);
			symbol.setType(type);
		}
		else if (node instanceof FieldDefinition){ 	// Add type to field declaration
			Type type = ((FieldDefinition) node).getType();
			if (type == null) {
				return;
			}

			StructTypeScope enclosingScope = (StructTypeScope) this.getCurrentScope();
			Symbol symbol = enclosingScope.getFieldDefinition((FieldDefinition) node);
			symbol.setType(type);
		}
	}
}