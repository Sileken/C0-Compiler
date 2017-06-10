package ast.expression;

public class StatementExpression extends Expression {
    private Expression expression;

    public StatementExpression(Expression expression){
        super();
        
        this.expression = expression;
        this.addChild(this.expression);
    }
}