package symboltable;

import java.util.List;

import ast.*;
import ast.declaration.*;
import ast.statement.*;
import ast.expression.primary.name.*;
import symboltable.*;

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

        Symbol duplicatedSymbol = this.getLocalVariable(variableDecl.getName().getName());
        if (duplicatedSymbol != null && !(duplicatedSymbol.getNode().getParent() instanceof ForStatement)) {
            throw new SymbolTableException("Duplicate Variable Declaration of " + symbolName);
        }

        Symbol symbol = new Symbol(symbolName, variableDecl, this);
        this.symbols.put(symbolName, symbol);
    }

    public Symbol getVariableDeclaration(VariableDeclaration variableDecl) {
        return this.symbols.get(this.nameForDecl(variableDecl));
    }

    public Symbol getLocalVariable(String symbolName) {
        Symbol localVariable = null;
        for (Symbol entry : this.symbols.values()) {
            if (entry.getName().endsWith("." + symbolName)) {
                localVariable = entry;
                break;
            }
        }

        if (localVariable == null && this.parent instanceof BlockScope) {
            return ((BlockScope) this.parent).getLocalVariable(symbolName);
        }

        return localVariable;
    }

    @Override
    public Symbol resolveVariableDeclaration(Name name) throws SymbolTableException {
        Symbol result = this.resolveVariableDeclarationLocal(name);
        if (result == null) {
            result = this.parent.resolveVariableDeclaration(name);
        }
        return result;
    }

    private Symbol resolveVariableDeclarationLocal(Name name) throws SymbolTableException {
        if (name instanceof SimpleName) {
            List<Symbol> matchedSymbols = this.findEntriesWithSuffix(this.symbols.values(), "." + name.getName());
            if (matchedSymbols.size() > 1) {
                throw new SymbolTableException(
                        "Resolved variable " + name.getName() + " to multiple definition " + matchedSymbols);
            } else if (matchedSymbols.size() == 1) {
                return matchedSymbols.get(0);
            }
        }
        return null;
    }
}