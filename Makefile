things_dir = things/
things = dactyl-bottom-left dactyl-bottom-right dactyl-top-left dactyl-top-right
scads = $(addprefix $(things_dir), $(addsuffix .scad, $(things)))
stls = $(addprefix $(things_dir), $(addsuffix .stl, $(things)))

all: $(stls)

$(scads): project.clj src/
	lein run

%.stl: %.scad
	openscad $< -o $@