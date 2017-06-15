import parser.*;
import ast.*;
import ast.identifier.*;
import ast.statement.*;
import symboltable.*;

public class C0Compiler {
  public static void main(String args[]) {
    C0Parser parser;

    if (args.length == 1) {
      System.out.println("C0 Compiler: Reading from file " + args[0] + " . . .");
      try {
        parser = new C0Parser(new java.io.FileInputStream(args[0]));
      } catch (java.io.FileNotFoundException e) {
        System.out.println("C0 Compiler: File " + args[0] + " not found.");
        return;
      }
    } else {
      //System.out.println("C0 Compiler: Usage :");
      //System.out.println("Please pass a C0 File-Path to the C0 Compiler.");
      System.out.println("C0 Compiler: Reading input from console.");
      parser = new C0Parser(System.in);
    }

    try {
      AST ast = parser.parseTree();
      System.out.println("SUCCESS!");
      ast.PrintPretty();

      initializeASTNodes(ast);

    } catch (ParseException e) {
      System.out.println("C0 Compiler: Encountered errors during parse.");
      e.printStackTrace();
    } catch (Exception e1) {
      System.out.println("C0 Compiler: Encountered errors during interpretation/tree building.");
      e1.printStackTrace();
    }
  }

  /**
   * Set additional information into the nodes of the nearly created AST.
   * <anmerkung> da AST als Referenz uebergeben wird ist return ueberfluessig (?) </anmerkung> 
   */
  private static void initializeASTNodes(AST ast) {
    final SymbolTable symbolTable = new SymbolTable();
    symbolTable.enterScope();
    createSymbolTable(symbolTable, ast.getRoot());

  }

  /**
   * Creates the symbol-table and checks for errors during the <symbol-table (hier richtigen Begriff)> phase.
   */
  private static void createSymbolTable(SymbolTable symbolTable, ASTNode astNode) {
    
    if (astNode instanceof Identifier) {
      symbolTable.addSymbol((Identifier) astNode);
      System.out.println("New identifier: " + ((Identifier) astNode).getName());

      // doMore stuff

    } else if (astNode instanceof Block) {
      symbolTable.enterScope();
      System.out.println("New Scope");

      // doMore stuff

    }

    // recursive traverse children
    for (ASTNode node : astNode.getChildren()) {
      createSymbolTable(symbolTable, node);
    }
  }
}