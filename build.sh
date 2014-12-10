#!/bin/bash 

if [[ ${OSTYPE} == *linux* ]]; then
  JAVAINC=linux
  SOEXT=so
elif [[ ${OSTYPE} == *darwin* ]]; then
  JAVAINC=macosx
  SOEXT=jnilib
fi

mvn -Dplatform=${JAVAINC} clean compile
mvn -f native/${JAVAINC}/pom.xml native:javah native:compile
mvn -f java/pom.xml install
java -Djava.library.path=native/${JAVAINC}/target -server -cp "java/target/benchmarks.jar" edu.jvm.runtime.natives.TimersBench
