.DEFAULT_GOAL := build-run

setup:
	npm install
	./gradlew wrapper --gradle-version 8.7

check-java-deps:
	./gradlew dependencyUpdates -Drevision=release

app:
	./gradlew bootRun

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
