package ast.expression.primary;

import ast.expression.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Primary extends Expression {
    protected Primary prefix = null;

    public Primary() {
        super();
    }

    public void setPrefix(Primary prefix) {
        this.prefix = prefix;
        this.childrenList.add(0, prefix);
        prefix.parent = this;
    }

    public Primary getPrefix() {
        return this.prefix;
    }
    
    @Override
    public int countArithmeticOps()
    {
        return super.countArithmeticOps() + 2;
    }
}