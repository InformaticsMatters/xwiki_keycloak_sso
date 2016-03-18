## Introduction
This sets up a simple Dockerised test environment for using the Keycloak SSO solution with XWiki.
All the docker related action is in the xwiki-site directory.

See the [Git Hub project](https://github.com/tdudgeon/xwiki_authenticator_keycloak) for details of the authenticator.

IMPORTANT: this setup assumes that the docker host ip address is 192.168.59.103.
If you have a different ip then you will need to make changes to (at least) the keycloak.json files and the Valid Redirect 
URIs for the sampleapp and xwiki clients in the squonk realm of keycloak. In a production setup permanent DNS names should
be in use and avoid this problem.

Versions:
Keycloak 1.9.1.Final
XWiki 7.4.2

## Building
### 1 Setup
First you MUST set an environment variable that points to the keycloak server (this will depend on the ip address of the docker host).

export KEYCLOAK_SERVER_URL="http://192.168.59.103:8080"

Replace localhost with the ip or hostname of your docker host if its not 192.168.59.103
Also, if its not 192.168.59.103 then you will also need to change the realm configuration that's imported in step 3

### 2 Build
```sh
docker-compose build
```

### 3 Import realm configuration into Keycloak 
docker-compose up -d postgres

If your docker host is not 192.168.59.103 (and it probably not) then you need to edit the squonk-realm.json file that will be imported.
You can do this in one command like this:

``` sh
sed 's/__server_name__/1.2.3.4/g' squonk-realm.json > yyy.json
```

(replacing 1.2.3.4 with the name/ip of your docker host).
NOTE: do NOT use localhost (it means different things inside and outside the container). If docker host is running on
localhost the specify the Docker gateway address.

Import the realm definition with this:

```sh
docker run -it --link xwikisite_postgres_1:postgres -e POSTGRES_DATABASE=keycloak -e POSTGRES_USER=keycloak -e POSTGRES_PASSWORD=keycloak --rm -v $PWD:/tmp/json jboss/keycloak-postgres:1.9.1.Final -b 0.0.0.0 -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=/tmp/json/squonk-realm.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING
```

(replace squonk-realm.json with yyy.json if you performed the edit at the start of this section)

Then Ctrl-C to terminate once started.



### 4 Fire up all containers
Now start the whole stack:
```sh  
docker-compose up -d --no-recreate
```

Access XWiki at:     http://192.168.59.103/
Access Keycloak at:  http://192.168.59.103:8080/
(change ip address as needed).

When you first access XWiki you will be taken through the installation process.

Check the Keycloak authentication is working corectly by trying to log in. You should see the Keycloak login screen and 
can log in user user1/user1.

After completing this you should tighten down security as you see fit.

## Other info

If needed export the realm definition from keycloak use this:

```sh
docker run -it --link xwikisite_postgres_1:postgres -e POSTGRES_DATABASE=keycloak -e POSTGRES_USER=keycloak -e POSTGRES_PASSWORD=keycloak --rm -v $PWD:/tmp/json jboss/keycloak-postgres /opt/jboss/keycloak/bin/standalone.sh -b 0.0.0.0 -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=/tmp/json/squonk-realm.json -Dkeycloak.migration.realmName=squonk
```
