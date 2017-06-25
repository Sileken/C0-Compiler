package ast.expression.primary;

import parser.*;
import ast.type.*;

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

    public LiteralType getLiteralType()
    {
        return literalType;
    }

	@Override
    public Type getType() {
        PrimitiveType.Primitive primitive = null;
        switch(this.getLiteralType()) {
            case BOOLLIT: primitive = PrimitiveType.Primitive.BOOL; break;
            case INTLIT:  primitive = PrimitiveType.Primitive.INT;  break;
            case NULL:    return new NullType(); 
            default:      
                break;
        }
        return new PrimitiveType(primitive);
	}

}