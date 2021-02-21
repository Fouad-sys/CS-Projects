
#include "image.h"
#include "gameboy.h"
#include "error.h"
#include "util.h"
#include "sidlib.h"
#include "alu.h"
#include "bus.h"


#include <sys/time.h>
#include <stdint.h>

// Key press bits
#define MY_KEY_UP_BIT    0x01
#define MY_KEY_DOWN_BIT  0x02
#define MY_KEY_RIGHT_BIT 0x04
#define MY_KEY_LEFT_BIT  0x08
#define MY_KEY_A_BIT     0x10
#define SCREEN_SCALE 3
gameboy_t gb;
struct timeval start;
struct timeval paused;

//=======================================================================
static void error(const char* pgm, const char* msg)
{
	fputs("ERROR: ", stderr);
	if (msg != NULL) fputs(msg, stderr);
	fprintf(stderr, "\nusage:    %s input_file [iterations]\n", pgm);
	fprintf(stderr, "examples: %s rom.gb 1000\n", pgm);
	fprintf(stderr, "          %s game.gb\n", pgm);
}


// ======================================================================
uint64_t get_time_in_GB_cyles_since(struct timeval* from) {
	struct timeval time;
	gettimeofday(&time, NULL);
	if (!timercmp(&time, from, >)) {
		return 0;
	}
	struct timeval delta;
	timersub(&time, from, &delta);
	return delta.tv_sec * GB_CYCLES_PER_S + (delta.tv_usec * GB_CYCLES_PER_S) / 1000000;
}



// ======================================================================
static void set_grey(guchar* pixels, int row, int col, int width, guchar grey)
{
	const size_t i = (size_t)(3 * (row * width + col)); // 3 = RGB
	pixels[i + 2] = pixels[i + 1] = pixels[i] = grey;
}

// ======================================================================
static void generate_image(guchar* pixels, int height, int width)
{
	if(pixels == NULL){
		return;
	}
	uint64_t time = get_time_in_GB_cyles_since(&start);
	gameboy_run_until(&gb, time);
	uint8_t pixelValue;
	for (size_t h = 0; h < height; h++) {
		for (size_t w = 0; w < width; w++) {
			image_get_pixel(&pixelValue, &(gb.screen.display), w/SCREEN_SCALE, h/SCREEN_SCALE);
			set_grey( pixels, h, w, width, 255 -85 * pixelValue);
		}
	}
}

// ======================================================================
#define do_key(X) \
    do { \
        if (! (psd->key_status & MY_KEY_ ## X ##_BIT)) { \
            psd->key_status |= MY_KEY_ ## X ##_BIT; \
            puts(#X " key pressed"); \
        } \
    } while(0)

static gboolean keypress_handler(guint keyval, gpointer data)
{
	simple_image_displayer_t* const psd = data;
	if (psd == NULL) return FALSE;

	switch (keyval) {
	case GDK_KEY_Up:
		joypad_key_pressed(&(gb.pad), UP_KEY);
		return TRUE;

	case GDK_KEY_Down:
		joypad_key_pressed(&(gb.pad), DOWN_KEY);
		return TRUE;

	case GDK_KEY_Right:
		joypad_key_pressed(&(gb.pad), RIGHT_KEY);
		return TRUE;

	case GDK_KEY_Left:
		joypad_key_pressed(&(gb.pad), LEFT_KEY);
		return TRUE;

	case 'A':
	case 'a':
		joypad_key_pressed(&(gb.pad), A_KEY);
		return TRUE;

	case 'S':
	case 's':
		joypad_key_pressed(&(gb.pad), B_KEY);
		return TRUE;

	case GDK_KEY_Page_Up:
		joypad_key_pressed(&(gb.pad), SELECT_KEY);
		return TRUE;

	case GDK_KEY_Page_Down:
		joypad_key_pressed(&(gb.pad), START_KEY);
		return TRUE;
	
	case GDK_KEY_space: {
		if (psd->timeout_id > 0) {
			gettimeofday(&paused, NULL);
		}
		else {
			struct timeval time;
			gettimeofday(&time, NULL);
			timersub(&time, &paused, &paused);
			timeradd(&paused, &start, &start);
			timerclear(&paused);
		}
		return ds_simple_key_handler(keyval, data);
	}
	}



	return ds_simple_key_handler(keyval, data);
}
#undef do_key

//======================================================================

int isValid(guint keyval) {
	if ((keyval == GDK_KEY_Up) || (keyval == GDK_KEY_Down) || (keyval == GDK_KEY_Right) || (keyval == GDK_KEY_Left) || (keyval == 'a') || (keyval == 'A') || (keyval == 's') || (keyval == 'S') || (keyval == GDK_KEY_Page_Up) || (keyval == GDK_KEY_Page_Down)) {
		return 1;
	}
	else {
		return 0;
	}
}


// ======================================================================
#define do_key(X) \
    do { \
        if (psd->key_status & MY_KEY_ ## X ##_BIT) { \
          psd->key_status &= (unsigned char) ~MY_KEY_ ## X ##_BIT; \
            puts(#X " key released"); \
        } \
    } while(0)

static gboolean keyrelease_handler(guint keyval, gpointer data)
{
	simple_image_displayer_t* const psd = data;
	if (psd == NULL) return FALSE;

	switch (keyval) {
	case GDK_KEY_Up:
		joypad_key_released(&(gb.pad), UP_KEY);
		return TRUE;

	case GDK_KEY_Down:
		joypad_key_released(&(gb.pad), DOWN_KEY);
		return TRUE;

	case GDK_KEY_Right:
		joypad_key_released(&(gb.pad), RIGHT_KEY);
		return TRUE;

	case GDK_KEY_Left:
		joypad_key_released(&(gb.pad), LEFT_KEY);
		return TRUE;

	case 'A':
	case 'a':
		joypad_key_released(&(gb.pad), A_KEY);
		return TRUE;

	case 'S':
	case 's':
		joypad_key_released(&(gb.pad), B_KEY);
		return TRUE;

	case GDK_KEY_Page_Up:
		joypad_key_released(&(gb.pad), SELECT_KEY);
		return TRUE;

	case GDK_KEY_Page_Down:
		joypad_key_released(&(gb.pad), START_KEY);
		return TRUE;
	}

	return FALSE;
}
#undef do_key

// ======================================================================
int main(int argc, char *argv[])
{
	int err;
	err = gettimeofday(&start, NULL);
	if (err != ERR_NONE) {
		return err;
	}
	err = timerclear(&paused);
	if (err != ERR_NONE) {
		return err;
	}
	if (argc < 2) {
		error(argv[0], "please provide input_file");
		return 1;
	}

	const char* const filename = argv[1];

	memset(&gb, 0, sizeof(gb));

	err = gameboy_create(&gb, filename);
	if (err != ERR_NONE) {
		gameboy_free(&gb);
		return err;
	}

	sd_launch(&argc, &argv,
		sd_init("gameboy simulator", LCD_WIDTH * SCREEN_SCALE, LCD_HEIGHT * SCREEN_SCALE, 40,
			generate_image, keypress_handler, keyrelease_handler));



	gameboy_free(&gb);


	return err;
}
