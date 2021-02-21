#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "bit_vector.h"
#include "image.h"
#include "error.h"

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_create(size_t size, bit_t value){
	if(size <= 0){
		return NULL;
	}
	
	//New size (0-> 0, 1 -> 1, 31 -> 1, 32 -> 1, 33 -> 2, ...)
	size_t nb_data = (size % IMAGE_LINE_WORD_BITS == 0) ? size / IMAGE_LINE_WORD_BITS : size / IMAGE_LINE_WORD_BITS + 1;
	//Max number of *size* (nb bits) to allocate
	const size_t N_MAX = (SIZE_MAX - sizeof(bit_vector_t));
	
	if(size <= N_MAX){
		//Create new vector
		bit_vector_t* vector = malloc(sizeof(bit_vector_t) + (nb_data * sizeof(uint32_t)));
		if(vector != NULL){
			//Number of bits
			vector->size = size;
			//Size of the array
			vector->content_size = nb_data;
		
			//Set bits to value
			for(size_t i = 0; i < vector->content_size; ++i){
				if(value == 0){
					vector->content[i] = VECTOR_32_ZEROS;
				}else{
					size_t size_rem = size - (IMAGE_LINE_WORD_BITS * i);
					if(size_rem >= IMAGE_LINE_WORD_BITS){
						vector->content[i] = VECTOR_32_ONES;
					}else{
						vector->content[i] = (VECTOR_32_ONES >> (IMAGE_LINE_WORD_BITS - size_rem));
					}
				}
			}
		}
		
		return vector;
	}else{
		return NULL;
	}
}

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_cpy(const bit_vector_t* pbv){
	if(pbv == NULL){
		return NULL;
	}
	
	//Create 0 vector
	bit_vector_t* vector_copy = bit_vector_create(pbv->size, 0);
	
	//Copy content
	for(size_t i = 0; i < vector_copy->content_size; ++i){
		vector_copy->content[i] = pbv->content[i];
	}
	
	return vector_copy;
}

/**
 * See corresponding .h file for documentation
 */
bit_t bit_vector_get(const bit_vector_t* pbv, size_t index){
	if(pbv == NULL || index >= pbv->size){
		return 0;
	}
	
	size_t array_index = index / IMAGE_LINE_WORD_BITS;
	size_t word_index = index % IMAGE_LINE_WORD_BITS;
	
	return (bit_t) (pbv->content[array_index] >> word_index) & 1;
}

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_not(bit_vector_t* pbv){
	if(pbv == NULL){
		return NULL;
	}
	
	for(size_t i = 0; i < pbv->content_size; ++i){
		size_t shift_content = pbv->size - i * IMAGE_LINE_WORD_BITS;
		size_t shift = (shift_content > IMAGE_LINE_WORD_BITS) ? 0 : IMAGE_LINE_WORD_BITS - shift_content;
		uint32_t mask = (VECTOR_32_ONES << shift) >> shift;
		
		pbv->content[i] = ~(pbv->content[i]) & mask;
	}
	
	return pbv;
}

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_and(bit_vector_t* pbv1, const bit_vector_t* pbv2){
	if(pbv1 == NULL || pbv2 == NULL){
		return NULL;
	}
	if(pbv1->content_size != pbv2->content_size || pbv1->size != pbv2->size){
		return NULL;
	}
	
	//AND
	for(size_t i = 0; i < pbv1->content_size; ++i){
		pbv1->content[i] &= pbv2->content[i]; 
	}
	
	return pbv1;
}

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_or(bit_vector_t* pbv1, const bit_vector_t* pbv2){
	if(pbv1 == NULL || pbv2 == NULL){
		return NULL;
	}
	if(pbv1->content_size != pbv2->content_size || pbv1->size != pbv2->size){
		return NULL;
	}
	
	//OR
	for(size_t i = 0; i < pbv1->content_size; ++i){
		pbv1->content[i] |= pbv2->content[i];
	}
	
	return pbv1;
}

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_xor(bit_vector_t* pbv1, const bit_vector_t* pbv2){
	if(pbv1 == NULL || pbv2 == NULL){
		return NULL;
	}
	if(pbv1->content_size != pbv2->content_size || pbv1->size != pbv2->size){
		return NULL;
	}
	
	//XOR
	for(size_t i = 0; i < pbv1->content_size; ++i){
		pbv1->content[i] ^= pbv2->content[i];
	}
	
	return pbv1;
}

