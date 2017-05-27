package ast.type;

import ast.identifier.*;

public class StructType extends Type {
    StructIdentifier structTypeIdentifier;

    public StructType(StructIdentifier structTypeIdentifier){
        this.structTypeIdentifier = structTypeIdentifier;
    }
}