package ast.expression.simple;

import ast.expression.*;

public class SimpleExpression extends Simple {
    Expression expression;

    public SimpleExpression(Expression expression){
        super();

        this.expression = expression;
        this.addChild(this.expression);
    }
}