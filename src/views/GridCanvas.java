package views;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Vector;

import lib.datastructs.OrientationEnum;
import lib.datastructs.Point;
import agent.Agent;
import environment.EnvCellEnum;

public class GridCanvas extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7241381545906291018L;
	private int rows, columns;
	private MainScreen parent;
	private Color[][] cells;
	public int origin_x, origin_y;
	private boolean app_triggered_painting = false;
	private boolean repaint_all = true;
	private boolean cancelRedrawUpdates;
	private Color[] seekers = {Color.BLUE, Color.RED, Color.BLUE};
	
	private Image breakpoint = Toolkit.getDefaultToolkit().getImage("break.png");
	
	public GridCanvas(int rows, int columns, MainScreen parent) {
		super();
		this.rows = rows;
		this.columns = columns;
		this.parent = parent;
		origin_x = origin_y = 0;
		cells = new Color[rows][columns];
		
		new ToolTip("", this, parent);
	}

	@Override
	public void paint(Graphics g) {
//		if(!app_triggered_painting)
//			super.paint(g);
		
		int width = getSize().width, height = getSize().height;
	    int heightOfRow = height / rows;
	    int widthOfCol = width / columns;
	    
	    g.setColor(Color.BLACK);
//	    for (int i = 0; i <= rows; i++) {
//	    //for (int i = 0; i <= rows; i+=rows) {
//	      g.drawLine(origin_x, origin_y+i*heightOfRow , width, i * heightOfRow );
//	    }
//	    
//	    for (int i = 0; i <= columns; i++) {
//	    //for (int i = 0; i <= rows; i+=columns) {
//	      g.drawLine(origin_x+i*widthOfCol , origin_y, i*widthOfCol , height);
//	    }
	    
	    Color color = null;
	    for(int i = 0; i < rows; i++) {
	    	for(int j = 0; j < columns; j++) {
	    		
	    		if(parent.env.options.viewMapKnowledge)
	    			color = getColorForMap(i, j);
	    		else if(parent.env.options.viewVisitedCells)
	    			color = getColorForSearch(i, j);
	    		
	    		if(!app_triggered_painting || repaint_all || 
	    				(cells[i][j] == null || !cells[i][j].equals(color))) {
	    			g.setColor(color);
	    			g.fillRect(origin_x+j*widthOfCol+1, origin_y+i*heightOfRow+1, widthOfCol-1, heightOfRow-1);
	    			cells[i][j] = color;
	    			if(parent.hasBreakpoint(i, j)) {
	    				g.drawImage(breakpoint, 
	    						origin_x+j*widthOfCol+1, origin_y+i*heightOfRow+1, 
	    						widthOfCol-1, heightOfRow-1, 
	    						this);
	    			}
	    		}
	    	}
	    }
	    
	    if(parent.env.options.viewMapPartitions) {
		    for (Agent seeker : parent.env.getSeekers()) {
		    	drawPartitions(g, seeker.getID());
			}
	    }
	    
	    if(parent.env.options.ViewTrajectories) {
//		    for (Seeker seeker : parent.env.getSeekers()) {
//		    	drawMeets(g, seeker.getTrajectory());
//			}
		    for (Agent seeker : parent.env.getSeekers()) {
		    	drawTrajectories(g, seeker.getID());
			}
	    }
	    
	    app_triggered_painting = false;
	    repaint_all = true;
	    cancelRedrawUpdates = false;
	}

	@Override
	public void update(Graphics g) {
		app_triggered_painting = true;
		//repaint_all = false;
		paint(g);
	}
	
	public void redrawChanges() {
		repaint_all = false;
		repaint();
	}
	
	public void redrawAll() {
		repaint_all = true;
		repaint();
	}

	public void cancelRedrawUpdates() {
		cancelRedrawUpdates = true;
	}
	
	private Color getColorForMap(int i, int j) {
		Point p = new Point(i, j);
		return getColor(p, parent.getCell(i, j), parent.env.getCellSharedMaskForMap(p));
//		if(cells[i][j] == Color.MAGENTA) 
//			return Color.MAGENTA;
//		
//		Color color = null;
//	    EnvCellEnum cell;
//	    int mask = 0, maxMask = parent.env.getMaxSharedMask();
//		cell = parent.getCell(i, j);
//		mask = parent.env.getCellSharedMask(new Point(i, j));
//		switch (cell) {
//		case BLOCKED:
//			if(mask == 0)	// Unknown for all
//				color = Color.DARK_GRAY;
//			else if(mask == maxMask) // Known for all
//				color = Color.BLACK;
//			else if(mask == 1) // Known for Seeker#1
//				color = Color.GRAY;
//			else if(mask == 2) // Known for Seeker#2
//				color = Color.GRAY;
//			break;
//		case FREE:
//			if(mask == 0)	// Unknown for all
//				color = Color.LIGHT_GRAY;
//			else if(mask == maxMask) // Known for all
//				color = Color.WHITE;
//			else if(mask == 1) // Known for Seeker#1
//				color = Color.ORANGE;
//			else if(mask == 2) // Known for Seeker#2
//				color = Color.CYAN;
//			break;
//		case OCCUP_HIDER:
//			color = Color.GREEN;
//			break;
//		case OCCUP_SEEKER:
//			color = seekers[parent.env.getSeekerAtCell(new Point(i, j)).getID()-1];
//			break;
//		default:
//			break;
//		}
//
//		boolean isMax = parent.env.maxs.contains(new Point(i,j));
//		boolean isDestined = parent.env.isDestinatedCell(new Point(i, j));
//		if(isMax && isDestined)
//			color = Color.PINK;
//		else if(isMax)
//			color = Color.RED;
//		else if(isDestined)
//			color = Color.YELLOW;
//		return color;
	}
	
	private Color getColorForSearch(int i, int j) {
		Point p = new Point(i, j);
		return getColor(p, parent.getCell(i, j), parent.env.getCellSharedMaskForSearch(p));
//		Color color = null;
//	    EnvCellEnum cell;
//	    int mask = 0, maxMask = parent.env.getMaxSharedMask();
//		cell = parent.getCell(i, j);
//		mask = parent.env.getCellScannedSharedMask(new Point(i, j));
//		switch (cell) {
//		case BLOCKED:
//			if(mask == 0)	// Unknown for all
//				color = Color.DARK_GRAY;
//			else if(mask == maxMask) // Known for all
//				color = Color.BLACK;
//			else if(mask == 1) // Known for Seeker#1
//				color = Color.GRAY;
//			else if(mask == 2) // Known for Seeker#2
//				color = Color.GRAY;
//			break;
//		case FREE:
//			if(mask == 0)	// Unknown for all
//				color = Color.LIGHT_GRAY;
//			else if(mask == maxMask) // Known for all
//				color = Color.WHITE;
//			else if(mask == 1) // Known for Seeker#1
//				color = Color.ORANGE;
//			else if(mask == 2) // Known for Seeker#2
//				color = Color.CYAN;
//			break;
//		case OCCUP_HIDER:
//			color = Color.GREEN;
//			break;
//		case OCCUP_SEEKER:
//			color = seekers[parent.env.getSeekerAtCell(new Point(i, j)).getID()-1];
//			break;
//		default:
//			break;
//		}
//		
//		boolean isMax = parent.env.maxs.contains(new Point(i,j));
//		boolean isDestined = parent.env.isDestinatedCell(new Point(i, j));
//		if(isMax && isDestined)
//			color = Color.PINK;
//		else if(isMax)
//			color = Color.RED;
//		else if(isDestined)
//			color = Color.YELLOW;
//		return color;
	}
	
	private Color getColor(Point cell, EnvCellEnum value, int mask) {
		Color color = null;
		int maxMask = parent.env.getMaxSharedMask();
		switch (value) {
		case BLOCKED:
			if(mask == 0)	// Unvisited for all
				color = Color.DARK_GRAY;
			else if(mask == maxMask) // visited for all
				color = Color.BLACK;
			else if(mask == 1) // visited for Seeker#1
				color = Color.GRAY;
			else if(mask == 2) // visited for Seeker#2
				color = Color.GRAY;
			break;
		case FREE:
			if(mask == 0)	// Unvisited for all
				color = Color.LIGHT_GRAY;
			else if(mask == maxMask) // visited for all
				color = Color.WHITE;
			else if(mask == 1) // visited for Seeker#1
				color = Color.ORANGE;
			else if(mask == 2) // visited for Seeker#2
				color = Color.CYAN;
			break;
		case OCCUP_AGENT:
			Agent s = parent.env.getSeekerAtCell(cell);
			color = (s == null) ? Color.BLUE : seekers[s.getID()-1];
			break;
		default:
			break;
		}
		
		boolean isMax = parent.env.maxs.contains(cell);
		boolean isDestined = parent.env.isDestinatedCell(cell);
		if(isMax && isDestined)
			color = Color.PINK;
		else if(isMax)
			color = Color.RED;
		else if(isDestined)
			color = Color.YELLOW;
		
		return color;
	}
	
	public void drawMeets(Graphics g, Vector<Point> traj) {
		int width = getSize().width, height = getSize().height;
	    int heightOfRow = height / rows;
	    int widthOfCol = width / columns;
		
		Vector<Integer> meets = parent.env.clock.getTimeStamps();
		if(meets.isEmpty() || traj.isEmpty()) return;
		
		Color color = Color.YELLOW;
		Point current;
		g.setColor(color);
		current = traj.get(0);
		for(int i = 1; i < traj.size() && !cancelRedrawUpdates; i++) {
			if(meets.contains(i-1) && cells[current.row][current.col] != color) {
				//cells[current.row][current.col] = color;
				g.fillRect(origin_x+current.col*widthOfCol+1, origin_y+current.row*heightOfRow+1,
						widthOfCol-1, heightOfRow-1);
			}
			current = traj.get(i);
		}
	}
	
	public void drawTrajectories(Graphics g, int id) {
		int width = getSize().width, height = getSize().height;
	    int heightOfRow = height / rows;
	    int widthOfCol = width / columns;
		
	    Agent seeker = parent.env.getSeekers()[id-1];
	    Vector<Point> traj = seeker.getTrajectory();
	    if(traj.isEmpty()) return;
	    
		Graphics2D g2d = (Graphics2D)g;
		BasicStroke solid =
	        new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
	        		10.0f, new float[] {10f}, 0.0f);
		BasicStroke dashed =
	        new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
	        		10.0f, new float[] {5f}, 0.0f);
		BasicStroke dotted =
	        new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 
	        		10.0f, new float[] {2f}, 0.0f);
	    g2d.setStroke(new BasicStroke(4));
		Point current, next;
		org.eclipse.swt.graphics.Point p_old = null, p1 = null, p2 = null;
		int x, y;
		
		current = traj.get(0);
		p_old = new org.eclipse.swt.graphics.Point(current.col*widthOfCol+widthOfCol/2, 
				current.row*heightOfRow+heightOfRow/2);
		for(int i = 1; i < traj.size() && !cancelRedrawUpdates; i++) {
			next = traj.get(i);
			
			// Don't draw extra useless work at the end of the episode
//			if(seeker.getHidingProb(current) > 0) {
//				Point p;
//				int j = i;
//				for(; j < traj.size(); j++) {
//					p = traj.get(j);
//					if (seeker.getHidingProb(p) > 0) break;
//				}
//				if(j == traj.size()) return;
//			}
			
			// Start Draw
			OrientationEnum o = getOrietation(current, next);
			switch (o) {
			case NORTH:
				x = current.col*widthOfCol+widthOfCol/4;
				p1 = new org.eclipse.swt.graphics.Point(
						x, current.row*heightOfRow+heightOfRow/4);
				p2 = new org.eclipse.swt.graphics.Point(
						x, next.row*heightOfRow+heightOfRow*3/4);
				break;
			case SOUTH:
				x = current.col*widthOfCol+widthOfCol*3/4;
				p1 = new org.eclipse.swt.graphics.Point(
						x, current.row*heightOfRow+heightOfRow*3/4);
				p2 = new org.eclipse.swt.graphics.Point(
						x, next.row*heightOfRow+heightOfRow/4);
				break;
			case EAST:
				y = current.row*heightOfRow+heightOfRow/4;
				p1 = new org.eclipse.swt.graphics.Point(
						current.col*widthOfCol+widthOfCol*3/4, y);
				p2 = new org.eclipse.swt.graphics.Point(
						next.col*widthOfCol+widthOfCol/4, y);
				break;
			case WEST:
				y = current.row*heightOfRow+heightOfRow*3/4;
				p1 = new org.eclipse.swt.graphics.Point(
						current.col*widthOfCol+widthOfCol/4, y);
				p2 = new org.eclipse.swt.graphics.Point(
						next.col*widthOfCol+widthOfCol*3/4, y);
				break;
			default:
				break;
			}
			
			Color color = Color.DARK_GRAY;
			if(!isDuplicated(current, next, seeker)) {
				color = seekers[id-1];
				if(id == 1) g2d.setStroke(dashed);
				else g2d.setStroke(dotted);
			} else
				g2d.setStroke(solid);
			g.setColor(color);

			g.drawLine(p_old.x, p_old.y, p1.x, p1.y);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
//			g.drawLine(current.col*widthdOfCol+widthdOfCol/2, 
//					current.row*heightOfRow+heightOfRow/4, 
//					next.col*widthdOfCol+widthdOfCol/2, 
//					next.row*heightOfRow+heightOfRow/4);
			p_old = new org.eclipse.swt.graphics.Point(p2.x, p2.y);
			current = next;
		}
	}

