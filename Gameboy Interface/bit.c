#include <stdio.h>
#include <stdint.h>   // for uint8_t and uint16_t types
#include <inttypes.h> // for PRIx8, etc.
#include <assert.h>
#include "bit.h"
#define UINT8_SIZE 8 //Size of an int with 8 bits..

// ======================================================================
/**
 * @brief a type to represent 1 single bit.
 */
/* Nous vous fournission ici un type à n'utiliser QUE lorsque vous
 * voulez représenter UN SEUL bit ; p.ex. :
 *     bit_t un_bit_tout_seul = 1;
 */
typedef uint8_t bit_t;


// ======================================================================
/**
 * @brief clamp a value to be a bit index between 0 and 7
 */
/* Nous vous fournission ici une macro (les macros seront présentées dans
 *     le cours, bien plus tard dans le semestre) permettant de forcer
 * une valeur entre 0 et 7.
 * Par exemple :
 *     i = CLAMP07(j);
 * fera que la variable i contiendra :
 *     + la même valeur que celle de j si celle-ci est comprise entre 0 et 7 ;
 *     + 0 si la valeur de j est inférieure ou égale à 0 ;
 *     + 0 si la vlaeur de j est supérieure ou égale à 8.
 */
#define CLAMP07(x) (((x) < 0) || ((x) > 7) ? 0 : (x))


// ======================================================================

/**
 * See corresponding .h file for documentation
 */
uint8_t lsb4(uint8_t value){
	uint8_t result = 0;

	result = value & 0xf;
	return result;
}

/**
 * See corresponding .h file for documentation
 */
uint8_t msb4(uint8_t value){
	uint8_t result = 0;

	result = value >> 4;
	return result;
}

/**
 * See corresponding .h file for documentation
 */
uint8_t lsb8(uint16_t value){
	uint8_t result = 0;

	result = value & 0xff;
	return result;
}

/**
 * See corresponding .h file for documentation
 */
uint8_t msb8(uint16_t value){
	uint8_t result = 0;

	result = value >> 8;
	return result;
}

/**
 * See corresponding .h file for documentation
 */
uint16_t merge8(uint8_t v1, uint8_t v2){
	uint16_t result = 0;

	result = (v2 << 8) | v1;
	return result;
}

/**
 * See corresponding .h file for documentation
 */
uint8_t merge4(uint8_t v1, uint8_t v2){
	uint8_t result = 0;

	result = (v2 << 4) | (v1 & 0xf);
	return result;
}

/**
 * See corresponding .h file for documentation
 */
bit_t bit_get(uint8_t value, int index){
	bit_t bit = 0;
	int i = CLAMP07(index);

        bit = (value >> i) & 1;
	return bit;
}

/**
 * See corresponding .h file for documentation
 */
void bit_set(uint8_t* value, int index){
	assert(value != NULL);
	
	uint8_t one_at_index = 1;
	int i = CLAMP07(index);

        one_at_index = one_at_index << i;

        *value = *value | one_at_index;
}

/**
 * See corresponding .h file for documentation
 */
void bit_unset(uint8_t* value, int index){
	assert(value != NULL);
	
	uint8_t one_at_index = 1;
	int i = CLAMP07(index);

	one_at_index = one_at_index << i;

        *value = *value & (~one_at_index);
}

/**
 * See corresponding .h file for documentation
 */
void bit_rotate(uint8_t* value, rot_dir_t dir, int d){
	assert(value != NULL);
	assert(dir == LEFT || dir == RIGHT);
	
	int d_clamped = CLAMP07(d);
	int d_complement = UINT8_SIZE - d_clamped;
	
	int x;
	int y;
	if(dir == LEFT){
		x = d_clamped;
		y = d_complement; 
	}else if(dir == RIGHT){
		x = d_complement;
		y = d_clamped;
	}
	
	uint8_t msb_part = *value << x;
	uint8_t lsb_part = *value >> y;
		
	*value = msb_part | lsb_part;
}

/**
 * See corresponding .h file for documentation
 */
void bit_edit(uint8_t* value, int index, uint8_t v){
	assert(value != NULL);
	
	if(v == 0){
		bit_unset(value, index);
	}else{
		bit_set(value, index);
	}
}
