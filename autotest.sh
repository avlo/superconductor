#!/bin/bash

# run script:
#  ./autotest.sh

# script parameters (see #user_prompt):
#  G/g gradle, M/m maven, (default/<enter>: gradle) 

#debug/verbose mode
# PS4="\033[1;33m+\033[0m "; set -x

# duration (seconds) between superconductor service process start and nostr-java integration-test start
IT_WAIT=15
# IT_WAIT > ( time @ superconductor service process in "running state" ) - ( time @ superconductor service process start )  

M2_HOME='/home/nick/.m2/repository'
NOSTR_JAVA_MAVEN_LOCAL_REPO=$M2_HOME/xyz/tcheeric
GIT_HOME='/home/nick/git'
NOSTR_HOME=$GIT_HOME/nostr-java-avlo-fork
SUPER_HOME=$GIT_HOME/superconductor

# build tool mode defaults to gradle
MODE="gradle"
FAST_LABEL="fast (gradle-managed entities state)"
# full re-run mode, default off
CLEAN_AND_RERUN_MODE=0

FAILURE_EXIT_CODE=1

gradle_clean_build_no_tests() { 
  gradle clean build -x test -x check
}

gradle_build_no_tests() {
  gradle build -x test -x check
}

maven_clean_install_no_tests() {
  mvn clean install -DskipTests
}

maven_install_no_tests() {
  mvn install -DskipTests
}

gradle_test_rerun_tasks() {
  gradle test --rerun-tasks
}

gradle_test() {
  gradle test --rerun-tasks
}

gradle_integration_test_rerun_tasks() {
  gradle check --rerun-tasks -x test
}

gradle_integration_test() {
  gradle check
}

is_clean_rerun_mode() {
  [[ $CLEAN_AND_RERUN_MODE -eq 1 ]]
}

gradle_full_rebuild() {
  is_clean_rerun_mode && { banner "gradle full re-build started"; gradle_clean_build_no_tests; return; }
}

gradle_fast_build() {
  { banner "$FAST_LABEL build started"; gradle_build_no_tests; return; }
}

maven_full_rebuild() {
  is_clean_rerun_mode && { banner "maven full re-build started"; maven_clean_install_no_tests; return; }
}

maven_fast_build() {
  { banner "maven build started"; maven_install_no_tests; return; }
}

full_unit_retest() {
  is_clean_rerun_mode && { banner "full unit retest started"; gradle_test_rerun_tasks; return; }
}

fast_unit_retest() {
  { banner "$FAST_LABEL unit tests started"; gradle_test; return; }
}

full_integration_retest() {
  is_clean_rerun_mode && ( banner "full integration tests-rerun mode started"; gradle_integration_test_rerun_tasks )
}

fast_integration_retest() {
  ( banner "$FAST_LABEL integration tests started"; gradle_integration_test )
}

invoke_builder() {
  if [ $MODE == "gradle" ]; then
    ( gradle_full_rebuild || gradle_fast_build ) || exit_with_code $FAILURE_EXIT_CODE
  else
    ( maven_full_rebuild || maven_fast_build ) || exit_with_code $FAILURE_EXIT_CODE
  fi
}

invoke_unit_tests() {
  if [ $MODE == "gradle" ]; then
    ( full_unit_retest || fast_unit_retest ) || exit_with_code $FAILURE_EXIT_CODE
  else
    mvn test || exit_with_code $FAILURE_EXIT_CODE
  fi
}

invoke_integration_tests() {
  if [ $MODE == "gradle" ]; then
    full_integration_retest || fast_integration_retest
    TESTER_PID=$!
#    debug_banner "invoke_integration_tests" [$TESTER_PID]
  else
    mvn verify
    TESTER_PID=$!
  fi
  return $!
}

publish() {
  if [ $MODE == "gradle" ]; then
    gradle publishToMavenLocal || exit_with_code $FAILURE_EXIT_CODE
  fi
# no explicit mvn install step as it's done after maven invoke_builder, above     
}

run_superconductor() {
  if [ $MODE == "gradle" ]; then
    gradle bootRunLocalWs &
    SUPER_PID=$!
  else
    { mvn spring-boot:run -P local_ws; } &
    SUPER_PID=$!
  fi
  return $!
}

