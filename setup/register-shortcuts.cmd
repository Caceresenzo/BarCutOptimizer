@ECHO off

CD "%~DP0\..\"

".\setup\shortcut.exe" /a:c /i:"%cd%\icon.ico" /f:"C:\Users\%username%\Desktop\Optimiseur de Coupe.lnk" /t:"%cd%\jre21\bin\javaw.exe" /p:"-jar %cd%\bar-cut-optimizer.jar"

".\setup\shortcut.exe" /a:c /i:"%cd%\icon.ico" /f:"C:\ProgramData\Microsoft\Windows\Start Menu\Programs\Optimiseur de Coupe.lnk" /t:"%cd%\jre21\bin\javaw.exe" /p:"-jar %cd%\bar-cut-optimizer.jar"
