## This application is simple template
Contains:
* markov text generator :)
* spring boot with undertow
* spring data (jpa)
* jwt
* basic metrics
* swagger (available at http://localhost:8080/swagger-ui.html)
* index.html (empty)
* logging 
* liquibase
* optional: integration with graphite
* lombok

## Building for production

    ./gradlew -Pprod clean bootRepackage

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

To launch your application's tests, run:

    ./gradlew test

