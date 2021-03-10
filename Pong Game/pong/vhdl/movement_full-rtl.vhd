architecture rtl of movement_full is

COMPONENT direction is
port(change : in std_logic;
     enable : in std_logic;
     reset  : in std_logic;
     clock  : in std_logic;
     dir    : out std_logic);
end COMPONENT;

COMPONENT movement is
generic(WIDTH : natural := 9;
        INIT  : std_logic_vector);
port(dir    : in std_logic;
     enable : in std_logic;
     reset  : in std_logic;
     clock  : in std_logic;
     pos    : out std_logic_vector(WIDTH - 1 downto 0));
end COMPONENT;

signal s_and1, s_and2, s_or, s_dir : std_logic;
signal s_pos : std_logic_vector(WIDTH-1 DOWNTO 0);

begin
   
s_and1 <= s_dir AND s_pos(WIDTH-2);
s_and2 <= s_pos(1) AND (NOT s_dir);
s_or <= ext_change OR s_and1 OR s_and2;

put : direction 
PORT MAP ( change => s_or,
enable => enable,
reset => reset,
clock => clock,
dir => s_dir);

add : movement
generic map (WIDTH => WIDTH, INIT => INIT) 
PORT MAP ( dir => s_dir,
enable => enable,
reset => reset,
clock => clock,
pos => s_pos);
 
dir_o <= s_dir;
pos <= s_pos;

end architecture rtl;