/**
 * @brief Returns a 32-bit vector that contains the bit at position index.
 * @param ext, the extract type.
 * @param index, the index.
 * @return The 32-bit vector.
 */
uint32_t get_contains_vector_extract(extract_type ext, const bit_vector_t* pbv, int64_t array_index){
	if(pbv == NULL){
		return VECTOR_32_ZEROS;
	}
	
	uint32_t word;
	
	if(array_index < 0 || array_index >= pbv->content_size){
		word = (ext == ZERO) ? VECTOR_32_ZEROS : pbv->content[(array_index % pbv->content_size + pbv->content_size) % pbv->content_size];
	}else{
		word = pbv->content[array_index];
	}
	
	//We need to copy the word if size small.
	if(ext == WRAP && pbv->size < IMAGE_LINE_WORD_BITS){
		uint32_t new_word = VECTOR_32_ZEROS;
		
		size_t n_step = (size_t) ceil((1.0 * IMAGE_LINE_WORD_BITS) / ((double) pbv->size));
		for(int i = 0; i < n_step; ++i){
			new_word |= word << (pbv->size * i);
		}
		
		return new_word;
	}else{
		return word;
	}
}

/**
 * @brief Returns a 32-bit vector that starts with the bit at position index.
 * @param ext, the extract type.
 * @param index, the index.
 * @return The 32-bit vector.
 */ 
uint32_t get_start_vector_extract(extract_type ext, const bit_vector_t* pbv, int64_t index){
	if(pbv == NULL){
		return VECTOR_32_ZEROS;
	}
	
	size_t array_index = (index < 0) ? index / IMAGE_LINE_WORD_BITS - 1 : index / IMAGE_LINE_WORD_BITS;
	
	//Only need 1 word
	if(index % IMAGE_LINE_WORD_BITS == 0){
		return get_contains_vector_extract(ext, pbv, array_index);
		
	//Always need 2 words
	}else{	
		size_t word1_index = array_index;
		size_t word2_index = word1_index + 1;
		
		uint32_t word1 = get_contains_vector_extract(ext, pbv, word1_index);
		uint32_t word2 = get_contains_vector_extract(ext, pbv, word2_index);
		
		size_t index_mod = index % IMAGE_LINE_WORD_BITS;
		
		int shift1_n = (index_mod < 0) ? IMAGE_LINE_WORD_BITS + index_mod : index_mod ;
		word1 = word1 >> shift1_n;
		
		int shift2_n = (index_mod < 0) ? -index_mod : IMAGE_LINE_WORD_BITS - index_mod;
		word2 = word2 << shift2_n;
		
		return word1 | word2;
	}
}

/**
 * @brief Build the vector, given the extract type and starting index.
 * @param ext, the extract type.
 * @param pbv, the vector to extract.
 * @param new_vector, the vector to modify.
 * @param index, the starting index.
 * @return new_vector modified.
 */
