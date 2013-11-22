package views.toolbars;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import environment.Environment;

public class OptionsView {

	protected Environment env;

	protected Display display;
	protected Shell shell;

	public boolean showen;

	public void view(Environment env) {
		this.env = env;
		showen = true;
		open();
	}

	protected void open() {
		display = Display.getDefault();
		Shell parent = env.screen.getShell();
		Rectangle rect = env.screen.getShell().getBounds();
		shell = new Shell(parent, SWT.NONE);
		shell.setSize(270, 400);
		shell.setBounds(rect.x+rect.width,
				rect.y+rect.height/2-shell.getBounds().height/2,
				shell.getSize().x, shell.getSize().y);
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	protected void createContents() {
		GridLayout layout = new GridLayout(1,true);
		layout.marginHeight = 50;
		layout.marginWidth = 30;
		layout.verticalSpacing = 15;
		shell.setLayout(layout);
		GridData menuData = new GridData(GridData.CENTER);
		shell.setLayoutData(menuData);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan =2;

		createGameOptionsComponents(shell);

		createeViewOptionsComponents(shell);

		////////////////////////////

		GridData okData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_END);
		okData.widthHint =90;
		okData.heightHint =25;
		Button okButton = new Button(shell,SWT.PUSH);
		okButton.setText("Done");
		okButton.setLayoutData(okData);
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent e) {
				closeView();
			}
		});
	}

	private void createGameOptionsComponents(Composite parent) {

		Group group = new Group(shell, SWT.SHADOW_IN);
		group.setText("Game Options");
		group.setLayout(new GridLayout(1,true));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Sight Distance Range
		Composite row = new Composite(group, SWT.NONE);
		row.setLayout(new RowLayout());
		(new CLabel(row, SWT.NONE)).setText("Sight Distance - Ds");
		final Spinner sightSpinner = new Spinner(row, SWT.SINGLE|SWT.BORDER);
		sightSpinner.setMinimum(1);
		sightSpinner.setMaximum(10);
		sightSpinner.setSelection(env.options.Ds);
		sightSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				env.options.Ds = sightSpinner.getSelection();
			}
		});

		//		GridData numGroupData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		//		numGroupData.widthHint =140;
		//		Group numGroup = new Group(shell, SWT.SHADOW_IN);
		//	    numGroup.setText("Sight Distance - Ds");
		//	    numGroup.setLayoutData(numGroupData);
		//	    numGroup.setLayout(new GridLayout(1,true));
		//
		//	    GridData numData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		//		numData.widthHint =100;
		//	    sightSpinner.setLayoutData(numData);


		//		Composite row = new Composite(shell, SWT.NONE);
		//		row.setLayout(new RowLayout());
		//		(new CLabel(row, SWT.NONE)).setText("Sight Distance - Ds");
		//	    Combo sightCmbo = new Combo(row, SWT.DROP_DOWN | SWT.READ_ONLY);
		//	    sightCmbo.setSize(150, 65);
		//	    sightCmbo.setItems(new String[]{"1", "2", "3", "4"});
		//	    sightCmbo.setSelection(new Point(env.options.Ds, 1));

		// Terminate game when Timeout
		final Button terminateTimeoutButton = new Button(group, SWT.CHECK);
		terminateTimeoutButton.setText("Terminate game on Timeout");
		terminateTimeoutButton.setSelection(env.options.terminateOnTimeout);
		terminateTimeoutButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				env.options.terminateOnTimeout = terminateTimeoutButton.getSelection();
			}
		});

	}

	private void createeViewOptionsComponents(Composite parent) {

		Group group = new Group(shell, SWT.SHADOW_IN);
		group.setText("View Options");
		group.setLayout(new GridLayout(1,true));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Update View Every Clock
		final Button updateViewButton = new Button(group, SWT.CHECK);
		updateViewButton.setText("Update view every clock");
		updateViewButton.setSelection(env.options.updateView);
		updateViewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				env.options.updateView = updateViewButton.getSelection();
			}
		});

		// Update View Every Clock
		final Button loopGamesButton = new Button(group, SWT.CHECK);
		loopGamesButton.setText("Keep looping on games");
		loopGamesButton.setSelection(env.options.loopOnGames);
		loopGamesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				env.options.loopOnGames = loopGamesButton.getSelection();
			}
		});

		// Viewing info.
		Group viewingInfoGroup = new Group(group, SWT.SHADOW_IN);
		viewingInfoGroup.setText("Viewing Info.");
		viewingInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		viewingInfoGroup.setLayout(new GridLayout(1,true));
		Button choice = null;

		choice = new Button(viewingInfoGroup, SWT.RADIO);
		choice.setText("View map building knowledge");
		choice.setSelection(true);
		choice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				env.options.setViewMapKnowledge();
			}
		});
		choice = new Button(viewingInfoGroup, SWT.RADIO);
		choice.setText("View Visited Cells");
		choice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				env.options.setViewVisitedCells();
			}
		});

		choice = new Button(viewingInfoGroup, SWT.RADIO);
		choice.setText("View Map Partitions");
		choice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				env.options.setViewMapPartitions();
			}
		});

		final Button debug = new Button(viewingInfoGroup, SWT.CHECK);
		debug.setText("Debug Mode");
		debug.setSelection(env.options.debugMode);
		debug.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				env.options.debugMode = debug.getSelection();
			}
		});
	}

	public void closeView() {
		shell.getParent().setEnabled(true);
		shell.close();
		showen = false;
	}

}
