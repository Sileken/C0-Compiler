Param(
    [Parameter(Mandatory=$True,Position=1)]
    [string]$mainFileName,
    [Object[]] $testFiles
);

$buildDestPath = 'compiler\bin';
$resolvedBuildDestPath = Resolve-Path $buildDestPath;

$tempLocation = Get-Location;
Set-Location $resolvedBuildDestPath;
Get-ChildItem $mainFileName -Recurse | Select-Object -First 1 | %{
    $mainFileLocation = $_.FullName.Substring(0, $_.FullName.LastIndexOf("\"));
    $mainFileName = $_.FullName.Substring($_.FullName.LastIndexOf("\") +1).TrimEnd(".class");
    Set-Location $mainFileLocation;

    if($testFiles -ne $null -and $testFiles -ne ""){
        write-host "Start running test files." -foreground "green";

        $testFiles | % {
            write-host "Start running test file: $($_.FullName)." -foreground "green";
            java $mainFileName $_.FullName;  
            write-host "Finished running test file: $($_.FullName)." -foreground "green";          
        }

        write-host "Finished testing." -foreground "green";  
    }else{
        write-host "Start C0 Compiler" -foreground "green";
        java $mainFileName;
    }    
}
Set-Location $tempLocation;