package project;

import java.util.Arrays;

public class Memory {
	public static int CODE_SIZE = 256;
	private long code[] = new long[CODE_SIZE];
	private int programSize = 0;
	public static int DATA_SIZE = 512;
	private int[] data = new int[DATA_SIZE];
	private int changedDataIndex = -1;
	public long getCode(int index) {
		try {
			if(index<=0 || index == CODE_SIZE) {
				throw new CodeAccessException("illegal index " + index);
			}
		}
		catch (CodeAccessException excpt) {
			System.out.println(excpt.getMessage());
			
		}
		return code[index];
	}
	public void setCode(int index, long value) {
		try {
			if(index <0 || index >= CODE_SIZE) {
				throw new CodeAccessException("illegal index " + index);
			}
		}
		catch(CodeAccessException excpt) {
			System.out.println(excpt.getMessage());
		}
		code[index] = value;
		programSize = Math.max(programSize,index+1);
	}
	public long[] getCodeRange(int min, int max) {
		try {
			if(min<0 || max >= CODE_SIZE || min>max) {
				throw new CodeAccessException("illegal indices " + min + " " + max);
			}
		}
		catch (CodeAccessException excpt) {
			System.out.println(excpt.getMessage());
		}
		return Arrays.copyOfRange(code,min,max);
	}
	public long[] getCodeArray() {
		return code;
	}
	public int getProgramSize() {
		return programSize;
	}
	public void setProgramSize(int programSize) {
		this.programSize = programSize;
	}
	public void clearCode() {
		for(int i=0; i<CODE_SIZE-1; i++) {
			code[i] = 0;
		}
		programSize = 0 ;
	}
	public int getData(int index){
		if (index<0 || index >=DATA_SIZE) {
			throw new DataAccessException("illegal index " + index);
			
		}
		return data[index];
	}
	public void setData(int index, int value) {
		if (index<0 || index >=DATA_SIZE) {
			new DataAccessException("illegal index " + index);
		}
		data[index] = value;
		changedDataIndex = index;
	}
	public int[] getDataRange(int min, int max) {
		if(min<0 || max>=DATA_SIZE) {
			throw new DataAccessException("illegal indices " + min + " " + max);
			
		}	
		return Arrays.copyOfRange(data, min, max);
	}
	public int[] getDataArray() {
		return data;
	}
	public int getChangedDataIndex() {
		return changedDataIndex;
	}
	public void clearData() {
		for(int i = 0; i<data.length; i++) {
			data[i] = 0;
		}
		changedDataIndex = -1;
	}
	
	
	
}
