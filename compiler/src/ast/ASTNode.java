package ast;

import java.util.ArrayList;
import java.util.List;

import ast.visitor.ASTVisitor;
import logger.*;

public abstract class ASTNode {
	protected ASTNode parent = null;
	protected String identifier = new String();
	protected List<ASTNode> childrenList = new ArrayList<ASTNode>();

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

	protected void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	protected void addChild(ASTNode child) {
		child.parent = this;
		this.childrenList.add(child);
	}

	protected void addChilds(List<? extends ASTNode> childs) {
		for (ASTNode child : childs) {
			this.addChild(child);
		}
	}

	public final void accept(ASTVisitor visitor) throws Exception {
		Logger.trace("Will visit <" + this.getClass().getSimpleName() + ">");
		visitor.willVisit(this);

		Logger.trace("Visiting <" + this.getClass().getSimpleName() + ">");
		if (visitor.visit(this)) {
			for (ASTNode childNode : this.childrenList) {
				childNode.accept(visitor);
			}
		}

		visitor.didVisit(this);
		Logger.trace("Did visit <" + this.getClass().getSimpleName() + ">");
	}

	public String printPretty(String indent, boolean last) {
		String out = "\n" + indent;

		if (last) {
			out += "\\-";
			indent += "  ";
		} else {
			out += "|-";
			indent += "| ";
		}

		String name = this.getClass().getSimpleName();
		if (this.getIdentifier() != null && !this.getIdentifier().trim().isEmpty()) {
			name += ": " + this.getIdentifier();
		}

		out += name;

		for (int i = 0; i < this.childrenList.size(); i++) {
			out += this.childrenList.get(i).printPretty(indent, i == this.childrenList.size() - 1);
		}

		return out;
	}

	public List<ASTNode> getChildren() {
		return this.childrenList;
	}

	// Calculates the amount of arithmetic-operations for a node and all of his children
	// Use-Case: For Code-Generation it is the maximum amount of the stack-frame for a function
	public int countArithmeticOps()
	{
		int max = 0;
		int[] sums  = new int[childrenList.size()];
		for (int i = 0; i < this.childrenList.size(); i++) {
			sums[i] = this.childrenList.get(i).countArithmeticOps();
			if(sums[i] > max)
				max = sums[i];
		}
		return max;
	}
}