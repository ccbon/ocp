@echo off
if "%LAUNCH4J_HOME%"=="" (
	echo Env var LAUNCH4J_HOME is not defined.
	goto end
)
if "%NSIS_HOME%"=="" (
	echo Env var NSIS_HOME is not defined.
	goto end
)

call mvn package -P windows-x86_64 -DskipTests
call mvn package -P windows-x86_32 -DskipTests
call mvn package -P linux-x86_64 -DskipTests
call mvn package -P linux-x86 -DskipTests
call mvn package -P macosx-cacao-x86_64 -DskipTests
call mvn package -P macosx-cacao-x86 -DskipTests

setlocal
cp build/gdse_64.l4j "%LAUNCH4J_HOME%\gdse_64.l4j"
cp build/gdse_32.l4j "%LAUNCH4J_HOME%\gdse_32.l4j"
cp target/ocp-agent-complete-windows-x86_64.jar "%LAUNCH4J_HOME%\ocp-agent-complete-windows-x86_64.jar"
cp target/ocp-agent-complete-windows-x86_32.jar "%LAUNCH4J_HOME%\ocp-agent-complete-windows-x86_32.jar"
cp build/Soccer.ico "%LAUNCH4J_HOME%\Soccer.ico"
cd "%LAUNCH4J_HOME%"
launch4jc gdse_64.l4j
launch4jc gdse_32.l4j
endlocal
move /Y "%LAUNCH4J_HOME%\gdse_x64.exe" "target\gdse_x64.exe"
move /Y "%LAUNCH4J_HOME%\gdse_x32.exe" "target\gdse_x32.exe"

setlocal
mkdir target\nsis
cp target\gdse_x64.exe target\nsis\gdse_x64.exe
cp target\gdse_x32.exe target\nsis\gdse_x32.exe
cp build\Soccer.ico target\nsis\Soccer.ico
cp build\gdse.nsi target\nsis\gdse.nsi
cd target\nsis
"%NSIS_HOME%\makensis.exe" gdse.nsi
endlocal
move /Y "target\nsis\gdse_setup.exe" "target\gdse_setup.exe"

:end