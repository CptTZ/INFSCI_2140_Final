# INFSCI 2140 final project - Yelp

{lez36,mel165,yiz141}#pitt.edu

## Build & Start

### Dependency

- OpenJDK 1.8, and have `java`, `javac` added to your `PATH` variable.
- Maven manages the rest of the dependencies

### How to

- Build

Skip the unit test to speed up. Command as follows:

```shell
./mvnw clean package -DskipTests=true
```

If you want to run tests, be sure to put `pgh_review.csv` in appropriate position.

- Start

```shell
java -jar target/finalprj-0.0.1.jar
```

Then goto any web browser, view <http://127.0.0.1:10088>.
