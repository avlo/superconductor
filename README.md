```java
 .::::::.  ...    :::::::::::::. .,:::::: :::::::..     .,-:::::     ...   :::.    :::.:::::::-.   ...    :::  .,-:::::::::::::::::   ...    :::::::..
;;;`    `  ;;     ;;; `;;;```.;;;;;;;'''' ;;;;``;;;;  ,;;;'````'  .;;;;;;;.`;;;;,  `;;; ;;,   `';, ;;     ;;;,;;;'````';;;;;;;;''''.;;;;;;;. ;;;;``;;;;
'[==/[[[[,[['     [[[  `]]nnn]]'  [[cccc   [[[,/[[['  [[[        ,[[     \[[,[[[[[. '[[ `[[     [[[['     [[[[[[            [[    ,[[     \[[,[[[,/[[['
  '''    $$$      $$$   $$$""     $$""""   $$$$$$c    $$$        $$$,     $$$$$$ "Y$c$$  $$,    $$$$      $$$$$$            $$    $$$,     $$$$$$$$$c
 88b    dP88    .d888   888o      888oo,__ 888b "88bo,`88bo,__,o,"888,_ _,88P888    Y88  888_,o8P'88    .d888`88bo,__,o,    88,   "888,_ _,88P888b "88bo,
  "YMmMY"  "YmmMMMM""   YMMMb     """"YUMMMMMMM   "W"   "YUMMMMMP" "YMMMMMP" MMM     YM  MMMMP"`   "YmmMMMM""  "YUMMMMMP"   MMM     "YMMMMMP" MMMM   "W"
```
# Java Nostr-Relay, Relay Framework & WebSocket Application Server

----

### Normal/Production Mode Instructions:
#### Confirm docker requirements

    $ docker --version
>     Docker version 27.5.0
    $ docker compose version
>     Docker Compose version v2.32.4

(Download links for the above)
- [Docker](https://hub.docker.com/_/docker) 27.5.0
- [Docker Compose](https://docs.docker.com/compose/install/) v2.32.4

_(note: Confirmed compatible with Docker 27.0.3 and Docker Compose version v2.28.1 or higher.  Earlier versions are at the liability of the developer/administrator)_

----

#### Download Superconductor Docker Image from [hub.docker](https://hub.docker.com/repository/docker/avlo/superconductor/tags)
    $ docker pull avlo/superconductor:1.15.1

----

#### Configure SuperConductor security level, 3 options:

<details>
  <summary>Highest | SSL Certificate (WSS/HTTPS)</summary>
  <ul>
    <li><a href="https://www.websitebuilderexpert.com/building-websites/how-to-get-an-ssl-certificate/">Obtain</a> an SSL certificate</li>
    <li><a href="https://www.baeldung.com/java-import-cer-certificate-into-keystore">Install</a> the certificate</li>
    <li>Download <a href="src/main/resources/application-prod_wss.properties.properties">application-prod_wss.properties</a> file & configure <a href="src/main/resources/application-prod_wss.properties.properties?plain=1#L6,8,L11-L15"> SSL settings</a></li>
    <li>Download <a href="superconductor/docker-compose-prod_wss.yml">docker-compose-prod_wss.yml</a> file <i>(and optionally <a href="superconductor/docker-compose-prod_wss.yml?plain=1#L10,32,L36-L37">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details>

<details>
  <summary>Medium | Self-Signed Certificate (WSS/HTTPS)</summary>
  <ul>
    <li><a href="https://www.baeldung.com/openssl-self-signed-cert">Create </a>a Self-Signed Certificate</li>
	<li><a href="https://www.baeldung.com/java-import-cer-certificate-into-keystore">Install</a> the certificate</li>
	<li>Download <a href="src/main/resources/application-prod_wss.properties.properties">application-prod_wss.properties</a> file & configure <a href="src/main/resources/application-prod_wss.properties.properties?plain=1#L6,8,L11-L15"> SSL settings</a></li>
    <li>Download <a href="superconductor/docker-compose-prod_wss.yml">docker-compose-prod_wss.yml</a> file <i>(and optionally <a href="superconductor/docker-compose-prod_wss.yml?plain=1#L10,32,L36-L37">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details> 

<details>
  <summary>Lowest | Non-secure / Non-encrypted (WS/HTTP)</summary>
  <ul>
    <li>Security-related configuration(s) not required</li>
    <li>Download <a href="superconductor/docker-compose-prod_ws.yml">docker-compose-prod_ws.yml</a> file <i>(and optionally <a href="superconductor/docker-compose-prod_ws.yml?plain=1#L10,32,L36-L37">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details>

----

#### Run SuperConductor

<details>
  <summary>WSS/HTTPS</summary>  

run without logging:

    docker compose -f superconductor/docker-compose-prod_wss.yml up 

run with container logging displayed to console:  

    docker compose -f superconductor/docker-compose-prod_wss.yml up --abort-on-container-failure --attach-dependencies

run with docker logging displayed to console:  

    docker compose -f superconductor/docker-compose-prod_wss.yml up -d && dcls | grep 'superconductor-app' | awk '{print $1}' | xargs docker logs -f
</details> 

<details>
  <summary>WS/HTTP</summary>  

run without logging:

    docker compose -f superconductor/docker-compose-prod_ws.yml up 

run with container logging displayed to console:

    docker compose -f superconductor/docker-compose-prod_ws.yml up --abort-on-container-failure --attach-dependencies

run with docker logging displayed to console:

    docker compose -f superconductor/docker-compose-prod_ws.yml up -d && dcls | grep 'superconductor-app' | awk '{print $1}' | xargs docker logs -f
</details> 

----

##### Stop SuperConductor

<details>
  <summary>WSS/HTTPS</summary>

    docker compose -f superconductor/docker-compose-prod_wss.yml stop superconductor superconductor-db
</details> 

<details>
  <summary>WS/HTTP</summary>  

    docker compose -f superconductor/docker-compose-prod_ws.yml stop superconductor superconductor-db
</details>

----  

##### Remove SuperConductor docker containers

<details>
  <summary>WSS/HTTPS</summary>

    docker compose -f superconductor/docker-compose-prod_wss.yml down --remove-orphans
</details> 

<details>
  <summary>WS/HTTP</summary>  

    docker compose -f superconductor/docker-compose-prod_ws.yml down --remove-orphans
</details>

<hr style="border:2px solid grey">

#### [Development Mode Instructions](DEVELOPMENT.md)
