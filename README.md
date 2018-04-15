## This application is simple spring-boot template
Contains:
* [Markov text generator](https://github.com/interair/SpringBootTemplate/blob/35624ac1de2767690672da783f9ed3f46060fd83/src/main/java/io/github/pronto/markov/service/MarkovGenerator.java#L24) :)
* Spring boot 
* [Rest API with undertow](https://github.com/interair/SpringBootTemplate/tree/master/src/main/java/io/github/pronto/markov/web/rest)
* [Spring data (jpa)](https://github.com/interair/SpringBootTemplate/tree/master/src/main/java/io/github/pronto/markov/repository)
* [jwt](https://github.com/interair/SpringBootTemplate/tree/master/src/main/java/io/github/pronto/markov/security/jwt)
* [Basic metrics](https://github.com/interair/SpringBootTemplate/blob/35624ac1de2767690672da783f9ed3f46060fd83/src/main/java/io/github/pronto/markov/config/MetricsConfiguration.java#L28)
* Swagger (available at http://localhost:8080/swagger-ui.html)
* Index.html (empty)
* Logging 
* [Liquibase](https://github.com/interair/SpringBootTemplate/blob/master/src/main/resources/config/liquibase/changelog/00000000000000_initial_schema.yml)
* Optional: integration with graphite
* Lombok
* [Test template](https://github.com/interair/SpringBootTemplate/blob/443ad5ad64ae6fea3221d069b40d167bce2fdc7c/src/test/java/io/github/pronto/markov/web/rest/ResultResourceIntTest.java#L47)

## Building for production

    ./gradlew -Pprod clean bootRepackage

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

To launch your application's tests, run:

    ./gradlew test

