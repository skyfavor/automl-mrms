#!/bin/bash
##########################################################################
BASE_DIR=
EXEC_FILE="$0"
BASE_NAME=$(basename "$EXEC_FILE")
if [ "$EXEC_FILE" = "./$BASE_NAME" ] || [ "$EXEC_FILE" = "$BASE_NAME" ]; then
  BASE_DIR=$(pwd)
else
  BASE_DIR=$(echo "$EXEC_FILE" | sed 's/'"\/${BASE_NAME}"'$//')
fi
##########################################################################
source "$BASE_DIR"/.libs

CLASSPATH=$CLASSPATH:automl-mrms-1.0.0.jar
RUN_PATH=./

JAVA_OPTS="-server"
JAVA_OPTS="${JAVA_OPTS} -Xms1G -Xmx1G"
JAVA_OPTS="${JAVA_OPTS} -DAPP=MRMServer -Dfile.encoding=UTF-8"

export LANG CLASSPATH RUN_PATH
"${JAVA_HOME}"/bin/java $JAVA_OPTS -classpath $CLASSPATH com.seculayer.mrms.MRMServerMain
