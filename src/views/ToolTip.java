package views;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

import lib.datastructs.Point;

public class ToolTip extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MainScreen screen;
	
	protected String tip;
	protected Component owner;
	
	private Container mainContainer;
	private LayoutManager mainLayout;
	
	private boolean shown;
	
	private final int VERTICAL_OFFSET = 30;
	private final int HORIZONTAL_ENLARGE = 10;
	
    public ToolTip(String tip, Component owner, MainScreen screen) {
    	this.tip = tip;
		this.owner = owner;
		this.screen = screen;
    	owner.addMouseListener(new MAdapter());
   		setBackground(new Color(255,255,220));
    }


	public void paint(Graphics g) {
		g.drawRect(0,0,getSize().width -1, getSize().height -1);
		g.drawString(tip, 3, getSize().height - 3);
	}

	private void addToolTip(int xPos, int yPos) {
		mainContainer.setLayout(null);
		
		FontMetrics fm = getFontMetrics(owner.getFont());    		
		setSize(fm.stringWidth(tip) + HORIZONTAL_ENLARGE, fm.getHeight());

//		setLocation((owner.getLocationOnScreen().x - mainContainer.getLocationOnScreen().x) , 
//					(owner.getLocationOnScreen().y - mainContainer.getLocationOnScreen().y + VERTICAL_OFFSET));
		setLocation((owner.getLocationOnScreen().x - mainContainer.getLocationOnScreen().x + xPos), 
				  (owner.getLocationOnScreen().y - mainContainer.getLocationOnScreen().y + yPos + VERTICAL_OFFSET));

		// correction, whole tool tip must be visible 
		if (mainContainer.getSize().width < ( getLocation().x + getSize().width )) {
			setLocation(mainContainer.getSize().width - getSize().width, getLocation().y);
		}
		
		setVisible(false);
		mainContainer.add(this, 0);
		setVisible(true);
		mainContainer.validate();
		repaint();
		shown = true;
	}

	
	private void removeToolTip() {
		if (shown) {
			mainContainer.remove(0);
			mainContainer.setLayout(mainLayout);
			mainContainer.validate();
		}
		shown = false;
	}

	private void findMainContainer() {
		Container parent = owner.getParent();
		while (true) {
			if ((parent instanceof Applet) || (parent instanceof Frame)) {
				mainContainer = parent;
				break;				
			} else {
				parent = parent.getParent();
			}
		}		
		mainLayout = mainContainer.getLayout();
	}

    class MAdapter extends MouseAdapter {
    	    	
		@Override
		public void mouseClicked(MouseEvent e) {
			if(!screen.env.options.debugMode) return;
			
			findMainContainer();
			Point cell = screen.getGridCanvas().getCellOfEvent(e);
			tip = screen.env.tipInfo.get(cell);
			if(tip == null) tip = "";
		    addToolTip(e.getX(), e.getY());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			removeToolTip();
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			removeToolTip();
		}
		
	}
}
