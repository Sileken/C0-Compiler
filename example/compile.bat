set name="Interpreter"

call javacc -output_directory:bin %name%.jj 

cd bin
call javac *.java

echo Finished compiling...

pause