package ast.declaration;

import java.util.ArrayList;
import java.util.List;
import ast.declaration.*;
import ast.definition.*;
import ast.identifier.*;
import ast.type.*;

public class FunctionDeclaration extends Declaration {
    private Type returnType;
    private List<VariableDeclaration> parameters = new ArrayList<VariableDeclaration>();

    public FunctionDeclaration(Type returnType, VariableFunctionIdentifier funcId,
            List<VariableDeclaration> parameters) {
        super(funcId);

        this.returnType = returnType;
        this.addChild(this.returnType);

        if (parameters != null && !parameters.isEmpty()) {
            this.parameters = parameters;
            this.addChilds(this.parameters);
        }
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