//	Color[] colors = {Color.LIGHT_GRAY};
	Color[] colors = {Color.BLUE, Color.GREEN, Color.RED, 
			Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK, 
			Color.GRAY, Color.LIGHT_GRAY};
	
	public void drawPartitions(Graphics g, int id) {
		int width = getSize().width, height = getSize().height;
	    int heightOfRow = height / rows;
	    int widthOfCol = width / columns;
		Agent agent = parent.env.getSeekers()[id-1];
		Color color;
		Point p;
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				p = new Point(i,j);
				int l = agent.getPartitionLabelOfCell(p);
				if(l == -1) color = Color.DARK_GRAY;
				else color = colors[(l-1)%colors.length];
				g.setColor(color);
				g.fillRect(origin_x+j*widthOfCol+1, origin_y+i*heightOfRow+1, 
						widthOfCol-1, heightOfRow-1);
			}
		}
	}
	
	private boolean isDuplicated(Point current, Point next, Agent iSeeker) {
		int count = 1;
		for (Agent seeker : parent.env.getSeekers()) {
			if(seeker.getTrajectory().indexOf(current) == seeker.getTrajectory().indexOf(next)-1
					&& iSeeker != seeker)
				count++;
		}
		return count > 1;
	}

	private OrientationEnum getOrietation(Point from, Point to) {
		if(from.row == to.row) {
			if(from.col < to.col) return OrientationEnum.EAST;
			else return OrientationEnum.WEST;
		} else if(from.col == to.col) {
			if(from.row < to.row) return OrientationEnum.SOUTH;
			else return OrientationEnum.NORTH;
		} 
		return null;
	}
	
	public Point getCellOfEvent(MouseEvent e) {
		int width = getSize().width, height = getSize().height;
	    int heightOfRow = height / rows, widthOfCol = width / columns;
	    int row = e.getY()/heightOfRow, col = e.getX()/widthOfCol;
	    return new Point(row, col);
	}

}
