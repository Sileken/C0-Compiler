package ast.type;

import ast.identifier.*;

public class StructType extends Type {
    StructIdentifier structTypeIdentifier;

    public StructType(StructIdentifier structTypeIdentifier) {
        super();

        this.structTypeIdentifier = structTypeIdentifier;
        this.addChild(this.structTypeIdentifier);
    }

    @Override
    public String getFullyQualifiedName() {
        return "struct " + structTypeIdentifier.getName();
    }
}