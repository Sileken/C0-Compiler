package ast.expression;

import parser.*;
import ast.declaration.*;
import ast.expression.primary.Primary;

public class AssignmentExpression extends Expression {
    private Expression leftvalue;
    private VariableDeclaration variableDeclaration;
    private Operator operator;
    private Expression rightExpression;
    
    public static enum Operator {
		ASSIGN, PLUSASSIGN, MINUSASSIGN, STARASSIGN, SLASHASSIGN, REMASSIGN, ANDASSIGN, XORASSIGN, ORASSIGN
	}

    public VariableDeclaration getVariableDeclaration(){ return variableDeclaration; }
    public Expression getRightValue() { return rightExpression; }
    public Expression getLeftValue(){ return leftvalue; }
    public Operator getOperator() { return operator; }

    public AssignmentExpression(Expression leftvalue, Operator operator, Expression rightExpression){
        super();

        this.leftvalue = leftvalue;
        this.addChild(this.leftvalue);

        this.operator = operator;

        this.rightExpression = rightExpression;
        this.addChild(this.rightExpression);
    }

    public AssignmentExpression(VariableDeclaration variableDeclaration, Expression rightExpression){
        super();

        this.variableDeclaration = variableDeclaration;
        this.addChild(this.variableDeclaration);

        this.operator = Operator.ASSIGN;

        this.rightExpression = rightExpression;
        this.addChild(this.rightExpression);
    }

    public static Operator parseOperator(Token token) throws ParseException{
        Operator parsedOperator;

        switch(token.image){
            case "=":
                parsedOperator = Operator.ASSIGN;
            break;
            case "+=":
                parsedOperator = Operator.PLUSASSIGN;
            break;
            case "-=":
                parsedOperator = Operator.MINUSASSIGN;
            break;
            case "*=":
                parsedOperator = Operator.STARASSIGN;
            break;
            case "/=":
                parsedOperator = Operator.SLASHASSIGN;
            break;
            case "%=":
                parsedOperator = Operator.REMASSIGN;
            break;
            case "&=":
                parsedOperator = Operator.ANDASSIGN;
            break;
            case "^=":
                parsedOperator = Operator.XORASSIGN;
            break;
            case "|=":
             parsedOperator = Operator.ORASSIGN;
            break;
            default:
                throw new ParseException("Can't parse assignment Operator: " + token.kind);
        }

        return parsedOperator;
    }
}