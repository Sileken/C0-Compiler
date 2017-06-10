package ast.declaration;

import ast.identifier.*;
import ast.type.*;

public class VariableDeclaration extends Declaration {
    private Type type;
    private VariableFunctionIdentifier variableId;

    public VariableDeclaration(Type type, VariableFunctionIdentifier variableId){
        super(variableId);
        
        this.type = type;
        this.addChild(this.type);
    }
}