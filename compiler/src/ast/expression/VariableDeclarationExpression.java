package ast.expression;

import ast.declaration.*;

public class VariableDeclarationExpression extends Expression {
    private VariableDeclaration variableDeclaration;

    public VariableDeclarationExpression(VariableDeclaration variableDeclaration) {
        super();

        this.variableDeclaration = variableDeclaration;
        this.addChild(variableDeclaration);
    }
}