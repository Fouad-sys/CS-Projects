#include <stdio.h>
#include "timer.h"
#include "error.h"

#define TIMER_INC 4

/**
 * See corresponding .h file for documentation
 */
int timer_init(gbtimer_t* timer, cpu_t* cpu){
	M_REQUIRE_NON_NULL(timer);
	M_REQUIRE_NON_NULL(cpu);

	timer->cpu = cpu;
	timer->counter = 0;
	//(*cpu->bus)[REG_DIV] = &;
	return ERR_NONE;
}

/**
 * @brief Method that returns the used bit related to TAC[1:0].
 * @param TAC_data, the TAC value.
 * @return the corresponding value of the used bit.
 */
size_t get_used_bit(data_t TAC_data) {
	data_t TAC_lsb = TAC_data & 0x03;
	size_t used_bit = 0;

	switch (TAC_lsb) {
	case 0x00: used_bit = 9; break;
	case 0x01: used_bit = 3; break;
	case 0x02: used_bit = 5; break;
	case 0x03: used_bit = 7; break;
	default: break;
	}

	return used_bit;
}


/**
 * @brief Determines "l'état du minuteur".
 * @param timer, the main timer.
 * @return "L'état du minuteur", or 0 by default (If an element is null).
 */
bit_t timer_state(gbtimer_t* timer) {
	if (timer != NULL && timer->cpu != NULL && timer->cpu->bus != NULL) {
		data_t TAC_data = 0;
		uint16_t counter_data = timer->counter;

		bus_read(*timer->cpu->bus, REG_TAC, &TAC_data);
		return bit_get(TAC_data, 2) & (counter_data >> get_used_bit(TAC_data));
	}
	else {
		return 0;
	}
}

/**
 * @brief Increments the second timer given the rule.
 * @param timer, the main timer.
 * @param old_state, old state of the "état du minuteur".
 */
void timer_incr_if_change(gbtimer_t* timer, bit_t old_state) {
	if (timer != NULL) {
		if (old_state & !timer_state(timer)) {
			data_t TIMA_timer = 0;

			int ret_TIMA = bus_read(*timer->cpu->bus, REG_TIMA, &TIMA_timer);
			if (ret_TIMA != ERR_NONE) {
				return;
			}

			//Increment second timer
			TIMA_timer += 1;

			//Launch interruption, and sets second timer to default value
			if (TIMA_timer == 0) {

				cpu_request_interrupt(timer->cpu, TIMER);

				int ret_TMA = bus_read(*timer->cpu->bus, REG_TMA, &TIMA_timer);
				if (ret_TMA != ERR_NONE) {
					return;
				}


			}

			bus_write(*timer->cpu->bus, REG_TIMA, TIMA_timer);
		}
	}
	return;
}

/**
 * See corresponding .h file for documentation
 */
int timer_cycle(gbtimer_t* timer){
	M_REQUIRE_NON_NULL(timer);
	M_REQUIRE_NON_NULL(timer->cpu);
	M_REQUIRE_NON_NULL(timer->cpu->bus);
	
	//Récupérer l'état courant du minuteur.
	bit_t old_state = timer_state(timer);
	
	//Ajouter 4 au compteur.
	timer->counter = timer->counter + TIMER_INC;
	
	
	//Copiez les 8 bits de poids fort du compteur principal dans DIV
	int ret_write = bus_write(*timer->cpu->bus, REG_DIV, msb8(timer->counter));
	if(ret_write != ERR_NONE){
		return ret_write;
	}
	
	//Appeler la méthode comme expliqué
	timer_incr_if_change(timer, old_state);

	return ERR_NONE;
}

/**
 * See corresponding .h file for documentation
 */
int timer_bus_listener(gbtimer_t* timer, addr_t addr) {
	M_REQUIRE_NON_NULL(timer);
	bit_t old_state = timer_state(timer);
	if (addr == REG_DIV) {
		timer->counter = 0;
		int ret_write = bus_write(*timer->cpu->bus, REG_DIV, msb8(timer->counter));
		if (ret_write != ERR_NONE) {
			return ret_write;
		}
		timer_incr_if_change(timer, old_state);
	}
	else if (addr == REG_TAC) {
		timer_incr_if_change(timer, old_state);
	}
	return ERR_NONE;
}

