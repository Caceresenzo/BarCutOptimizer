@echo off

NET SESSION
IF %ERRORLEVEL% NEQ 0 GOTO NEED_ELEVATE
GOTO ADMINTASKS

:NEED_ELEVATE
cls

echo x
echo x
echo x
echo x Vous devez executer cette application avec les privileges administrateurs.
echo x
echo x
echo x

pause
EXIT

:ADMINTASKS
cls

%SystemRoot%\System32\reg.exe ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizer"               /ve /d "Optimiseur de Coupe" /f
%SystemRoot%\System32\reg.exe ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizer"               /v Icon /t REG_SZ /d "\"%~DP0\ressources\icon.ico\"" /f
%SystemRoot%\System32\reg.exe ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizer\command"       /ve /d "\"%~DP0\start.cmd\"       \"%%1\"" /f

%SystemRoot%\System32\reg.exe ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizerdebug"          /ve /d "Optimiseur de Coupe (DEBUG)" /f
%SystemRoot%\System32\reg.exe ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizerdebug"          /v Icon /t REG_SZ /d "\"%~DP0\ressources\icon.ico\"" /f
%SystemRoot%\System32\reg.exe ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizerdebug\command"  /ve /d "\"%~DP0\startpause.cmd\"  \"%%1\"" /f

"%~DP0\bin\shortcut.exe" /a:c /i:"%~DP0\ressources\icon.ico" /f:"C:\Users\%username%\Desktop\Optimiseur de Coupe.lnk" /t:"%~DP0\bar-cut-optimizer.cmd"
"%~DP0\bin\shortcut.exe" /a:c /i:"%~DP0\ressources\icon.ico" /f:"C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Optimiseur de Coupe.lnk" /t:"%~DP0\bar-cut-optimizer.cmd"

pause;