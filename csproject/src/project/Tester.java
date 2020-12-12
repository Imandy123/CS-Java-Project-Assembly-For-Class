package project;
public class Tester {
	public static void main(String[] args) {
		System.out.println("Expected NOP, actual " + Instruction.getText((byte)0, 0));
		System.out.println("Expected NOT, actual " + Instruction.getText((byte)1, 0));
		System.out.println("Expected LOD 14, actual " + Instruction.getText((byte)2, 20));
		System.out.println("Expected LOD [-15], actual " + Instruction.getText((byte)3, -21));
		System.out.println("Expected LOD [[16]], actual " + Instruction.getText((byte)4, 22));
		System.out.println("Expected STO [-17], actual " + Instruction.getText((byte)5, -23));
		System.out.println("Expected STO [[18]], actual " + Instruction.getText((byte)6, 24));
		System.out.println("Expected ADD -19, actual " + Instruction.getText((byte)7, -25));
		System.out.println("Expected ADD [1A], actual " + Instruction.getText((byte)8, 26));
		System.out.println("Expected ADD [[-1B]], actual " + Instruction.getText((byte)9, -27));
		System.out.println("Expected SUB 1C, actual " + Instruction.getText((byte)10, 28));
		System.out.println("Expected SUB [-1D], actual " + Instruction.getText((byte)11, -29));
		System.out.println("Expected SUB [[1E]], actual " + Instruction.getText((byte)12, 30));
		System.out.println("Expected MUL -1F, actual " + Instruction.getText((byte)13, -31));
		System.out.println("Expected MUL [20], actual " + Instruction.getText((byte)14, 32));
		System.out.println("Expected MUL [[-21]], actual " + Instruction.getText((byte)15, -33));
		System.out.println("Expected DIV 22, actual " + Instruction.getText((byte)16, 34));
		System.out.println("Expected DIV [-23], actual " + Instruction.getText((byte)17, -35));
		System.out.println("Expected DIV [[24]], actual " + Instruction.getText((byte)18, 36));
		System.out.println("Expected AND -25, actual " + Instruction.getText((byte)19, -37));
		System.out.println("Expected AND [26], actual " + Instruction.getText((byte)20, 38));
		System.out.println("Expected AND [[-27]], actual " + Instruction.getText((byte)21, -39));
		System.out.println("Expected CMPL [28], actual " + Instruction.getText((byte)22, 40));
		System.out.println("Expected CMPL [[-29]], actual " + Instruction.getText((byte)23, -41));
		System.out.println("Expected CMPZ [2A], actual " + Instruction.getText((byte)24, 42));
		System.out.println("Expected CMPZ [[-2B]], actual " + Instruction.getText((byte)25, -43));
		System.out.println("Expected JUMP 2C, actual " + Instruction.getText((byte)26, 44));
		System.out.println("Expected JUMP [-2D], actual " + Instruction.getText((byte)27, -45));
		System.out.println("Expected JUMP [[2E]], actual " + Instruction.getText((byte)28, 46));
		System.out.println("Expected JUMP {2F}, actual " + Instruction.getText((byte)29, 47));
		System.out.println("Expected JMPZ 30, actual " + Instruction.getText((byte)30, 48));
		System.out.println("Expected JMPZ [-31], actual " + Instruction.getText((byte)31, -49));
		System.out.println("Expected JMPZ [[32]], actual " + Instruction.getText((byte)32, 50));
		System.out.println("Expected JMPZ {33}, actual " + Instruction.getText((byte)33, 51));
		System.out.println("Expected HALT, actual " + Instruction.getText((byte)34, 0));
	}
}
