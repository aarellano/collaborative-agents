package views.toolbars;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
	private Combo mapCombo;

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
			@Override
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
			@Override
			public void widgetSelected(SelectionEvent event) {
				screen.stopGameThread();
				screen.env.resetExperiment(screen.env.options.mapName);
				screen.startGameThread();
			}
		});

		//Create the options button and set it as the top right
		optionsBtn = new Button(parent, SWT.TOGGLE);
		optionsBtn.setText("Config Parameters");
		optionsBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				screen.showOptionsView();
			}
		});

		mapCombo = new Combo(parent, SWT.READ_ONLY);
		mapCombo.setToolTipText("Map Name");
		mapCombo.add("empty");
		mapCombo.add("reg");
		mapCombo.add("rep");
		mapCombo.add("rooms");
		mapCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				screen.env.options.mapName = mapCombo.getText();
			}
		});

		//		data = new GridData();
		//		data.exclude = false;
		//		data.horizontalAlignment = GridData.FILL_BOTH;
		//		mapCombo.setLayoutData(data);

		sleep_trajectories_Slider = new Scale(parent, SWT.HORIZONTAL);
		sleep_trajectories_Slider.setMinimum(0);
		sleep_trajectories_Slider.setMaximum(500);
		sleep_trajectories_Slider.setIncrement(50);
		sleep_trajectories_Slider.setPageIncrement(50);
		sleep_trajectories_Slider.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(screen.isAlive()) {
					screen.env.options.sleepTime = sleep_trajectories_Slider.getSelection();
					sleep_trajectories_Slider.setToolTipText(screen.env.options.sleepTime+"");
				}
				//				else if(screen.env.options.ViewTrajectories) {
				//					screen.env.options.loadGameNumber = sleep_trajectories_Slider.getSelection();
				//					sleep_trajectories_Slider.setToolTipText(screen.env.options.loadGameNumber+"");
				//					screen.env.loadTrajectories(screen.env.options.loadGameNumber);
				//					screen.env.screen.redraw();
				//				}
			}
		});

		calendarLabel = new CLabel(parent, SWT.NONE);
		calendarLabel.setText("0");

		//		data = new GridData();
		//		data.exclude = false;
		//		data.horizontalAlignment = GridData.FILL_BOTH;
		//		sleep_trajectories_Slider.setLayoutData(data);
		//		calendarLabel.setLayoutData(data);
		//		//		parent.layout(true);


		mapCombo.setFocus();
		updateStatus();

	}

	public int getComponentsCount() {
		return 6;
	}

	public void updateView() {
		String text = "";

		text = screen.env.clock.getRelativeTimeInClocks()+"";
		calendarLabel.setText(text);
		updateStatus();
	}

	public void updateStatus() {
		if(screen.env.isGameOver()) {
			startBtn.setImage(new Image(parent.getDisplay(), "res/imgs/run.png"));
		} else {
			startBtn.setImage(new Image(parent.getDisplay(), "res/imgs/terminate.png"));
		}
		sleep_trajectories_Slider.setEnabled(!screen.env.isGameOver());
		mapCombo.setEnabled(screen.env.isGameOver());
		sleep_trajectories_Slider.setSelection(screen.env.options.sleepTime);
		sleep_trajectories_Slider.setToolTipText(screen.env.options.sleepTime+"");
		mapCombo.setText(screen.env.options.mapName);
	}
}
