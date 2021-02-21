#include <stdio.h>
#include "cartridge.h"
#include "error.h"

#define MAX_STRING_LENGTH sizeof(data_t)

/**
 * See corresponding .h file for documentation
 */
int cartridge_init_from_file(component_t* c, const char* filename){
	M_REQUIRE_NON_NULL(c);
	M_REQUIRE_NON_NULL(filename);
	
	FILE* file = fopen(filename, "rb");
	if (file == NULL) {
		//fclose(file);
		return ERR_IO;
	}
	
	data_t data = 0;
	size_t i_until = BANK_ROM0_START + c->mem->size;
	for(size_t i = BANK_ROM0_START; i <= i_until; ++i){
		fscanf(file, "%c", &data);
		c->mem->memory[i] = data;
		if (i == CARTRIDGE_TYPE_ADDR && data != 0){
				fclose(file);
				return ERR_NOT_IMPLEMENTED;
		}
	}
	
	fclose(file);
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int cartridge_init(cartridge_t* ct, const char* filename){
	M_REQUIRE_NON_NULL(ct);
	M_REQUIRE_NON_NULL(filename);

	int ret = component_create(&(ct->c), BANK_ROM_SIZE);
	if(ret != ERR_NONE){
		component_free(&(ct->c));
		return ret;
	}
	
	return cartridge_init_from_file(&(ct->c), filename);
}

/**
 * See corresponding .h file for documentation
 */
int cartridge_plug(cartridge_t* ct, bus_t bus){
	M_REQUIRE_NON_NULL(ct);
	
	return bus_forced_plug(bus, &(ct->c), BANK_ROM0_START, BANK_ROM1_END, 0);
}

/**
 * See corresponding .h file for documentation
 */
void cartridge_free(cartridge_t* ct){
	if (ct != NULL) {
		component_free(&(ct->c));
	}
	
}
