package ast.expression;

import ast.expression.primay.*;
import java.util.ArrayList;
import java.util.List;

public class MethodInvokeExpression extends Expression {
    private Primary prefix;
    private List<Expression> arguments = new ArrayList<Expression>() ;

    public MethodInvokeExpression(Primary prefix, List<Expression> arguments){
        super();

        this.prefix = prefix;
        this.addChild(prefix);

        if(arguments != null && !arguments.isEmpty()){
            this.arguments = arguments;
            this.addChilds(arguments);
        }        
    }
}