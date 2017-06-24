package ast.statement;

import ast.expression.*;

public class IfStatement extends Statement {
    Expression condition;
    Statement ifStatement;
    ElseStatement elseStatement;

    public Expression getCondition(){ return condition; }
    public IfStatement(Expression condition, Statement ifStatement, ElseStatement elseStatement){
        super();

        this.condition = condition;
        this.addChild(this.condition);
        
        this.ifStatement = ifStatement;
        this.addChild(this.ifStatement);

        if(elseStatement != null){
            this.elseStatement = elseStatement;
            this.addChild(this.elseStatement);
        }
    }
}