#!/bin/bash

# clear;. ./autotest.sh &
# ps| grep java|awk '{print $1}' | xargs kill -9

SLEEP=12
GIT_HOME='/home/nick/git'
M2_HOME='/home/nick/.m2/repository/'
NOSTR_HOME=$GIT_HOME/avlo-nostr-java-fork/
SUPER_HOME=$GIT_HOME/superconductor/
NOSTR_JAVA_MAVEN_LOCAL_REPO=$M2_HOME/xyz/tcheeric

banner() {
  echo "+-------------------------------------------------------------------+"
  printf "| %-65s |\n" "$(date)"
  echo "|                                                                   |"
  for arg
    do
        content_line "$arg"
    done
  echo "+-------------------------------------------------------------------+"
  printf "\n"
}

content_line() {
  printf "|$(tput bold) %-65s $(tput sgr0)|\n" "$@"
}

cd_nostr() {
  banner "cd to nostr-java:" "" "     [$NOSTR_HOME]"
  cd $NOSTR_HOME || exit 
}

cd_superconductor() {
  banner "cd to superconductor:" "" "     [$SUPER_HOME]"
  cd $SUPER_HOME || exit 
}

rm_maven_local() {
  banner "clear nostr-java [$NOSTR_JAVA_MAVEN_LOCAL_REPO] repo"
  rm -rf $NOSTR_JAVA_MAVEN_LOCAL_REPO || exit   
}

build_nostr() {
  banner "building nostr-java..."
  gradle clean build -x test || exit
  banner "...completed nostr-java build" 
}

publish_nostr_to_m2_local() {
  banner "publishing to m2/local local:" "" "       [$NOSTR_JAVA_MAVEN_LOCAL_REPO]"  
  gradle publishToMavenLocal || exit
  banner "...completed publishing to m2/local"
  banner "$NOSTR_JAVA_MAVEN_LOCAL_REPO contents:"
  tree -D $NOSTR_JAVA_MAVEN_LOCAL_REPO
}

build_and_test_super() {
  banner "building superconductor & running tests..."
  sleep .01
  gradle clean build --rerun-tasks || return 
  banner "...completed superconductor build and test"
  sleep .01  
}

terminate_super() {
  kill -9 $SUPER_PID
  banner "super terminated"
}

terminate_nostr() {
  kill -9 $NOSTR_PID
  banner "nostr terminated"
}

killsuper() {
  terminate_super
}

killnostr() {
  terminate_nostr
}

killboth() {
  terminate_super
  terminate_nostr
}

killshell() {
  kill $BASHPID
  killboth  
}

run_super_service() {
  cd_superconductor
  { gradle bootRunLocalWs & } || exit 
  SUPER_PID=$!
  banner "starting superconductor service pid: [$SUPER_PID]" "& waiting [$SLEEP] seconds prior to starting nostr-java test"
  sleep $SLEEP
}

run_nostr_tests() {
  banner "...[$SLEEP] seconds wait over, starting nostr-java tests..."
  cd_nostr
  { gradle test --rerun-tasks & } || exit
  NOSTR_PID=$!
  banner "...nostr-java tests started, pid: [$NOSTR_PID]..."
  wait $NOSTR_PID
  banner "...nostr-java tests done"
}

########################################
###########     main    ################
########################################

export -f killshell

{ cd_nostr; rm_maven_local; build_nostr; publish_nostr_to_m2_local; } 
banner "nostr-java dependencies completed"

cd_superconductor
build_and_test_super
super_completion_code=$?
banner "super exit code [$super_completion_code]"
if [ $super_completion_code != 0 ]; then
  banner "erroneous exit, tests not performed"
  terminate_super
  exit;
fi

banner "superconductor dependencies completed"

run_super_service
export -f killsuper 

run_nostr_tests
export -f killnostr
export -f killboth

banner "tests passed"
terminate_super
