package ast.declaration;

import ast.ASTNode;
import ast.identifier.*;
import ast.type.*;

public abstract class Declaration extends ASTNode {
    private Identifier identifier;
    private Type type;

    public Declaration(Identifier identifier, Type type) {
        super();
        
        this.identifier = identifier;
        this.type = type;
        this.addChild(this.identifier);
    }

    public Identifier getIdentifierNode() {
        return this.identifier;
    }

    public Type getType() {
        return this.type;
    }
}