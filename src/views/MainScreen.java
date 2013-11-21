package views;

import java.awt.Frame;

import lib.datastructs.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import views.toolbars.ControlsToolBar;
import views.toolbars.DebugToolBar;
import views.toolbars.OptionsView;
import environment.EnvCellEnum;
import environment.Environment;

public class MainScreen implements Runnable {

	// Controller
	public Environment env;
	private Thread gameThread;
	
	// View variables
	protected Display display;
	protected Shell shell;

	private GridCanvas grid;
	private ControlsToolBar toolbar;
	private DebugToolBar debugToolbar;
	private OptionsView optionsView;

	private Composite gameSpaceComposite;
	
	
	public void view(Environment env) {
		this.env = env;
		open();
	}

	protected void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
	
	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Create View Components
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	protected void createContents() {
		shell = new Shell();
		shell.setSize(700, 381);
		shell.setText("H&S simul.");
		GridLayout mainLayout = new GridLayout(1,false);
		shell.setLayout(mainLayout);
		GridData data = new GridData(GridData.FILL_VERTICAL);
		shell.setSize(500+26, 500+165);
		shell.setBounds(150,50,shell.getSize().x,shell.getSize().y);
				
		//creating the game space composite
		Composite gameComposite = new Composite(shell, SWT.BORDER);
		gameComposite.setLayoutData(data);
		createGameComposite(gameComposite);
		
		optionsView = new OptionsView();
		
		shell.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent arg0) {
				stopGameThread();
			}
		});
		
	}

	private void createGameComposite(Composite parent) {
		GridLayout layout = new GridLayout(1,true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(data);
		
		//creating the game space contents
	    //////////////////////////////////
		Composite toolBarComposite = new Composite(parent, SWT.BORDER);
		createControlsTitleBar(toolBarComposite);
		
		Composite debugtoolBarComposite = new Composite(parent, SWT.BORDER);
		createDegugToolBar(debugtoolBarComposite);
		
		data.heightHint = 20;
		data.widthHint = parent.getBounds().width;
		parent.layout();
		
		gameSpaceComposite = new Composite(parent, SWT.EMBEDDED);
	    createGameSpaceContents(gameSpaceComposite);
	}
	
	private void createGameSpaceContents(Composite parent) {
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(data);
		
		grid = new GridCanvas(env.getEnvHeight(), env.getEnvWidth(), this);
		Frame frame = SWT_AWT.new_Frame(parent);
		frame.add(grid);
	}

	private void createControlsTitleBar(Composite parent) {
		toolbar = new ControlsToolBar(this, parent);
		int columns = toolbar.getComponentsCount();
		
		GridLayout barLayout = new GridLayout(columns, false);
		barLayout.marginHeight = 0;
		barLayout.marginWidth = 3;
		parent.setLayout(barLayout);
		GridData barData = new GridData(GridData.FILL_HORIZONTAL);
		parent.setLayoutData(barData);
	}
	
	private void createDegugToolBar(Composite parent) {
		debugToolbar = new DebugToolBar(this, parent);
		int columns = debugToolbar.getComponentsCount();
		
		GridLayout barLayout = new GridLayout(columns, false);
		barLayout.marginHeight = 0;
		barLayout.marginWidth = 3;
		parent.setLayout(barLayout);
		GridData barData = new GridData(GridData.FILL_HORIZONTAL);
		parent.setLayoutData(barData);
	}
	
	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Game Logic
	 * ///////////////////////////////////////////////////////////////////////////
	 */
	
	// Game thread started when 'Play' button is clicked
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
		
		// Update View
		if(debugToolbar != null)
			debugToolbar.updateStatus();
		if(toolbar != null)
			toolbar.resetSlider();
	}
	
	// Game thread body
	public void run() {
		
		// Experiment is a set of run tests
		env.resetExperiment();
		do {
			env.startGame();
			env.closeLoggers();
		} while(env.options.loopOnGames && env.clock.getGamesCount() < env.options.testsCount*env.options.rePlayGameTimes);
	}

	// Game thread terminated when 'Stop' button is clicked
	public void stopGameThread() {
		if(isAlive()) {
			env.endGame();
			while(isAlive());
			gameThread = null;
			env.closeLoggers();
			
			// Update View
			debugToolbar.updateStatus();
			toolbar.resetSlider();
			
			env.options.terminateGame = false;
		}
	}
	
	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category View Control
	 * ///////////////////////////////////////////////////////////////////////////
	 */
	
	public void redraw() {
		grid.redraw();
	}
	
	public void updateView() {
		display.asyncExec(new Runnable() {
            public void run() {
            	toolbar.updateView();
            	toolbar.parent.layout();
            }
        });
	}

	public void showOptionsView() {
		if(!optionsView.showen)
			optionsView.view(env);
		else
			optionsView.closeView();
	}
	
	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Getters
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public boolean isAlive() {
		return gameThread != null && gameThread.isAlive();
	}
	
	public EnvCellEnum getCell(int i, int j) {
		return env.readSensorsForCell(new Point(i, j));
	}
	
	public Shell getShell() {
		return this.shell;
	}

	public GridCanvas getGridCanvas() {
		return grid;
	}

	public boolean hasBreakpoint(int i, int j) {
		return debugToolbar.hasBreakpoint(new Point(i, j));
	}
		
}

