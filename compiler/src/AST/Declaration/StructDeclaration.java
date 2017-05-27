package ast.declaration;

import ast.identifier.*;
import ast.type.*;

public class StructDeclaration extends Declaration {   
    private StructType structType;

    public StructDeclaration(StructType structType, StructIdentifier structId){
        super(structId);
        this.structType = structType;
    }
}