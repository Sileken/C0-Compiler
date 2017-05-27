package ast.definition;

import ast.ASTNode;
import ast.identifier.*;

public abstract class Definition extends ASTNode {
    private Identifier identifier;

    protected Definition(Identifier identifier){
        super();
        
        this.identifier = identifier;
        this.addChild(this.identifier);
    }
}