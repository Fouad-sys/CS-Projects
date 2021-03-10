architecture rtl of bat is

signal s_bat : std_logic_vector(8 DOWNTO 0);

begin

bat_o <= s_bat;

dff:PROCESS (clock) IS 
BEGIN 
IF (rising_edge(clock)) THEN 
IF (reset = '1') THEN s_bat <= "000111000"; 
ELSIF (enable ='1') THEN 
IF button_up='1' and button_down ='0' and s_bat(8)= '0' THEN
s_bat <= s_bat(7 DOWNTO 0) & '0';
ELSIF button_down='1' and button_up='0' and s_bat(0) = '0' THEN
s_bat<= '0' & s_bat(8 DOWNTO 1);
END IF; 
END IF; 
END IF; 

END PROCESS dff;

end architecture rtl;
