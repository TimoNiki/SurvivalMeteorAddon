# этот файл правильно скомпилирует проект так как
# в нем учитываются настройки проекта и gradle
# чтобы скомпилировать код вам потребуется Java 21
# и тут если у вас есть эта джава она
# будет учитываться.
# цвет текста добавил LightLight01 а все остальное это от TimoNiki
@echo off
color 05
Echo ###########################
echo Fast-Compile by TimoNiki
echo Copyright (c) 2026 TimoNiki
echo ###########################
echo setting Java To 21.
set "JAVA_HOME=C:\Program Files\Java\jdk-21"

if not exist "%JAVA_HOME%\bin\java.exe" (
    echo Error: code 1. See error-codes.txt for help.
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"
./gradlew build
