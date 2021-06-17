# Pay My Buddy
Openclassrooms project number 6

<!-- ABOUT THE PROJECT -->
## About The Project

Web application prototype for the startup Pay My Buddy. This is a Spring Boot application for project number 6 of [Openclassrooms](https://openclassrooms.com/) java back-end formation.

Project goals:
* Create database tables.
* Provide UML class diagram (see 2 images below)
* Provide [SQL scripts to create tables](https://github.com/jerome13250/paymybuddy/tree/master/resources), please note that a specific test db exists for integration tests.
* Create a java web app Java with repository pattern.

![UML-class-diagram](https://github.com/jerome13250/paymybuddy/blob/master/images/PayMyBuddy_ClassDiagram.png)

![database](https://github.com/jerome13250/paymybuddy/blob/master/images/PayMyBuddy_diagram.png)


### Built With

* [Java 11](https://adoptopenjdk.net/)

<!-- GETTING STARTED -->
## Getting Started

This is how to set up the project locally.
To get a local copy up and running follow these simple example steps:

### Prerequisites

Check that you have : 
* Java 11 installed
  ```sh
  java -version
  ```

### Installation

1. Choose a directory
   ```sh
   cd /path/to/directory/project
   ```
2. Clone the repo
   ```sh
   git clone https://github.com/jerome13250/paymybuddy.git
   ```
3. Select the paymybuddy directory
   ```sh
   cd paymybuddy
   ```
4. Package the application (fat jar file) using [maven wrapper](https://github.com/takari/maven-wrapper) provided in the folder, it downloads automatically the correct Maven version if it's not found.
   ```sh
   mvnw package
   ```
5. Execute the jar file
   ```JS
   java -jar ./target/paymybuddy-0.0.1-SNAPSHOT.jar
   ```
6. To access the application, open your browser, go to [http://localhost:8080](http://localhost:8080)

![homepage](https://github.com/jerome13250/paymybuddy/blob/master/images/PayMyBuddy_homepage.png)