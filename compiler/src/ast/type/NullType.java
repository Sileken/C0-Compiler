
package ast.type;

public class NullType extends Type{

    public NullType()
    {
        super();
    }

    @Override
    public String getFullyQualifiedName() {
        return "NULL";
    }
}