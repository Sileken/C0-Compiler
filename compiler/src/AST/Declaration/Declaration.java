package ast.declaration;

import ast.ASTNode;
import ast.identifier.*;

public abstract class Declaration extends ASTNode {
    private Identifier identifier;

    public Declaration(Identifier identifier) {
        super();
        this.identifier = identifier;
    }
}