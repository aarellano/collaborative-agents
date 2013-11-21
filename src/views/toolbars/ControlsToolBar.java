package views.toolbars;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;

import views.MainScreen;

public class ControlsToolBar {

	private MainScreen screen;
	
	private Button startBtn;
	private Button optionsBtn;
	private Button resetBtn;
	private Scale sleep_trajectories_Slider;
	private CLabel calendarLabel;
	
	public Composite parent;
	
	public ControlsToolBar(MainScreen screen, Composite parent) {
		this.screen = screen;
		this.parent = parent;
		
		createViews();
	}

	private void createViews() {
		
		//parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(data);
		
		startBtn = new Button(parent, SWT.TOGGLE);
		startBtn.setImage(new Image(parent.getDisplay(), "res/imgs/run.png"));
		startBtn.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  if(screen.env.isGameOver()) {
	    		  screen.startGameThread();
	    	  } else {
	    		  screen.stopGameThread();
	    	  }
	      }
	    });
		
		resetBtn = new Button(parent, SWT.PUSH);
		resetBtn.setText("Reset");
		resetBtn.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  screen.stopGameThread();
	    	  screen.env.resetExperiment();
	    	  screen.startGameThread();
	      }
	    });
		
		//Create the options button and set it as the top right
		optionsBtn = new Button(parent, SWT.TOGGLE);
		optionsBtn.setText("Config Parameters");
		optionsBtn.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  screen.showOptionsView();
	      }
	    });
		
		sleep_trajectories_Slider = new Scale(parent, SWT.HORIZONTAL);
		sleep_trajectories_Slider.addListener(SWT.Selection, new Listener() {
	        public void handleEvent(Event event) {
	        	if(screen.isAlive()) {
	        		screen.env.options.sleepTime = sleep_trajectories_Slider.getSelection();
	        		sleep_trajectories_Slider.setToolTipText(screen.env.options.sleepTime+"");
	        	} else if(screen.env.options.ViewTrajectories) {
	        		screen.env.options.loadGameNumber = sleep_trajectories_Slider.getSelection();
	        		sleep_trajectories_Slider.setToolTipText(screen.env.options.loadGameNumber+"");
		        	screen.env.loadTrajectories(screen.env.options.loadGameNumber);
		        	screen.env.screen.redraw();
	        	}
	        }
	    });   
		sleep_trajectories_Slider.setFocus();
		
	    calendarLabel = new CLabel(parent, SWT.NONE);
		calendarLabel.setText("Iterations / Games");
		

		resetSlider(); 		
	}
	
	public void resetSlider() {
		if(parent.isDisposed()) return;
		if(screen.isAlive()) {
			sleep_trajectories_Slider.setMinimum(0);
			sleep_trajectories_Slider.setMaximum(500);
			sleep_trajectories_Slider.setIncrement(50);
			sleep_trajectories_Slider.setPageIncrement(50);
		    sleep_trajectories_Slider.setSelection(screen.env.options.sleepTime);
		    sleep_trajectories_Slider.setToolTipText(screen.env.options.sleepTime+"");
		} else {
			sleep_trajectories_Slider.setMinimum(1);
		    sleep_trajectories_Slider.setMaximum(1000);
		    sleep_trajectories_Slider.setIncrement(1);
		    sleep_trajectories_Slider.setPageIncrement(1);
		    sleep_trajectories_Slider.setSelection(screen.env.options.loadGameNumber);
		    sleep_trajectories_Slider.setToolTipText(screen.env.options.loadGameNumber+"");
		}
	}

	public int getComponentSize() {
		return (startBtn.computeSize(SWT.DEFAULT,SWT.DEFAULT).x+
				resetBtn.computeSize(SWT.DEFAULT,SWT.DEFAULT).x+
				calendarLabel.computeSize(SWT.DEFAULT,SWT.DEFAULT).x+
				sleep_trajectories_Slider.computeSize(SWT.DEFAULT,SWT.DEFAULT).x+
				optionsBtn.computeSize(SWT.DEFAULT,SWT.DEFAULT).x);
	}

	public int getComponentsCount() {
		return 5;
	}

	public void updateView() {
		String text = "";
		
		text = screen.env.clock.getRelativeTimeInClocks()+" / "+(screen.env.clock.getGamesCount()+1)+" Games";
		calendarLabel.setText(text);
		updateStatus();
	}
	
	public void updateStatus() {
  	  if(screen.env.isGameOver()) {
  		  startBtn.setImage(new Image(parent.getDisplay(), "res/imgs/run.png"));
  	  } else {
  		startBtn.setImage(new Image(parent.getDisplay(), "res/imgs/terminate.png"));	    		  
  	  }
	}
}
