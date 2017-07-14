package codegen;

import ast.*;
import symboltable.*;
import symboltable.semanticsvisitor.*;
import logger.*;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUnitWriter extends SemanticsVisitor {
    public FileUnitWriter(SymbolTable table) {
        super(table);
    }

    @Override
    public boolean visit(ASTNode node) {
        if (node instanceof FileUnit) {
            this.writeCodeIntoFile((FileUnit) node);
        }

        return false;
    }

    private void writeCodeIntoFile(FileUnit fileUnit) {
        List<String> code = fileUnit.getGeneratedCode();
        String fileName = fileUnit.getIdentifier();
        fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        fileName += ".cma";

        File cmaFile = new File(fileName);

        Logger.debug("Writing content to: " + cmaFile.getAbsoluteFile());

        File dir = cmaFile.getAbsoluteFile().getParentFile();
        if (dir != null) {
            dir.mkdirs();
        }

        try {
            cmaFile.createNewFile();
            try (FileWriter fw = new FileWriter(cmaFile); BufferedWriter bw = new BufferedWriter(fw);) {
                for (int i = 0; i < code.size(); i++) {
                    String line = code.get(i);

                    if (line.endsWith(":")) {
                        line += " " + code.get(++i);
                    }

                    bw.write(line);
                    bw.newLine();
                }

                bw.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}