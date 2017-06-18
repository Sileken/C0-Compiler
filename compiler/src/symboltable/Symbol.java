/**
 * Symbol class for "easy" extension of the symbol-table
 *
 * Possible extension could be for the TypeChecker an enum type
 * 
 * History:
 * V.1.1  - identifier is now ast.identifier.Identifier instead of String
 *
 * @version 1.1
 * @date 15.06.2017
 */
package symboltable;

import ast.*;
import ast.identifier.*;
import ast.type.*;

public class Symbol {

    private String name;
    private ASTNode node;
    private Scope withinScope;
    private Type type;

    public Symbol(String name, ASTNode inode, Scope withinScope) {
        this.name = name;
        this.node = inode;
        this.withinScope = withinScope;
    }

    public ASTNode getNode() {
        return node;
    }

    public String getName() {
        return name;
    }

    public Scope getWithinScope() {
        return withinScope;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String str = "<" + this.getNode().getClass().getSimpleName() + "> ";
        if (this.type != null) {
            try {
                str += "[" + this.type.getFullyQualifiedName() + "]";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            str += "[VOID]";
        str += name;
        return str;
    }
}