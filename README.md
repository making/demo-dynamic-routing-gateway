```
curl http://localhost:9999/get
```


```
# update src/main/resources/routes.yml

curl -u admin:admin -XPOST localhost:9999/routes -d "$(cat src/main/resources/routes.yml)"
```