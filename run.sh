#!/bin/sh
export CLASSPATH=$CLASSPATH:/usr/share/java/postgresql.jar
javac Main.java Programa.java Sqlas.java
java Main

