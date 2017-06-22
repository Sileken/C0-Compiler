package ast.definition;

import ast.ASTNode;
import ast.identifier.*;
import ast.type.*;

public abstract class Definition extends ASTNode {
    private Identifier identifier;

    protected Definition(Identifier identifier) {
        super();

        this.identifier = identifier;
        this.addChild(this.identifier);

        this.setIdentifier(this.identifier.getName());
    }

    public Identifier getName() {
        return this.identifier;
    }

    public abstract Type getType();
}