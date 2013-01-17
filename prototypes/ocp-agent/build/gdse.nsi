!include LogicLib.nsh

!define NAME "GDSE"
Var gdseprog

Function .onInit
  ExecWait "javaw -version" $0
  ${If} $0 == 0
    ExecWait "java -d64 -version" $1
    ${If} $1 == 0
	  StrCpy $InstDir "$PROGRAMFILES64\${NAME}"
	  StrCpy $gdseprog "gdse_x64.exe"
	${Else}
	  StrCpy $InstDir "$PROGRAMFILES\${NAME}"
	  StrCpy $gdseprog "gdse.exe"
	${EndIf}
  ${Else}
	MessageBox MB_OK "Java is required to run the installer. Please install a Java 1.7 or later."
	Abort
  ${EndIf}
FunctionEnd

Name "${NAME}"
outFile "${NAME}_setup.exe"
RequestExecutionLevel admin

Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

Section "${NAME} (required)"
  SectionIn RO
  
  SetOutPath $INSTDIR
  ${If} $1 == 0
    File "gdse_x64.exe"
  ${Else}
    File "gdse_x32.exe"
  ${EndIf}
  File "soccer.ico"
   
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\${NAME} "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${NAME}" "DisplayIcon" "$INSTDIR\$gdseprog"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${NAME}" "DisplayName" "${NAME}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${NAME}" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${NAME}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${NAME}" "NoRepair" 1
  WriteUninstaller "uninstall.exe"
  
SectionEnd

Section "Start Menu Shortcuts"
  CreateDirectory "$SMPROGRAMS\${NAME}"
  CreateShortCut "$SMPROGRAMS\${NAME}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${NAME}\${NAME}.lnk" "$INSTDIR\$gdseprog" "" "$INSTDIR\$gdseprog" 0
SectionEnd

Section "Desktop shortcut"
  CreateShortCut "$DESKTOP\${NAME}.lnk" "$INSTDIR\$gdseprog" "" "$INSTDIR\$gdseprog" 0
SectionEnd

;--------------------------------
; Uninstaller
Section "Uninstall"  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${NAME}"
  DeleteRegKey HKLM SOFTWARE\${NAME}

  ; Remove files and uninstaller
  Delete $INSTDIR\$gdseprog
  Delete $INSTDIR\uninstall.exe
  
  Delete "$SMPROGRAMS\${NAME}\*.*"
  Delete "$DESKTOP\${NAME}.lnk"
  RMDir "$SMPROGRAMS\${NAME}"
  RMDir "$INSTDIR"
SectionEnd
