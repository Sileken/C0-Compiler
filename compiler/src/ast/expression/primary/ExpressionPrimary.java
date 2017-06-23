package ast.expression.primary;

import ast.expression.Expression;

public class ExpressionPrimary extends Primary {
    Expression expression;

    public ExpressionPrimary(Expression expression){
        super();

        this.expression = expression;
        this.addChild(expression);
    }

    public Expression getExpression(){
        return this.expression;
    }
}