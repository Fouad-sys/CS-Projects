architecture rtl of collision is

begin

evaluation : PROCESS (x_dir, y_dir, x_pos, y_pos, bat_pos) IS

BEGIN

IF (x_pos(9)= '1' AND x_dir ='1' AND y_dir = '1' AND NOT ((y_pos AND ('0' & bat_pos(8 DOWNTO 1))) = "000000000")) THEN 
change <= '1';

ELSIF (x_pos(9)= '1' AND x_dir ='1' AND y_dir = '0' AND NOT ((y_pos AND (bat_pos(7 DOWNTO 0) & '0')) = "000000000")) THEN 
change <= '1';

ELSIF (x_pos(11)= '1' AND x_dir ='0' AND y_dir = '0' AND NOT ((y_pos AND (bat_pos(7 DOWNTO 0) & '0')) = "000000000")) THEN
change <= '1';

ELSIF (x_pos(11)= '1' AND x_dir ='0' AND y_dir = '1' AND NOT ((y_pos AND ('0' & bat_pos(8 DOWNTO 1))) = "000000000")) THEN
change <= '1';

ELSE change <='0';

END IF;

END PROCESS evaluation;

end architecture rtl;


