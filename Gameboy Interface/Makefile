## ======================================================================
## partial Makefile provided to students
##

GTK_INCLUDE := `pkg-config --cflags gtk+-3.0`
GTK_LIBS := `pkg-config --libs gtk+-3.0`

.PHONY: clean new style feedback submit1 submit2 submit

CFLAGS += -std=c11 -Wall -pedantic -g -D_DEFAULT_SOURCE
CFLAGS += $(GTK_INCLUDE)
LDFLAGS += -L.
LDLIBS += $(GTK_LIBS)


CPPFLAGS += -DBLARGG

#CFLAGS += -fsanitize=address
#LDFLAGS += -fsanitize=address

# a bit more checks if you'd like to (uncomment
# CFLAGS += -Wextra -Wfloat-equal -Wshadow                         \
# -Wpointer-arith -Wbad-function-cast -Wcast-align -Wwrite-strings \
# -Wconversion -Wunreachable-code

# uncomment if you want to add DEBUG flag
# CPPFLAGS += -DDEBUG

# ----------------------------------------------------------------------
# feel free to update/modifiy this part as you wish

# all those libs are required on Debian, feel free to adapt it to your box
LDLIBS += -lcheck -lm -lrt -pthread -lsubunit

LDFLAGS += -L.
LDLIBS += -lcs212gbfinalext

export LD_LIBRARY_PATH=.

