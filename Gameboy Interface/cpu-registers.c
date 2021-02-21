#include <assert.h>
#include "cpu-registers.h"
#include "error.h"

/**
 * @brief Checks if a pair of register is valid.
 *
 * @param reg a pair of register
 * @return 1 if reg is valid, 0 otherwise.
 */
int is_valid_reg_pair(reg_pair_kind reg){
	if(reg == REG_BC_CODE || reg == REG_DE_CODE || reg == REG_HL_CODE || reg == REG_AF_CODE){
		return 1;
	}else{
		return 0;
	}
}

/**
 * @brief Checks if a register is valid.
 *
 * @param reg a register
 * @return 1 if reg is valid, 0 otherwise.
 */
int is_valid_reg(reg_kind reg){
	if(reg == REG_B_CODE || reg == REG_C_CODE || reg == REG_D_CODE 
	|| reg == REG_E_CODE || reg == REG_H_CODE || reg == REG_L_CODE || reg == REG_A_CODE){
		return 1;
	}else{
		return 0;
	}
}

/**
 * See corresponding .h file for documentation
 */
uint16_t cpu_reg_pair_get(const cpu_t* cpu, reg_pair_kind reg){
	M_REQUIRE_NON_NULL(cpu);
	
	if(is_valid_reg_pair(reg)){
		uint16_t reg_pair_ret;
		
		switch(reg){
			case REG_BC_CODE: reg_pair_ret = cpu->BC; break;
			case REG_DE_CODE: reg_pair_ret = cpu->DE; break;
			case REG_HL_CODE: reg_pair_ret = cpu->HL; break;
			case REG_AF_CODE: reg_pair_ret = cpu->AF; break;
		}
		
		return reg_pair_ret;
	}else{
		return 0;
	}
}

/**
 * See corresponding .h file for documentation
 */
void cpu_reg_pair_set(cpu_t* cpu, reg_pair_kind reg, uint16_t value){
	assert(cpu != NULL);
	assert(is_valid_reg_pair(reg));
	
	switch(reg){
			case REG_BC_CODE: cpu->BC = value; break;
			case REG_DE_CODE: cpu->DE = value; break;
			case REG_HL_CODE: cpu->HL = value; break;
			case REG_AF_CODE: cpu->AF = value & 0xFFF0; break;
		}
}

/**
 * See corresponding .h file for documentation
 */
uint8_t cpu_reg_get(const cpu_t* cpu, reg_kind reg){
	M_REQUIRE_NON_NULL(cpu);
	
	if(is_valid_reg(reg)){
		uint8_t reg_ret;
		
		switch(reg){
			case REG_B_CODE: reg_ret = cpu->B; break;
			case REG_C_CODE: reg_ret = cpu->C; break;
			case REG_D_CODE: reg_ret = cpu->D; break;
			case REG_E_CODE: reg_ret = cpu->E; break;
			case REG_H_CODE: reg_ret = cpu->H; break;
			case REG_L_CODE: reg_ret = cpu->L; break;
			case REG_A_CODE: reg_ret = cpu->A; break;
		}
		return reg_ret;
	}else{
		return 0;
	}
}

/**
 * See corresponding .h file for documentation
 */
void cpu_reg_set(cpu_t* cpu, reg_kind reg, uint8_t value){
	assert(cpu != NULL);
	assert(is_valid_reg(reg));
	
	switch(reg){
			case REG_B_CODE: cpu->B = value; break;
			case REG_C_CODE: cpu->C = value; break;
			case REG_D_CODE: cpu->D = value; break;
			case REG_E_CODE: cpu->E = value; break;
			case REG_H_CODE: cpu->H = value; break;
			case REG_L_CODE: cpu->L = value; break;
			case REG_A_CODE: cpu->A = value; break;
		}
}
