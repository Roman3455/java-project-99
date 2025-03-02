.DEFAULT_GOAL := build-run

setup:
	chmod +x gradlew
	npm install
	./gradlew wrapper --gradle-version 8.7
	./gradlew build

check-java-deps:
	./gradlew dependencyUpdates -Drevision=release

app:
	chmod +x gradlew
	./gradlew bootRun

dev: app

clean:
	chmod +x gradlew
	./gradlew clean

build:
	chmod +x gradlew
	./gradlew build

install:
	chmod +x gradlew
	./gradlew installDist

lint:
	chmod +x gradlew
	./gradlew checkstyleMain

test:
	chmod +x gradlew
	./gradlew test

report:
	chmod +x gradlew
	./gradlew jacocoTestReport

build-run: clean setup build install lint test report

.PHONY: build
