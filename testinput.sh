#!/bin/bash

# clear;. ./autotest.sh &
# ps| grep java|awk '{print $1}' | xargs kill -9

MODE="gradle"
# build tool defaults to gradle

invoke_builder() {
  if [ $MODE == "gradle" ]; then
    echo "gradle builder"; return
  fi
  echo "maven builder"    
}


#BUILDER=$(gradle clean build -x test)
#PUBLISHER=$(gradle publishToMavenLocal)
#RUNNER=$(gradle bootRunLocalWs)
#TESTER=$(gradle test --rerun-tasks)

#use_maven() {
#  BUILDER=$(mvn clean -Dmaven.test.skip=true)
#  PUBLISHER=$(mvn install)
#  RUNNER=$(mvn spring-boot:run -P local_ws)
#  TESTER=$(mvn verify)
#}

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

invoke_builder
