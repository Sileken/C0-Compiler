package ast.declaration;

import ast.identifier.*;
import ast.type.*;

public class StructDeclaration extends Declaration {   
    private StructType structType;

    public StructDeclaration(StructType structType, StructIdentifier structId){
        super(structId, structType);
        
        this.structType = structType;
        this.addChild(this.structType);
    }
}