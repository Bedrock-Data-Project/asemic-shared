## Generating asemic client

- put contents of `http://localhost:8083/v3/api-docs` (asemic backend) to asemic-api.json
- maven clean/compile

## Build native cli executable
```shell
cd cli
mvn -Pnative -DskipTests package
```
Requires that graalvm is installed (JAVA_HOME needs to be set also) and all the native build tools set
On mac, jenv can be used to set jdk
See https://www.infoq.com/articles/java-native-cli-graalvm-picocli/
