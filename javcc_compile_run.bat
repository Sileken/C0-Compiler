@echo off

rem filename for the C0-INPUT-FILE. Set to NOTHING if you want to use console input
SET c0_file=example.c0

if defined c0_file (SET c0_dir=../../C0Code/%c0_file%) else (SET c0_dir=)

rem Parser Name, specified in .jj
SET parser_name=C0Parser
rem output directory
SET out_dir=compiler/bin
SET current_path=%~dp0

rem Check for parameter (Silvan: Not really needed anymore right?)
IF [%1] == [] SET /P compiler_file="Enter a file name: "
IF [%compiler_file%] == [] SET compiler_file=%1

rem Start processing
call javacc -output_directory:%out_dir% %compiler_file%

cd %out_dir%
call javac %parser_name%.java
call java %parser_name% %c0_dir%

rem Benny's old code. Not needed anymore. Just kept it for whatever reason.
rem cd %current_path% 
rem cmd.exe /k
rem exit