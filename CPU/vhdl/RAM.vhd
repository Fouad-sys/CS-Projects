library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity RAM is
    port(
        clk     : in  std_logic;
        cs      : in  std_logic;
        read    : in  std_logic;
        write   : in  std_logic;
        address : in  std_logic_vector(9 downto 0);
        wrdata  : in  std_logic_vector(31 downto 0);
        rddata  : out std_logic_vector(31 downto 0));
end RAM;

architecture synth of RAM is
    type ram_type is array(0 to 1023) of std_logic_vector(31 downto 0);
    signal ram: ram_type;
    signal s_rddata: std_logic_vector(31 downto 0); -- removed a )
    signal temp_cs: std_logic;
    signal temp_read: std_logic;
    signal temp_address: std_logic_vector(9 downto 0); -- changed from std_logic to std_logic_vector
begin

    rddata <= s_rddata;

    read_proc: process(temp_cs, temp_read, temp_address)
    begin 
        s_rddata <= (others => 'Z');
        if (temp_cs = '1') and (temp_read = '1') then 
            s_rddata <= ram(to_integer(unsigned(temp_address)));
        end if;
    end process;

    latency: process(clk)
    begin 
        if (rising_edge(clk)) then
            temp_cs <= cs;
            temp_read <= read;
            temp_address <= address;
        end if;
    end process;
            

    write_proc: process(clk)
    begin
        if (rising_edge(clk)) then
            if (cs = '1') then
                if (write = '1') then 
                    ram(to_integer(unsigned(address))) <= wrdata;
                end if;
            end if;
        end if;
    end process;
    
end synth;