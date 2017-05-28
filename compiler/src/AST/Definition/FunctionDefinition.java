package ast.definition;

import java.util.ArrayList;
import java.util.List;
import ast.identifier.*;
import ast.statement.*;
import ast.type.*;

public class FunctionDefinition extends Definition {
  private Type returnType;
  private List<ParameterDefinition> parameterDefs = new ArrayList<ParameterDefinition>();
  private List<Statement> statements = new ArrayList<Statement>();

  public FunctionDefinition(Type returnType, VariableFunctionIdentifier funcId, List<ParameterDefinition> parameterDefs, List<Statement> statements){
    super(funcId);
    this.returnType = returnType;
    this.addChild(this.returnType);

    if(parameterDefs != null && !parameterDefs.isEmpty()){
        this.parameterDefs = parameterDefs;
        this.addChilds(this.parameterDefs);
    }

    if(statements != null && !statements.isEmpty()){
        this.statements = statements;
        this.addChilds(this.statements);
    }
  }
}