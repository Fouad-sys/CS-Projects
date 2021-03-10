library ieee;
use ieee.std_logic_1164.all;

entity multiplexer is
    port(
        i0  : in  std_logic_vector(31 downto 0);
        i1  : in  std_logic_vector(31 downto 0);
        i2  : in  std_logic_vector(31 downto 0);
        i3  : in  std_logic_vector(31 downto 0);
        sel : in  std_logic_vector(1 downto 0);
        o   : out std_logic_vector(31 downto 0)
    );
end multiplexer;

architecture synth of multiplexer is
    signal s_o : std_logic_vector(31 downto 0);
begin
    o <= s_o;
    
    multi_proc : process(i0, i1, i2, i3, sel)
    begin
        case sel is 
            when "00" => s_o <= i0;
            when "01" => s_o <= i1;
            when "10" => s_o <= i2;
            when others => s_o <= i3;
        end case;
    end process;
end synth;