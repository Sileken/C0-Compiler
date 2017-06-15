package ast;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {
    private ASTNode parent = null;
    private String identifier = new String();
    private List<ASTNode> childrenList = new ArrayList<ASTNode>();

	public ASTNode() {
	}

    /**
	 * @return the parent
	 */
	public ASTNode getParent() {
		return parent;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

    public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

    public void addChild(ASTNode child) {
		child.parent = this;
		this.childrenList.add(child);
	}

	public void addChilds(List<? extends ASTNode> childs) {
		for (ASTNode child : childs) {
			this.addChild(child);
		}
	}

	public void PrintPretty(String indent, boolean last)
	{
		System.out.print(indent);
		if (last)
		{
			System.out.print("\\-");
			indent += "  ";
		}
		else
		{
			System.out.print("|-");
			indent += "| ";
		}
       	System.out.println(this.getClass().getSimpleName());

		for (int i = 0; i < this.childrenList.size(); i++){
			this.childrenList.get(i).PrintPretty(indent, i == this.childrenList.size() - 1);
		}
   }

   public List<ASTNode> getChildren() {
	   return this.childrenList;
   }
}