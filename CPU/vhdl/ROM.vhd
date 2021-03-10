library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity ROM is
    port(
        clk     : in  std_logic;
        cs      : in  std_logic;
        read    : in  std_logic;
        address : in  std_logic_vector(9 downto 0);
        rddata  : out std_logic_vector(31 downto 0)
    );
end ROM;

architecture synth of ROM is

    type rom_type is array(0 to 1023) of std_logic_vector(31 downto 0);
    signal rom : rom_type;
    signal temp_read, temp_cs: std_logic;
    signal s_rddata : std_logic_vector(31 downto 0);
    signal temp_address: std_logic_vector(9 downto 0); 

    component ROM_Block 
	PORT
	(
		address		: IN STD_LOGIC_VECTOR (9 DOWNTO 0);
		clock		: IN STD_LOGIC  := '1';
		q		: OUT STD_LOGIC_VECTOR (31 DOWNTO 0)
	);
    END component ROM_Block;

begin
    rddata <= s_rddata;

    init : ROM_Block
    port map( 
        address => address,
        clock => clk,
        q => s_rddata
    );

    read_proc: process(temp_cs, temp_read, temp_address)
    begin 
        s_rddata <= (others => 'Z');
        if (temp_cs = '1') and (temp_read = '1') then 
            s_rddata <= rom(to_integer(unsigned(temp_address)));
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

end synth;
