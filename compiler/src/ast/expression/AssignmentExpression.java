package ast.expression;

import ast.declaration.*;

public class AssignmentExpression extends Expression {
    //private PrimaryExpression primaryExpression;
    private VariableDeclaration variableDeclaration;
    private Operator operator;
    private Expression rightExpression;
    
    public static enum Operator {
		ASSIGN, PLUSASSIGN, MINUSASSIGN, STARASSIGN, SLASHASSIGN, REMASSIGN, ANDASSIGN, XORASSIGN, ORASSIGN
	}

/*
    public AssignmentExpression(PrimaryExpression primaryExpression, Operator operator, Expression rightExpression){
        super();

        this.primaryExpression = primaryExpression;
        this.addChild(this.primaryExpression);

        this.operator = operator;

        this.rightExpression = rightExpression;
        this.addChild(this.rightExpression);
    }*/

    public AssignmentExpression(VariableDeclaration variableDeclaration, Expression rightExpression){
        super();

        this.variableDeclaration = variableDeclaration;
        this.addChild(this.variableDeclaration);

        this.operator = Operator.ASSIGN;

        this.rightExpression = rightExpression;
        this.addChild(this.rightExpression);
    }
}