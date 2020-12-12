package project;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

public class Loader {
	public static String load(Memory memory,File program) {
		if(program == null) return null;
		ByteBuffer buff = null;
		try (FileInputStream fStream = new FileInputStream(program);
				FileChannel fChan = fStream.getChannel()) {
			long fSize = fChan.size();
			buff = ByteBuffer.allocate((int) fSize);
			fChan.read(buff);
		} catch (FileNotFoundException e) {
			return("File " + program.getName() + " Not Found");
		} catch (IOException e) {
			return("Unexpected IO exception in loading " + program.getName());
		}
		if (buff != null) {
			buff.rewind();
			int codeIndex = 0;
			while(buff.hasRemaining()) {
				byte b = buff.get();
				Encoding enc =null;
				String instrName = Instruction.getInstruction(b).toString();
				 if(Assembler.noArgMnemonics.contains(instrName)){
					 enc = new Encoding(true,b, 0);
				 }
				 else {
					 enc = new Encoding(false,b,buff.getInt());
				 }
				memory.setCode(codeIndex++, enc.asLong());
			}
			return "read " + codeIndex + " instructions";
		}
		return "empty file";
	}
	public static void main(String[] args) {
		System.out.println("Enter the name of the file in the \"pexe\" "
				+ "folder without extension: ");
		try (Scanner keyboard = new Scanner(System.in)) {
			String filename = keyboard.nextLine();
			Memory test = new Memory();
			System.out.println(Loader.load(
					test, new File("pexe/" + filename + ".pexe")));
			System.out.println("Code");
			for(int i = 0; i < test.getProgramSize(); ++i) {
				long lng = test.getCode(i);
				System.out.println(i + " => " + Encoding.opFromLong(lng)
					+ ":" + Encoding.argFromLong(lng));
			}
		}
	}
 
}
