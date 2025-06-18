@echo off
echo Compiling Library Book Manager...

if not exist target\classes mkdir target\classes

echo Compiling model classes...
javac -encoding UTF-8 -d target/classes src/main/java/org/library/model/Book.java
javac -encoding UTF-8 -cp target/classes -d target/classes src/main/java/org/library/model/User.java

echo Compiling utils classes...
javac -encoding UTF-8 -cp target/classes -d target/classes src/main/java/org/library/utils/MyHashTable.java
javac -encoding UTF-8 -cp target/classes -d target/classes src/main/java/org/library/utils/MySorts.java

echo Compiling service classes...
javac -encoding UTF-8 -cp target/classes -d target/classes src/main/java/org/library/service/LibraryService.java

echo Compiling app classes...
javac -encoding UTF-8 -cp target/classes -d target/classes src/main/java/org/library/app/LibraryApp.java

echo Compiling main class...
javac -encoding UTF-8 -cp target/classes -d target/classes src/main/java/org/example/Main.java

echo Compilation completed!
echo.
echo To run the application, use:
echo java -cp target/classes org.example.Main 