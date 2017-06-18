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
        String name = this.getName() + "." + variableDecl.getName().getName();
        return name;
    }

    public void addVariableDeclaration(VariableDeclaration variableDecl) throws SymbolTableException {
        String name = this.nameForDecl(variableDecl);

        Symbol duplicatedSymbol = this.getLocalVariable(variableDecl.getName().getName());
        if (duplicatedSymbol != null && !(duplicatedSymbol.getNode().getParent() instanceof ForStatement)) {
            throw new SymbolTableException("Duplicate Variable Declaration of " + name);
        }

        Symbol symbol = new Symbol(name, variableDecl, this);

        this.symbols.put(name, symbol);
    }

    public Symbol getVariableDeclaration(VariableDeclaration variableDecl) {
        return this.symbols.get(this.nameForDecl(variableDecl));
    }

    public Symbol getLocalVariable(String name) {
        Symbol localVariable = null;
        for (Symbol entry : this.symbols.values()) {
            if(entry.getName().endsWith("." + name) ){
                localVariable = entry;
                break;
            }
        }

        if ( localVariable != null && this.parent instanceof BlockScope) {
            return ((BlockScope) this.parent).getLocalVariable(name);
        }

        return localVariable;
    }
}