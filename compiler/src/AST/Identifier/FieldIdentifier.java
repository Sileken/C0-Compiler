package ast.identifier;

import parser.*;

public class FieldIdentifier extends Identifier {
    private Token idToken;

    public FieldIdentifier(Token idToken){
        this.idToken = idToken;
    }
}