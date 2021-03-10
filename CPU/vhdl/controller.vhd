library ieee;
use ieee.std_logic_1164.all;

entity controller is
    port(
        clk        : in  std_logic;
        reset_n    : in  std_logic;
        -- instruction opcode
        op       : in  std_logic_vector(5 downto 0);
        opx        : in  std_logic_vector(5 downto 0);
        -- activates branch condition
        branch_op  : out std_logic;
        -- immediate value sign extention
        imm_signed : out std_logic;
        -- instruction register enable
        ir_en      : out std_logic;
        -- PC control signals
        pc_add_imm : out std_logic;
        pc_en      : out std_logic;
        pc_sel_a   : out std_logic;
        pc_sel_imm : out std_logic;
        -- register file enable
        rf_wren    : out std_logic;
        -- multiplexers selections
        sel_addr   : out std_logic;
        sel_b      : out std_logic;
        sel_mem    : out std_logic;
        sel_pc     : out std_logic;
        sel_ra     : out std_logic;
        sel_rC     : out std_logic;
        -- write memory output
        read       : out std_logic;
        write      : out std_logic;
        -- alus_op
        op_alu     : out std_logic_vector(5 downto 0)
    );
end controller;

architecture synth of controller is

    type state is (FETCH1, FETCH2, DECODE, R_OP, STORE, BREAK, LOAD1, LOAD2, I_OP, BRANCH, CALL, CALLR, JMP, JMPI, I_OP_UNSIGNED, R_OP_IMM);
    signal s_current_state, s_next_state : state;
    signal s_op, s_opx : std_logic_vector(7 downto 0); -- fixes Length of expected is 6; length of actual is 8

