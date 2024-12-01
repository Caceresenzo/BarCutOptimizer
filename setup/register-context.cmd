@ECHO off

CD "%~DP0\..\"

"%SystemRoot%\System32\reg.exe" ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizer"               /ve /d "Optimiseur de Coupe" /f
"%SystemRoot%\System32\reg.exe" ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizer"               /v Icon /t REG_SZ /d "\"%cd%\icon.ico\"" /f
"%SystemRoot%\System32\reg.exe" ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizer\command"       /ve /d "\"%cd%\jre21\bin\javaw.exe\" -jar \"%cd%\bar-cut-optimizer.jar\" -input \"%%1\"" /f

"%SystemRoot%\System32\reg.exe" ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizerdebug"          /ve /d "Optimiseur de Coupe (Debogage)" /f
"%SystemRoot%\System32\reg.exe" ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizerdebug"          /v Icon /t REG_SZ /d "\"%cd%\icon.ico\"" /f
"%SystemRoot%\System32\reg.exe" ADD "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\shell\barcutoptimizerdebug\command"  /ve /d "\"%cd%\start-pause.cmd\"  \"%%1\"" /f
