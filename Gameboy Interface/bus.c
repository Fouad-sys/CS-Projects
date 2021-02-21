#include "bus.h"
#include "memory.h"
#include "component.h"
#include "error.h"
#include "bit.h"

/**
 * See corresponding .h file for documentation
 */
int bus_remap(bus_t bus, component_t* c, addr_t offset){
	M_REQUIRE_NON_NULL(c);
	M_REQUIRE_NON_NULL(bus);
	M_REQUIRE_NON_NULL(c->mem);
	M_REQUIRE_NON_NULL(c->mem->memory);
	if((c->end - c->start + offset) >= c->mem->size){
		return ERR_ADDRESS;
	}
	
	for(int i = c->start; i <= c->end ; ++i){
		int j = i- c->start ; // 0 to (end-start)
		bus[i] = c->mem->memory + offset + j;
	}
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int bus_forced_plug(bus_t bus, component_t* c, addr_t start, addr_t end, addr_t offset){
	M_REQUIRE_NON_NULL(c);
	M_REQUIRE_NON_NULL(bus);
	M_REQUIRE_NON_NULL(c->mem);
	if((end - start + offset) >= c->mem->size){
		return ERR_ADDRESS;
	}
	if(start > end){
		return ERR_BAD_PARAMETER;
	}
	
	c->start = start;
	c->end = end;
	
	int ret_value = bus_remap(bus, c, offset);
	
	if(ret_value != ERR_NONE){
		component_free(c);
		return ret_value;
	}
	
	return ERR_NONE;
}
/**
 * @brief Method to check whether or not a part of the bus is free.
 *
 * @param bus bus to check
 * @param start starting address (included)
 * @param end ending address (included)
 * @return 0 if the bus is free, 1 if it is already (partially) occupied
 */
int bus_occupied(bus_t bus, addr_t start, addr_t end){
	M_REQUIRE_NON_NULL(bus);
	
	for(int i = start; i <= end; ++i){
		if(bus[i] != NULL){
			return 1;
		}
	}
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int bus_plug(bus_t bus, component_t* c, addr_t start, addr_t end){
	M_REQUIRE_NON_NULL(c);
	M_REQUIRE_NON_NULL(bus);
	M_REQUIRE_NON_NULL(c->mem);
	M_REQUIRE_NON_NULL(c->mem->memory);
	if(bus_occupied(bus, start, end) != ERR_NONE){
		return ERR_ADDRESS;
	}
	
	return bus_forced_plug(bus, c, start, end, 0);
}

/**
 * See corresponding .h file for documentation
 */
int bus_unplug(bus_t bus, component_t* c){
	M_REQUIRE_NON_NULL(c);
	M_REQUIRE_NON_NULL(bus);
	
	for(int i = c->start; i <= c->end; ++i){
		bus[i] = NULL;
	}
	
	c->start = 0;
	c->end = 0;
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int bus_read(const bus_t bus, addr_t address, data_t* data){
	M_REQUIRE_NON_NULL(bus);
	M_REQUIRE_NON_NULL(data);
	
	if(bus[address] == NULL){
		*data = 0xFF;
		return ERR_NONE;		
	}else{
		*data = *bus[address];	
		return ERR_NONE;
	}
}

/**
 * See corresponding .h file for documentation
 */
int bus_read16(const bus_t bus, addr_t address, addr_t* data16){
	if(bus[address] == NULL || bus[address+1] == NULL){
		*data16 = 0xFF;
		return ERR_ADDRESS;
	}
	*data16 = merge8(*bus[address], *bus[address+1]);
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int bus_write(bus_t bus, addr_t address, data_t data){
	M_REQUIRE_NON_NULL(bus);
	M_REQUIRE_NON_NULL(bus[address]);
	*bus[address] = data;
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int bus_write16(bus_t bus, addr_t address, addr_t data16){
	bus_write(bus, address, lsb8(data16));
	bus_write(bus, address + 1, msb8(data16));
	
	return ERR_NONE;
}






