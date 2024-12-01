@ECHO off

net session >nul 2>&1
IF %ERRORLEVEL% NEQ 0 GOTO NEED_ELEVATE
GOTO ADMINTASKS

:NEED_ELEVATE
	ECHO You must run this application with administrator privileges.

	PAUSE
	EXIT 1

:ADMINTASKS
	ECHO Setting shortcuts
	cmd /c "%~DP0\setup\register-shortcuts.cmd"

	ECHO Setting the context menu
	cmd /c "%~DP0\setup\register-context.cmd"

	ECHO Software installed

	PAUSE
