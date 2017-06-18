package ast.identifier;

import ast.ASTNode;
import ast.type.*;
import parser.*;

public abstract class Identifier extends ASTNode {
    private final Token idToken;
    private Type type;

    public Identifier(Token idToken) {
        super();
        
        this.idToken = idToken;
    }

    public String getName() {
        return this.idToken.image;
    }
}