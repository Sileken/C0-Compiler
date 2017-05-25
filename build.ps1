$buildSourcePath = 'compiler\src';
$buildTempPath = 'compiler\temp\compiler';
$buildDestPath = 'compiler\bin\';

$tempLocation = Get-Location;
$resolvedBuildSourcePath = Resolve-Path $buildSourcePath;

Write-Host "Start building." -foreground "green"

function CleanUpBuild() {
    Write-Host "Start to clean up bin folder." -foreground "green"

    Remove-Item $buildDestPath -Force -Recurse -ErrorAction Ignore;

    if (!(Test-Path $buildDestPath -PathType Container)) {
        New-Item -ItemType Directory -Force -Path $buildDestPath.ToLower();
    }   

    Write-Host "Finished to clean up bin folder." -foreground "green"
}

function CleanUpTemp() {
    Write-Host "Start to clean up temp folder." -foreground "green"

    Remove-Item $buildTempPath -Force -Recurse -ErrorAction Ignore;

    if (!(Test-Path $buildTempPath -PathType Container)) {
        New-Item -ItemType Directory -Force -Path $buildTempPath.ToLower();
    }

    Write-Host "Finished to clean up temp folder." -foreground "green"   
}

function CopyJavaFiles() {
    Write-Host "Start to copy java files to build folder." -foreground "green"

    $includeFiles = @("*.java");
    
    Get-ChildItem -Path $resolvedBuildSourcePath -Recurse -Include $includeFiles | % { 
        $destFolder = $_.PSParentPath.Replace($resolvedBuildSourcePath, $resolvedbuildTempPath);
        $destFile = $_.FullName.Replace($resolvedBuildSourcePath, $resolvedbuildTempPath);

        if (!(Test-Path $destFolder -PathType Container)) {
            New-Item -ItemType Directory -Force -Path $destFolder.ToLower();
        }

        Copy-Item $_.fullname $destFile -Force;
    }

     Write-Host "Finished to copy java files to build folder." -foreground "green"
}

function BuildJavaParser() {  
    Write-Host "Start building javacc parser." -foreground "green"

    $includeFiles = @("*.jj");    
    $parserDest = $(Join-Path $resolvedbuildTempPath "Parser").ToLower();

    if (!(Test-Path $parserDest -PathType Container)) {
        New-Item -ItemType Directory -Force -Path $parserDest;
    }

    Get-ChildItem -Path $resolvedBuildSourcePath -Recurse -Include $includeFiles | % { 
        javacc -output_directory:$parserDest $_.FullName;
    }

    Write-Host "Finished building javacc parser." -foreground "green"
}

function compileJavaFiles() {
    Write-Host "Start compiling java files." -foreground "green"

    if (!(Test-Path $buildDestPath -PathType Container)) {
        New-Item -ItemType Directory -Force -Path $buildDestPath;
    }
	
    $resolvedBuildDestPath = Resolve-Path $buildDestPath;
	
    Set-Location $buildTempPath
    javac -d $resolvedBuildDestPath *.java

    Write-Host "Finished compiling java files." -foreground "green"
}

CleanUpBuild;
CleanUpTemp;
$resolvedbuildTempPath = Resolve-Path $buildTempPath;
CopyJavaFiles;
BuildJavaParser;
compileJavaFiles;

Set-Location $tempLocation;

Write-Host "Finished building." -foreground "green"