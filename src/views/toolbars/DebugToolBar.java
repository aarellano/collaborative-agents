package views.toolbars;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import lib.datastructs.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import views.MainScreen;

public class DebugToolBar {

	private MainScreen screen;
	public Composite parent;
	
	private Button resumeBtn, suspendBtn, stepoverBtn, terminateBtn;
	
	private Button breakPointBtn;
	private MouseAdapter mouseAdapter;
	private Vector<Point> breakPointsList;
	
	public DebugToolBar(MainScreen screen, Composite parent) {
		this.screen = screen;
		this.parent = parent;
		
		breakPointsList = new Vector<Point>();
		
		createViews();
	}

	private void createViews() {
		
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(data);
		
		resumeBtn = new Button(parent, SWT.PUSH);
		resumeBtn.setEnabled(false);
		resumeBtn.setImage(new Image(parent.getDisplay(), "res/imgs/resume.png"));
		resumeBtn.setToolTipText("Continue");
		resumeBtn.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  screen.env.options.suspendGame = false;
	    	  updateStatus();
	      }
	    });
		
		suspendBtn = new Button(parent, SWT.PUSH);
		suspendBtn.setImage(new Image(parent.getDisplay(), "res/imgs/suspend.png"));
		suspendBtn.setToolTipText("Suspend");
		suspendBtn.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  screen.env.options.suspendGame = true;
	    	  updateStatus();
	      }
	    });
		
		stepoverBtn = new Button(parent, SWT.PUSH);
		stepoverBtn.setEnabled(false);
		stepoverBtn.setImage(new Image(parent.getDisplay(), "res/imgs/stepover.png"));
		stepoverBtn.setToolTipText("Step Over");
		stepoverBtn.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  screen.env.options.suspendGame = false;
	    	  screen.env.options.stepOverGame = true;
	      }
	    });
		
		terminateBtn = new Button(parent, SWT.PUSH);
		terminateBtn.setImage(new Image(parent.getDisplay(), "res/imgs/terminate.png"));
		terminateBtn.setToolTipText("Terminate");
		terminateBtn.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  screen.env.options.suspendGame = false;
	    	  screen.env.options.terminateGame = true;
	    	  screen.stopGameThread();
	      }
	    });
		
		breakPointBtn = new Button(parent, SWT.TOGGLE);
		breakPointBtn.setImage(new Image(parent.getDisplay(), "res/imgs/break.png"));
		breakPointBtn.setToolTipText("Toggle Breakpoint");
		mouseAdapter = new MouseAdapter() {
			public void mouseClicked(final MouseEvent e) {
				Point cell = screen.getGridCanvas().getCellOfEvent(e);
				if(breakPointsList.contains(cell)) {
					breakPointsList.remove(cell);
				} else {
					breakPointsList.add(cell);
//					final int index = breakPointsList.indexOf(cell);
//					Display.getDefault().syncExec( new Runnable() {  public void run() { 
//						final Button brkpt = new Button(screen.getShell(), SWT.PUSH);
//						brkpt.setBounds(e.getX(), e.getY(), 200, 200);
//						brkpt.setImage(new Image(parent.getDisplay(), "res/imgs/break.png"));
//						breakPointBtnsList.insertElementAt(brkpt, index);
//						brkpt.addSelectionListener(new SelectionAdapter() {
//						      public void widgetSelected(SelectionEvent event) {
//						    	  breakPointsList.remove(index);
//						    	  breakPointBtnsList.remove(index);
//						    	  brkpt.dispose();
//						      }
//						    });
//					} });
				}
				screen.redrawAll();
			}};
		breakPointBtn.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  if(breakPointBtn.getSelection()) {
	    		  screen.getGridCanvas().addMouseListener(mouseAdapter);
	    	  } else {
	    		  screen.getGridCanvas().removeMouseListener(mouseAdapter);
	    	  }
	      }
	    });
		
		parent.setVisible(false);
		updateStatus();
	}

	public int getComponentsCount() {
		return 5;
	}
	
	public void updateStatus() {
		if(parent.isDisposed() || !screen.env.options.debugMode) return;
		
		if(screen.isAlive()) {
			resumeBtn.setEnabled(false);
			stepoverBtn.setEnabled(false);
			suspendBtn.setEnabled(true);
			parent.setVisible(true);
		} else {
			parent.setVisible(false);
		}
		
		if(screen.env.options.stepOverGame) {
			stepoverBtn.setEnabled(true);
			suspendBtn.setEnabled(false);
	    	resumeBtn.setEnabled(true);
		} else if(screen.env.options.suspendGame) {
			suspendBtn.setEnabled(false);
	    	resumeBtn.setEnabled(true);
	    	stepoverBtn.setEnabled(true);
		} else {
			resumeBtn.setEnabled(false);
	    	suspendBtn.setEnabled(true);
	    	stepoverBtn.setEnabled(false);
		}
	}
	
	public boolean hasBreakpoint(Point cell) {
		return screen.env.options.debugMode && breakPointsList.contains(cell);
	}
}
