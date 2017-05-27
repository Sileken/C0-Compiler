package ast.definition;

import ast.type.*;
import ast.identifier.*;

public class ParameterDefinition extends Definition {
    private Type parameterType;

    public ParameterDefinition(Type parameterType, ParameterIdentifier parameterId){
        super(parameterId);
        this.parameterType = parameterType;
    }
}