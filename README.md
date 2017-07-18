## Ausführung:

`cd C0-Compiler/compiler/src/parser/ `  
`javacc C0Parser.jj`  
`cd ..`  
`javac C0Compiler.java`  
`java C0Compiler < .c0 - File >`  
  
< .c0 - filename without filetype>.cma will be placed in the same directory where 'C0Compiler.class' is located