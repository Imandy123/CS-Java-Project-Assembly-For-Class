package projectview;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import project.Encoding;
import project.Instruction;
import project.Loader;
import project.Memory;
import project.Processor;
public class CodeViewPanel {
	private Memory memory;
	private Processor processor;
	private JScrollPane scroller;
	private JTextField[] codeBinHex;
	private JTextField[] codeText;
	private int previousColor = -1;
	private long instr;
	
	public CodeViewPanel(Memory m, Processor p) {
		memory= m;
		processor = p;
		codeBinHex = new JTextField[Memory.CODE_SIZE];
		codeText = new JTextField[Memory.CODE_SIZE];
	}
	public JComponent createCodeDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		Border border = BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				"Code Memory View",
				TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
		panel.setBorder(border);
		panel.setPreferredSize(new Dimension(300,150));
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BorderLayout());
		
		JPanel numPanel = new JPanel();
		numPanel.setLayout(new GridLayout(0,1));
		
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(0,1));
		
		JPanel hexPanel = new JPanel();
		hexPanel.setLayout(new GridLayout(0,1));
		
		innerPanel.add(numPanel, BorderLayout.LINE_START);
		innerPanel.add(textPanel, BorderLayout.CENTER);
		innerPanel.add(hexPanel, BorderLayout.LINE_END);
		
		for(int i = 0; i < Memory.CODE_SIZE; i++) {
			numPanel.add(new JLabel(i+": ", JLabel.RIGHT));
			codeText[i] = new JTextField(10);
			codeBinHex[i] = new JTextField(12);
			textPanel.add(codeText[i]);
			hexPanel.add(codeBinHex[i]);
		}
		scroller =new JScrollPane(innerPanel);
		panel.add(scroller);
		return panel;
	}
	public void update(String command) {
		if("Load Code".equals(command)) {
			for(int i = 0; i < memory.getProgramSize(); i++) {
				instr = memory.getCode(i);
				byte opCode = Encoding.opFromLong(instr);
				int arg = Encoding.argFromLong(instr);
				codeText[i].setText(Instruction.getText(opCode, arg));
				codeBinHex[i].setText(Long.toHexString(instr));
			}
			previousColor = processor.getIP();
			codeBinHex[previousColor].setBackground(Color.YELLOW);
			codeText[previousColor].setBackground(Color.YELLOW);
		}else if("Clear".equals(command)) {
			for(int i = 0; i < Memory.CODE_SIZE; i++) {
				codeText[i].setText("");
				codeBinHex[i].setText("");
			}
			if(previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
				codeText[previousColor].setBackground(Color.WHITE);
				codeBinHex[previousColor].setBackground(Color.WHITE);
			}
			previousColor = -1;
		}
		if(this.previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
			codeText[previousColor].setBackground(Color.WHITE);
			codeBinHex[previousColor].setBackground(Color.WHITE);
		}
		previousColor = processor.getIP();
		if(this.previousColor >= 0 && previousColor < Memory.CODE_SIZE) {
			codeText[previousColor].setBackground(Color.YELLOW);
			codeBinHex[previousColor].setBackground(Color.YELLOW);
		}
		if(scroller != null && instr != 0 && memory!= null) {
			JScrollBar bar= scroller.getVerticalScrollBar();
			int pc = processor.getIP();
			if(pc >= 0 && pc < Memory.CODE_SIZE && codeBinHex[pc] != null) {
				Rectangle bounds = codeBinHex[pc].getBounds();
				bar.setValue(Math.max(0, bounds.y -15*bounds.height));
			}
		}
	}
		public static void main(String[] args) {
			Memory memory = new Memory();
			Processor proc = new Processor();
			CodeViewPanel panel = new CodeViewPanel(memory, proc);
			JFrame frame = new JFrame("TEST");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(400, 700);frame.setLocationRelativeTo(null);
			frame.add(panel.createCodeDisplay());frame.setVisible(true);
			System.out.println(Loader.load(memory, new File("pexe/z001.pexe")));
			panel.update("Load Code");
		}
}
