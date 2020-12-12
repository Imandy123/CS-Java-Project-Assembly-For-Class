package project;
import java.io.File;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.TreeMap;
public class Assembler {
	public final static Set<String> mnemonics =Set.of("NOP","NOT","HALT","LOD","STO",
			"ADD","SUB","MUL","DIV","AND","CMPL","CMPZ","JUMP","JMPZ");
	public final static Set<String> noArgMnemonics = Set.of("NOP", "NOT", "HALT");
	public final static Set<String> noImmedMnemonics = Set.of("STO", " CMPL", "CMPZ");
	public final static Set<String> jumpMnemonics = Set.of("JUMP", "JMPZ");

	public static boolean blankLineCheck(String str, Set<String> errors, int lineNum){
		if(str.trim().length() == 0) {
			errors.add("Error on line " + lineNum
					+ ": Illegal blank line in the source file");
			return false;
		}
		return true;
	}
	public static boolean noSpaceCheck(String str, Set<String> errors, int lineNum) {
		if(str.length() != str.trim().length()) {
			errors.add("Error on line "+ lineNum
					+ ": Illegal space in the source file");
			return false;
		}
		return true;
	}
	public static boolean lengthOfParts(String str, Set<String>errors, int lineNum) {
		String[] parts = str.split("\\s+");
		if(parts.length!= 1 && parts.length!= 2) {
			errors.add("Error on line " + lineNum
					+ ": Illegal length of parts in the source file");
			return false;
		}
		return true;
	}
	public static boolean illegalmnemonics(String str, Set<String>errors, int lineNum) {
		String[] parts = str.split("\\s+");
		if(!mnemonics.contains(parts[0].toUpperCase())){
			errors.add("error on line " + lineNum
					+ ": Mnemonic doesn't exist");
			return false;
		}
		return true;
	}
	public static boolean doesNotContain0(String str, Set<String>errors, int lineNum) {
		String[] parts = str.split("\\s+");
		if(mnemonics.contains(parts[0].toUpperCase())) {
			if(!mnemonics.contains(parts[0])) {
				errors.add("error on line " + lineNum
						+ ": Mnemonics must be uppercase");
				return false;
			}
		}
		return true;
	}
	public static boolean noArgMnemonicslength1(String str, Set<String>errors, int lineNum) {
		String[] parts = str.split("\\s+");
		if(noArgMnemonics.contains(parts[0])) {
			if(parts.length!=1){
				errors.add("error on line " + lineNum
						+ ": Illegal argument for a no-argument mnemonic");
				return false;
			}
		}
		else {
			return false;
		}
		return true;
	}
	public static boolean LengthOfPartsis2(String[] parts,String str, Set<String>errors, int lineNum) {
		if(parts.length == 2) {
			if(!mnemonics.contains(parts[0]) || noArgMnemonics.contains(parts[0])) {
				errors.add("error on line " + lineNum
						+ ": Illegal arg");
				return false;
			}
			else {
				return true;
			}
		}
		return true;
	}
	public static boolean longcheck (String str, Set<String>errors, int lineNum, int arg,String mode) {
		String[] parts = str.split("\\s+");
		if(parts.length == 2) {
			if(parts[1].startsWith("[[")) {
				if(!parts[1].endsWith("]]")) {
					errors.add("error on line " + lineNum
							+ ": Incomplete argument");
					return false;
				}else {
					parts[1] = parts[1].substring(2, parts[1].length() -2);
					mode = "_IND";
				}
			}else if (parts[1].startsWith("[")) {
				if(!parts[1].endsWith("]")) {
					errors.add("error on line " + lineNum
							+ ": Incomplete argument");
					return false;
				}else {
					parts[1] = parts[1].substring(1, parts[1].length() -1);
					mode = "_DIR";
				}
			}else if(parts[1].startsWith("{")) {
				if(!parts[1].endsWith("}")) {
					errors.add("error on line " + lineNum
							+ ": Incomplete argument");
					return false;
				}else {
					parts[1] = parts[1].substring(1, parts[1].length() -1);
					mode = "_ABS";
				}
			}else {
				mode = "_IMM";
			}
			try {
				arg = Integer.parseInt(parts[1], 16);
			} catch (NumberFormatException e) {
				errors.add("error on line " + lineNum
						+ ": Arg is not a number");
				return false;
			}

		}
		return true;
	}
	public static int assemble(String inputFileName, String outputFileName, StringBuilder error) {
		String[] source = null;
		try (Stream<String> lines = Files.lines(Paths.get(inputFileName))) {
			source = lines.toArray(String[]::new);
		}catch (IOException e) {
			e.printStackTrace();
		}
		if(error == null) {
			throw new IllegalArgumentException
			("Coding error: the error buffer is null");
		}
		Set<String> errors = new TreeSet<>();
		Encoding.resetNumNoArg();
		List<Encoding> output = new ArrayList<>();

		for(int i = 0;i<source.length; i++) {
			String[] parts = source[i].split("\\s+");
			//	System.out.println(parts[0]);
			String mode = "";
			int arg = 0;	
			if(!blankLineCheck(source[i], errors, i+1)) continue;
			if(!noSpaceCheck(source[i],errors,i+1))continue;
			if(!lengthOfParts(source[i],errors,i+1))continue;
			if(!illegalmnemonics(source[i],errors,i+1))continue;
			if(!doesNotContain0(source[i],errors,i+1))continue;
			if(noArgMnemonics.contains(parts[0])) {
				if(parts.length!=1){
					errors.add("error on line " + (i+1)
							+ ": Illegal argument for a no-argument mnemonic");
					continue;
				}
				else {
					output.add(new Encoding(true, (byte)Instruction.valueOf(parts[0]).getOpcode(), 0));
					continue;
				}
			}
			if(mnemonics.contains(parts[0])) {
				if(parts.length == 2) {
					if(parts[1].startsWith("[[")) {
						if(!parts[1].endsWith("]]")) {
							errors.add("error on line " + (i+1)
									+ ": Incomplete argument");
							continue;
						}else {
							parts[1] = parts[1].substring(2, parts[1].length() -2);
							mode = "_IND";
						}
					}else if (parts[1].startsWith("[")) {
						if(!parts[1].endsWith("]")) {
							errors.add("error on line " + (i+1)
									+ ": Incomplete argument");
							continue;
						}else {
							parts[1] = parts[1].substring(1, parts[1].length() -1);
							mode = "_DIR";
						}
					}else if(parts[1].startsWith("{")) {
						if(!parts[1].endsWith("}")) {
							errors.add("error on line " + (i+1)
									+ ": Incomplete argument");
							continue;
						}else {
							parts[1] = parts[1].substring(1, parts[1].length() -1);
							mode = "_ABS";
						}
					}else {
						mode = "_IMM";
					}
					try {
						arg = Integer.parseInt(parts[1], 16);
					} catch (NumberFormatException e) {
						errors.add("error on line " + (i+1)
								+ ": Arg is not a number");
						continue;
					}
				}
				else {
					errors.add("error on line " + (i+1) + ": missing arg");
					continue;
				}

				if(mode.equals("_IMM") && noImmedMnemonics.contains(parts[0])) {
					errors.add("error on line " + (i+1)
							+ ": immediate mode not perm");
					continue;
				}
				if(mode.equals("_ABS") && !jumpMnemonics.contains(parts[0])) {
					errors.add("error on line " + (i+1)
							+ ": Absolute mode not permitted");
					continue;
				}
				Encoding temp = new Encoding(false, (byte)Instruction.valueOf(parts[0] + mode).getOpcode(),arg);
				output.add(temp);
				continue;
			}
		}

		if(errors.size() == 0) {
			int bytesNeeded = Encoding.getNumNoArg() + 
					5*(output.size()-Encoding.getNumNoArg());
			ByteBuffer buff = ByteBuffer.allocate(bytesNeeded);
			output.stream()
			.forEach(instr -> {
				buff.put(instr.getOpcode());
				if(!instr.isNoArg()) {
					buff.putInt(instr.getArg());
				}
			});
			buff.rewind(); // go back to the beginning of the buffer before writing
			boolean append = false;
			try (FileChannel wChannel = new FileOutputStream(
					new File(outputFileName), append).getChannel()){
				wChannel.write(buff);
				wChannel.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Map<Integer, String> map = new TreeMap<>();
			for(String str : errors) {
				Scanner scan = new Scanner(str.substring(14, str.indexOf(':')));
				map.put(scan.nextInt(), str);
			}
			for(int i : map.keySet()) {
				error.append(map.get(i) + "\n");
			}
		}
		return errors.size();
	}
	public static void main(String[] args) {

		StringBuilder error = new StringBuilder(); 
		System.out.println("Enter the name of the file in the \"pasm\" folder without extension: "); 
		try (Scanner keyboard = new Scanner(System.in)) { 
			String filename = keyboard.nextLine(); 
			int i = Assembler.assemble("pasm/" +filename + ".pasm",
					"pexe/" + filename + ".pexe", error); 
			System.out.println("error = " + error);
			System.out.println("result = " + i); 
		}

		//		 StringBuilder error = new StringBuilder(); for(int i = 2; i < 16; ++i) {
		//		 String filename = (i<10?"0":"") + i; Assembler.assemble("pasm/z0" + filename
		//		 + "e.pasm", "pexe/" + filename + ".pexe", error);
		//		 error.append("=====================\n"); } System.out.println(error);

	}
}