bit_vector_t* build_vector_extract(extract_type ext, const bit_vector_t* pbv, bit_vector_t* new_vector, uint64_t index){
	if(pbv == NULL || new_vector == NULL){
		return NULL;
	}
	
	for(size_t i = 0; i < new_vector->content_size; ++i){
			size_t shift = (new_vector->size - (i * IMAGE_LINE_WORD_BITS) > IMAGE_LINE_WORD_BITS) ? 0 : IMAGE_LINE_WORD_BITS - new_vector->size;
			uint32_t mask = (VECTOR_32_ONES << shift) >> shift;
			new_vector->content[i] = get_start_vector_extract(ext, pbv, index + (IMAGE_LINE_WORD_BITS * i)) & mask;
	}
	
	return new_vector;
} 

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_extract_zero_ext(const bit_vector_t* pbv, int64_t index, size_t size){
	if(size == 0){
		return NULL;
	}
	
	//Creates a new vector
	bit_vector_t* new_vector = bit_vector_create(size, 0);
	
	if(pbv != NULL){
		build_vector_extract(ZERO, pbv, new_vector, index);
	}
	
	return new_vector;
}

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_extract_wrap_ext(const bit_vector_t* pbv, int64_t index, size_t size){
	if(pbv == NULL || size == 0){
		return NULL;
	}
	
	//Creates a new vector
	bit_vector_t* new_vector = bit_vector_create(size, 0);
	
	build_vector_extract(WRAP, pbv, new_vector, index);
	
	return new_vector;
}

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_shift(const bit_vector_t* pbv, int64_t shift){
	if(pbv == NULL){
		return NULL;
	}
	
	return bit_vector_extract_zero_ext(pbv, -shift, pbv->size);
}

/**
 * See corresponding .h file for documentation
 */
bit_vector_t* bit_vector_join(const bit_vector_t* pbv1, const bit_vector_t* pbv2, int64_t shift){
	if(pbv1 == NULL || pbv2 == NULL){
		return NULL;
	}
	if(pbv1->content_size != pbv2->content_size || pbv1->size != pbv2->size || shift < 0){ //smaller than 0
		return NULL;
	}
	
	bit_vector_t* new_vector = bit_vector_create(pbv1->size, 0);
	
	if(new_vector == NULL){
		return NULL;
	}
	for(size_t i = 0; i < new_vector->content_size; ++i){
			int64_t shift_content = shift - i * IMAGE_LINE_WORD_BITS;
			int64_t shift1 = (shift_content > IMAGE_LINE_WORD_BITS) ? 0 : IMAGE_LINE_WORD_BITS - shift_content;
			int64_t shift2 = (shift_content > IMAGE_LINE_WORD_BITS) ? IMAGE_LINE_WORD_BITS : shift_content;
			
			uint32_t mask1 = (shift1 == IMAGE_LINE_WORD_BITS) ? VECTOR_32_ZEROS : (VECTOR_32_ONES << shift1) >> shift1;
			uint32_t mask2 = (shift2 == IMAGE_LINE_WORD_BITS) ? VECTOR_32_ZEROS : (VECTOR_32_ONES >> shift2) << shift2;
			
			new_vector->content[i] = (pbv1->content[i] & mask1) | (pbv2->content[i] & mask2);
	}
	
	return new_vector;	
}

#define MIN(a, b) (((a) < (b)) ? (a) : (b))

/**
 * See corresponding .h file for documentation
 */
int bit_vector_print(const bit_vector_t* pbv){
	if(pbv == NULL || pbv->size == 0){
		return 0;
	}
	fprintf(stderr, "start\n");
	for(size_t i = 0; i < pbv->content_size; ++i){
		size_t index = pbv->content_size - i - 1;
		size_t vect_par_size = MIN(pbv->size - (index * IMAGE_LINE_WORD_BITS), IMAGE_LINE_WORD_BITS);
		
		for(int j = 0; j < vect_par_size; ++j){
			size_t shift = IMAGE_LINE_WORD_BITS - vect_par_size + j;
			
			uint32_t bit = (pbv->content[index] << shift) >> (IMAGE_LINE_WORD_BITS - 1);
			
			printf("%d", bit);
		}
	}
	
	return pbv->size;
}

/**
 * See corresponding .h file for documentation
 */
int bit_vector_println(const char* prefix, const bit_vector_t* pbv){
	return printf("%s", prefix) + bit_vector_print(pbv) + printf("\n");
}

/**
 * See corresponding .h file for documentation
 */
void bit_vector_free(bit_vector_t** pbv){
	if(pbv != NULL){	
		free(*pbv);
		*pbv = NULL;
	}
}
