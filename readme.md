#### Envs:
PROD, UAT, LOCAL-PROD, DEV, DEV_NATIVE, DEV_TEST

#### DEV Env vars:
APP__ELASTIC_FETCHER_TASK_ENABLED=true;APP__ELASTIC_HOST=localhost;APP__ELASTIC_PORT=9200;APP__ELASTIC_SCHEME=http;LOGBACK_LOGSTASH_ENABLED=true;LOGBACK_LOGSTASH_FULL_HOST=localhost:5044;SERVER_ENV=DEV;SERVER_PORT=3100;SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:3110/app_db;SPRING_FLYWAY_URL=jdbc:postgresql://localhost:3110/app_db

#### DEV TEST Env vars:
APP__ELASTIC_FETCHER_TASK_ENABLED=false;APP__ELASTIC_HOST=localhost;APP__ELASTIC_PORT=4024;APP__ELASTIC_SCHEME=http;LOGBACK_LOGSTASH_ENABLED=false;LOGBACK_LOGSTASH_FULL_HOST=localhost:5044;SERVER_ENV=DEV_TEST;SERVER_PORT=3104;SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:3114/app_db;SPRING_FLYWAY_URL=jdbc:postgresql://localhost:3114/app_db

#### Create native runnable application - NO test run

```shell
./mvnw clean
    native:compile
    -Pnative
    -DskipTests
    -Dspring.profiles.active=native
    -Dserver.port=4005
    -Dserver.env=DEV_NATIVE
    -Dslogback.logstash.enabled=true
    -Dlogback.logstash.full_host=localhost:5044 
```

#### Start native runnable application on windows

```shell
./target/elastic_fetcher_api.exe 
    --spring.profiles.active=native
    --server.port=4005
    --server.env=DEV_NATIVE
    --native.reflection-configuration-generator.enabled=false
    --logback.logstash.enabled=true
    --logback.logstash.full_host=localhost:5044
    --native.reflection-configuration-generator.enabled=false
```
