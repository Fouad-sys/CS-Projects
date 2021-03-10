architecture rtl of debouncer is

signal s_dff1, s_dff2, s_dff3, s_button : std_logic;
signal enable_or_reset, and_1, or_1, and_2 : std_logic;

begin

and_1 <= s_dff2 AND (NOT s_dff3);
or_1 <= and_1 OR s_button;
enable_or_reset <= enable OR reset;
and_2 <= or_1 AND (NOT enable_or_reset);
button_o <= s_button;


dff:PROCESS (clock, button, enable, reset) IS 
BEGIN 
IF (rising_edge(clock)) THEN 

s_dff1 <= button;
s_dff2 <= s_dff1;
s_dff3 <= s_dff2;
s_button <= and_2;

END IF; 
END PROCESS dff;


end architecture rtl; 
   
