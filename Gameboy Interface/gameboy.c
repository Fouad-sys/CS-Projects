#include <assert.h>

#include "gameboy.h"
#include "error.h"
#include "bootrom.h"
#include "timer.h"
#include "cartridge.h"

/**
 * @brief initializes a component in the gameboy at a given index.
 * @param gameboy, the gameboy.
 * @param index, the given index
 * @return Returns the component
 */
int init_plug_comp(gameboy_t* gameboy, int index, size_t size, addr_t start_index, addr_t end_index){
	component_t* comp = &(gameboy->components[index]);
	
	int ret = component_create(comp, size);
	if(ret != ERR_NONE){
		return ret;
	}
	
    int ret_plug = bus_plug(gameboy->bus, comp, start_index, end_index);
	if(ret_plug != ERR_NONE){
		return ret_plug;
	}
	
	return ERR_NONE;
}

#define checkerr(err) if(err != ERR_NONE)return err;

int init_plug_gameboy(gameboy_t* gameboy, const char* filename) {
	int err = init_plug_comp(gameboy, 0, MEM_SIZE(WORK_RAM), WORK_RAM_START, WORK_RAM_END);
	checkerr(err);
	err = init_plug_comp(gameboy, 1, MEM_SIZE(ECHO_RAM), ECHO_RAM_START, ECHO_RAM_END);
	checkerr(err);
	err = init_plug_comp(gameboy, 2, MEM_SIZE(REGISTERS), REGISTERS_START, REGISTERS_END);
	checkerr(err);
	err = init_plug_comp(gameboy, 3, MEM_SIZE(EXTERN_RAM), EXTERN_RAM_START, EXTERN_RAM_END);
	checkerr(err);
	err = init_plug_comp(gameboy, 4, MEM_SIZE(VIDEO_RAM), VIDEO_RAM_START, VIDEO_RAM_END);
	checkerr(err);
	err = init_plug_comp(gameboy, 5, MEM_SIZE(GRAPH_RAM), GRAPH_RAM_START, GRAPH_RAM_END);
	checkerr(err);
	//SHARE ECHO <-> WORK
	err = component_shared(&(gameboy->components[1]), &(gameboy->components[0]));
	checkerr(err);
	//CPU
	err = cpu_init(&(gameboy->cpu));
	checkerr(err);
	err = cpu_plug(&(gameboy->cpu), &(gameboy->bus));
	checkerr(err);
	//CARTRIDGE
	err = cartridge_init(&(gameboy->cartridge), filename);
	checkerr(err);
	err = cartridge_plug(&(gameboy->cartridge), gameboy->bus);
	checkerr(err);
	//TIMER
	err = timer_init(&(gameboy->timer), &(gameboy->cpu));
	checkerr(err);
	//JOYPAD
	err = joypad_init_and_plug(&(gameboy->pad), &(gameboy->cpu));
	checkerr(err);
	//LCDC
	err = lcdc_init(gameboy);
	checkerr(err);
	err = lcdc_plug(&(gameboy->screen), gameboy->bus);
	checkerr(err);
	//BOOTROM: pas fait au bon moment...
	err = bootrom_init(&(gameboy->bootrom));
	checkerr(err);
	err = bootrom_plug(&(gameboy->bootrom), gameboy->bus);
	checkerr(err);
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int gameboy_create(gameboy_t* gameboy, const char* filename){
	M_REQUIRE_NON_NULL(gameboy);
	memset(gameboy, 0, sizeof(gameboy_t));
	gameboy->nb_components = GB_NB_COMPONENTS;
	
	int err = init_plug_gameboy(gameboy, filename);
	checkerr(err);
	gameboy->cycles = 1;
	gameboy->boot = 1;

	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
void gameboy_free(gameboy_t* gameboy){
	assert(gameboy != NULL);
	

	//start iterating from 1 because eco and work have shared components
	for(int i = 1; i < GB_NB_COMPONENTS; ++i){
		bus_unplug(gameboy->bus, &(gameboy->components[i]));
		component_free(&(gameboy->components[i]));
	}
	
	cartridge_free(&(gameboy->cartridge));
	cpu_free(&(gameboy->cpu));
	component_free(&(gameboy->bootrom));
	lcdc_free(&(gameboy->screen));
}

/**
 * See corresponding .h file for documentation
 */
int gameboy_run_until(gameboy_t* gameboy, uint64_t cycle) {
	while (gameboy->cycles < cycle) {

		//cycle the timer
		timer_cycle(&(gameboy->timer));

		lcdc_cycle(&(gameboy->screen),gameboy->cycles);

		//cycle in the cpu
		int err = cpu_cycle(&(gameboy->cpu));
		checkerr(err);

		
		timer_bus_listener(&(gameboy->timer), gameboy->cpu.write_listener);
		bootrom_bus_listener(gameboy, gameboy->cpu.write_listener);
		
	
		lcdc_bus_listener(&(gameboy->screen), gameboy->cpu.write_listener);
		joypad_bus_listener(&(gameboy->pad), gameboy->cpu.write_listener);

		++gameboy->cycles;
	}
	return ERR_NONE;
}
