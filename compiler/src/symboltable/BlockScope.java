package symboltable;

import ast.*;
import ast.declaration.*;
import ast.statement.*;
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
            if(entry.getName().endsWith("." + symbolName) ){
                localVariable = entry;
                break;
            }
        }

        if (localVariable == null && this.parent instanceof BlockScope) {
            return ((BlockScope) this.parent).getLocalVariable(symbolName);
        }

        return localVariable;
    }
}