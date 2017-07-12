package symboltable;

import java.util.List;

import ast.*;
import ast.declaration.*;
import ast.statement.*;
import ast.expression.primary.name.*;
import symboltable.*;
import utils.*;

public class BlockScope extends Scope {
    protected Scope parent;

    public BlockScope(String name, Scope parent, ASTNode referenceNode) {
        super(name, referenceNode);
        this.parent = parent;
    }

    public String nameForDecl(VariableDeclaration variableDecl) {
        return this.getName() + "." + variableDecl.getName().getName();
    }

    public void addVariableDeclaration(VariableDeclaration variableDecl) throws SymbolTableException {
        String symbolName = this.nameForDecl(variableDecl);

        Symbol duplicatedSymbol = this.getVariableDeclaration(variableDecl);
        if (duplicatedSymbol != null && !(duplicatedSymbol.getNode().getParent() instanceof ForStatement)) {
            String errorMsg = "Duplicate Variable Declaration of " + symbolName;
            Logger.error(errorMsg);
            throw new SymbolTableException(errorMsg);
        }

        Symbol symbol = new Symbol(symbolName, variableDecl, this);
        this.putSymbol(symbolName, symbol);
    }

    /** Get varaible declaration in current Scope */
    public Symbol getVariableDeclaration(VariableDeclaration variableDecl) {
        return this.symbols.get(this.nameForDecl(variableDecl));
    }

    /** Get varaible declaration in current Scope or in scope up to last block scope 
    * last block scope == function block scope */
    public Symbol resolveVariableDeclaration(Name name) throws SymbolTableException {
        Symbol result = this.resolveVariableDeclarationLocal(name);
        if (result == null && this.parent instanceof BlockScope) {
            result = ((BlockScope) this.parent).resolveVariableDeclaration(name);
        }

        return result;
    }

    private Symbol resolveVariableDeclarationLocal(Name name) throws SymbolTableException {
        if (name instanceof SimpleName) {
            List<Symbol> matchedSymbols = this.findEntriesWithSuffix(this.symbols.values(), "." + name.getName());
            if (matchedSymbols.size() > 1) {
                String errorMsg = "Resolved variable " + name.getName() + " to multiple definition " + matchedSymbols;
                Logger.error(errorMsg);
                throw new SymbolTableException(errorMsg);
            } else if (matchedSymbols.size() == 1) {
                return matchedSymbols.get(0);
            }
        }

        return null;
    }
}