package ast.statement;

import ast.expression.*;

public class ForStatement extends Statement {
    private Simple initialization;
    private Expression condition;
    private Simple increment;
    private Statement statement;

    public ForStatement(Simple initialization, Expression condition, Simple increment, Statement statement){
        super();

        if(initialization != null){
            this.initialization = initialization;
            this.addChild(this.initialization);
        }

        this.condition = condition;
        this.addChild(this.condition);

        if(increment != null){
            this.increment = increment;
            this.addChild(this.increment);
        }

        this.statement = statement;
        this.addChild(this.statement);
    }
}