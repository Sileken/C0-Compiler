package ast.expression;

import ast.expression.primary.*;
import java.util.ArrayList;
import java.util.List;

public class MethodInvokeExpression extends Expression {
    private Primary prefix;
    private List<Expression> arguments = new ArrayList<Expression>();

    public MethodInvokeExpression(Primary prefix, List<Expression> arguments) {
        super();

        this.prefix = prefix;
        this.addChild(prefix);

        if (arguments != null && !arguments.isEmpty()) {
            this.arguments = arguments;
            this.addChilds(arguments);
        }
    }

    public Primary getPrimary(){
        return this.prefix;
    }

    public List<Expression> getArguments(){
        return this.arguments;
    }
}