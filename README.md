## Geplanter Projektverlauf:

1. Launch Script erstellen #done
2. Test-File in tests Ordner erstellen #started
3. Skips zeichen definieren (für \n counter definieren) #done
4. Lexical Tokens für den Scanner definieren #almost finished
5. Start Produktion für den Parser definieren (z.b. Deklaration | Statement) #almost finished
6. Produktionen verfeinern #aktive
7. Creating syntax tree nodes #aktive
8. Integrate syntrax tree #aktive


## Ausführung:

`cd C0-Compiler/compiler/src/parser/ `
`javacc C0Parser.jj`
`cd ..`
`javac C0Compiler.java`
`java C0Compiler < .c0 - File >`