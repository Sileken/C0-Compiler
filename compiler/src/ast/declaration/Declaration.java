package ast.declaration;

import ast.ASTNode;
import ast.identifier.*;
import ast.type.*;

public abstract class Declaration extends ASTNode {
    private Identifier identifier;

    public Declaration(Identifier identifier) {
        super();
        
        this.identifier = identifier;
        this.addChild(this.identifier);

        this.setIdentifier(this.identifier.getName());
    }

    public Identifier getName(){
        return this.identifier;
    }

    public abstract Type getType();
}