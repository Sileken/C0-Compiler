@echo off

rem Parser Name, specified in .jj
SET parser_name=C0Parser
rem output directory
SET out_dir=bin
SET current_path=%~dp0

rem Check for parameter
IF [%1] == [] SET /P compiler_file="Enter a file name: "
IF [%compiler_file%] == [] SET compiler_file=%1

rem Start processing
call javacc -output_directory:%out_dir% %compiler_file%

cd %out_dir%
call javac %parser_name%.java
call java %parser_name%
cd %current_path%

cmd.exe /k
exit