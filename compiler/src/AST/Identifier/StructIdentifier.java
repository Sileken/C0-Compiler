package ast.identifier;

import ast.type.*;
import parser.*;

public class StructIdentifier extends Identifier {
    private Token idToken;

    public StructIdentifier(Token idToken){
        this.idToken = idToken;
    }
}