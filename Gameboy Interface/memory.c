#include <stdlib.h>
#include <assert.h>

#include "memory.h"
#include "error.h"

/**
 * See corresponding .h file for documentation
 */
int mem_create(memory_t* mem, size_t size){
	M_REQUIRE_NON_NULL(mem);
	
	if(size == 0){
		return ERR_BAD_PARAMETER;
	}
	mem->memory = calloc(size, sizeof(data_t));
	mem->size = 0;
	if(mem->memory == NULL){
		return ERR_MEM;
	}
	mem->size = size;
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
void mem_free(memory_t* mem){
	
	if(mem != NULL){
		free(mem->memory);
		mem->memory = NULL;
	
		mem->size = 0;
	}
}
