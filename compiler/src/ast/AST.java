package ast;

public class AST {
  private ASTNode root;

  public ASTNode getRoot() {
    return this.root;
  }

  public void setRoot(ASTNode node) {
    this.root = node;
  }

  public String PrintPretty() {
    return this.root.PrintPretty("", true);
  }
}