package ast.type;

import parser.*;
import utils.*;

public class PrimitiveType extends Type {
	public static enum Primitive {
		INT, BOOL, VOID
	}

	private Primitive type;

	public PrimitiveType(String typeName) throws ParseException {
		super();

		this.type = this.stringToType(typeName.toUpperCase());
	}

	public PrimitiveType(Primitive type) {
		super();

		this.type = type;
	}

	private Primitive stringToType(String name) throws ParseException {
		for (Primitive type : Primitive.values()) {
			if (type.name().equals(name))
				return type;
		}

		String errorMsg = "Unknown primitive type " + name;
		Logger.error(errorMsg);
		throw new ParseException("Unknown primitive type " + name);
	}

	public Primitive getPrimitive() {
		return type;
	}

	@Override
	public String getFullyQualifiedName() {
		return this.getPrimitive().name();
	}

	@Override
	public String getIdentifier() {
		return this.getPrimitive().name();
	}
}