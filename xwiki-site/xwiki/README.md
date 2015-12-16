# docker-xwiki

A basic working image for xwiki

# Usage

## Create persistence

Persistent volumes are already defined by the image, so there is no
need to explicitly define volumes using `-v`.

```shell
docker create \
  --name 'xwiki-persistence' \
  --entrypoint /bin/true \
  hellyna/xwiki:7.1
```

## Run!

Run with the above persistent container.

```shell
docker run -it --rm \
  --name 'xwiki' \
  --volumes-from 'xwiki-persistence' \
  -p 8080:8080 \
  hellyna/xwiki:7.1
```

Try to point your browser to 'http://localhost:8080/' if you are running
a local instance of docker!
