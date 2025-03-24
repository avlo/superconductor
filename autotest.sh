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
# no explicit mvn install step as it's done after maven invoke_builder, above     
}

invoke_runner_thread() {
  if [ $MODE == "gradle" ]; then
    gradle bootRunLocalWs &
    SUPER_PID=$!
  else
    { mvn spring-boot:run -P local_ws; } &
    SUPER_PID=$!
  fi
  return $!
}

invoke_tester_thread() {
  if [ $MODE == "gradle" ]; then
    gradle test --rerun-tasks
    TESTER_PID=$!
  else
    mvn verify
    TESTER_PID=$!
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

kill_pids() {
#  banner "parent pid [$1]"
  for var in $(pgrep -P "$1"); 
    do 
#      banner "kill child pid [$var]"
      kill -15 "$var"
    done
#  banner "kill parent pid [$1]"
  kill -15 "$1"
}

terminate_superconductor() {
  cd_superconductor
  kill_pids "$SUPER_PID"
  banner "superconductor pid [$SUPER_PID] terminated"
  exit_with_code "$1"
}

# below should connect to failing nostr java tests
terminate_nostr_java() {
  kill_pids "$TESTER_PID"
  banner "nostr-java pid [$TESTER_PID] terminated"
  terminate_superconductor
}

exit_with_code() {
  exit "$1"
}

run_superconductor_tests() {
  cd_superconductor
  banner "building superconductor & running tests..."
  sleep .01
  invoke_tester_thread
  banner "...superconductor build and test completed"
}

run_nostr_java_tests() {
  banner "...[$IT_WAIT] seconds wait over, starting nostr-java tests..."
  cd_nostr_java
  invoke_tester_thread
  sleep .01
  banner "...nostr-java tests completed"
}

start_superconductor() {
  cd_superconductor
  invoke_runner_thread 
  sleep .01
  banner "starting superconductor service pid: [$SUPER_PID]" "& waiting [$IT_WAIT] seconds prior to starting nostr-java test"
  sleep $IT_WAIT
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
run_superconductor_tests
start_superconductor
run_nostr_java_tests
terminate_superconductor "0"
