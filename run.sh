#!/bin/bash
# запуск тестов из консоли одной командой: bash run.sh
cd "$(dirname "$0")"

# скачать JUnit, если его ещё нет
if [ ! -f junit.jar ]; then
  echo "качаю JUnit..."
  curl -sSL -o junit.jar https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.11.3/junit-platform-console-standalone-1.11.3.jar
fi

# скомпилировать
javac -cp junit.jar Main.java MainTest.java || exit 1

# запустить тесты
java -jar junit.jar execute -cp . -c MainTest --details=tree
