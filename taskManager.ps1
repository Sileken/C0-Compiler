Param(
    [Parameter(Mandatory=$True,Position=1)]
    [string]$task
);

$mainFileName = "C0Compiler.class";
$userTestFile = "C0Code/example1.c0";
$allTestFileFolder = "C0Code";

Write-Host "Start running task $($task)" -foreground "green";

if($task -eq "build"){
    &.\build.ps1;
    &.\run.ps1 -mainFileName $mainFileName;
} elseif($task -eq "test"){
    &.\build.ps1;
    $testfiles = Get-ChildItem $allTestFileFolder -Recurse | ?{ !$_.PSIsContainer }
    &.\run.ps1 -mainFileName $mainFileName -testFiles $testfiles;
} elseif($task -eq "testown"){
    &.\build.ps1;
    $testfiles = Get-Item $userTestFile | ?{ !$_.PSIsContainer }
    &.\run.ps1 -mainFileName $mainFileName -testFiles $testfiles;
} elseif($task -eq "run"){
    &.\run.ps1 -mainFileName $mainFileName;
}


Write-Host "Finished running task $($task)" -foreground "green";