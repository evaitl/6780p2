IMGS:= retr_term.png
%.png: %.dia
	dia -e $@ $<

.PHONY: all clean

all: $(IMGS)


# We don't clean images so they are available on github
# for the README.md
clean:
