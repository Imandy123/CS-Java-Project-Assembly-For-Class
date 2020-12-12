package project;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InstructionTester {
	Memory memory = new Memory();
	Processor proc = new Processor();

	int[] memoryCopy = new int[Memory.DATA_SIZE];
	int accInit;
	int ipInit;

	@BeforeEach
	public void setup() {
		for (int i = 0; i < Memory.DATA_SIZE; i++) {
			memoryCopy[i] = -5*Memory.DATA_SIZE + 10*i;
			memory.setData(i, -5*Memory.DATA_SIZE + 10*i);
			// Initially the memory will contain a known spread
			// of different numbers: 
			// -10240, -10230, -10220, ..., 0, 10, 20, ..., 10230 
			// This allows us to check that the Instructions do 
			// not corrupt memory unexpectedly.
			// 0 is at index 1024
		}
		Instruction.setMemory(memory);
		Instruction.setProcessor(proc);
		Instruction.setHalt(() -> System.exit(0));

		accInit = 30;
		ipInit = 30;
		proc.setAcc(accInit);
		proc.setIP(ipInit);
	}

	@Test
	public void testNOP(){
		Instruction instr = Instruction.NOP;
		instr.execute(0);
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator untouched
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator unchanged")
				);
	}

	@Test 
	// Check NOT greater than 0 gives false
	// there is no argument and mode is null
	public void testNOTaccGT0() {
		Instruction instr = Instruction.NOT;
		proc.setAcc(1);
		instr.execute(0); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check NOT equal to 0 gives true
	// there is no argument and mode is null
	public void testNOTaccEQ0() {
		Instruction instr = Instruction.NOT;
		proc.setAcc(0);
		instr.execute(0); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check NOT less than 0 gives false
	// there is no argument and mode is null
	public void testNOTaccLT0() {
		Instruction instr = Instruction.NOT;
		proc.setAcc(-1);
		instr.execute(0); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test
	// Test whether load is correct with immediate addressing
	public void testLODimmediate(){
		Instruction instr = Instruction.LOD_IMM;
		proc.setAcc(27);
		int arg = 12;
		// should load 12 into the accumulator
		instr.execute(arg);
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(12, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test
	// Test whether load is correct with direct addressing
	public void testLODdirect(){
		Instruction instr = Instruction.LOD_DIR;
		proc.setAcc(27);
		int arg = 283; // data[283] = 270 
		// 
		instr.execute(arg);
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(270, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test
	// Test whether load is correct with direct addressing
	public void testLODindirect() {
		Instruction instr = Instruction.LOD_IND;
		proc.setAcc(-15);
		int arg = 283; // data[283] = 270, data[270] = 140 
		instr.execute(arg);
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> {
					assertEquals(140, proc.getAcc(), "Accumulator modified");
				}
				);
	}	

	@Test
	// Test whether store is correct with direct addressing
	public void testSTOdirect() {
		Instruction instr = Instruction.STO_DIR;
		int arg = 265; // data[265] = 90 
		proc.setAcc(567);
		memoryCopy[265] = 567;
		instr.execute(arg);
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(567, proc.getAcc(), "Accumulator unchanged")
				);
	}

	@Test
	// Test whether store is correct with indirect addressing
	public void testSTOindirect() {
		Instruction instr = Instruction.STO_IND;
		int arg = 270; // data[270] = 140 
		proc.setAcc(567);
		memoryCopy[140] = 567;
		instr.execute(arg);
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(567, proc.getAcc(), "Accumulator unchanged")
				);
	}

	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is immediate
	public void testADDimmediate() {
		Instruction instr = Instruction.ADD_IMM;
		int arg = 12; 
		proc.setAcc(200);
		instr.execute(arg); 
		// should have added 12 to accumulator
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(200+12, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is direct
	public void testADDdirect() {
		Instruction instr = Instruction.ADD_DIR;
		int arg = 250; // data[250] = -60 
		proc.setAcc(250);
		// should add memoryCopy[offsetinit+12] to the accumulator
		instr.execute(arg); 
		// should have added 12 to accumulator
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250-60, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the add is done correctly, when
	// addressing is indirect
	public void testADDindirect() {
		Instruction instr = Instruction.ADD_IND;
		int arg = 270; // data[270] = 140, data[140] = -1160
		proc.setAcc(250);
		// if offset1 = memoryCopy[offsetinit+1028-160] = memoryCopy[1068] = 10*(68-24) = 440
		// should add memoryCopy[offsetinit+offset1] = memoryCopy[640] = 6400-10240 to the accumulator	
		// -3840
		instr.execute(arg); 
		// should have added 12 to accumulator
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250-1160, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the subtraction is done correctly, when
	// addressing is immediate
	public void testSUBimmediate() {
		Instruction instr = Instruction.SUB_IMM;
		int arg = 12; 
		proc.setAcc(200);
		instr.execute(arg); 
		// should have subtracted 12 from accumulator
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(200-12, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the subtraction is done correctly, when
	// addressing is direct
	public void testSUBdirect() {
		Instruction instr = Instruction.SUB_DIR;
		int arg = 260; // data[260] = 40
		proc.setAcc(250);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250 - 40, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the subtraction is done correctly, when
	// addressing is indirect
	public void testSUBindirect() {
		Instruction instr = Instruction.SUB_IND;
		int arg = 270; // data[270] = 140, data[140] = -1160
		proc.setAcc(250);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250+1160, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is immediate
	public void testMULimmediate() {
		Instruction instr = Instruction.MUL_IMM;
		int arg = 12; 
		proc.setAcc(200);
		instr.execute(arg); 
		// should have multiplied the accumulator by 12
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(200*12, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is direct
	public void testMULdirect() {
		Instruction instr = Instruction.MUL_DIR;
		int arg = 200; // data[200] = -560
		proc.setAcc(250);
		// should multiply the accumulator by memoryCopy[offsetinit+12] = memoryCopy[212] = -10240 + 2120
		instr.execute(arg); 
		// should have added 12 to accumulator
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250*-560, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the multiplication is done correctly, when
	// addressing is indirect
	public void testMULindirect() {
		Instruction instr = Instruction.MUL_IND;
		int arg = 270; // data[270] = 140, data[140] = -1160
		proc.setAcc(250);
		// if offset1 = memoryCopy[offsetinit+1028-160] = memoryCopy[1068] = 10*(68-24) = 440
		// should multiply the accumulator by memoryCopy[offsetinit+offset1] = memoryCopy[640] = 6400-10240	
		// -3840
		instr.execute(arg); 
		// should have added 12 to accumulator
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(250*(-1160), proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is immediate
	public void testDIVimmediate() {
		Instruction instr = Instruction.DIV_IMM;
		int arg = 12; 
		proc.setAcc(200);
		instr.execute(arg); 
		// should have multiplied the accumulator by 12
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(200/12, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is direct
	public void testDIVdirect() {
		Instruction instr = Instruction.DIV_DIR;
		int arg = 200; // data[200] = -560
		proc.setAcc(1024011);
		// should divide the accumulator by memoryCopy[offsetinit+12] = memoryCopy[212] = -10240 + 2120
		instr.execute(arg); 
		// should have added 12 to accumulator
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1024011/-560, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// this test checks whether the division is done correctly, when
	// addressing is indirect
	public void testDIVindirect() {
		Instruction instr = Instruction.DIV_IND;
		int arg = 271; // data[271] = 150, data[150] = -1060
		proc.setAcc(400000);
		instr.execute(arg); // divide by -1060 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(400000/-1060, proc.getAcc(), "Accumulator modified")
				);
	}

	@Test 
	// Test whether DIV throws divide by zero exception with immediate addressing mode
	public void testDIVzerodivisionImmed() {
		Instruction instr = Instruction.DIV_IMM;
		Throwable exception = assertThrows(DivideByZeroException.class,
				() -> instr.execute(0));		
		assertEquals("Division by zero", exception.getMessage());

	}

	@Test 
	// Test whether DIV throws divide by zero exception with direct addressing mode
	public void testDIVzerodivisionDirect() {
		Instruction instr = Instruction.DIV_DIR;
		Throwable exception = assertThrows(DivideByZeroException.class,
				() -> instr.execute(256));		
		assertEquals("Division by zero", exception.getMessage());

	}

	@Test 
	// Test whether DIV throws divide by zero exception with indirect addressing mode
	public void testDIVzerodivisionIndirect() {
		Instruction instr = Instruction.DIV_IND;
		memory.setData(100, 256); // data[256] = 0
		Throwable exception = assertThrows(DivideByZeroException.class,
				() -> instr.execute(100));		
		assertEquals("Division by zero", exception.getMessage());

	}

	@Test 
	// Check AND when accum and arg equal to 0 gives false
	// addressing is immediate
	public void testANDimmediateAccEQ0argEQ0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = 0;
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum and arg equal to 0 gives false
	// addressing is immediate
	public void testANDimmediateAccLT0argEQ0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = 0;
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum > 0 and arg equal to 0 gives false
	// addressing is immediate
	public void testANDimmediateAccGT0argEQ0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = 0;
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum = 0 and arg < 0 gives false
	// addressing is immediate
	public void testANDimmediateAccEQ0argLT0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = -1;
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 and arg < 0 gives true
	// addressing is immediate
	public void testANDimmediateAccLT0argLT0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = -1;
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum = 0 and arg > 0 gives false
	// addressing is immediate
	public void testANDimmediateAccEQ0argGT0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = 1;
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum > 0 and arg > 0 gives true
	// addressing is immediate
	public void testANDimmediateAccGT0argGT0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = 0;
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 and arg > 0 gives true
	// addressing is immediate
	public void testANDimmediateAccLT0argGT0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = 1;
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 and arg > 0 gives true
	// addressing is immediate
	public void testANDimmediateAccGT0argLT0() {
		Instruction instr = Instruction.AND_IMM;
		int arg = -1;
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum direct mem equal to 0 gives false
	// addressing is direct
	public void testANDdirectAccEQ0memEQ0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 256; // data[256] = 0 
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 direct mem equal to 0 gives false
	// addressing is direct
	public void testANDdirectAccLT0memEQ0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 256; // data[256] = 0; 
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test
	// Check AND when accum > 0 direct mem equal to 0 gives false
	// addressing is direct
	public void testANDdirectAccGT0memEQ0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 256; // data[256] = 0 
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum = 0 direct mem < 0 gives false
	// addressing is direct
	public void testANDdirectAccEQ0memLT0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 100; // data[100] = -1560
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 direct mem < 0 gives true
	// addressing is direct
	public void testANDdirectAccLT0memLT0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 100; // data[100] = -1560
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 direct mem < 0 gives true
	// addressing is direct
	public void testANDdirectAccGT0memLT0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 100; // data[100] = -1560
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum = 0 direct mem > 0 gives false
	// addressing is direct
	public void testANDdirectAccEQ0memGT0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 257; // data [256] = 10
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 direct mem > 0 gives true
	// addressing is direct
	public void testANDdirectAccLT0memGT0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 257; // data [256] = 10
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 direct mem > 0 gives true
	// addressing is direct
	public void testANDdirectAccGT0memGT0() {
		Instruction instr = Instruction.AND_DIR;
		int arg = 257; // data [256] = 10
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum indirect mem equal to 0 gives false
	// addressing is indirect
	public void testANDindirectAccEQ0memEQ0() {
		Instruction instr = Instruction.AND_IND;
		memory.setData(281, 256);
		memoryCopy[281] = 256;
		int arg = 281; // data[281] = 256, data[256] = 0
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 indirect mem equal to 0 gives false
	// addressing is indirect
	public void testANDindirectAccLT0memEQ0() {
		Instruction instr = Instruction.AND_IND;
		memory.setData(281, 256);
		memoryCopy[281] = 256;
		int arg = 281; // data[281] = 256, data[256] = 0
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum > 0 indirect mem equal to 0 gives false
	// addressing is indirect
	public void testANDindirectAccGT0memEQ0() {
		Instruction instr = Instruction.AND_IND;
		memory.setData(281, 256);
		memoryCopy[281] = 256;
		int arg = 281; // data[281] = 256, data[256] = 0
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum = 0 indirect mem < 0 gives false
	// addressing is indirect
	public void testANDindirectAccEQ0memLT0() {
		Instruction instr = Instruction.AND_IND;
		int arg = 281; // data[281] = 250, data [250] = -60
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 indirect mem < 0 gives true
	// addressing is indirect
	public void testANDindirectAccLT0memLT0() {
		Instruction instr = Instruction.AND_IND;
		int arg = 281; // data[281] = 250, data [250] = -60
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 indirect mem < 0 gives true
	// addressing is indirect
	public void testANDindirectAccGT0memLT0() {
		Instruction instr = Instruction.AND_IND;
		int arg = 281; // data[281] = 250, data [250] = -60
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum = 0 indirect mem > 0 gives false
	// addressing is indirect
	public void testANDindirectAccEQ0memGT0() {
		Instruction instr = Instruction.AND_IND;
		int arg = 282; // data[282] = 260, data [260] = 40
		proc.setAcc(0);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check AND when accum < 0 indirect mem > 0 gives true
	// addressing is indirect
	public void testANDindirectAccLT0memGT0() {
		Instruction instr = Instruction.AND_IND;
		int arg = 282; // data[282] = 260, data [260] = 40
		proc.setAcc(-1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check AND when accum > 0 indirect mem > 0 gives true
	// addressing is indirect
	public void testANDindirectAccGT0memGT0() {
		Instruction instr = Instruction.AND_IND;
		int arg = 282; // data[282] = 260, data [260] = 40
		proc.setAcc(1);
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check CMPL when comparing less than 0 gives true
	// addressing is direct
	public void testCMPLdirectMemLT0() {
		Instruction instr = Instruction.CMPL_DIR;
		int arg = 100; // data[100] = -1560
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check CMPL when comparing grater than 0 gives false
	// addressing is direct
	public void testCMPLdirectMemGT0() {
		Instruction instr = Instruction.CMPL_DIR;
		int arg = 300; // data[300] = 440
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPL when comparing equal to 0 gives false
	// addressing is direct
	public void testCMPLdirectMemEQ0() {
		Instruction instr = Instruction.CMPL_DIR;
		int arg = 256; // data[256] = 0
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPL when comparing less than 0 gives true
	// addressing is indirect
	public void testCMPLindirectMemLT0() {
		Instruction instr = Instruction.CMPL_IND;
		int arg = 280; // data[280] = 240, data[240] = -160
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check CMPL when comparing greater than 0 gives false
	// addressing is indirect
	public void testCMPLindirectMemGT0() {
		Instruction instr = Instruction.CMPL_IND;
		int arg = 282; // data[282] = 260, data[260] = 40
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPL when comparing equal to 0 gives false
	// addressing is indirect
	public void testCMPLindirectMemEQ0() {
		Instruction instr = Instruction.CMPL_IND;
		memory.setData(200, 256);
		memoryCopy[200] = 256;
		int arg = 200; // data[200] = 256, data[256] = 0
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing less than 0 gives false
	// addressing is direct
	public void testCMPZdirectMemLT0() {
		Instruction instr = Instruction.CMPZ_DIR;
		int arg = 100; // data[100] = -1560
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing grater than 0 gives false
	// addressing is direct
	public void testCMPZdirectMemGT0() {
		Instruction instr = Instruction.CMPZ_DIR;
		int arg = 300; // data[300] = 440
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing equal to 0 gives true
	// addressing is direct
	public void testCMPZdirectMemEQ0() {
		Instruction instr = Instruction.CMPZ_DIR;
		int arg = 256; // data[256] = 0
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// Check CMPL when comparing less than 0 gives false
	// addressing is indirect
	public void testCMPZindirectMemLT0() {
		Instruction instr = Instruction.CMPZ_IND;
		int arg = 280; // data[280] = 240, data[240] = -160
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing greater than 0 gives false
	// addressing is indirect
	public void testCMPZindirectMemGT0() {
		Instruction instr = Instruction.CMPZ_IND;
		int arg = 282; // data[282] = 260, data[260] = 40
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator 0")
				);
	}

	@Test 
	// Check CMPZ when comparing equal to 0 gives true
	// addressing is indirect
	public void testCMPZindirectMemEQ0() {
		Instruction instr = Instruction.CMPZ_IND;
		memory.setData(200, 256);
		memoryCopy[200] = 256;
		int arg = 200; // data[200] = 256, data[256] = 0
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(1, proc.getAcc(), "Accumulator 1")
				);
	}

	@Test 
	// this test checks whether the relative JUMP is done correctly, when
	// addressing is immediate
	public void testJUMPimmediate() {
		Instruction instr = Instruction.JUMP_IMM;
		int arg = 260; 
		instr.execute(arg); 
		// should set the instruction pointer to 260
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer modified
				() -> assertEquals(260 + ipInit, proc.getIP(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JUMP is done correctly, when
	// addressing is direct
	public void testJUMPdirect() {
		Instruction instr = Instruction.JUMP_DIR;
		int arg = 254; // data[254] = -20   
		instr.execute(arg); 
		// should set the instruction pointer to 400
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer modified
				() -> assertEquals(ipInit - 20, proc.getIP(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JUMP is done correctly, when
	// addressing is indirect
	public void testJUMPindirect() {
		Instruction instr = Instruction.JUMP_IND;
		int arg = 282; // data[282] = 260, data[260] = 40    
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer modified
				() -> assertEquals(40 + ipInit, proc.getIP(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the non-relative JUMP is done correctly, when
	// addressing is not relative to current instruction pointer
	public void testJUMPabsolute() {
		Instruction instr = Instruction.JUMP_ABS;
		int arg = 260;  // data[260] = 40
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer modified
				() -> assertEquals(40, proc.getIP(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ is done like JUMP when accumulator is 0
	// addressing is immediate
	public void testJMPZimmediateAccEQ0() {
		Instruction instr = Instruction.JMPZ_IMM;
		proc.setAcc(0);
		int arg = 260;  
		instr.execute(arg); 
		// should set the instruction pointer to 260
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer modified
				() -> assertEquals(260 + ipInit, proc.getIP(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ is done like JUMP when accumulator is 0
	// addressing is direct
	public void testJMPZdirectAccEQ0() {
		Instruction instr = Instruction.JMPZ_DIR;
		proc.setAcc(0);
		int arg = 300; // data[300] =  440 
		instr.execute(arg); 
		// should set the instruction pointer to 400
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer modified
				() -> assertEquals(440 + ipInit, proc.getIP(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ is done like JUMP when accumulator is 0
	// addressing is indirect
	public void testJMPZindirectAccEQ0() {
		Instruction instr = Instruction.JMPZ_IND;
		proc.setAcc(0);
		int arg = 283; // data[283] = 270, data[270] = 140  
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer modified
				() -> assertEquals(140 + ipInit, proc.getIP(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test
	// this test checks whether the non-relative JMPZ is done like JUMP when accumulator is 0
	// addressing is not relative to current instruction pointer
	public void testJMPZabsoluteAccEQ0() {
		Instruction instr = Instruction.JMPZ_ABS;
		proc.setAcc(0);
		int arg = 260;  // data[260] = 40
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer modified
				() -> assertEquals(40, proc.getIP(), "Instruction pointer modified"),
				//Test accumulator modified
				() -> assertEquals(0, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ only increments instruction pointer
	// addressing is immediate
	public void testJMPZimmediateAccNZ() {
		Instruction instr = Instruction.JMPZ_IMM;
		int arg = 260;  
		instr.execute(arg); 
		// should set the instruction pointer to 260
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ only increments instruction pointer
	// addressing is direct
	public void testJMPZdirectAccNZ() {
		Instruction instr = Instruction.JMPZ_DIR;
		int arg = 1024-160; // the memory value is Memory[offsetinit-160 + 1024] = 400  
		instr.execute(arg); 
		// should set the instruction pointer to 400
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the relative JMPZ only increments instruction pointer
	// addressing is indirect
	public void testJMPZindirectAccNZ() {
		Instruction instr = Instruction.JMPZ_IND;
		int arg = 910;   
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator was not changed")
				);
	}

	@Test 
	// this test checks whether the non-relative JMPZ only increments instruction pointer
	// addressing is not relative to current instruction pointer
	public void testJMPZabsoluteAccNZ() {
		Instruction instr = Instruction.JMPZ_ABS;
		int arg = 100;   
		instr.execute(arg); 
		assertAll (
				//Test memory is not changed
				() -> assertArrayEquals(memoryCopy, memory.getDataArray()),
				//Test instruction pointer incremented
				() -> assertEquals(ipInit+1, proc.getIP(), "Instruction pointer incremented"),
				//Test accumulator modified
				() -> assertEquals(accInit, proc.getAcc(), "Accumulator was not changed")
				);
	}

}

