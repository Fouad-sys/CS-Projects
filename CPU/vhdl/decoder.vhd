library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity decoder is
    port(
        address : in  std_logic_vector(15 downto 0);
        cs_LEDS : out std_logic;
        cs_RAM  : out std_logic;
        cs_ROM  : out std_logic;
        cs_buttons : out std_logic
    );
end decoder;

architecture synth of decoder is
    signal s_cs_LEDS: std_logic;
    signal s_cs_RAM: std_logic;
    signal s_cs_ROM: std_logic;
    signal s_cs_buttons: std_logic;
begin

    cs_LEDS <= s_cs_LEDS;
    cs_RAM <= s_cs_RAM;
    cs_ROM <= s_cs_ROM;
    cs_buttons <= s_cs_buttons;

    decode: process(address)
        begin
            if (to_integer(unsigned(address)) >= 16#0000# and to_integer(unsigned(address)) <= 16#0FFC#) then
                s_cs_LEDS <= '0';
                s_cs_RAM <= '0';
                s_cs_ROM <= '1';
                s_cs_buttons <= '0';
            elsif (to_integer(unsigned(address)) >= 16#1000# and to_integer(unsigned(address)) <= 16#1FFC#) then
                s_cs_LEDS <= '0';
                s_cs_RAM <= '1';
                s_cs_ROM <= '0';
                s_cs_buttons <= '0';
            elsif (to_integer(unsigned(address)) >= 16#2000# and to_integer(unsigned(address)) <= 16#200C#) then
                s_cs_LEDS <= '1';
                s_cs_RAM <= '0';
                s_cs_ROM <= '0';
                s_cs_buttons <= '0';
            elsif (to_integer(unsigned(address)) >= 16#2030# and to_integer(unsigned(address)) <= 16#2034#) then
                s_cs_LEDS <= '0';
                s_cs_RAM <= '0';
                s_cs_ROM <= '0';
                s_cs_buttons <= '1';
            else 
                s_cs_LEDS <= '0';
                s_cs_RAM <= '0';
                s_cs_ROM <= '0';
                s_cs_buttons <= '0';
            end if;
        end process;

end synth;