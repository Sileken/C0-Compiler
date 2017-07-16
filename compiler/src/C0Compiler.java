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
  private static LogLevel defaultLogLevel = LogLevel.ERROR;

  public static void main(String args[]) {
    try {
      initializeLogger();
      Logger.log("Start compiling ...");

      C0Parser parser = getC0ParserByArguments(args);

      if (parser != null) {
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
      }
      Logger.log("Finished compiling.");
    } catch (Exception exception) {
      if (Logger.getLogLevel().ordinal() <= LogLevel.TRACE.ordinal()) {
        exception.printStackTrace();
      } else {
        Logger.error(exception.getMessage());
      }
    }
  }

  private static C0Parser getC0ParserByArguments(String args[]) throws IllegalArgumentException {
    C0Parser parser = null;

    if (args.length >= 1) {
      Logger.info(klaff + ": Reading from file " + args[0]);
      try {
        parser = new C0Parser(args[0]);
        Logger.info(klaff + ": Finished reading from file " + args[0]);
      } catch (java.io.FileNotFoundException e) {
        throw new IllegalArgumentException(klaff + ": File " + args[0] + " not found.");
      }
    } else {
      Logger.log(klaff + ": Usage :");
      Logger.log("Please pass a C0 File-Path to the " + klaff + ".");
    }

    return parser;
  }

  private static void typeLinking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.info("\n" + klaff + ": Typ Linking startet");

    Logger.debug(klaff + ": Typ Linking for Global Declarations startet");
    ast.getRoot().accept(new GlobalDeclarationAndDefinitionVisitor(symbolTable));
    Logger.debug(klaff + ": Typ Linking for Global Declarations finished");

    Logger.info("");
    Logger.debug(klaff + ": Typ Linking for Deep Declarations startet");
    ast.getRoot().accept(new DeepDeclarationVisitor(symbolTable));
    Logger.debug(klaff + ": Typ Linking for Deep Declarations finished");

    Logger.info(klaff + ": Typ Linking finished");
  }

  private static void nameLinking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.info("\n" + klaff + ": Name Linking startet");
    ast.getRoot().accept(new NameLinker(symbolTable));
    Logger.info(klaff + ": Name Linking finished");
  }

  private static void typeChecking(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.info("\n" + klaff + ": Type Checking startet");
    ast.getRoot().accept(new TypeChecker(symbolTable));
    Logger.info(klaff + ": Type Checking finished");
  }

  private static void indexing(AST ast) throws Exception {
    Logger.info("\n" + klaff + ": Indexing staret");
    ast.getRoot().accept(new IndexerVisitor());
    Logger.info(klaff + ": Indexing finished");
  }

  private static void generateCode(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.info("\n" + klaff + ": Code generation started");
    ast.getRoot().accept(new CodeGenerator(symbolTable));

    Logger.info("\n" + klaff + ": Peep Hole optimization started");
    ast.getRoot().accept(new PeepHoleOptimizer(symbolTable));
    Logger.info(klaff + ": Peep Hole optimization finished");

    Logger.info("\n" + klaff + ": Code generation finished");
  }

  private static void writeCode(AST ast, SymbolTable symbolTable) throws Exception {
    Logger.info("\n" + klaff + ": Writing code started");
    ast.getRoot().accept(new FileUnitWriter(symbolTable));
    Logger.info(klaff + ": Writing code finished");
  }

  private static void initializeLogger() {
    Logger.enableLogging();
    Logger.setLogLevel(defaultLogLevel);
    Logger.addLogDestination(new ConsoleLogDestionation());
    Logger.addLogDestination(new FileLogDestination(falk));
  }
}