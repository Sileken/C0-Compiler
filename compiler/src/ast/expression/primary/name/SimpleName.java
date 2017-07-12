package ast.expression.primary.name;

import ast.identifier.*;

public class SimpleName extends Name {
    VariableFunctionIdentifier variableFunctionIdentifier;

    public SimpleName(VariableFunctionIdentifier variableFunctionIdentifier) {
        super();

        this.variableFunctionIdentifier = variableFunctionIdentifier;
        this.addChild(this.variableFunctionIdentifier);
        this.setIdentifier(this.variableFunctionIdentifier.getName());
    }

    @Override
    public String getName() {
        return variableFunctionIdentifier.getName();
    }
}