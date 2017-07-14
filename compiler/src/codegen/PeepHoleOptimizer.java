package codegen;

import ast.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;
import logger.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class PeepHoleOptimizer extends SemanticsVisitor {
    List<PeepHole> peepHoles;

    public PeepHoleOptimizer(SymbolTable table) {
        super(table);
        peepHoles = initializePeepHoles();
    }

    @Override
    public boolean visit(ASTNode node) {
        if (node instanceof FileUnit) {
            FileUnit fileUnit = (FileUnit) node;
            List<String> code = fileUnit.getGeneratedCode();

            boolean gotMatch = false;
            do {
                gotMatch = false;
                for (PeepHole peepHole : peepHoles) {
                    int index = Collections.indexOfSubList(code, peepHole.getPattern());
                    if (index == -1) {
                        gotMatch |= false;
                    } else {
                        for (int i = index; i < (index + peepHole.getPattern().size()); i++) {
                            code.remove(i);
                        }
                        if (!peepHole.replacement.trim().isEmpty()) {
                            code.add(index, peepHole.replacement);
                        }
                        gotMatch |= true;
                    }

                }
            } while (gotMatch);
        }

        return false;
    }

    private List<PeepHole> initializePeepHoles() {
        return new ArrayList<PeepHole>() {
            {
                add(new PeepHole(new ArrayList<String>() {
                    {
                        add("alloc 0");
                    }
                }, ""));
            }
        };
    }
}