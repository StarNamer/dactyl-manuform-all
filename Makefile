# GNU makefile. This compiles to Java at need and combines bundled
# YAML configuration files into demonstration models of the DMOTE.
# https://www.gnu.org/software/make/manual/make.html

.PHONY: default visualization orthographic flat threaded threaded-visualization solid all test clean

OBJECTS = $(shell find src/)

things/left-hand-case.scad things/right-hand-case.scad: target/dmote.jar
	lein run

doc/options.md: target/dmote.jar
	lein run --describe-parameters > doc/options.md

target/dmote.jar: $(OBJECTS)
	lein uberjar

test:
	lein test

%.stl: %.scad
	openscad -o $@ $^

# “all” will overwrite its own outputs.
# Intended for code sanity checking before pushing a commit.
all: things/left-hand-case.stl things/right-hand-case.stl doc/options.md

clean:
	-rm things/*.scad
	-rm things/*.stl
	lein clean
