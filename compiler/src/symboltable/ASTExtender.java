package symboltable;

import ast.*;
import ast.expression.primary.name.*;
import ast.statement.*;
import ast.declaration.*;
import ast.definition.*;
import ast.identifier.*;

public class ASTExtender {
    
    private final SymbolTable globalSymbolTable;
    
    public ASTExtender() {
        this.globalSymbolTable = new SymbolTable();
    }

    public void extendAST(AST ast) throws Exception {
        globalSymbolTable.enterScope();

        initializeASTNodes(globalSymbolTable, ast.getRoot());
    }

    /**
    * Set additional information into the nodes of the nearly created AST.
    */
    private void initializeASTNodes(SymbolTable symbolTable, ASTNode astNode) throws Exception {

        
        if (astNode instanceof Declaration) {
            checkDeclaration(symbolTable, (Declaration) astNode);
            
            // recursive traverse children
            for (ASTNode node : astNode.getChildren()) {
                initializeASTNodes(symbolTable, node);
            }
        }
        
        else if (astNode instanceof Definition) {
            //checkDefinition(symbolTable, (Definition) astNode);
            
            // recursive traverse children
            for (ASTNode node : astNode.getChildren()) {
                initializeASTNodes(symbolTable, node);
            }
        }

        else if (astNode instanceof SimpleName) {
            checkSimpleName(symbolTable, (SimpleName) astNode);  

            
               // recursive traverse children
            for (ASTNode node : astNode.getChildren()) {
                initializeASTNodes(symbolTable, node);
            }          
        }

        else if (astNode instanceof Block) {
            symbolTable.enterScope();
            System.out.println("New Scope");

            // doMore stuff traverse children
             // recursive traverse children
            for (ASTNode node : astNode.getChildren()) {
                initializeASTNodes(symbolTable, node);
            }

            symbolTable.exitScope();
        }
        else {
            for (ASTNode node : astNode.getChildren()) {
                initializeASTNodes(symbolTable, node);
            }
        }

        
    }

    private void checkDeclaration(SymbolTable symbolTable, Declaration declarationNode) throws Exception {
        
        Identifier identifier = declarationNode.getIdentifierNode();
        String s = identifier.getName();

        try {                 
            // symbol exists already in the same scope
            if (symbolTable.getSymbol(s) != null) {
                throw new IdentifierAlreadyDeclaredException("Identifier '" + s + "' has already been declared within the same scope.");
            }

        } catch(IdentifierNotFoundException ex) {
            // everything is fine
        }            
            identifier.setType(declarationNode.getType());
            symbolTable.addSymbol(identifier);
    }

    private  void checkDefinition(SymbolTable symbolTable, Definition definitionNode) throws IdentifierNotFoundException {

        Identifier identifier = definitionNode.getIdentifierNode();
        String s = identifier.getName();

        // throws IdentifierNotFoundException when identifier not found in any scope
        Symbol symbol = symbolTable.lookup(s);

        Identifier nextReference = symbol.getIdentifier();
        identifier.setDeclarationNode(nextReference);
    }

    private void checkSimpleName(SymbolTable symbolTable, SimpleName simpleName) throws Exception {
        VariableFunctionIdentifier identifier = simpleName.getVariableFunctionIdentifier();
        
        String s = identifier.getName();
        Symbol symbol = symbolTable.lookup(s);

        if (symbol == null) {
            throw new Exception("Identifier '" + s + "' not declared in this scope.");
        }

        Identifier nextReference = symbol.getIdentifier();

        identifier.setDeclarationNode(nextReference);
    }
}