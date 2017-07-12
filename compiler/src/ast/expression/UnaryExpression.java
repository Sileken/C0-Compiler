package ast.expression;

public class UnaryExpression extends Expression {
    private Operator operator;
    private Expression operand;

    public static enum Operator {
        BANG, TILDE, MINUS, STAR, INCR, DECR
    }

    public UnaryExpression(Expression operand, Operator operator) {
        super();

        this.operand = operand;
        this.addChild(this.operand);

        this.operator = operator;
    }

    public Operator getOperator() {
        return this.operator;
    }

     public Expression getOperand() {
        return this.operand;
    }
}