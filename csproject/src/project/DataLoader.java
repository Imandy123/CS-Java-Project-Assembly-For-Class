package project;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class DataLoader {
	public static String load(Memory memory, File programData) {
		if(programData == null) {
			return null;
		}
		int lineNum = 0;
		try (Scanner input = new Scanner(programData)) {
			while(input.hasNextLine()) {
				lineNum = lineNum +1;
				String data = input.nextLine().trim();
				Scanner parts = new Scanner(data);
				memory.setData(parts.nextInt(16), parts.nextInt(16));
				parts.close();
			}
			return "read " + lineNum + "data lines";
		}catch (FileNotFoundException e) {
			return("File " + programData.getName() + " Not Found");
		}catch (Exception e) {
			return("Unexpected exception in loading line " + lineNum
					+ " in " + programData.getName() + " " + e.getMessage());
		}
	}

}
