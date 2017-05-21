$buildSourcePath = 'compiler\src';
$buildTempPath = 'compiler\temp\compiler';
$buildDestPath = 'compiler\bin\';

$tempLocation = Get-Location;
$resolvedBuildSourcePath = Resolve-Path $buildSourcePath;

function CleanUpBuild() {
    Remove-Item $buildDestPath -Force -Recurse -ErrorAction Ignore;

    if (!(Test-Path $buildDestPath -PathType Container)) {
        New-Item -ItemType Directory -Force -Path $buildDestPath.ToLower();
    }   
}

function CleanUpTemp() {
    Remove-Item $buildTempPath -Force -Recurse -ErrorAction Ignore;

    if (!(Test-Path $buildTempPath -PathType Container)) {
        New-Item -ItemType Directory -Force -Path $buildTempPath.ToLower();
    }   
}

function CopyJavaFiles() {
    $includeFiles = @("*.java");
    
    Get-ChildItem -Path $resolvedBuildSourcePath -Recurse -Include $includeFiles | % { 
        $destFolder = $_.PSParentPath.Replace($resolvedBuildSourcePath, $resolvedbuildTempPath);
        $destFile = $_.FullName.Replace($resolvedBuildSourcePath, $resolvedbuildTempPath);

        if (!(Test-Path $destFolder -PathType Container)) {
            New-Item -ItemType Directory -Force -Path $destFolder.ToLower();
        }

        Copy-Item $_.fullname $destFile -Force;
    }
}

function BuildJavaParser() {    
    $includeFiles = @("*.jj");    
    $parserDest = $(Join-Path $resolvedbuildTempPath "Parser").ToLower();

    if (!(Test-Path $parserDest -PathType Container)) {
        New-Item -ItemType Directory -Force -Path $parserDest;
    }

    Get-ChildItem -Path $resolvedBuildSourcePath -Recurse -Include $includeFiles | % { 
        javacc -output_directory:$parserDest $_.FullName;
    }

}

function compileJavaFiles() {
    if (!(Test-Path $buildDestPath -PathType Container)) {
        New-Item -ItemType Directory -Force -Path $buildDestPath;
    }
	
    $resolvedBuildDestPath = Resolve-Path $buildDestPath;
	
    Set-Location $buildTempPath
    javac -d $resolvedBuildDestPath *.java
}

CleanUpBuild;
CleanUpTemp;
$resolvedbuildTempPath = Resolve-Path $buildTempPath;
CopyJavaFiles;
BuildJavaParser;
compileJavaFiles;

Set-Location $tempLocation;