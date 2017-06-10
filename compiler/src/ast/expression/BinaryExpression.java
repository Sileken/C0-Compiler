package ast.expression;

public class BinaryExpression extends Expression {
    private Expression leftOperand;
    private Operator operator;
    private Expression rightOperand;

    public static enum Operator {
		OR, AND, BOR, BXOR, BAND, EQ, NEQ, LT, GT, LEQ, GEQ, PLUS, MINUS, STAR, SLASH, REM  
	}

    public BinaryExpression(Expression leftOperand, Operator operator, Expression rightOperand){
        super();
        this.leftOperand = leftOperand;
        this.addChild(this.leftOperand);

        this.operator = operator;

        this.rightOperand = rightOperand;
        this.addChild(this.rightOperand);
    }
}