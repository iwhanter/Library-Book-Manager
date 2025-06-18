@echo off
echo Compiling and running tests for Library Book Manager...

if not exist target\test-classes mkdir target\test-classes

echo Compiling main classes...
call compile.bat

echo Compiling test classes...
javac -encoding UTF-8 -cp "target/classes;lib/*" -d target/test-classes src/test/java/org/library/utils/MyHashTableTest.java
javac -encoding UTF-8 -cp "target/classes;lib/*" -d target/test-classes src/test/java/org/library/utils/MySortsTest.java
javac -encoding UTF-8 -cp "target/classes;lib/*" -d target/test-classes src/test/java/org/library/utils/MyHashTableStressTest.java
javac -encoding UTF-8 -cp "target/classes;lib/*" -d target/test-classes src/test/java/org/library/utils/MySortsStressTest.java
javac -encoding UTF-8 -cp "target/classes;lib/*" -d target/test-classes src/test/java/org/library/utils/BinarySearchStressTest.java
javac -encoding UTF-8 -cp "target/classes;lib/*" -d target/test-classes src/test/java/org/library/service/LibraryServiceTest.java
javac -encoding UTF-8 -cp "target/classes;lib/*" -d target/test-classes src/test/java/org/library/service/LibraryServiceStressTest.java

echo Running basic tests...
java -cp "target/classes;target/test-classes" org.junit.platform.console.ConsoleLauncher --class-path "target/classes;target/test-classes" --select-class org.library.utils.MyHashTableTest
java -cp "target/classes;target/test-classes" org.junit.platform.console.ConsoleLauncher --class-path "target/classes;target/test-classes" --select-class org.library.utils.MySortsTest
java -cp "target/classes;target/test-classes" org.junit.platform.console.ConsoleLauncher --class-path "target/classes;target/test-classes" --select-class org.library.service.LibraryServiceTest

echo.
echo Note: Stress tests require JUnit 5 dependencies.
echo To run stress tests, install Maven and run: mvn test -Dtest="*StressTest"
echo.
echo Basic compilation and test setup completed! 