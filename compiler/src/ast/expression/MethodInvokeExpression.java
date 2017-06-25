package ast.expression;

import ast.expression.primary.*;
import java.util.ArrayList;
import java.util.List;

public class MethodInvokeExpression extends Expression {
    private List<Expression> arguments = new ArrayList<Expression>() ;

    public MethodInvokeExpression(List<Expression> arguments){
        super();

        if(arguments != null && !arguments.isEmpty()){
            this.arguments = arguments;
            this.addChilds(arguments);
        }
    }

    public Primary getPrefix() {
        if(this.getParent() instanceof ExpressionPrimary){
            return ((ExpressionPrimary) this.getParent()).getPrefix();
        }
        return null;
	}
    
    public List<Expression> getArguments(){
        return this.arguments;
    }

    public int numArguments() {
        return this.arguments.size();
    }
}