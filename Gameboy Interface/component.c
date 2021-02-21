#include <stdlib.h>
#include <assert.h>

#include "memory.h"
#include "component.h"
#include "error.h"

/**
 * See corresponding .h file for documentation
 */
int component_create(component_t* c, size_t mem_size){
	M_REQUIRE_NON_NULL(c);

	if(mem_size == 0){
		c->mem = NULL;
	}else{
		c->mem = calloc(1, sizeof(memory_t)+1);
		if(c->mem == NULL){
			return ERR_MEM;
		}
		int ret = mem_create(c->mem, mem_size);
		if(ret != ERR_NONE){
			return ret;
		}
	}
	c->start = 0;
	c-> end = 0;
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int component_shared(component_t* c, component_t* c_old){
	M_REQUIRE_NON_NULL(c);
	M_REQUIRE_NON_NULL(c_old);
	M_REQUIRE_NON_NULL(c_old->mem);
	M_REQUIRE_NON_NULL(c_old->mem->memory);
	
	c->start = 0;
	c->end = 0;
	c->mem = c_old->mem;
	
	return ERR_NONE;
	
}

/**
 * See corresponding .h file for documentation
 */
void component_free(component_t* c){
	if(c != NULL){
		mem_free(c->mem);
		free(c->mem);
		c->mem = NULL;
	
		c->start = 0;
		c->end = 0;
	}
}






