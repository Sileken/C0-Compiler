package ast.identifier;

import ast.ASTNode;
import ast.type.*;
import parser.*;

public abstract class Identifier extends ASTNode {
    private final Token idToken;
    private Identifier declarationNode;
    private Type type;

    public Identifier(Token idToken) {
        super();
        
        this.idToken = idToken;
    }

    public String getName() {
        return this.idToken.image;
    }

    public void setDeclarationNode(Identifier declarationNode) {
        this.declarationNode = declarationNode;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Identifier getDeclarationNode() {
        return this.declarationNode;
    }

    public Type getType() {
        return this.type;
    }
}