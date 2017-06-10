package ast.expression.primary;

import parser.*;

public class LiteralPrimary extends Primary {
    String value;
    LiteralType literalType;

    public static enum LiteralType {
        BOOLLIT, INTLIT, NULL
    }

    public LiteralPrimary(LiteralType literalType, Token token){
        super();

        this.literalType = literalType;
        this.value = token.image;
    }
}