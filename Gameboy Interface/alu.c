#include <stdio.h>
#include <stdint.h>   // for uint8_t and uint16_t types
#include <inttypes.h> // for PRIx8, etc.
#include <assert.h>
#include "bit.h" // for bit_t
#include "alu.h"
#include "error.h"

#define MSB8 128
#define MSB4 16
#define OVERFLOW_VALUE 0x10

/**
 * @brief Checks if a flag is valid.
 *
 * @param flag a flag
 * @return 1 if flag is valid, 0 otherwise.
 */
int is_valid_flag(flag_bit_t flag){
	if(flag == FLAG_Z || flag == FLAG_N || flag == FLAG_H || flag == FLAG_C){
		return 1;
	}else{
		return 0;
	}
}

/**
 * See corresponding .h file for documentation
 */
flag_bit_t get_flag(flags_t flags, flag_bit_t flag){
	if(is_valid_flag(flag)){
		return flags & flag;
	}else{
		return 0;
	}
}

/**
 * See corresponding .h file for documentation
 */
void set_flag(flags_t* flags, flag_bit_t flag){
	assert(flags != NULL);
	
	if(is_valid_flag(flag)){
		*flags = *flags | flag;
	}
}

void set_flags(uint8_t h, uint8_t c, uint16_t checkzero, flags_t* flags) {
	if (checkzero == 0) {
		set_Z(flags);
	}
	if ((h & OVERFLOW_VALUE) == OVERFLOW_VALUE) {
		set_H(flags);
	}
	if ((c & OVERFLOW_VALUE) == OVERFLOW_VALUE) {
		set_C(flags);
	}
	return;
}

/**
 * See corresponding .h file for documentation
 */
int alu_add8(alu_output_t* result, uint8_t x, uint8_t y, bit_t c0){
	M_REQUIRE_NON_NULL(result);
	
	uint8_t v1 = lsb4(x) + lsb4(y) + c0;
	uint8_t v2 = msb4(x) + msb4(y) + msb4(v1);
	result -> value = merge4(v1, v2);
	set_flags(v1, v2, result->value, &(result->flags));
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int alu_sub8(alu_output_t* result, uint8_t x, uint8_t y,bit_t b0){
	M_REQUIRE_NON_NULL(result);
	
	uint8_t v1 = lsb4(x) - lsb4(y) - b0;
	uint8_t v2 = msb4(x) - msb4(y) - (msb4(v1) & 1);
	result -> value = merge4(v1, v2);
	set_flags(v1, v2, result->value, &(result->flags));
	set_N(&(result->flags)); // n = 1
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int alu_add16_low(alu_output_t* result, uint16_t x, uint16_t y){
	M_REQUIRE_NON_NULL(result);
	result->flags = 0;
	uint8_t v1 = lsb4(x) + lsb4(y);
	uint8_t v2 = msb4(lsb8(x)) + msb4(lsb8(y)) + msb4(v1);
	uint16_t v3 = lsb8(x) + lsb8(y);
	uint16_t v4 = msb8(v3) + msb8(x) + msb8(y);
	result -> value = merge8(v3, v4);
	set_flags(v1, v2, result->value, &(result->flags));

	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int alu_add16_high(alu_output_t* result, uint16_t x, uint16_t y){
	M_REQUIRE_NON_NULL(result);
	result->flags = 0;
	uint16_t v1 = lsb8(x) + lsb8(y);
	bit_t c = (v1 & 0x100) >> 8;
	
	uint8_t v2 = lsb4(msb8(x)) + lsb4(msb8(y)) + c;
	uint8_t v3 = msb4(msb8(x)) + msb4(msb8(y)) + msb4(v2);
	uint8_t v4 = msb8(v1) + msb8(x) + msb8(y);
	result ->value = merge8(lsb8(v1), v4);
	set_flags(v2, v3, result->value, &(result->flags));

	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int alu_shift(alu_output_t* result, uint8_t x, rot_dir_t dir){
	M_REQUIRE_NON_NULL(result);
	
	flags_t* r_flags = &(result -> flags);
	
	if(dir == RIGHT){
		if((x & 1) == 1){
			set_C(r_flags); 
		}
		uint8_t valueSRL = x >> 1;
		
		if(valueSRL == 0){
			set_Z(r_flags);
		}
		result -> value = valueSRL;
	}else if(dir == LEFT){
		if((x & MSB8) == MSB8){
			set_C(r_flags); 
		}
		uint8_t valueSLL = x << 1;
		
		if(valueSLL == 0){
			set_Z(r_flags);
		}
		result -> value = valueSLL;
	}else{
		return ERR_BAD_PARAMETER;
	}
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int alu_shiftR_A(alu_output_t* result, uint8_t x){
	M_REQUIRE_NON_NULL(result);
	
	flags_t* r_flags = &(result -> flags);
	
	if((x & 1) == 1){
		set_C(r_flags);
	}
	uint8_t valueSRA;
	
	if((x & MSB8) == MSB8){
		valueSRA = (x >> 1) | MSB8;
	}else{
		valueSRA = x >> 1;
	}
	if(valueSRA == 0){
		set_Z(r_flags);
	}
	result -> value = valueSRA;
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int alu_rotate(alu_output_t* result, uint8_t x, rot_dir_t dir){
	M_REQUIRE_NON_NULL(result);
	if(dir != RIGHT && dir != LEFT){
		return ERR_BAD_PARAMETER;
	}
	
	flags_t* r_flags = &(result -> flags);
	
	if(dir == RIGHT && (x & 1) == 1){
		set_C(r_flags);
	}
	if(dir == LEFT && (x & MSB8) == MSB8){
		set_C(r_flags);
	}
	
	int a;
	int b;
	if(dir == LEFT){
		a = 1;
		b = 7; 
	}else if(dir == RIGHT){
		a = 7;
		b = 1;
	}
	
	uint8_t msb_part = x << a;
	uint8_t lsb_part = x >> b;
		
	result -> value = msb_part | lsb_part;	
	
	if(result -> value == 0){
			set_Z(r_flags);
	}
	
	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int alu_carry_rotate(alu_output_t* result, uint8_t x, rot_dir_t dir, flags_t flags){
	M_REQUIRE_NON_NULL(result);
	
	flags_t* r_flags = &(result -> flags);
	
	if(dir == RIGHT){
		bit_t least_significant = x & 1;
		uint8_t valueRR = x >> 1;
		valueRR = valueRR | (get_flag(flags, FLAG_C) << 3);
		if(least_significant == 1){
			set_C(r_flags);
		}
		if(valueRR == 0){
			set_flag(r_flags, FLAG_Z);
		}
		
		result -> value = valueRR;
	}else if(dir == LEFT){
		uint8_t valueRL = x << 1;
		valueRL = valueRL | (get_flag(flags, FLAG_C) >> 4);
		
		if((x & MSB8) == MSB8){
			set_C(r_flags);
		}
		if(valueRL == 0){
			set_Z(r_flags);
		}
		
		result -> value = valueRL;
	}else{
		return ERR_BAD_PARAMETER;
	}
	
	return ERR_NONE;
}
