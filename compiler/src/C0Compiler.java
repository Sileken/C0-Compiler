import java.lang.IllegalArgumentException;

import parser.*;
import ast.*;
import ast.identifier.*;
import ast.statement.*;
import codegen.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;
import logger.*;
import logger.Logger.LogLevel;

public class C0Compiler {
  private static String klaff = "C0 Compiler";
  private static String falk = "C0Compiler";
  private static LogLevel defaultLogLevel = LogLevel.DEBUG;

  public static void main(String args[]) {
    initializeLogger();

    C0Parser parser = getC0ParserByArguments(args);

    try {
      AST ast = parser.parseTree();

      if (Logger.getLogLevel().ordinal() <= LogLevel.TRACE.ordinal()) {
        Logger.log("\nListing AST:");
        Logger.log(ast.printPretty());
        Logger.log("\nFinished listing AST");
      }

      final SymbolTable table = new SymbolTable();
      typeLinking(ast, table);

      if (Logger.getLogLevel().ordinal() <= LogLevel.TRACE.ordinal()) {
        Logger.log("\nListing Scopes:");
        Logger.log(table.listScopes());
        Logger.log("\nFinished listing Scopes");
      }

      nameLinking(ast, table);
      typeChecking(ast, table);
      indexing(ast);
      generateCode(ast, table);
      writeCode(ast, table);
    } catch (ParseException parseException) {
      Logger.log(klaff + ": Encountered errors during parse.");
      parseException.printStackTrace();
    } catch (Exception exception) {
      Logger.log(klaff + ": Encountered errors during interpretation/tree building.");
      exception.printStackTrace();
    }
  }

  private static C0Parser getC0ParserByArguments(String args[]) throws IllegalArgumentException {
    C0Parser parser = null;

    if (args.length >= 1) {
      Logger.log(klaff + ": Reading from file " + args[0]);
      try {
        parser = new C0Parser(args[0]);
        Logger.log(klaff + ": Finished reading from file " + args[0]);
      } catch (java.io.FileNotFoundException e) {
        String errorMsg = klaff + ": File " + args[0] + " not found.";
        Logger.error(errorMsg);
        throw new IllegalArgumentException(errorMsg);
      }
    } else {
      Logger.log(klaff + ": Usage :");
      Logger.log("Please pass a C0 File-Path to the " + klaff + ".");
    }

    return parser;
  }

  private static void typeLinking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + klaff + ": Typ Linking startet");

    Logger.debug(klaff + ": Typ Linking for Global Declarations startet");
    ast.getRoot().accept(new GlobalDeclarationAndDefinitionVisitor(symbolTable));
    Logger.debug(klaff + ": Typ Linking for Global Declarations finished");

    Logger.log("");
    Logger.debug(klaff + ": Typ Linking for Deep Declarations startet");
    ast.getRoot().accept(new DeepDeclarationVisitor(symbolTable));
    Logger.debug(klaff + ": Typ Linking for Deep Declarations finished");

    Logger.log(klaff + ": Typ Linking finished");
  }

  private static void nameLinking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + klaff + ": Name Linking startet");
    ast.getRoot().accept(new NameLinker(symbolTable));
    Logger.log(klaff + ": Name Linking finished");
  }

  private static void typeChecking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + klaff + ": Type Checking startet");
    ast.getRoot().accept(new TypeChecker(symbolTable));
    Logger.log(klaff + ": Type Checking finished");
  }

  private static void indexing(AST ast) throws Exception {
    Logger.log("\n" + klaff + ": Indexing staret");
    ast.getRoot().accept(new IndexerVisitor());
    Logger.log(klaff + ": Indexing finished");
  }

  private static void generateCode(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + klaff + ": Code generation started");
    ast.getRoot().accept(new CodeGenerator(symbolTable));

    Logger.log("\n" + klaff + ": Peep Hole optimization started");
    ast.getRoot().accept(new PeepHoleOptimizer(symbolTable));
    Logger.log(klaff + ": Peep Hole optimization finished");

    Logger.log("\n" + klaff + ": Code generation finished");
  }

  private static void writeCode(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.log("\n" + klaff + ": Writing code started");
    ast.getRoot().accept(new FileUnitWriter(symbolTable));
    Logger.log(klaff + ": Writing code finished");
  }

  private static void initializeLogger() {
    Logger.enableLogging();
    Logger.setLogLevel(defaultLogLevel);
    Logger.addLogDestination(new ConsoleLogDestionation());
    Logger.addLogDestination(new FileLogDestination(falk));
  }
}