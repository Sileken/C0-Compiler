package ast.type;

public class ReferenceType extends Type {
    private Type innerType;

    public ReferenceType(Type innerType){
        this.innerType = innerType;
        this.addChild(this.innerType);
    }
}