package ast.expression;

public class ConditionalExpression extends Expression {
    private Expression condition;
    private Expression trueExpression;
    private Expression falseExpression;

    public ConditionalExpression(Expression condition, Expression trueExpression, Expression falseExpression) {
        super();

        this.condition = condition;
        this.addChild(this.condition);

        this.trueExpression = trueExpression;
        this.addChild(this.trueExpression);

        this.falseExpression = falseExpression;
        this.addChild(this.falseExpression);
    }

    public Expression getCondition(){
        return condition;
    }

    public Expression getTrueExpression(){
        return trueExpression;
    }

    public Expression getFalseExpression(){
        return falseExpression;
    }
}