package ast.type;

import parser.*;
import ast.identifier.*;

public class PrimitiveType extends Type {
    public static enum Primitive {
		INT, BOOL, STRING, CHAR, VOID
	}

    private Primitive type;

    public PrimitiveType(String typeName) throws ParseException{
        Primitive type = this.stringToType(typeName.toUpperCase());
    }

    private Primitive stringToType(String name) throws ParseException {
		for (Primitive type : Primitive.values()) {
			if (type.name().equals(name))
				return type;
		}
		throw new ParseException("Unknown primitive type " + name);
	}
}