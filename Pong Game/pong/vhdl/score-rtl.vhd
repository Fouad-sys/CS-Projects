architecture rtl of score is

CONSTANT c_0 : std_logic_vector(7 DOWNTO 0) := "11111100";
CONSTANT c_1 : std_logic_vector(7 DOWNTO 0) := "01100000";
CONSTANT c_2 : std_logic_vector(7 DOWNTO 0) := "11011010";
CONSTANT c_3 : std_logic_vector(7 DOWNTO 0) := "11110010";
CONSTANT c_4 : std_logic_vector(7 DOWNTO 0) := "01100110";
CONSTANT c_5 : std_logic_vector(7 DOWNTO 0) := "10110110";
CONSTANT c_6 : std_logic_vector(7 DOWNTO 0) := "10111110";
CONSTANT c_7 : std_logic_vector(7 DOWNTO 0) := "11100000";
CONSTANT c_8 : std_logic_vector(7 DOWNTO 0) := "11111110";
CONSTANT c_9 : std_logic_vector(7 DOWNTO 0) := "11110110";

signal s_user, s_sys : std_logic_vector (7 DOWNTO 0);
signal s_over : std_logic;

begin

over <= s_over;
sys <= s_sys;
user <= s_user;
s_over <= '1' WHEN s_user = c_9 OR s_sys = c_9 else '0';

dff:PROCESS (clock) IS 
BEGIN 
IF (rising_edge(clock)) THEN 
IF (reset = '1') THEN 
s_sys <= c_0; 
s_user <= c_0;
ELSIF enable = '1' THEN 
IF (x_pos(0) = '1' and s_over='0') THEN

CASE s_user IS
WHEN (c_0) => s_user <= c_1;
WHEN (c_1) => s_user <= c_2;
WHEN (c_2) => s_user <= c_3;
WHEN (c_3) => s_user <= c_4;
WHEN (c_4) => s_user <= c_5;
WHEN (c_5) => s_user <= c_6;
WHEN (c_6) => s_user <= c_7;
WHEN (c_7) => s_user <= c_8;
WHEN OTHERS => s_user <= c_9;
END CASE;

ELSIF (x_pos(11) = '1' and s_over='0') THEN

CASE s_sys IS
WHEN (c_0) => s_sys <= c_1;
WHEN (c_1) => s_sys <= c_2;
WHEN (c_2) => s_sys <= c_3;
WHEN (c_3) => s_sys <= c_4;
WHEN (c_4) => s_sys <= c_5;
WHEN (c_5) => s_sys <= c_6;
WHEN (c_6) => s_sys <= c_7;
WHEN (c_7) => s_sys <= c_8;
WHEN OTHERS => s_sys <= c_9;
END CASE;

END IF;
END IF; 
END IF; 
END PROCESS dff;




end architecture rtl;
