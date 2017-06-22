package ast.definition;

import java.util.ArrayList;
import java.util.List;
import ast.declaration.*;
import ast.identifier.*;
import ast.statement.*;
import ast.type.*;

public class FunctionDefinition extends Definition {
  private Type returnType;
  private List<VariableDeclaration> parameters = new ArrayList<VariableDeclaration>();
  private Block block;

  public FunctionDefinition(Type returnType, VariableFunctionIdentifier funcId, List<VariableDeclaration> parameters,
      Block block) {
    super(funcId);

    this.returnType = returnType;
    this.addChild(this.returnType);

    if (parameters != null && !parameters.isEmpty()) {
      this.parameters = parameters;
      this.addChilds(this.parameters);
    }

    this.block = block;
    this.addChild(this.block);
  }

  public List<Type> getParameterTypes() {
    List<Type> types = new ArrayList<Type>();
    for (VariableDeclaration param : parameters) {
      types.add(param.getType());
    }

    return types;
  }

  public Type getType() {
		return returnType;
	}
}