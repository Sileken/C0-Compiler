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
                str += "[" + this.type.getFullyQualifiedName() + "] ";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            str += "[UNDEFINED] ";
        str += name;
        return str;
    }
}