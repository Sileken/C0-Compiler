package ast.type;

import ast.identifier.*;

public class StructType extends Type {
    private StructIdentifier structTypeIdentifier;

    public StructType(StructIdentifier structTypeIdentifier) {
        super();

        this.structTypeIdentifier = structTypeIdentifier;
        this.addChild(this.structTypeIdentifier);
    }

    public StructIdentifier getIdentifierNode() {
        return structTypeIdentifier;
    }

    @Override
    public String getFullyQualifiedName() {
        return "struct " + structTypeIdentifier.getName();
    }

    @Override
    public String getIdentifier() {
        return "struct " + this.structTypeIdentifier.getName();
    }

    public String getScopeName() {
        return "struct." + structTypeIdentifier.getName();
    }
}