package codegen;

import ast.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;

import java.util.List;
import java.util.ArrayList;

public class PeepHoleOptimizer extends SemanticsVisitor {

    public PeepHoleOptimizer(SymbolTable table) {
        super(table);
    }

    @Override
    public boolean visit(ASTNode node) {
        if (node instanceof FileUnit) {

        }

        return false;
    }

}