package ast.type;

public class ArrayType extends Type {
    private Type innerType;

    public ArrayType(Type innerType){
        super();
        
        this.innerType = innerType;
        this.addChild(this.innerType);
    }
}