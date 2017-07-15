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

    // Build up the prefixes tree in java-code
    // This function gets recursively called.
    public void addPrefixes(List<Primary> prefixes) {
        // Delete last element which is this element itself
        if (prefixes.size() >= 1) {
            prefixes.remove(prefixes.size() - 1);
        }

        // If the prefix-list is not empty add the next prefix as a child and call this func resursively
        if (!prefixes.isEmpty()) {
            // Get the next prefix which is the last element now
            Primary nextPrefix = prefixes.get(prefixes.size() - 1);
            this.setPrefix(nextPrefix);

            // Recursive call
            nextPrefix.addPrefixes(prefixes);
        }
    }

    @Override
    public int countArithmeticOps()
    {
        return super.countArithmeticOps() + 2;
    }
}