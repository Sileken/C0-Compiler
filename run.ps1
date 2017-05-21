Param(
    [Parameter(Mandatory=$True,Position=1)]
    [string]$mainFileName
);

$buildDestPath = 'compiler\bin';
$resolvedBuildDestPath = Resolve-Path $buildDestPath;

$tempLocation = Get-Location;
Set-Location $resolvedBuildDestPath;
Get-ChildItem $mainFileName -Recurse | Select-Object -First 1 | %{
    $mainFileLocation = $_.FullName.Substring(0, $_.FullName.LastIndexOf("\"));
    $mainFileName = $_.FullName.Substring($_.FullName.LastIndexOf("\") +1).TrimEnd(".class");
    Set-Location $mainFileLocation
    java $mainFileName
}
Set-Location $tempLocation;