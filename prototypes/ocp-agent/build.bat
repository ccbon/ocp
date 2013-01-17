@echo off
if "%LAUNCH4J_HOME%"=="" (
	echo Env var LAUNCH4J_HOME is not defined.
	goto end
)
if "%NSIS_HOME%"=="" (
	echo Env var NSIS_HOME is not defined.
	goto end
)

setlocal
@echo on
copy /Y build\gdse_64.l4j "%LAUNCH4J_HOME%\gdse_64.l4j"
copy /Y build\gdse_32.l4j "%LAUNCH4J_HOME%\gdse_32.l4j"
copy /Y target\ocp-agent-complete-windows-x86_64.jar "%LAUNCH4J_HOME%\ocp-agent-complete-windows-x86_64.jar"
copy /Y target\ocp-agent-complete-windows-x86_32.jar "%LAUNCH4J_HOME%\ocp-agent-complete-windows-x86_32.jar"
copy /Y build\Soccer.ico "%LAUNCH4J_HOME%\Soccer.ico"
cd "%LAUNCH4J_HOME%"
launch4jc gdse_64.l4j
launch4jc gdse_32.l4j
endlocal
move /Y "%LAUNCH4J_HOME%\gdse_x64.exe" "target\gdse_x64.exe"
move /Y "%LAUNCH4J_HOME%\gdse_x32.exe" "target\gdse_x32.exe"

setlocal
mkdir target\nsis
copy /Y target\gdse_x64.exe target\nsis\gdse_x64.exe
copy /Y target\gdse_x32.exe target\nsis\gdse_x32.exe
copy /Y build\Soccer.ico target\nsis\Soccer.ico
copy /Y build\gdse.nsi target\nsis\gdse.nsi
cd target\nsis
"%NSIS_HOME%\makensis.exe" gdse.nsi
endlocal
move /Y "target\nsis\gdse_setup.exe" "target\gdse_setup.exe"

:end