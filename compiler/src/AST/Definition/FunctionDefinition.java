package ast.definition;

import java.util.ArrayList;
import java.util.List;
import ast.identifier.*;
import ast.type.*;

public class FunctionDefinition extends Definition {
  private Type returnType;
  private List<ParameterDefinition> parameterDefs = new ArrayList<ParameterDefinition>();

  public FunctionDefinition(Type returnType, VariableFunctionIdentifier funcId, List<ParameterDefinition> parameterDefs){
    super(funcId);
    this.returnType = returnType;
    this.addChild(this.returnType);

    if(parameterDefs != null && !parameterDefs.isEmpty()){
        this.parameterDefs = parameterDefs;
        this.addChilds(this.parameterDefs);
    }
  }
}