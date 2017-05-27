package ast.declaration;

import java.util.ArrayList;
import java.util.List;
import ast.definition.*;
import ast.identifier.*;
import ast.type.*;

public class FunctionDeclaration extends Declaration {
    private Type returnType;
    private List<ParameterDefinition> parameterDefs = new ArrayList<ParameterDefinition>();

    public FunctionDeclaration(Type returnType, VariableFunctionIdentifier funcId, List<ParameterDefinition> parameterDefs){
        super(funcId);
        this.returnType = returnType;
        if(parameterDefs != null && !parameterDefs.isEmpty()){
            this.parameterDefs = parameterDefs;
        }
    }
}