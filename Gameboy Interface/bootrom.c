#include <stdio.h>
#include "bootrom.h"
#include "gameboy.h"
#include "error.h"

/**
 * See corresponding .h file for documentation
 */
int bootrom_init(component_t* c){
	M_REQUIRE_NON_NULL(c);
	
	int ret = component_create(c, MEM_SIZE(BOOT_ROM));
	if(ret != ERR_NONE){
		return ret;
	}
	
	M_REQUIRE_NON_NULL(c->mem);
	M_REQUIRE_NON_NULL(c->mem->memory);
	
	//Et dont le contenu est celui donné par la macro GAMEBOY_BOOT_ROM_CONTENT...
	data_t bootrom_data[MEM_SIZE(BOOT_ROM)] = GAMEBOY_BOOT_ROM_CONTENT;
	for (int i = 0; i < MEM_SIZE(BOOT_ROM); ++i) {
		c->mem->memory[i] = bootrom_data[i];
	}

	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
 int bootrom_bus_listener(gameboy_t* gameboy, addr_t addr){
	 M_REQUIRE_NON_NULL(gameboy);
	 
	 if(addr == REG_BOOT_ROM_DISABLE && gameboy->boot == 1){ //Et à la première d'entre elles (?)
		 int ret = bus_unplug(gameboy->bus, &(gameboy->bootrom));
		 if(ret != ERR_NONE){
			 return ret;
		 }
		 cartridge_plug(&(gameboy->cartridge), gameboy->bus);
		 gameboy->boot = 0;
	 }
	 return ERR_NONE;
 }
