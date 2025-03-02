.DEFAULT_GOAL := build-run

setup:
	chmod +x gradlew
	make -C setup

check-java-deps:
	chmod +x gradlew
	make -C app check-java-deps:
app:
	chmod +x gradlew
	make -C app
clean:
	chmod +x gradlew
	make -C app clean
build:
	chmod +x gradlew
	make -C app build
install:
	chmod +x gradlew
	make -C app install
dev:
	chmod +x gradlew
	make -C app dev
lint:
	chmod +x gradlew
	make -C app lint
test:
	chmod +x gradlew
	make -C app test
report:
	chmod +x gradlew
	make -C app report
build-run:
	chmod +x gradlew
	make -C app build-run

.PHONY: build
