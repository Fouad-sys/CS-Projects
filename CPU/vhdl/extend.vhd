library ieee;
use ieee.std_logic_1164.all;

entity extend is
    port(
        imm16  : in  std_logic_vector(15 downto 0);
        signed : in  std_logic;
        imm32  : out std_logic_vector(31 downto 0)
    );
end extend;

architecture synth of extend is
    signal s_imm32 : std_logic_vector(31 downto 0);
begin
    imm32 <= s_imm32;

    sign_proc : process(imm16, signed) 
    begin 
        --s_imm32 <= ((31 downto 16) => signed) & imm16;
        if (signed = '1' and not(imm16(15) = '0')) then 
           s_imm32 <= x"ffff" & imm16;    
        else 
            s_imm32 <= x"0000" & imm16;
        end if;
    end process;
end synth;