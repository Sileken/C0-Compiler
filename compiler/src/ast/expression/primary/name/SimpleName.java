package ast.expression.primary.name;

import ast.identifier.*;

public class SimpleName extends Name {
    VariableFunctionIdentifier variableFunctionIdentifier;

    public SimpleName(VariableFunctionIdentifier variableFunctionIdentifier) {
        super();

        this.variableFunctionIdentifier = variableFunctionIdentifier;
        this.addChild(this.variableFunctionIdentifier);
    }

    public VariableFunctionIdentifier getVariableFunctionIdentifier() {
        return this.variableFunctionIdentifier;
    }
}