package ast.expression.primary;

import ast.expression.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Primary extends Expression {
    public Primary() {
        super();
    }

    // Build up the suffixes tree in java-code
    // This function gets recursively called.
    public void addSuffixes(List<Primary> suffixes)
    {
        // Delete last element which is this element itself
        if(suffixes.size() >= 1)
        {
            suffixes.remove(suffixes.size() - 1);
        }

        // If the suffix-list is not empty add the next suffix as a child and call this func resursively
        if(!suffixes.isEmpty())
        {
            // Get the next suffix which is the last element now
            Primary nextSuffix = suffixes.get(suffixes.size() - 1);
            this.addChild(nextSuffix);

            // Recursive call
            nextSuffix.addSuffixes(suffixes);
        }

    }
}