#!/bin/bash 

if [[ ${OSTYPE} == *linux* ]]; then
  JAVAINC=linux
  SOEXT=so
elif [[ ${OSTYPE} == *darwin* ]]; then
  JAVAINC=macosx
  SOEXT=jnilib
fi
  
# java side
#rm -f SysTime.h
#rm -f SysTime.class
#rm -f ClockBench.class
#rm -f libsystime.${SOEXT}
export M2_HOME=
#ulimit -c unlimited
/usr/share/maven3/bin/mvn -Dplatform=${JAVAINC} -f pom.xml clean install
/usr/share/maven3/bin/mvn -f native/${JAVAINC}/pom.xml native:javah
/usr/share/maven3/bin/mvn -f native/${JAVAINC}/pom.xml native:compile
/usr/share/maven3/bin/mvn -f java/pom.xml clean install
java -Djava.library.path=native/${JAVAINC}/target -server -cp "java/target/benchmarks.jar" edu.SysTime
