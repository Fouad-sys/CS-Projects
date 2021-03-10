architecture rtl of direction is

signal s_direction : std_logic;

BEGIN 

dir <= s_direction;

dff:PROCESS (clock) IS
BEGIN 
IF (rising_edge(clock)) THEN 
IF (reset = '1') THEN s_direction <= '1'; 
ELSE 
IF (s_direction = '1' and change = '1' and enable = '1') 
THEN s_direction <= '0';
ELSIF (s_direction = '0' and change = '1' and enable = '1')
THEN s_direction <= '1';
END IF;
END IF; 
END IF; 
END PROCESS dff;


end architecture rtl;     
