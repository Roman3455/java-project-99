.DEFAULT_GOAL := build-run

setup:
	./gradlew wrapper --gradle-version 8.3
	./gradlew build

reload-classes:
	./gradlew -t classes

check-java-deps:
	./gradlew dependencyUpdates -Drevision=release

app:
	./gradlew bootRun --args='--spring.profiles.active=dev'

dev: app

clean:
	./gradlew clean

build:
	./gradlew build

install:
	./gradlew installDist

lint:
	./gradlew checkstyleMain

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

build-run: clean build install lint test report

.PHONY: build
