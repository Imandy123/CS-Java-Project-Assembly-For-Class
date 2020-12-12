package projectview;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
public class MenuBarBuilder {
	private JMenuItem assemble = new JMenuItem("Assemble Source...");
	private JMenuItem loadCode= new JMenuItem("Load Program...");
	private JMenuItem loadData= new JMenuItem("Load Data...");
	private JMenuItem exit = new JMenuItem("Exit");
	private JMenuItem go = new JMenuItem("Go");
	private Director director;
	private FilesMgr filesMgr;
	
	public MenuBarBuilder(Director director, FilesMgr filesMgr) {
		super();
		this.director = director;
		this.filesMgr = filesMgr;
	}

	public void update() {
		assemble.setEnabled(director.getCurrentState().isAssembleFileActive());
		loadCode.setEnabled(director.getCurrentState().isLoadFileActive());
		loadData.setEnabled(director.getCurrentState().isLoadFileActive());
		go.setEnabled(director.getCurrentState().isStepActive());
	}
	public JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		assemble.setMnemonic(KeyEvent.VK_M);
		assemble.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		assemble.addActionListener(e -> filesMgr.assembleFile());
		menu.add(assemble);

		loadCode.setMnemonic(KeyEvent.VK_L);
		loadCode.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		loadCode.addActionListener(e -> filesMgr.loadCodeFile());
		menu.add(loadCode);
		
		loadData.setMnemonic(KeyEvent.VK_D);
		loadData.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		loadData.addActionListener(e -> filesMgr.loadDataFile());
		menu.add(loadData);
		menu.addSeparator();
		
		exit.setMnemonic(KeyEvent.VK_E);
		exit.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		exit.addActionListener(e -> director.exit());
		menu.add(exit);
		return menu;
	}
	public JMenu createExecuteMenu() {
		JMenu menu = new JMenu("Execute");
		go.setMnemonic(KeyEvent.VK_G);
		go.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		go.addActionListener(e -> director.execute());
		menu.add(go);
		return menu;
	}
	
}
