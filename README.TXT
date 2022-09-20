To merge this code into your own Java code, you must include path to the demo folder in
the Debug Configuration of Eclipse. The DLL files should be kept in file search path.

JAR FILE
========
j4210n.jar : This is the main jar file. You may use this jar file in your own application.
This jar file needs j4210n.dll and other DLLs in the demo directory. All these DLLs
should be in the same directory as the jar file or in Java search path.

DEMO
====
j4210n.exe : This is a demo program. Just run the program, this should launch the 
application. If Java is not found in the path, you first need to install Java or add
Java folder in path. If the EXE does not execute, the application could be run from
a command prompt by typing the following command:

java -jar j4210n.jar

Alternatively, if you have correctly installed Java, then simply double clicking the
j4210n.jar should launch the application.

BUILDING JAR
============
j4210n.xml : Run this Ant script to rebuild the jar file after your modification. The
jar file will be created in the demo folder.

BUILDING EXE
============
launch4j.xml : After you created your application, you may distribute the application as
EXE file. You need to install Launch4J utility from the Internet and install it. Then
run this utility and load the launch4j.xml file. You may add your own jar files, if 
needed and rebuild the EXE by clicking the build icon. The new EXE will be created in
the demo folder. 

NOTE: When using Launch4J to build the EXE, you do not need to distribute the jar file.
But all the DLL files should be included with your EXE.

DLL
===
j4210n.dll : This is a critical driver DLL. This DLL must be in path along with other
supporting DLLs.