check:: all
	@if ls tests/*.*.sh 1> /dev/null 2>&1; then \
	  for file in tests/*.*.sh; do [ -x $$file ] || echo "Launching $$file"; ./$$file || exit 1; done; \
	fi

all: alu.o bit.o bus.o memory.o component.o gameboy.o cpu.o cpu-registers.o cpu-storage.o cpu-alu.o opcode.o bootrom.o cartridge.o timer.o bit_vector.o image.o error.o util.o test-cpu-week08.o test-cpu-week08 test-cpu-week09.o test-cpu-week09 test-gameboy.o test-gameboy gbsimulator.o gbsimulator

#== MAIN FILES =====

alu.o: alu.c bit.h alu.h error.h

bit.o: bit.c bit.h

bus.o: bus.c bus.h memory.h component.h error.h bit.h

memory.o: memory.c memory.h error.h

component.o: component.c component.h memory.h error.h

gameboy.o: gameboy.c gameboy.h bus.h memory.h component.h timer.h bit.h cpu.h alu.h cartridge.h error.h bootrom.h lcdc.h joypad.h

cpu.o: cpu.c error.h opcode.h bit.h cpu.h alu.h bus.h memory.h component.h cpu-alu.h cpu-registers.h cpu-storage.h util.h

cpu-registers.o: cpu-registers.c cpu-registers.h cpu.h alu.h bit.h bus.h memory.h component.h error.h

cpu-storage.o: cpu-storage.c error.h cpu-storage.h memory.h opcode.h bit.h cpu.h alu.h bus.h component.h cpu-registers.h gameboy.h util.h

cpu-alu.o: cpu-alu.c error.h bit.h alu.h cpu-alu.h opcode.h cpu.h bus.h memory.h component.h cpu-storage.h cpu-registers.h

opcode.o: opcode.c opcode.h bit.h

bootrom.o: bootrom.c bootrom.h bus.h memory.h component.h gameboy.h cpu.h alu.h bit.h error.h

cartridge.o: cartridge.c cartridge.h

timer.o: timer.c timer.h error.h

bit_vector.o: bit_vector.c bit_vector.h bit.h image.h error.h

image.o: image.c error.h image.h bit_vector.h bit.h

image: image.o error.o bit_vector.o bit.o




#===================

#== OTHERS =========

error.o: error.c

util.o: util.c

#libsid_demo.o: libsid_demo.c sidlib.h

unit-test-alu.o: unit-test-alu.c tests.h error.h alu.h bit.h
unit-test-alu: unit-test-alu.o error.o alu.o bit.o

unit-test-bit.o: unit-test-bit.c tests.h error.h bit.h
unit-test-bit: unit-test-bit.o error.o bit.o

unit-test-memory.o: unit-test-memory.c tests.h error.h bus.h memory.h component.h
unit-test-memory: unit-test-memory.o error.o memory.o component.o

unit-test-component.o: unit-test-component.c tests.h error.h bus.h memory.h component.h
unit-test-component: unit-test-component.o error.o component.o memory.o

unit-test-bus.o: unit-test-bus.c tests.h error.h bus.h memory.h component.h util.h bit.h
unit-test-bus: unit-test-bus.o error.o bus.o component.o util.o bit.o memory.o error.o

unit-test-cpu.o: unit-test-cpu.c tests.h error.h alu.h bit.h opcode.h util.h cpu.h bus.h memory.h component.h cpu-registers.h cpu-storage.h cpu-alu.h bit_vector.h
unit-test-cpu: unit-test-cpu.o error.o alu.o bit.o opcode.o cpu.o bus.o memory.o component.o cpu-registers.o cpu-storage.o cpu-alu.o bit_vector.o

test-cpu-week08.o: test-cpu-week08.c opcode.h bit.h cpu.h alu.h bus.h memory.h component.h cpu-storage.h cpu-registers.h cpu-alu.h util.h error.h bit_vector.h
test-cpu-week08: test-cpu-week08.o opcode.o bit.o cpu.o alu.o bus.o memory.o component.o cpu-storage.o cpu-registers.o cpu-alu.o util.o error.o bit_vector.o

test-cpu-week09.o: test-cpu-week09.c opcode.h bit.h cpu.h alu.h bus.h memory.h component.h cpu-storage.h cpu-registers.h cpu-alu.h util.h error.h bit_vector.h
test-cpu-week09: test-cpu-week09.o opcode.o bit.o cpu.o alu.o bus.o memory.o component.o cpu-storage.o cpu-registers.o cpu-alu.o util.o error.o bit_vector.o

unit-test-cpu-dispatch-week08.o: unit-test-cpu-dispatch-week08.c tests.h error.h alu.h opcode.h bit.h gameboy.h util.h unit-test-cpu-dispatch.h component.h bus.h cpu-registers.h cpu-alu.h cpu-storage.h memory.h error.h bootrom.h timer.h cartridge.h lcdc.h joypad.h bit_vector.h
unit-test-cpu-dispatch-week08: unit-test-cpu-dispatch-week08.o error.o alu.o opcode.o bit.o component.o bus.o cpu-registers.o cpu-alu.o cpu-storage.o memory.o gameboy.o util.o error.o bootrom.o timer.o cartridge.o bit_vector.o

unit-test-cpu-dispatch-week09.o: unit-test-cpu-dispatch-week09.c tests.h error.h alu.h opcode.h bit.h gameboy.h util.h unit-test-cpu-dispatch.h component.h bus.h cpu-registers.h cpu-alu.h cpu-storage.h memory.h error.h bootrom.h timer.h cartridge.h lcdc.h joypad.h bit_vector.h
unit-test-cpu-dispatch-week09: unit-test-cpu-dispatch-week09.o error.o alu.o opcode.o bit.o component.o bus.o cpu-registers.o cpu-alu.o cpu-storage.o memory.o gameboy.o util.o error.o bootrom.o timer.o cartridge.o bit_vector.o

unit-test-cartridge.o: unit-test-cartridge.c tests.h error.h cartridge.h component.h memory.h bus.h cpu.h alu.h bit.h cpu-alu.h cpu-storage.h cpu-registers.h opcode.h bit_vector.h
unit-test-cartridge: unit-test-cartridge.o error.o cartridge.o component.o memory.o bus.o cpu.o alu.o bit.o cpu-alu.o cpu-storage.o cpu-registers.o opcode.o bit_vector.o

unit-test-timer.o: unit-test-timer.c util.h tests.h error.h timer.h component.h memory.h bit.h cpu.h alu.h bus.h cpu-alu.h cpu-storage.h cpu-registers.h opcode.h bit_vector.h
unit-test-timer: unit-test-timer.o util.o error.o timer.o component.o memory.o bit.o cpu.o alu.o bus.o cpu-alu.o cpu-storage.o cpu-registers.o opcode.o bit_vector.o

test-gameboy.o: test-gameboy.c gameboy.h bus.h memory.h component.h timer.h bit.h cpu.h alu.h cartridge.h util.h error.h cpu-registers.h cpu-alu.h cpu-storage.h opcode.h bootrom.h bit_vector.h
test-gameboy: test-gameboy.o gameboy.o bus.o memory.o component.o timer.o bit.o cpu.o alu.o cartridge.o util.o error.o cpu-registers.o cpu-alu.o cpu-storage.o opcode.o bootrom.o bit_vector.o

unit-test-alu_ext.o: unit-test-alu_ext.c tests.h error.h alu.h bit.h alu_ext.h component.h bus.h memory.h bit_vector.h
unit-test-alu_ext: unit-test-alu_ext.o cpu-storage.o cpu-registers.o cpu-alu.o alu.o bus.o bit.o error.o component.o memory.o bit_vector.o

unit-test-bit-vector.o: unit-test-bit-vector.c error.h bit_vector.h bit.h image.h
unit-test-bit-vector: unit-test-bit-vector.o error.o bit_vector.o bit.o image.o



test-image.o: test-image.c error.h util.h image.h bit_vector.h bit.h sidlib.h
test-image: test-image.o error.o util.o image.o bit_vector.o bit.o sidlib.o

gbsimulator.o: gbsimulator.c error.h util.h cpu.h alu.h bit.h bus.h memory.h component.h image.h bit_vector.h gameboy.h cpu.c timer.c bootrom.c cartridge.c opcode.c cpu-registers.c cpu-storage.c cpu-alu.c sidlib.h
gbsimulator: gbsimulator.o error.o util.o cpu.o alu.o bit.o bus.o memory.o component.o image.o bit_vector.o gameboy.o  cpu.o timer.o bootrom.o cartridge.o opcode.o cpu-registers.o cpu-storage.o cpu-alu.o sidlib.o

#===================
#== USELESS ========

sidlib.o: sidlib.c sidlib.h

#===================

TARGETS := 
CHECK_TARGETS := unit-test-bit unit-test-alu unit-test-memory unit-test-component unit-test-bus unit-test-cpu unit-test-cpu-dispatch-week08 unit-test-cpu-dispatch-week09 unit-test-cartridge unit-test-bit-vector unit-test-timer
OBJS = 
OBJS_NO_STATIC_TESTS =
OBJS_STATIC_TESTS = 
OBJS = $(OBJS_STATIC_TESTS) $(OBJS_NO_STATIC_TESTS)

# ----------------------------------------------------------------------
# This part is to make your life easier. See handouts how to make use of it.


clean::
	-@/bin/rm -f *.o *~ $(CHECK_TARGETS)

new: clean all

static-check:
	scan-build -analyze-headers --status-bugs -maxloop 64 make CC=clang new

style:
	astyle -n -o -A8 -xt0 *.[ch]

# all those libs are required on Debian, adapt to your box
$(CHECK_TARGETS): LDLIBS += -lcheck -lm -lrt -pthread -lsubunit

check:: $(CHECK_TARGETS)
	$(foreach target,$(CHECK_TARGETS),./$(target) &&) true

# target to run tests


IMAGE=chappeli/feedback:latest
feedback:
	@docker pull $(IMAGE)
	@docker run -it --rm -v ${PWD}:/home/tester/done $(IMAGE)

SUBMIT_SCRIPT=../provided/submit.sh
submit1: $(SUBMIT_SCRIPT)
	@$(SUBMIT_SCRIPT) 1

submit2: $(SUBMIT_SCRIPT)
	@$(SUBMIT_SCRIPT) 2

submit:
	@printf 'what "make submit"??\nIt'\''s either "make submit1" or "make submit2"...\n'

libsid.so: sidlib.c
	$(CC) -fPIC -shared $(CPPFLAGS) $(CFLAGS) $(GTK_INCLUDE) $^ -o $@

#libsid_demo.o: CFLAGS += $(GTK_INCLUDE)

#libsid_demo: LDFLAGS += -L.
#libsid_demo: LDLIBS += $(GTK_LIBS) -lsid
#libsid_demo: libsid_demo.o libsid.so

