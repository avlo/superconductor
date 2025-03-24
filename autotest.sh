#!/bin/bash

# run script:
#   . ./autotest.sh

# script parameters (see #user_prompt):
#   G/g gradle, M/m maven, (default/<enter>: gradle) 

# kill process/thread/job
#   <ctrl>-C
#   kill %1
#   ps| grep java|awk '{print $1}' | xargs kill -9

# duration (seconds) between superconductor service process start and nostr-java integration-test start
IT_WAIT=15
# IT_WAIT > ( time @ superconductor service process in "running state" ) - ( time @ superconductor service process start )  

M2_HOME='/home/nick/.m2/repository/'
NOSTR_JAVA_MAVEN_LOCAL_REPO=$M2_HOME/xyz/tcheeric
GIT_HOME='/home/nick/git'
NOSTR_HOME=$GIT_HOME/avlo-nostr-java-fork/
SUPER_HOME=$GIT_HOME/superconductor/

# build tool mode defaults to gradle
MODE="gradle"

invoke_builder() {
  if [ $MODE == "gradle" ]; then
    gradle clean build -x test || exit_with_code "33"
  else
    mvn install -DskipTests || exit_with_code "33"
  fi
}

invoke_publisher() {
  if [ $MODE == "gradle" ]; then
    gradle publishToMavenLocal || exit_with_code "33"
  fi
# no explicit mvn install step as it's done after build/line 24     
}

invoke_runner_thread() {
  if [ $MODE == "gradle" ]; then
    gradle bootRunLocalWs || terminate_both "33"
  else
    mvn spring-boot:run -P local_ws || terminate_both "33"
  fi
  return $!
}

invoke_tester() {
  if [ $MODE == "gradle" ]; then
    gradle test --rerun-tasks || terminate_both "33"
  else
    mvn verify || terminate_both "33"
  fi
  return $!
}

banner_line_content() {
  numeric=$(echo "$@" | grep -oE '[0-9]+([.][0-9]+)?')
  if [ ${#numeric} -gt 3 ]; then    
    printf "|$(tput bold setaf 003) %-65s $(tput sgr0)|\n" "$@" 
  else
    printf "|$(tput bold) %-65s $(tput sgr0)|\n" "$@"
  fi
}

banner() {
  echo "+-------------------------------------------------------------------+"
  printf "| %-65s |\n" "$(date)"
  echo "|                                                                   |"
  for arg
    do 
        banner_line_content "$arg"
    done
  echo "+-------------------------------------------------------------------+"
  printf "\n"
}

cd_nostr_java() {
  banner "cd to nostr-java:" "" "     [$NOSTR_HOME]"
  cd $NOSTR_HOME || exit_with_code "33" 
}

cd_superconductor() {
  banner "cd to superconductor:" "" "     [$SUPER_HOME]"
  cd $SUPER_HOME || exit_with_code "33"
}

rm_maven_local() {
  banner "clear nostr-java [$NOSTR_JAVA_MAVEN_LOCAL_REPO] repo"
  rm -rf $NOSTR_JAVA_MAVEN_LOCAL_REPO || exit_with_code "33"   
}

build_nostr_java() {
  banner "building nostr-java..."
  invoke_builder || exit_with_code "33"
  banner "...completed nostr-java build" 
}

publish_nostr_java_to_m2_local() {
  banner "publishing to m2/local local:" "" "       [$NOSTR_JAVA_MAVEN_LOCAL_REPO]"  
  invoke_publisher || exit_with_code "33"
  banner "...completed publishing to m2/local"
  banner "$NOSTR_JAVA_MAVEN_LOCAL_REPO contents:"
  tree -D $NOSTR_JAVA_MAVEN_LOCAL_REPO
}

run_superconductor_tests() {
  banner "building superconductor & running tests..."
  sleep .01
  invoke_tester
  super_completion_code=$?
  sleep .01
  if [ $super_completion_code != 0 ]; then
    banner "...superconductor test failure, code: [$super_completion_code], exiting"
    terminate_super "33"
  fi  
  banner "...completed superconductor build and test"
}

run_nostr_java_tests() {
  banner "...[$IT_WAIT] seconds wait over, starting nostr-java tests..."
  cd_nostr_java
  invoke_tester & NOSTR_PID=$! || terminate_both "33"
  banner "...nostr-java tests started, pid: [$NOSTR_PID]..."
  wait $NOSTR_PID
  banner "...nostr-java tests done"
}

start_superconductor() {
  cd_superconductor
  invoke_runner_thread & SUPER_PID=$! || terminate_super "33" 
  banner "starting superconductor service pid: [$SUPER_PID]" "& waiting [$IT_WAIT] seconds prior to starting nostr-java test"
  sleep $IT_WAIT
}

terminate_superconductor() {
  kill -9 "$SUPER_PID"
  pkill -P $$
  banner "superconductor terminated"
  exit_with_code "$1"
}

terminate_nostr_java() {
  kill -9 "$NOSTR_PID"
  kill -9 "$SUPER_PID"
  pkill -P $$
  banner "nostr-java terminated"
  exit_with_code "$1"
}

terminate_both() {
  terminate_superconductor "$1"
  terminate_nostr_java "$1"
}

exit_with_code() {
  cd_superconductor
  exit "$1"
}

usage() { echo "Usage: . ./autotest.sh" 1>&2; exit 1; }

user_prompt() {
  while true; do
      read -p "G/g gradle, M/m maven, (default/<enter>: gradle)" yesno
      case $yesno in
          [Yy]* ) 
              echo "Gradle selected"
              return
          ;;
          [Mm]* ) 
              echo "Maven selected"
              MODE="maven"
              return
          ;;
          * ) echo "Default (Gradle) selected"
              return
          ;;
      esac
  done
}

###########     main    ################

user_prompt
 
cd_nostr_java
rm_maven_local
build_nostr_java
publish_nostr_java_to_m2_local 
banner "nostr-java dependencies completed"

cd_superconductor
run_superconductor_tests
banner "superconductor dependencies completed"

start_superconductor
run_nostr_java_tests
banner "tests passed"

terminate_superconductor "0"
