@echo off

set "currentDir=%~dp0"
set "lib=%currentDir%lib"
set "bin=%currentDir%bin"
set "src=%currentDir%src"

set "testlib=C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\temp\WEB-INF\lib"

: complilation
echo "debut de la compilation"
:: Créer une liste de tous les fichiers .java dans le répertoire src et ses sous-répertoires
dir /s /B "%src%\*.java" > sources.txt

mkdir "compTest";

for /F "tokens=*" %%f in (sources.txt) do (
    
    xcopy "%%f" "compTest"
)

javac -cp "%lib%\*" -d "%bin%" compTest\*java

del sources.txt
rd /s /q "compTest" 
echo "fin de la compilation, compilation reussi"
: Jarisation
echo "debut de l'export to jar"

jar cvf YellowFrameWork.jar -C "%bin%" . 
echo "export to jar Done!"
: copie to TestFrameWork
copy YellowFrameWork.jar "%testlib%"
