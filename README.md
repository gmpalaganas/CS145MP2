# CS145MP2
A Network Game written in Java using Slick2D game library

How to:
======

**Note:** *A bash file will be created when the main Class has been created*

**Note:** *Make sure the file has proper permissions to execute bash scripts*

*To change permission of the bash scripts type in terminal* `chmod 755 <filename> `


Compile
------

```
javac -cp ".:lib/slick.jar:lib/lwjgl.jar" <file_name>.java
```

*If you are running on a Linux machine then*


```
./compile
```

Run
------

```
java -cp ".:lib/slick.jar:lib/lwjgl.jar" -Djava.library.path=lib/native Test
```

*If you are running on a Linux machine then*

```
./run
```
