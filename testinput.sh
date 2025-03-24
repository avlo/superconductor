#!/bin/bash

# clear;. ./autotest.sh &
# ps| grep java|awk '{print $1}' | xargs kill -9

MODE="gradle"
# build tool defaults to gradle

invoke_builder() {
  echo "gradle builder" 
  sleep 1000 &
  SUPER_PID=$!
}

start_superconductor() {
  invoke_builder || terminate_super "33" 
  banner "starting superconductor service pid: [$SUPER_PID]" 
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

terminate_superconductor() {
  banner "prekill $SUPER_PID"
  kill -9 "$SUPER_PID"
  banner "postkill $SUPER_PID"
#  pkill -P $$
#  `pstree "$SUPER_PID"`
#  banner "$procval"
  banner "superconductor terminated"
}

user_prompt() {
  while true; do
      read -p "Y/y or enter for Gradle, M/m for Maven)" yesno
      case $yesno in
          [Yy]* ) 
              echo "Running with Gradle"
              return
          ;;
          [Mm]* ) 
              echo "Running with Maven"
              MODE="maven"
              return
          ;;
          * ) echo "Default selected, running with Gradle"
              return
          ;;
      esac
  done
}

########################################
###########     main    ################
########################################

user_prompt
start_superconductor
terminate_superconductor
