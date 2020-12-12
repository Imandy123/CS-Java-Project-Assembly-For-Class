package projectview;
import java.awt.GridLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import project.Processor;

public class ProcessorViewPanel {
	private Processor processor;
	private JTextField acc = new JTextField();
	private JTextField ip = new JTextField();
	public ProcessorViewPanel(Processor p) {
		processor= p;
	}
	public JComponent createProcessorDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,0));
		panel.add(new JLabel("Accumulator: ", JLabel.RIGHT));
		panel.add(acc);
		panel.add(new JLabel("Instruction Pointer: ", JLabel.RIGHT));
		panel.add(ip);
		return panel;
		
	}
	public void update() {
		if(processor!= null) {
			acc.setText("" + processor.getAcc());
			ip.setText("" + processor.getIP());
		}
	}
	public static void main(String[] args) {
		Processor proc= new Processor();
		proc.setAcc(123);proc.setIP(23);
		ProcessorViewPanel panel = new ProcessorViewPanel(proc);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 60);frame.setLocationRelativeTo(null);
		frame.add(panel.createProcessorDisplay());
		frame.setVisible(true);panel.update();
	}
}