banner_content_line() {
  numeric=$(echo "$@" | grep -oE '[0-9]+([.][0-9]+)?')
  if [ ${#numeric} -gt 3 ]; then    
    printf "|$(tput bold setaf 003) %-65s $(tput sgr0)|\n" "$@" 
  else
    printf "|$(tput bold) %-65s $(tput sgr0)|\n" "$@"
  fi
}

banner_content() {
  printf "| %-65s |\n" "$(date +"%F %T.%3N")"
    echo "|                                                                   |"
    for arg
      do 
        banner_content_line "$arg"
      done
}

banner() {
  echo "+-------------------------------------------------------------------+"
  banner_content "$@"
  echo "+-------------------------------------------------------------------+"
  printf "\n"
}

cd_nostr_java() {
  banner "cd to nostr-java:" "" "     [$NOSTR_HOME]"
  cd $NOSTR_HOME || exit_with_code $FAILURE_EXIT_CODE 
}

cd_superconductor() {
  banner "cd to superconductor:" "" "     [$SUPER_HOME]"
  cd $SUPER_HOME || exit_with_code $FAILURE_EXIT_CODE
}

rm_maven_local() {
  banner "clear nostr-java [$NOSTR_JAVA_MAVEN_LOCAL_REPO] repo"
  rm -rf $NOSTR_JAVA_MAVEN_LOCAL_REPO || exit_with_code $FAILURE_EXIT_CODE   
}

build_nostr_java() {
  cd_nostr_java
  rm_maven_local
  banner "building nostr-java..."
  invoke_builder || exit_with_code $FAILURE_EXIT_CODE
  banner "...nostr-java unit tests, next..."
  invoke_unit_tests || exit_with_code $FAILURE_EXIT_CODE
  banner "...completed nostr-java build & unit tests" 
}

build_superconductor() {
  cd_superconductor
  banner "building superconductor..."
  invoke_builder || exit_with_code $FAILURE_EXIT_CODE
  banner "...completed superconductor build" 
}

publish_nostr_java_to_m2_local() {
  banner "publishing to m2/local local:" "" "       [$NOSTR_JAVA_MAVEN_LOCAL_REPO]"  
  publish || exit_with_code $FAILURE_EXIT_CODE
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

exit_with_code() {
  exit $1
}

terminate_superconductor() {
  cd_superconductor
  kill_pids "$SUPER_PID"
  banner "superconductor pid [$SUPER_PID] terminated, autotest complete"
  exit_with_code $1
}

# below should connect to failing nostr java tests
terminate_nostr_java() {
  kill_pids "$TESTER_PID"
  banner "nostr-java pid [$TESTER_PID] terminated"
  terminate_superconductor
}

run_superconductor_integration_tests() {
  banner "running superconductor integration tests..."
  cd_superconductor
  sleep .01
  invoke_integration_tests || terminate_superconductor $FAILURE_EXIT_CODE
  banner "...superconductor integration tests completed"
}

run_nostr_java_integration_tests() {
  banner "...[$IT_WAIT] seconds done, starting nostr-java integration tests..."
  cd_nostr_java
  invoke_integration_tests
  sleep .01
  banner "...nostr-java integration tests completed"
}

start_superconductor() {
  cd_superconductor
  run_superconductor 
  sleep .01
  banner "starting superconductor service pid: [$SUPER_PID]" "& waiting [$IT_WAIT] seconds prior to starting nostr-java test"
  sleep $IT_WAIT
}

usage() { echo "Usage:  ./autotest.sh" 1>&2; exit 1; }

user_prompt() {
  while true; do
    read -p "superconductor integration-test script options:
  (m) -> maven
  (v) -> force test-rerun (gradle only)
  (enter/default) -> updated gradle-cache classes/tests only; aka, fast mode): " -a args_array
    
    [[ ${#args_array[@]} -eq 0 ]] && 
      { printf "$(tput bold setaf 003)%-0s$(tput sgr0)\n" "default (gradle) builder & runner selected";
        printf "$(tput bold setaf 003)%-0s$(tput sgr0)\n" "default (changed-tests only) mode selected";
        return; }
        
    for entry in "${args_array[@]}"; do
      case $entry in
        [g] ) 
            printf "$(tput bold setaf 003)%-0s$(tput sgr0)\n" "gradle builder & runner selected"
            ;;
        [m] ) 
            printf "$(tput bold setaf 003)%-0s$(tput sgr0)\n" "maven builder & runner selected"
            MODE="maven"
            ;;
        [v] ) 
            printf "$(tput bold setaf 003)%-0s$(tput sgr0)\n" "gradle force test-rerun mode selected"
            CLEAN_AND_RERUN_MODE=1
            ;;    
      esac
    done
    return
  done
}

###########     main    ################
user_prompt

# unit tested nostr-java snapshot build 
build_nostr_java
publish_nostr_java_to_m2_local

# unit & integration tested superconductor snapshot build
build_superconductor 
run_superconductor_integration_tests

# run nostr-java integration tests
start_superconductor
run_nostr_java_integration_tests || terminate_nostr_java $FAILURE_EXIT_CODE

terminate_superconductor "0"
