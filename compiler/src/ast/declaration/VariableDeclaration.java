package ast.declaration;

import ast.identifier.*;
import ast.type.*;

public class VariableDeclaration extends Declaration {

    public VariableDeclaration(Type type, VariableFunctionIdentifier variableId){
        super(variableId, type);
        
        this.addChild(type);
    }
}