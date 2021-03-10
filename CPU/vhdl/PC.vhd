library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity PC is
    port(
        clk     : in  std_logic;
        reset_n : in  std_logic;
        en      : in  std_logic;
        sel_a   : in  std_logic;
        sel_imm : in  std_logic;
        add_imm : in  std_logic;
        imm     : in  std_logic_vector(15 downto 0);
        a       : in  std_logic_vector(15 downto 0);
        addr    : out std_logic_vector(31 downto 0)
    );
end PC;

architecture synth of PC is
    signal addr_reg : std_logic_vector(15 downto 0);
    constant word_align : std_logic_vector(31 downto 0) := (31 downto 2 => '1', OTHERS => '0');
begin
    addr <= ((31 downto 16 => '0') & addr_reg) and word_align;

    PC_proc : process(clk, reset_n)
    begin 
        if (reset_n = '0') then 
            addr_reg <= (OTHERS => '0');
        elsif (rising_edge(clk)) then 
            if (en = '1') then 
                if (add_imm = '1') then 
                    addr_reg <= std_logic_vector(unsigned(addr_reg) + unsigned(imm));
                elsif (sel_imm = '1') then 
                    addr_reg <= imm(13 downto 0) & "00";
                elsif (sel_a = '1') then 
                    addr_reg <= a;
                else 
                    addr_reg <= std_logic_vector(unsigned(addr_reg) + 4);
                end if;
            end if;
        end if;
    end process;
end synth;