package project;

public class Processor {
	private int	accumulator;
	private int instructionPointer;
	
	public int getAcc() {
		return accumulator;
	}
	public void setAcc(int accumlator) {
		this.accumulator = accumlator;
	}
	public int getIP() {
		return instructionPointer;
	}
	public void setIP(int instructionPointer) {
		this.instructionPointer = instructionPointer;
	}
	public void incIP() {
		instructionPointer = instructionPointer + 1;
	}
	public void applyNot() {
		if(accumulator == 0) {
			accumulator = 1;
		}
		else {
			accumulator = 0;
		}
	}
}
