package projectview;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
public class ControlPanel {
	private Director director;
	private JButton stepButton = new JButton("Step");
	private JButton clearButton = new JButton("Clear");
	private JButton runButton = new JButton("Run/Pause");
	private JButton reloadButton = new JButton("Reload");
	public ControlPanel(Director director) {
		super();
		this.director = director;
	}
	public JComponent createControlDisplay() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1,0));
		stepButton.setBackground(Color.WHITE);
		stepButton.addActionListener(e -> director.step());
		panel.add(stepButton);
		clearButton.setBackground(Color.WHITE);
		clearButton.addActionListener(e -> director.clear());
		panel.add(clearButton);
		runButton.setBackground(Color.WHITE);
		runButton.addActionListener(e -> director.toggleAutoStep());
		panel.add(runButton);
		reloadButton.setBackground(Color.WHITE);
		reloadButton.addActionListener(e -> director.reload());
		panel.add(reloadButton);
		JSlider slider = new JSlider(5,1000);
		slider.addChangeListener(e -> director.setPeriod(slider.getValue()));
		panel.add(slider);
		return panel;
	}
	public void update() {
		runButton.setEnabled(director.getCurrentState().isRunPauseActive());
		stepButton.setEnabled(director.getCurrentState().isStepActive());
		clearButton.setEnabled(director.getCurrentState().isClearActive());
		reloadButton.setEnabled(director.getCurrentState().isReloadActive());
	}
	public static void main(String[] args) {
		Director dir = new Director();
		ControlPanel panel = new ControlPanel(dir);
		JFrame frame = new JFrame("TEST");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 60);frame.setLocationRelativeTo(null);
		frame.add(panel.createControlDisplay());
		frame.setVisible(true);panel.update();
	}
	

}
