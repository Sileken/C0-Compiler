Param(
    [Parameter(Mandatory=$True,Position=1)]
    [string]$task
);

Write-Host "Running task $($task)";

if($task -eq "build"){
    &.\build.ps1;
    invoke-expression 'cmd /c start powershell -Command { &.\run.ps1 -mainFileName C0Compiler.class }'
} elseif($task -eq "test"){
    &.\build.ps1;
    #todo: run test files
    #invoke-expression 'cmd /c start powershell -Command { &.\run.ps1 -mainFileName C0Compiler.class }'
} elseif($task -eq "run"){
    invoke-expression 'cmd /c start powershell -Command { &.\run.ps1 -mainFileName C0Compiler.class }'
}
