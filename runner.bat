@echo off
setlocal
:: Run Maven clean package and copy dependencies
echo Running Maven clean package...
start cmd /k "mvn clean package -DskipTests && mvn dependency:copy-dependencies && exit"

echo Waiting for Maven to finish...
timeout /t 60

echo Running the TestRunner file:
java -javaagent:%USERPROFILE%\.m2\repository\org\aspectj\aspectjweaver\1.9.20.1\aspectjweaver-1.9.20.1.jar -cp target/test-classes;target/classes;target/dependency/* com.icon.forwardplus.runner.TestRunner"

endlocal