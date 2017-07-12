package ast.type;

import ast.ASTNode;

public abstract class Type extends ASTNode {
    public Type() {
        super();
    }

    public abstract String getFullyQualifiedName();

    @Override
    public String toString() {
        return getFullyQualifiedName();
    }
}