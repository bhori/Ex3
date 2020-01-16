package gameClient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.json.JSONObject;

import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI implements ActionListener, MouseListener, Runnable {
	private double pixel;
	private boolean isManualGame = false;
	private Range rx;
	private Range ry;
	private static Thread t;
	private GameManager game_manager;
	private String[] scenarioList = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "13", "14", "15","16", "17", "18", "19", "20", "21", "22", "23" };
	private String[] gameOption = {"Manual","Automatic"};
	public static Color[] Colors = { Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA };

	public MyGameGUI() {
		initStartGUI();
	}

	/**
	 * Open empty gui window and enable to start game from the menuBar
	 */
	private void initStartGUI() {
		StdDraw.setCanvasSize(1000, 600);
		StdDraw.setJMenuBar(createMenuBar());
		StdDraw.addMouseListener(this);
		StdDraw.enableDoubleBuffering();
	}

	/**
	 * Updates the scale of the window in accordance to the range of the graph and
	 * draws the graph and the fruits.
	 */
	private void initGUI() {
		rx = get_x_Range();
		ry = get_y_Range();
		pixel = rx.get_length() / 100;
		double rX = rx.get_length() / 20;
		double rY = ry.get_length() / 15;
		StdDraw.clear();
		StdDraw.setXscale(rx.get_min() - rX, rx.get_max() + rX);
		StdDraw.setYscale(ry.get_min() - rY, ry.get_max() + rY);
		drawFruits();
		drawGraph();
		StdDraw.show();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		menuBar.add(menu);
		JMenuItem menuItem = new JMenuItem("Select scenario");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		return menuBar;
	}

	/**
	 * finds the range of x coordinates in the graph and return this range.
	 * 
	 * @return the range of x coordinates in the graph.
	 */
	private Range get_x_Range() {
		double minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		Collection<node_data> nodes = game_manager.getGraph().getV();
		for (node_data n : nodes) {
			if (n.getLocation().x() < minX) {
				minX = n.getLocation().x();
			} else if (n.getLocation().x() > maxX) {
				maxX = n.getLocation().x();
			}
		}
		Range rx = new Range(minX, maxX);
		return rx;
	}

	/**
	 * finds the range of y coordinates in the graph and return this range.
	 * 
	 * @return the range of y coordinates in the graph.
	 */
	private Range get_y_Range() {
		double minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
		Collection<node_data> nodes = game_manager.getGraph().getV();
		for (node_data n : nodes) {
			if (n.getLocation().y() < minY) {
				minY = n.getLocation().y();
			} else if (n.getLocation().y() > maxY) {
				maxY = n.getLocation().y();
			}
		}
		Range ry = new Range(minY, maxY);
		return ry;
	}

	/**
	 * draws the graph: vertices are painted in blue, edges are painted in black, on
	 * every edge have a yellow point near to the destination node that show the direction
	 * of the edge, the weight of the edge represented near to the destination node too.
	 */
	private void drawGraph() {
		double x0, x1, y0, y1, xDirection, yDirection;
		Collection<node_data> nodes = game_manager.getGraph().getV();
		for (node_data i : nodes) {
			x0 = game_manager.getGraph().getNode(i.getKey()).getLocation().x();
			y0 = game_manager.getGraph().getNode(i.getKey()).getLocation().y();
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setPenRadius(0.003);
			StdDraw.text(x0, y0 + pixel, "" + i.getKey());
			StdDraw.setPenRadius(0.03);
			StdDraw.point(x0, y0);
			Collection<edge_data> edges = game_manager.getGraph().getE(i.getKey());
			for (edge_data j : edges) {
				x1 = game_manager.getGraph().getNode(j.getDest()).getLocation().x();
				y1 = game_manager.getGraph().getNode(j.getDest()).getLocation().y();
				xDirection = (9 * x1 + x0) / 10;
				yDirection = (9 * y1 + y0) / 10;
				StdDraw.setPenColor(Color.YELLOW);
				StdDraw.setPenRadius(0.02);
				StdDraw.point(xDirection, yDirection);
				StdDraw.setPenColor(Color.BLACK);
				StdDraw.setPenRadius(0.003);
				StdDraw.line(x0, y0, x1, y1);
				StdDraw.text((x0 + 2 * x1) / 3, (y0 + 2 * y1) / 3 + pixel,
						"" + new DecimalFormat("0.0").format(j.getWeight()));
			}
		}
	}

	private void drawRobots() {
		double x, y;
		for (int a = 0; a < game_manager.getGame().getRobots().size(); a++) {
			int c = a % Colors.length;
			StdDraw.setPenColor(Colors[c]);
			x = game_manager.getRobots().get(a).getPos().x();
			y = game_manager.getRobots().get(a).getPos().y();
			StdDraw.setPenRadius(0.05);
			StdDraw.point(x, y);
		}
	}

	private void drawFruits() { // in scenario 17 there is apple on banana and we cannot see the banana!
		double x, y;
		for (Fruit fruit : game_manager.getFruits()) {
			x = fruit.getPos().x();
			y = fruit.getPos().y();
			if (fruit.getType() == 1) {
				StdDraw.picture(x, y, "src\\Apple.jpg", 3 * pixel, 3 * pixel);
			} else {
				StdDraw.picture(x, y, "src\\banana.jpeg", 3 * pixel, 3 * pixel);
			}
		}
	}
	
	public static Thread getThread() {
		return t;
	}

	/**
	 * Checks whether there is a vertex in the graph at the point of the mouse click
	 * (approximate), if so, places a robot at that vertex or moves a robot to it if
	 * there is a robot near that vertex. Used only in manual game!
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		double x_location = StdDraw.userX(e.getX());
		double y_location = StdDraw.userY(e.getY());
		if (isManualGame) {
			t = new Thread(this);
			game_manager.manualGame(x_location, y_location);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Select scenario")) {
			String scenario = (String) JOptionPane.showInputDialog(StdDraw.frame, "Please choose scenario number:",
					"scenario", JOptionPane.PLAIN_MESSAGE, null, scenarioList, scenarioList[0]);
			try {
				int scenario_num = Integer.parseInt(scenario);
				game_manager = new GameManager(scenario_num);
				initGUI();
				selectGameOption(scenario_num); // return
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(StdDraw.frame, "this scenario is wrong, should be a number between 0-23",
						"Error", JOptionPane.ERROR_MESSAGE);
//				return;
			}
		}
	}

	private void selectGameOption(int scenario_num) {
		int index = JOptionPane.showOptionDialog(StdDraw.frame, "Please choose game option:",
				"Game Option", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, gameOption, gameOption[0]);
		String gameOp = gameOption[index];
		if(gameOp.equals("Manual")) {
			isManualGame = true;
			if (game_manager.getRobots().size() == 1) {
				JOptionPane.showMessageDialog(StdDraw.frame,
						"Please select a location for 1 robot, please note that after the robot location the game will start immediately\r\n",
						"", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(StdDraw.frame, "Please select a location for " + game_manager.getRobots().size()
						+ " robots, please note that after the last robot location the game will start immediately\r\n",
						"", JOptionPane.INFORMATION_MESSAGE);
			}
		}else if(gameOp.equals("Automatic")) {
			t= new Thread(this);
			game_manager.automaticGame(scenario_num);
		}
	}

	/**
	 * updates the fruits and robots in the game and draws all parameters of the
	 * game.
	 */
	public void drawGame() {
		game_manager.updateFruits(game_manager.getGame().getFruits());
		game_manager.updateRobots(game_manager.getGame().move());
		drawFruits();
		drawGraph();
		drawRobots();
		drawInfo();
		StdDraw.show();
	}

	/**
	 * Displays the result achieved in the meantime and the time to end the game.
	 */
	public void drawInfo() {
		int result = 0;
		int timeToEnd = (int) game_manager.getGame().timeToEnd() / 1000;
		System.out.println(game_manager.getGame().timeToEnd());
		double rX = rx.get_length() / 20;
		double rY = ry.get_length() / 15;
		try {
			JSONObject gameServer = new JSONObject(game_manager.getGame().toString()).getJSONObject("GameServer");
			result = gameServer.getInt("grade");
		} catch (Exception e) {
			e.printStackTrace();
		}
		StdDraw.setPenColor();
		StdDraw.text(rx.get_min() - rX + 3.5 * pixel, ry.get_min() - rY + 3 * pixel, "score:" + result);
		StdDraw.text(rx.get_min() - rX + 3.5 * pixel, ry.get_min() - rY + 2 * pixel, "time:" + timeToEnd);
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		String results="";
		if (isManualGame) {
			while (game_manager.getGame().isRunning()) {
				game_manager.getGame().move();
				StdDraw.clear();
				drawGame();
				StdDraw.show();
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			results = game_manager.getGame().toString();
			isManualGame = false;
		} else {
			while (game_manager.getGame().isRunning()) {
					game_manager.autoMoveRobots();
					StdDraw.clear();
					drawGame();
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			results = game_manager.getGame().toString();
		}
		System.out.println("Game Over: " + results);
		JOptionPane.showMessageDialog(StdDraw.frame, "Game Over: " + results, "Game Over", JOptionPane.INFORMATION_MESSAGE);
	}
}