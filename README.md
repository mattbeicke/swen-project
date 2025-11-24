# U-Fund: Goated (Homeless Shelter)

An online U-Fund system built with Java **21** + Spring, with Angular for frontend.
  
## Team

- Anthony Ficalora
- Matthew Beicke
- Ricardo Lopez
- Zachary Coy

## Prerequisites

- Java 21 (Make sure to have correct JAVA_HOME setup in your environment)
- Maven
- Angular 20


## How to run it

1. Clone the repository and go to the root directory.
2. Execute `mvn compile exec:java`
3. Open in your browser `http://localhost:8080/`
4. The server should be open. Many functions demand an API key which can be generated in the /accounts endpoint.

## Known bugs and disclaimers
We are not aware of any outstanding bugs.

## How to test it

The Maven build script provides hooks for run unit tests and generate code coverage
reports in HTML.

To run tests on all tiers together do this:

1. Execute `mvn clean test jacoco:report`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/index.html`

To run tests on a single tier do this:

1. Execute `mvn clean test-compile surefire:test@tier jacoco:report@tier` where `tier` is one of `controller`, `model`, `persistence`
2. Open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/{controller, model, persistence}/index.html`

To run tests on all the tiers in isolation do this:

1. Execute `mvn exec:exec@tests-and-coverage`
2. To view the Controller tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
3. To view the Model tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`
4. To view the Persistence tier tests open in your browser the file at `PROJECT_API_HOME/target/site/jacoco/model/index.html`

*(Consider using `mvn clean verify` to attest you have reached the target threshold for coverage)
  
  
## How to generate the Design documentation PDF

1. Access the `PROJECT_DOCS_HOME/` directory
2. Execute `mvn exec:exec@docs`
3. The generated PDF will be in `PROJECT_DOCS_HOME/` directory


## How to setup/run/test program 
1. First obtain the Acceptance Test plan
2. Follow the instructions above (in 'How to run it') to start the program.

## License

MIT License

See LICENSE for details.
