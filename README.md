```java
███████╗██╗   ██╗██████╗ ███████╗██████╗  ██████╗ ██████╗ ███╗   ██╗██████╗ ██╗   ██╗ ██████╗████████╗ ██████╗ ██████╗
██╔════╝██║   ██║██╔══██╗██╔════╝██╔══██╗██╔════╝██╔═══██╗████╗  ██║██╔══██╗██║   ██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗
███████╗██║   ██║██████╔╝█████╗  ██████╔╝██║     ██║   ██║██╔██╗ ██║██║  ██║██║   ██║██║        ██║   ██║   ██║██████╔╝
╚════██║██║   ██║██╔═══╝ ██╔══╝  ██╔══██╗██║     ██║   ██║██║╚██╗██║██║  ██║██║   ██║██║        ██║   ██║   ██║██╔══██╗
███████║╚██████╔╝██║     ███████╗██║  ██║╚██████╗╚██████╔╝██║ ╚████║██████╔╝╚██████╔╝╚██████╗   ██║   ╚██████╔╝██║  ██║
╚══════╝ ╚═════╝ ╚═╝     ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚═════╝  ╚═════╝  ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
```
# Java Nostr-Relay Framework & Web Application
  

<details>
  <summary>Supported Nips</summary>
  <ul>
    <li> [NIP-01](https://nostr-nips.com/nip-01) (Basic protocol & Standard Tags) </li>
    <li> [NIP-02](https://nostr-nips.com/nip-02) (Contact List and Petnames) </li>
    <li> [NIP-03](https://nostr-nips.com/nip-03) (OpenTimestamps Attestations for Events) </li>
    <li> [NIP-04](https://nostr-nips.com/nip-04) (Encrypted Direct Message) </li>
    <li> [NIP-05](https://nostr-nips.com/nip-05) (DNS-based internet identifiers) </li>
    <li> [NIP-06](https://nostr-nips.com/nip-06) (Basic key derivation from mnemonic seed phrase) </li>
    <li> [NIP-07](https://nostr-nips.com/nip-07) (window.nostr capability for web browsers) </li>
    <li> [NIP-08](https://nostr-nips.com/nip-08) (Handling Mentions) </li>
    <li> [NIP-10](https://nostr-nips.com/nip-10) (Marked "e" tags) </li>
    <li> [NIP-11](https://nostr-nips.com/nip-11) (Relay Information Document) </li>
    <li> [NIP-12](https://nostr-nips.com/nip-12) (Generic Tag Queries) </li>
    <li> [NIP-14](https://nostr-nips.com/nip-14) (Subject tag in Text events) </li>
    <li> [NIP-16](https://nostr-nips.com/nip-16) (Event treatment) </li>
    <li> [NIP-18](https://nostr-nips.com/nip-18) (Reposts) </li>
    <li> [NIP-19](https://nostr-nips.com/nip-19) (Bech-32 encoded entities) </li>
    <li> [NIP-20](https://nostr-nips.com/nip-20) (Command Results) </li>
    <li> [NIP-21](https://nostr-nips.com/nip-21) (URI scheme) </li>
    <li> [NIP-22](https://nostr-nips.com/nip-22) (Event "created_at" limits) </li>
    <li> [NIP-25](https://nostr-nips.com/nip-25) (Reactions) </li>
    <li> [NIP-27](https://nostr-nips.com/nip-27) (Text Note References) </li>
    <li> [NIP-28](https://nostr-nips.com/nip-28) (Public Chat) </li>
    <li> [NIP-30](https://nostr-nips.com/nip-30) (Custom Emoji) </li>
    <li> [NIP-31](https://nostr-nips.com/nip-31) (Unknown event kinds) </li>
    <li> [NIP-33](https://nostr-nips.com/nip-33) (Parameterized Replaceable Events) </li>
    <li> [NIP-36](https://nostr-nips.com/nip-36) (Sensitive Content / Content Warning) </li>
    <li> [NIP-38](https://nostr-nips.com/nip-38) (User Statuses) </li>
    <li> [NIP-39](https://nostr-nips.com/nip-39) (External Identities in Profiles) </li>
    <li> [NIP-42](https://nostr-nips.com/nip-42) (Authentication of clients to relays) </li>
    <li> [NIP-46](https://nostr-nips.com/nip-46) (Nostr Connect) </li>
    <li> [NIP-48](https://nostr-nips.com/nip-48) (Proxy Tags) </li>
    <li> [NIP-52](https://nostr-nips.com/nip-52) (Calendar Events) </li>
    <li> [NIP-53](https://nostr-nips.com/nip-53) (Live Activities) </li>
    <li> [NIP-56](https://nostr-nips.com/nip-56) (Reporting) </li>
    <li> [NIP-57](https://nostr-nips.com/nip-57) (Lightning Zaps) </li>
    <li> [NIP-58](https://nostr-nips.com/nip-58) (Badges) </li>
    <li> [NIP-89](https://nostr-nips.com/nip-89) (Recommended Application Handlers) </li>
    <li> [NIP-99](https://nostr-nips.com/nip-99) (Classified Listings) </li>
    <li> used by [Barchetta](https://github.com/avlo/barchetta) Smart-Contract Negotiation Protocol (in progress) atop [Bitcoin](https://en.wikipedia.org/wiki/Bitcoin) [ </li>Lightning-Network](https://en.wikipedia.org/wiki/Lightning_Network) [RGB](https://rgb.tech/)
  </ul>
</details>  

----
### Normal/Production Mode (for most users) Instructions:
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

#### Download Superconductor Docker Image from [hub.docker](https://hub.docker.com/repository/docker/avlo/superconductor-app/tags)
    $ docker pull avlo/superconductor:1.10.1

----

#### Configure SuperConductor security level, 3 options:

<details>
  <summary>Highest | SSL Certificate (WSS/HTTPS)</summary>
  <ul>
    <li><a href="https://www.websitebuilderexpert.com/building-websites/how-to-get-an-ssl-certificate/">Obtain</a> an SSL certificate</li>
    <li><a href="https://www.baeldung.com/java-import-cer-certificate-into-keystore">Install</a> the certificate</li>
    <li>Download <a href="src/main/resources/application-prod_wss.properties.properties">application-prod_wss.properties</a> file & configure <a href="src/main/resources/application-prod_wss.properties.properties?plain=1#L6,8,L11-L15"> SSL settings</a></li>
    <li>Download <a href="docker-compose-prod_wss.yml">docker-compose-prod_wss.yml</a> file <i>(and optionally <a href="docker-compose-prod_wss.yml?plain=1#L10,32,L36-L37">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details>

<details>
  <summary>Medium | Self-Signed Certificate (WSS/HTTPS)</summary>
  <ul>
    <li><a href="https://www.baeldung.com/openssl-self-signed-cert">Create </a>a Self-Signed Certificate</li>
	<li><a href="https://www.baeldung.com/java-import-cer-certificate-into-keystore">Install</a> the certificate</li>
	<li>Download <a href="src/main/resources/application-prod_wss.properties.properties">application-prod_wss.properties</a> file & configure <a href="src/main/resources/application-prod_wss.properties.properties?plain=1#L6,8,L11-L15"> SSL settings</a></li>
    <li>Download <a href="docker-compose-prod_wss.yml">docker-compose-prod_wss.yml</a> file <i>(and optionally <a href="docker-compose-prod_wss.yml?plain=1#L10,32,L36-L37">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details> 

<details>
  <summary>Lowest | Non-secure / Non-encrypted (WS/HTTP)</summary>
  <ul>
    <li>Security-related configuration(s) not required</li>
    <li>Download <a href="docker-compose-prod_ws.yml">docker-compose-prod_ws.yml</a> file <i>(and optionally <a href="docker-compose-prod_ws.yml?plain=1#L10,32,L36-L37">edit relevant parameters</a> as applicable)</i></li>
  </ul>
</details>

----

#### Run SuperConductor

<details>
  <summary>WSS/HTTPS</summary>  

run without logging:

    docker compose -f docker-compose-prod_wss.yml up 

run with container logging displayed to console:  

    docker compose -f docker-compose-prod_wss.yml up --abort-on-container-failure --attach-dependencies

run with docker logging displayed to console:  

    docker compose -f docker-compose-prod_wss.yml up -d && dcls | grep 'superconductor-app' | awk '{print $1}' | xargs docker logs -f
</details> 

<details>
  <summary>WS/HTTP</summary>  

run without logging:

    docker compose -f docker-compose-prod_ws.yml up 

run with container logging displayed to console:

    docker compose -f docker-compose-prod_ws.yml up --abort-on-container-failure --attach-dependencies

run with docker logging displayed to console:

    docker compose -f docker-compose-prod_ws.yml up -d && dcls | grep 'superconductor-app' | awk '{print $1}' | xargs docker logs -f
</details> 

----

##### Stop SuperConductor

<details>
  <summary>WSS/HTTPS</summary>

    docker compose -f docker-compose-prod_wss.yml stop superconductor superconductor-db
</details> 

<details>
  <summary>WS/HTTP</summary>  

    docker compose -f docker-compose-prod_ws.yml stop superconductor superconductor-db
</details>

----  

##### Remove SuperConductor docker containers

<details>
  <summary>WSS/HTTPS</summary>

    docker compose -f docker-compose-prod_wss.yml down --remove-orphans
</details> 

<details>
  <summary>WS/HTTP</summary>  

    docker compose -f docker-compose-prod_ws.yml down --remove-orphans
</details>

<hr style="border:2px solid grey">

#### [Development Mode Instructions](DEVELOPMENT.md)