begin

    s_op <= "00" & op;
    s_opx <= "00" & opx;

    dff : process(clk, reset_n)
    begin
        if reset_n = '0' then s_current_state <= FETCH1;
        elsif rising_edge(clk) then s_current_state <= s_next_state;
        end if;
    end process;

    transition_logic : process(s_current_state, s_op, s_opx)
    begin
        branch_op  <= '0'; 
        imm_signed <= '0'; 
        ir_en      <= '0'; 
        pc_add_imm <= '0'; 
        pc_en      <= '0'; 
        pc_sel_a   <= '0'; 
        pc_sel_imm <= '0'; 
        rf_wren    <= '0'; 
        sel_addr   <= '0'; 
        sel_b      <= '0'; 
        sel_mem    <= '0'; 
        sel_pc     <= '0'; 
        sel_ra     <= '0'; 
        sel_rC     <= '0'; 
        read       <= '0'; 
        write      <= '0'; 
    
    case s_current_state is

        when FETCH1 =>
            read <= '1';
            s_next_state <= FETCH2;

        when FETCH2 => 
            pc_en <= '1';
            ir_en <= '1';
            s_next_state <= DECODE;

        when DECODE =>
            case s_op is

                when x"3A" =>
                    case s_opx is

                        when x"34" => 
                            s_next_state <= BREAK;

                        when x"1D" =>
                            s_next_state <= CALLR;
                        
                        when x"0D" | x"05" =>
                            s_next_state <= JMP;

                        when x"12" | x"1A" | x"3A" | x"02" =>
                            s_next_state <= R_OP_IMM;

                        when others =>
                            s_next_state <= R_OP;
                    end case;
                
                when x"04" | x"08" | x"10" | x"18" | x"20" =>
                    s_next_state <= I_OP;

                when x"17" =>
                    s_next_state <= LOAD1;

                when x"15" =>
                    s_next_state <= STORE;

                when x"06" | x"0E" | x"16" | x"1E" | x"26" | x"2E" | x"36" =>
                    s_next_state <= BRANCH;

                when x"00" => 
                    s_next_state <= CALL;
                
                when x"01" =>
                    s_next_state <= JMPI;
                
                when x"0C" | x"14" | x"1C" | x"28" | x"30" =>
                    s_next_state <= I_OP_UNSIGNED;

                when others => -- fixes Case statement choices cover only 22 out of 43046721 cases.
                
            end case;
        
        when R_OP =>
            rf_wren <= '1';
            sel_b <= '1';
            sel_rC <= '1';
            s_next_state <= FETCH1;

        when STORE =>
            sel_addr <= '1';
            write <= '1';
            --removed sel_b
            imm_signed <= '1';
            s_next_state <= FETCH1;

        when BREAK => 
            s_next_state <= BREAK;
        
        when LOAD1 =>
            sel_addr <= '1';
            read <= '1';
            imm_signed <= '1';
            s_next_state <= LOAD2;
        
        when LOAD2 =>
            rf_wren <= '1';
            sel_mem <= '1';
            s_next_state <= FETCH1;

        when I_OP =>
            rf_wren <= '1';
            imm_signed <= '1';
            s_next_state <= FETCH1;

        when BRANCH =>
            branch_op <= '1';
            sel_b <= '1';
            pc_add_imm <= '1';
            s_next_state <= FETCH1;
        
        when CALL =>
            rf_wren <= '1';
            pc_en <= '1';
            pc_sel_imm <= '1';
            sel_pc <= '1';
            sel_ra <= '1';
            s_next_state <= FETCH1;
        
        when CALLR => 
            rf_wren <= '1';
            pc_en <= '1';
            pc_sel_a <= '1';
            sel_pc <= '1';
            sel_ra <= '1';
            s_next_state <= FETCH1;
        
        when JMP =>
            pc_en <= '1';
            pc_sel_a <= '1';
            s_next_state <= FETCH1;
        
        when JMPI =>
            pc_en <= '1';
            pc_sel_imm <= '1';
            s_next_state <= FETCH1;

        when I_OP_UNSIGNED =>
            rf_wren <= '1';
            s_next_state <= FETCH1;
        
        when R_OP_IMM =>
            pc_sel_imm <= '1';
            sel_rC <= '1';
            rf_wren <= '1';
            s_next_state <= FETCH1;  
    
        end case;

    end process;

    op_alu_process : process (s_op, s_opx)
    begin 

        op_alu(2 downto 0) <= s_op(5 downto 3); -- for I-type

        case s_op is 
            
            when x"3A" =>
                op_alu(2 downto 0) <= s_opx(5 downto 3); -- for R-type

                case s_opx is 

                    when x"31" =>
                        op_alu(5 downto 3) <= "000"; --ADD 
                    
                    when x"39" =>
                        op_alu(5 downto 3) <= "001"; --SUB
                    
                    when x"08" | x"10" | x"18" | x"20" | x"28" | x"30" =>
                        op_alu(5 downto 3) <= "011"; -- <= or >

                    when x"06" | x"0E" | x"16" | x"1E" =>
                        op_alu(5 downto 3) <= "100"; -- logical
                    
                    when x"13" | x"1B" | x"3B" | x"12" | x"1A" | x"3A" | x"03" | x"0B" | x"02" =>
                        op_alu(5 downto 3) <= "110"; -- shift/rotate
                    
                    when others => -- fixes Case statement choices cover only 22 out of 43046721 cases.
                    
                end case;
            
            when x"04" | x"17" | x"15" =>
                op_alu(5 downto 3) <= "000"; -- imm 
            
            when x"06" =>
                op_alu <= "011100"; -- unconditional branch

            when x"0C" | x"14" | x"1C" => -- logical
                op_alu(5 downto 3) <= "100"; 
            
            when x"08" | x"10" | x"18" | x"20" | x"28" | x"30" | x"0E" | x"16" | x"1E" | x"26" | x"2E" | x"36" => -- comparison
                op_alu(5 downto 3) <= "011";
            
            when others => -- fixes Case statement choices cover only 22 out of 43046721 cases.

        end case;
    end process;

end synth;