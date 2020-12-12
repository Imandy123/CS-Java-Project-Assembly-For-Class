package projectview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import project.Assembler;
import project.DataLoader;
import project.Loader;
import project.Memory;

public class FilesMgr {
	private Director director;
	private Memory memory;
	private JFrame frame;
	private String defaultDir; 
	private String sourceDir; 
	private String dataDir; 
	private String executableDir; 
	private Properties properties = null;
	private File currentlyExecutingFile = null;
	private File currentDataFile = null;

	public FilesMgr(Director director, Memory memory, JFrame frame) {
		this.director = director;
		this.memory = memory;
		this.frame = frame;
		locateDefaultDirectory();
		loadPropertiesFile();
	}

	private void locateDefaultDirectory() {
		//CODE TO DISCOVER THE ECLIPSE DEFAULT DIRECTORY:
		//There will be a property file if the program has been used for a while
		//because it which will store the locations of the pasm and pexe files
		//but we allow the possibility that it does not exist yet.
		File temp = new File("propertyfile.txt");
		if(!temp.exists()) {
			PrintWriter out; // make a file that we will delete later
			try {
				out = new PrintWriter(temp);
				out.close();
				defaultDir = temp.getAbsolutePath();
				temp.delete();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			defaultDir = temp.getAbsolutePath();
		}
		// change to forward slashes, making it platform independent
		defaultDir = defaultDir.replace('\\','/'); // for Windows machines
		int lastSlash = defaultDir.lastIndexOf('/');
		//remove the file name and keep the directory path
		defaultDir  = defaultDir.substring(0, lastSlash + 1);
	}

	void loadPropertiesFile() {
		try { // load properties file "propertyfile.txt", if it exists
			properties = new Properties();
			properties.load(new FileInputStream("propertyfile.txt"));
			sourceDir = properties.getProperty("SourceDirectory");
			executableDir = properties.getProperty("ExecutableDirectory");
			dataDir = properties.getProperty("DataDirectory");
			// CLEAN UP ANY ERRORS IN WHAT IS STORED:
			if (sourceDir == null || sourceDir.length() == 0 
					|| !new File(sourceDir).exists()) {
				sourceDir = defaultDir;
			}
			if (executableDir == null || executableDir.length() == 0 
					|| !new File(executableDir).exists()) {
				executableDir = defaultDir;
			}
			if (dataDir == null || dataDir.length() == 0 
					|| !new File(dataDir).exists()) {
				dataDir = defaultDir;
			}
		} catch (Exception e) {
			// PROPERTIES FILE DID NOT EXIST
			sourceDir = defaultDir;
			dataDir = defaultDir;
			executableDir = defaultDir;
		}
	}

	public void assembleFile() {
		File source = null;
		File outputExe = null;
		JFileChooser chooser = new JFileChooser(sourceDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Pippin Source Files", "pasm");
		chooser.setFileFilter(filter);
		// CODE TO LOAD DESIRED FILE
		int openOK = chooser.showOpenDialog(null);
		if(openOK == JFileChooser.APPROVE_OPTION) {
			source = chooser.getSelectedFile();
		}
		if(source != null && source.exists()) {
			// CODE TO REMEMBER WHICH DIRECTORY HAS THE pexe FILES
			// WHICH WE WILL ALLOW TO BE DIFFERENT
			sourceDir = source.getAbsolutePath();
			sourceDir = sourceDir.replace('\\','/'); // deal with Windows machines
			int lastDot = sourceDir.lastIndexOf('.');
			String outName = sourceDir.substring(0, lastDot + 1) + "pexe";			
			int lastSlash = sourceDir.lastIndexOf('/');
			sourceDir = sourceDir.substring(0, lastSlash + 1);
			outName = outName.substring(lastSlash+1); 
			filter = new FileNameExtensionFilter(
					"Pippin Executable Files", "pexe");
			if(executableDir.equals(defaultDir)) {
				chooser = new JFileChooser(sourceDir);
			} else {
				chooser = new JFileChooser(executableDir);
			}
			chooser.setFileFilter(filter);
			chooser.setSelectedFile(new File(outName));
			int saveOK = chooser.showSaveDialog(null);
			if(saveOK == JFileChooser.APPROVE_OPTION) {
				outputExe = chooser.getSelectedFile();
			}
			if(outputExe != null) {
				executableDir = outputExe.getAbsolutePath();
				executableDir = executableDir.replace('\\','/');
				lastSlash = executableDir.lastIndexOf('/');
				executableDir = executableDir.substring(0, lastSlash + 1);
				try { 
					properties.setProperty("SourceDirectory", sourceDir);
					properties.setProperty("DataDirectory", dataDir);
					properties.setProperty("ExecutableDirectory", executableDir);
					properties.store(new FileOutputStream("propertyfile.txt"), 
							"File locations");
				} catch (Exception e) {
					// Never seen this happen
					JOptionPane.showMessageDialog(
							frame, 
							"Problem with Java.\n" +
									"Error writing properties file",
									"Warning",
									JOptionPane.OK_OPTION);
				}
				StringBuilder errors = new StringBuilder();
				// as soon as you have a FullAssembler compiled, change this to
				// Assembler assembler = new FullAssembler(); 
				// and start testing it
				int errorIndicator = Assembler.assemble(source.getAbsolutePath(), 
						outputExe.getAbsolutePath(), errors);
				if (errorIndicator == 0){
					JOptionPane.showMessageDialog(
							frame, 
							"The source was assembled to an executable",
							"Success",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(
							frame, 
							errors.toString(),
							"Source code error",
							JOptionPane.INFORMATION_MESSAGE);
				}
			} else {// outputExe still null
				JOptionPane.showMessageDialog(
						frame, 
						"The output file has problems.\n" +
								"Cannot assemble the program",
								"Warning",
								JOptionPane.OK_OPTION);
			}
		} else {// source file does not exist
			JOptionPane.showMessageDialog(
					frame, 
					"The source file has problems.\n" +
							"Cannot assemble the program",
							"Warning",
							JOptionPane.OK_OPTION);				
		}
	}

	public void loadCodeFile() {
		JFileChooser chooser = new JFileChooser(executableDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Pippin Executable Files", "pexe");
		chooser.setFileFilter(filter);
		// CODE TO LOAD DESIRED FILE
		int openOK = chooser.showOpenDialog(null);

		if(openOK == JFileChooser.APPROVE_OPTION) {
			currentlyExecutingFile = chooser.getSelectedFile();
		}
		if(openOK == JFileChooser.CANCEL_OPTION) {
			currentlyExecutingFile = null;
		}

		if(currentlyExecutingFile != null && currentlyExecutingFile.exists()) {
			// CODE TO REMEMBER WHICH DIRECTORY HAS THE pexe FILES
			executableDir = currentlyExecutingFile.getAbsolutePath();
			executableDir = executableDir.replace('\\','/');
			int lastSlash = executableDir.lastIndexOf('/');
			executableDir = executableDir.substring(0, lastSlash + 1);
			try { 
				properties.setProperty("SourceDirectory", sourceDir);
				properties.setProperty("DataDirectory", dataDir);
				properties.setProperty("ExecutableDirectory", executableDir);
				properties.store(new FileOutputStream("propertyfile.txt"), 
						"File locations");
			} catch (Exception e) {
				// Never seen this happen
				JOptionPane.showMessageDialog(
						frame, 
						"Problem with Java.\n" +
								"Error writing properties file",
								"Warning",
								JOptionPane.OK_OPTION);
			}			
		}
		if(currentlyExecutingFile != null) {
			finalCodeLoad_Reload();
		} else {
			JOptionPane.showMessageDialog(
					frame,  
					"No file selected.\n" +
							"Cannot load the program",
							"Warning",
							JOptionPane.OK_OPTION);
		}
	}

	public void loadDataFile() {
		JFileChooser chooser = new JFileChooser(dataDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Pippin Data Files", "pdata");
		chooser.setFileFilter(filter);
		// CODE TO LOAD DESIRED FILE
		int openOK = chooser.showOpenDialog(null);

		if(openOK == JFileChooser.APPROVE_OPTION) {
			currentDataFile = chooser.getSelectedFile();
		}
		if(openOK == JFileChooser.CANCEL_OPTION) {
			currentDataFile = null;
		}

		if(currentDataFile != null && currentDataFile.exists()) {
			// CODE TO REMEMBER WHICH DIRECTORY HAS THE pexe FILES
			dataDir = currentDataFile.getAbsolutePath();
			dataDir = dataDir.replace('\\','/');
			int lastSlash = dataDir.lastIndexOf('/');
			dataDir = dataDir.substring(0, lastSlash + 1);
			try { 
				properties.setProperty("SourceDirectory", sourceDir);
				properties.setProperty("DataDirectory", dataDir);
				properties.setProperty("ExecutableDirectory", executableDir);
				properties.store(new FileOutputStream("propertyfile.txt"), 
						"File locations");
			} catch (Exception e) {
				// Never seen this happen
				JOptionPane.showMessageDialog(
						frame, 
						"Problem with Java.\n" +
								"Error writing properties file",
								"Warning",
								JOptionPane.OK_OPTION);
			}			
		}
		if(currentDataFile != null) {			
			finalDataLoad_Reload();
		} else {
			JOptionPane.showMessageDialog(
					frame,  
					"No file selected.\n" +
							"Cannot load the program",
							"Warning",
							JOptionPane.OK_OPTION);
		}
	}

	void finalCodeLoad_Reload() {
		director.clear();
		String str = Loader.load(memory, currentlyExecutingFile);
		if(!str.startsWith("read")) {
			JOptionPane.showMessageDialog(
					frame,  
					"The file being selected has problems.\n" +
							str + "\n" +
							"Cannot load the program",
							"Warning",
							JOptionPane.OK_OPTION);			
		} else {
			Scanner scan = new Scanner(str);
			scan.next(); // read "read"
			int len = scan.nextInt();
			// ignore "instructions"
			memory.setProgramSize(len);
			director.makeReady("Load Code");
			scan.close();
		}
	}

	void finalDataLoad_Reload() {
		if(currentDataFile != null && currentDataFile.exists()) {
			String str = DataLoader.load(memory, currentDataFile);
			if(!str.startsWith("read")) {
				JOptionPane.showMessageDialog(
						frame,  
						"The data file being selected has problems.\n" +
								str + "\n" +
								"Cannot load the data",
								"Warning",
								JOptionPane.OK_OPTION);   
			}
			director.makeReady("");
		}
	}

}
