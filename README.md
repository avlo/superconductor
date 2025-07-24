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

<details>
  <summary>redis</summary>  

      $ docker pull avlo/superconductor-app-redis:1.16.0
</details>
<details>
  <summary>sqlite</summary>  

      $ docker pull avlo/superconductor-app-sqlite:1.16.0
</details>
<details>
  <summary>mysql</summary>  

      $ docker pull avlo/superconductor-app-mysql:1.16.0
</details>

----

#### Configure SuperConductor security level, 3 options:

<details>
  <summary>Highest | SSL Certificate (WSS/HTTPS)</summary>
  <ul>
    <li><a href="https://www.websitebuilderexpert.com/building-websites/how-to-get-an-ssl-certificate/">Obtain</a> an SSL certificate</li>
    <li><a href="https://www.baeldung.com/java-import-cer-certificate-into-keystore">Install</a> the certificate</li>
    <li>Download <a href="superconductor/redis/src/main/resources/application-prod_wss.properties">application-prod_wss.properties</a> file & configure <a href="superconductor/redis/src/main/resources/application-prod_wss.properties"> SSL settings</a></li>
    <li>Download <a href="superconductor/redis/docker-compose-prod_wss.yml">docker-compose-prod_wss.yml</a> file <i>(and optionally <a href="superconductor/redis/docker-compose-prod_wss.yml">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details>

<details>
  <summary>Medium | Self-Signed Certificate (WSS/HTTPS)</summary>
  <ul>
    <li><a href="https://www.baeldung.com/openssl-self-signed-cert">Create </a>a Self-Signed Certificate</li>
	<li><a href="https://www.baeldung.com/java-import-cer-certificate-into-keystore">Install</a> the certificate</li>
	<li>Download <a href="superconductor/redis/src/main/resources/application-prod_wss.properties">application-prod_wss.properties</a> file & configure <a href="superconductor/redis/src/main/resources/application-prod_wss.properties"> SSL settings</a></li>
    <li>Download <a href="superconductor/redis/docker-compose-prod_wss.yml">docker-compose-prod_wss.yml</a> file <i>(and optionally <a href="superconductor/redis/docker-compose-prod_wss.yml">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details> 

<details>
  <summary>Lowest | Non-secure / Non-encrypted (WS/HTTP)</summary>
  <ul>
    <li>Security-related configuration(s) not required</li>
    <li>Download <a href="superconductor/redis/docker-compose-prod_ws.yml">docker-compose-prod_ws.yml</a> file <i>(and optionally <a href="superconductor/redis/docker-compose-prod_ws.yml">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details>

----

#### Run SuperConductor

<details><summary>WS/HTTP</summary>
    <blockquote>

###### [relay security keys](superconductor/redis/src/main/resources/application-prod_ws.properties?plain=1#L4-L5) required

</blockquote>
    <blockquote>
        <details><summary>redis</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/redis/docker-compose-prod_ws.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/redis/docker-compose-prod_ws.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/redis/docker-compose-prod_ws.yml up -d && dcls | grep 'superconductor-app-redis' | awk '{print $1}' | xargs docker logs -f
</blockquote>
        </details>
        <details><summary>sqlite</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/sqlite/docker-compose-prod_ws.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/sqlite/docker-compose-prod_ws.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/sqlite/docker-compose-prod_ws.yml up -d && dcls | grep 'superconductor-app-sqlite' | awk '{print $1}' | xargs docker logs -f

</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/mysql/docker-compose-prod_ws.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/mysql/docker-compose-prod_ws.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/mysql/docker-compose-prod_ws.yml up -d && dcls | grep 'superconductor-app-mysql' | awk '{print $1}' | xargs docker logs -f

</blockquote>
        </details>
    </blockquote>
</details>

<details><summary>WSS/HTTPS</summary>
    <blockquote>

###### [relay security keys](superconductor/redis/src/main/resources/application-prod_ws.properties?plain=1#L4-L5) required & [superconductor/docker-compose-prod_wss.yml](superconductor/docker-compose-prod_wss.yml?plain=1#L10,L32,L36-L37) parameters as applicable
</blockquote>
    <blockquote>
        <details><summary>redis</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/redis/docker-compose-prod_wss.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/redis/docker-compose-prod_wss.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/redis/docker-compose-prod_wss.yml up -d && dcls | grep 'superconductor-app-redis' | awk '{print $1}' | xargs docker logs -f
</blockquote>
        </details>
        <details><summary>sqlite</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/sqlite/docker-compose-prod_wss.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/sqlite/docker-compose-prod_wss.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/sqlite/docker-compose-prod_wss.yml up -d && dcls | grep 'superconductor-app-sqlite' | awk '{print $1}' | xargs docker logs -f

</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

###### run without logging:

    $ docker compose -f superconductor/mysql/docker-compose-prod_wss.yml up 

###### run with container logging displayed to console:

    $ docker compose -f superconductor/mysql/docker-compose-prod_wss.yml up --abort-on-container-failure --attach-dependencies

###### run with docker logging displayed to console:

    $ docker compose -f superconductor/mysql/docker-compose-prod_wss.yml up -d && dcls | grep 'superconductor-app-mysql' | awk '{print $1}' | xargs docker logs -f

</blockquote>
        </details>
    </blockquote>
</details>

----

##### Stop SuperConductor

<details><summary>WS/HTTP</summary>
    <blockquote>
        <details><summary>redis</summary>
            <blockquote>

    $ docker compose -f superconductor/redis/docker-compose-prod_ws.yml stop 
</blockquote>
        </details>
        <details><summary>sqlite</summary>
            <blockquote>

    $ docker compose -f superconductor/sqlite/docker-compose-prod_ws.yml stop 
</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

    $ docker compose -f superconductor/mysql/docker-compose-prod_ws.yml stop 
</blockquote>
        </details>
    </blockquote>
</details>

<details><summary>WSS/HTTPS</summary>
    <blockquote>
        <details><summary>redis</summary>
            <blockquote>

    $ docker compose -f superconductor/redis/docker-compose-prod_wss.yml stop 
</blockquote>
        </details>
        <details><summary>sqlite</summary>
            <blockquote>

    $ docker compose -f superconductor/sqlite/docker-compose-prod_wss.yml stop 
</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

    $ docker compose -f superconductor/mysql/docker-compose-prod_wss.yml stop 
</blockquote>
        </details>
    </blockquote>
</details>

----  

##### Remove SuperConductor docker containers

<details><summary>WS/HTTP</summary>
    <blockquote>
        <details><summary>redis</summary>
            <blockquote>

    $ docker compose -f superconductor/redis/docker-compose-prod_ws.yml down --remove-orphans
</blockquote>
        </details>
        <details><summary>sqlite</summary>
            <blockquote>

    $ docker compose -f superconductor/sqlite/docker-compose-prod_ws.yml down --remove-orphans

</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

    $ docker compose -f superconductor/mysql/docker-compose-prod_ws.yml down --remove-orphans

</blockquote>
        </details>
    </blockquote>
</details>

<details><summary>WSS/HTTPS</summary>
    <blockquote>
        <details><summary>redis</summary>
            <blockquote>

    $ docker compose -f superconductor/redis/docker-compose-prod_wss.yml down --remove-orphans
</blockquote>
        </details>
        <details><summary>sqlite</summary>
            <blockquote>

    $ docker compose -f superconductor/sqlite/docker-compose-prod_wss.yml down --remove-orphans

</blockquote>
        </details>
        <details><summary>mysql</summary>
            <blockquote>

    $ docker compose -f superconductor/mysql/docker-compose-prod_wss.yml down --remove-orphans

</blockquote>
        </details>
    </blockquote>
</details>  

<hr style="border:2px solid grey">

#### [Development Mode Instructions](DEVELOPMENT.md)
