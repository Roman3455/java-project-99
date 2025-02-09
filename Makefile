.DEFAULT_GOAL := build-run

setup:
	make -C app setup
reload-classes:
	make -C app reload-classes:
check-java-deps:
	make -C app check-java-deps:
app:
	make -C app
clean:
	make -C app clean
build:
	make -C app build
install:
	make -C app install
dev:
	make -C app dev
lint:
	make -C app lint
test:
	make -C app test
report:
	make -C app report
build-run:
	make -C app build-run

.PHONY: build