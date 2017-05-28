package ast.statement;

import ast.expression.*;

public class ForStatement extends Statement {
    private SimpleStatement initialization;
    private Expression condition;
    private SimpleStatement increment;
    private Statement statement;

    public ForStatement(SimpleStatement initialization, Expression condition, SimpleStatement increment, Statement statement){
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