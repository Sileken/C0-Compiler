package ast.identifier;

import ast.ASTNode;
import parser.*;

public abstract class Identifier extends ASTNode {
    private Token idToken;

    public Identifier(Token idToken) {
        super();
        this.idToken = idToken;
    }
}