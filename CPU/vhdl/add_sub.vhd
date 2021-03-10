library IEEE;
use IEEE.std_logic_1164.all;
use ieee.numeric_std.all;

entity add_sub is
    port(
        a        : in  std_logic_vector(31 downto 0);
        b        : in  std_logic_vector(31 downto 0);
        sub_mode : in  std_logic;
        carry    : out std_logic;
        zero     : out std_logic;
        r        : out std_logic_vector(31 downto 0)
    );
end add_sub;

architecture synth of add_sub is

    signal s_carry, s_zero : std_logic;
    signal s_b, s_a, s_r, s_sub_mode: std_logic_vector(32 downto 0);
    constant zero_vector : std_logic_vector(31 downto 0):= (others => '0');

begin

s_a <= '0' & a;
s_b <= '0' & (b xor (31 downto 0 => sub_mode));
s_sub_mode <= zero_vector & sub_mode;
carry <= s_carry;
zero <= s_zero;
s_carry <= s_r(32);
s_r <= std_logic_vector(unsigned(s_a) + unsigned(s_b) + unsigned(s_sub_mode));
r <= s_r(31 downto 0);

zero_proc : process(s_r)
begin
    if(s_r(31 downto 0) = zero_vector) then s_zero <= '1';
    else s_zero <= '0';
    end if;
end process;

end synth;
