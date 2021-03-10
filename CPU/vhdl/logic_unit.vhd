library ieee;
use ieee.std_logic_1164.all;

entity logic_unit is
    port(
        a  : in  std_logic_vector(31 downto 0);
        b  : in  std_logic_vector(31 downto 0);
        op : in  std_logic_vector(1 downto 0);
        r  : out std_logic_vector(31 downto 0)
    );
end logic_unit;

architecture synth of logic_unit is
    signal s_r : std_logic_vector(31 downto 0);

begin
    r <= s_r;
    
    logic: process(a, b, op)
    begin
        if(op = "00") then s_r <= a nor b;
        elsif(op = "01") then s_r <= a and b;
        elsif(op = "10") then s_r <= a or b;
        else s_r <= a xnor b;
        end if;
    end process; 
end synth;
