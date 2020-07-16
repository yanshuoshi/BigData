#!/bin/bash -e

PID=$$
PRG="$0"
BAS_PRG=`basename ${PRG}`
BIN=`cd $(dirname $(readlink -f "$PRG")); pwd`
HOME=`dirname "$BIN"`

start(){
  LIB=`find ${HOME}/lib/ -name "*.jar"`
	LOG=${HOME}/log
	classpath="."
	for item in ${LIB}
	do
	  classpath=${classpath}:${item}
	done
	JVM_OPTS="-server -Xms128M -Xmx128M -Xloggc:${LOG}/gc.log -XX:-PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintGCApplicationStoppedTime -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:+HeapDumpOnOutOfMemoryError"
	cd ${HOME}
	LOG_CONFIG="-Dlogging.config=config/log4j2.xml"

	java ${LOG_CONFIG} ${JVM_OPTS} -cp ${classpath} com.yss.Application > /dev/null 2>&1 &
	PID=`ps -ef|grep "${HOME}/lib"|grep -v "grep" | awk '{print $2}'`
	echo ${PID} > ${BIN}/pid
}

start