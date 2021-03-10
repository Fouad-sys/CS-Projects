architecture rtl of movement is

signal s_position : std_logic_vector(WIDTH-1 DOWNTO 0);

begin

pos <= s_position;

dff:PROCESS (clock) IS 
BEGIN 
IF (rising_edge(clock)) THEN 
IF (reset = '1') THEN s_position <= INIT; 

ELSIF (enable = '1') THEN
IF (dir = '1') THEN 
	IF (s_position(WIDTH-1) = '0') then 
	s_position <= s_position(WIDTH-2 DOWNTO 0) & '0';
	END IF;
ELSE
	IF (s_position(0) = '0') then 
	s_position <= '0' & s_position(WIDTH-1 DOWNTO 1);
	END IF;
END IF;

END IF; 
END IF; 
END PROCESS dff;


end architecture rtl;
