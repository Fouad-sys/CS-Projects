library ieee;
use ieee.std_logic_1164.all;

entity mux2x32 is
    port(
        i0  : in  std_logic_vector(31 downto 0);
        i1  : in  std_logic_vector(31 downto 0);
        sel : in  std_logic;
        o   : out std_logic_vector(31 downto 0)
    );
end mux2x32;

architecture synth of mux2x32 is
    signal s_o : std_logic_vector(31 downto 0);
begin
    o <= s_o;
    
    mul_proc: process(sel, i0, i1)
    begin 
        if (sel = '0') then s_o <= i0;
        else s_o <= i1;
        end if;
    end process;
end synth;