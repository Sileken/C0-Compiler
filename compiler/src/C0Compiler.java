import parser.*;
import ast.*;

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
      //ast.PrintPretty();
    } catch (ParseException e) {
      System.out.println("C0 Compiler: Encountered errors during parse.");
      e.printStackTrace();
    } catch (Exception e1) {
      System.out.println("C0 Compiler: Encountered errors during interpretation/tree building.");
      e1.printStackTrace();
    }
  }
}
