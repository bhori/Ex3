package gameClient;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI implements ActionListener, MouseListener, Runnable {
	private DGraph g;
	private List<Robot> r;
	private List<Fruit> f;
	private game_service game;
	private double pixel;
	private boolean isManualGame = false;
	private int numOfRobots, robotsOnGraph = 0;
	private Range rx;
	private Range ry;
	private Thread t;
	private autoGame auto;
	private String[] scenarioList = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "12", "13", "14", "15",
			"16", "17", "18", "19", "20", "21", "22", "23" };
	private String[] gameOption = {"Manual","Automatic"};
	public static Color[] Colors = { Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA };

	public MyGameGUI() {
		initStartGUI();
	}

	public MyGameGUI(int scenario_num) {
		game = Game_Server.getServer(scenario_num);
		g = new DGraph();
		g.init(game.getGraph());
		r = new ArrayList<Robot>();
		f = new ArrayList<Fruit>();
		try {
			JSONObject line = new JSONObject(game.toString());
			JSONObject info = line.getJSONObject("GameServer");
			initRobots(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initFruits(game.getFruits());
		fruitsEdges();
		drawFruits();
		robotsPlace();
		initGUI();
	}

	private void initRobots(JSONObject r) {
		try {
			int sumRobots = r.getInt("robots");
			for (int i = 0; i < sumRobots; i++) {
				this.r.add(new Robot(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initFruits(List<String> f) {
		for (int i = 0; i < f.size(); i++) {
			this.f.add(new Fruit(f.get(i)));
		}
	}

	private void fruitsEdges() {
//		ArrayListg.getV();
		edge_data e;
		Graph_Algo graph = new Graph_Algo(g);
		for (Fruit fruit : f) {
			e = graph.findEdgeToPoint(fruit.getPos());
			if (fruit.getType() == -1) {
				fruit.setSrc(Math.max(e.getSrc(), e.getDest()));
				fruit.setDest(Math.min(e.getSrc(), e.getDest()));
			} else {
				fruit.setSrc(Math.min(e.getSrc(), e.getDest()));
				fruit.setDest(Math.max(e.getSrc(), e.getDest()));
			}
		}
	}

	private void robotsPlace() {
		int i = 0;
		int node_id;
		Point3D p;
		node_data n;
		Fruit_Comperator c = new Fruit_Comperator();
		f.sort(c);
		for (Fruit j : f) {
			System.out.println(j.getValue() + ", " + j.getSrc() + ", " + j.getPos());
		}
		for (Robot robot : r) {
			if (i < f.size()) {
				node_id = f.get(i).getSrc();
				n = new Node(g.getNode(node_id));
				p = new Point3D(n.getLocation());
				robot.setPos(p);
				i++;
			} else {
				i = 0;
				node_id = f.get(i).getSrc();
				n = new Node(g.getNode(node_id));
				p = new Point3D(n.getLocation());
				robot.setPos(p);
				i++;
			}
			this.game.addRobot(node_id);
		}
	}

	public void updateFruits(List<String> f) {
		this.f.clear();
		initFruits(f);
		fruitsEdges();
	}

	public void updateRobots(List<String> r) {
//		this.r=new ArrayList<Robot>();
		Robot robot;
		for (int i = 0; i < r.size(); i++) {
			String robot_json = r.get(i);
			try {
				JSONObject line = new JSONObject(robot_json);
				JSONObject ttt = line.getJSONObject("Robot");
				robot = new Robot(ttt.getInt("id"));
				robot = this.getRobot(ttt.getInt("id"));
				robot.getInfoFromJson(ttt);
				robot.setDest(-1);
//				this.r.add(robot);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void game(int scenario_num) {
		game = Game_Server.getServer(scenario_num);
		g = new DGraph();
		g.init(game.getGraph());
		r = new ArrayList<Robot>();
		f = new ArrayList<Fruit>();
		try {
			JSONObject line = new JSONObject(game.toString());
			JSONObject info = line.getJSONObject("GameServer");
			numOfRobots = info.getInt("robots");
			initRobots(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initFruits(game.getFruits());
		fruitsEdges();
		initGUI();
//		game();
	}

//	public void game() {
//		initGUI();
//	}

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
		Collection<node_data> nodes = g.getV();
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
		Collection<node_data> nodes = g.getV();
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

	private void manualGame() {
//		t = new Thread(this);
//		game.startGame();
//		t.start();
	}

	private void automaticGame(int scenario_num) {
		auto = new autoGame(scenario_num);
		robotsPlace();
//		drawRobots();
//		StdDraw.show();
		StdDraw.clear();
		t = new Thread(this);
		auto.getGui().game.startGame();
		t.start();
	}

	/**
	 * draws the graph: vertices are painted in blue, edges are painted in black, on
	 * every edge have a yellow point near to the destination node that show the direction
	 * of the edge, the weight of the edge represented near to the destination node too.
	 */
	private void drawGraph() {
		double x0, x1, y0, y1, xDirection, yDirection;
		Collection<node_data> nodes = g.getV();
		for (node_data i : nodes) {
			x0 = g.getNode(i.getKey()).getLocation().x();
			y0 = g.getNode(i.getKey()).getLocation().y();
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setPenRadius(0.003);
			StdDraw.text(x0, y0 + pixel, "" + i.getKey());
			StdDraw.setPenRadius(0.03);
			StdDraw.point(x0, y0);
			Collection<edge_data> edges = g.getE(i.getKey());
			for (edge_data j : edges) {
				x1 = g.getNode(j.getDest()).getLocation().x();
				y1 = g.getNode(j.getDest()).getLocation().y();
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
		for (int a = 0; a < game.getRobots().size(); a++) {
			int c = a % Colors.length;
			StdDraw.setPenColor(Colors[c]);
			x = r.get(a).getPos().x();
			y = r.get(a).getPos().y();
			StdDraw.setPenRadius(0.05);
			StdDraw.point(x, y);
		}
	}

	private void drawFruits() { // in scenario 17 there is apple on banana and we cannot see the banana!
		double x, y;
		for (Fruit fruit : f) {
			x = fruit.getPos().x();
			y = fruit.getPos().y();
			if (fruit.getType() == 1) {
				StdDraw.picture(x, y, "src\\Apple.jpg", 3 * pixel, 3 * pixel);
			} else {
				StdDraw.picture(x, y, "src\\banana.jpeg", 3 * pixel, 3 * pixel);
			}
		}
	}

	public DGraph getGraph() {
		return g;
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
		node_data n = new Node();
		if (isManualGame) {
			n = findNode(x_location, y_location);
			if (n != null) {
				if (robotsOnGraph < numOfRobots) { /*At this point we place the robots on the graph according to the mouse clicks */
					game.addRobot(n.getKey());
					updateRobots(game.getRobots());
					drawRobots();
					StdDraw.show();
					if (robotsOnGraph == numOfRobots - 1) { /*At this point we placed the last robot on the graph so the game starts right away */
						game.startGame();
						t = new Thread(this);
						t.start();
					}
					robotsOnGraph++;
//				} else if (robotsOnGraph == numOfRobots - 1) { /** At this point we place the last robot on the graph and starts the game */
//					game.addRobot(n.getKey());
//					updateRobots(game.getRobots());
//					drawRobots();
//					StdDraw.show();
//					robotsOnGraph++;
//					game.startGame();
//					t = new Thread(this);
//					t.start();
				} else { /* At this point all the robots are placed on the graph and we move them according to the mouse clicks */
					for (int i = 0; i < robotsOnGraph; i++) {
						if (r.get(i).getDest() == -1 && g.getEdge(r.get(i).getSrc(), n.getKey()) != null) {
							game.chooseNextEdge(r.get(i).getId(), n.getKey());
							StdDraw.show();
							/* This break is located here to avoid a situation where two robots reach the same vertex */
							break;
						}
					}

				}
			}
		}
	}

	/**
	 * checks if there is a node in location (x,y) and return this node use in
	 * manual game to check the points that clicked with the mouse
	 * 
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @return the node with the location (x,y) (approximately), null if none.
	 */
	private node_data findNode(double x, double y) {
		Point3D p = new Point3D(x, y);
		for (node_data n : g.getV()) {
			if (p.distance2D(n.getLocation()) <= 0.0003) {// 0.0003
				return n;
			}
		}
		return null;
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

	public List<Robot> getRobots() {
		return r;
	}

	public Robot getRobot(int id) {
		for (int i = 0; i < r.size(); i++) {
			if (r.get(i).getId() == id)
				return r.get(i);
		}
		return null;
	}

	public List<Fruit> getFruits() {
		return f;
	}

	public game_service getGame() {
		return game;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Select scenario")) {
			String scenario = (String) JOptionPane.showInputDialog(StdDraw.frame, "Please choose scenario number:",
					"scenario", JOptionPane.PLAIN_MESSAGE, null, scenarioList, scenarioList[0]);
			try {
				int scenario_num = Integer.parseInt(scenario);
				game(scenario_num); // return
				selectGameOption(scenario_num); // return
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(StdDraw.frame, "this scenario is wrong, should be a number between 0-23",
						"Error", JOptionPane.ERROR_MESSAGE);
//				return;
			}
//			final JFrame window = new JFrame("Select scenario");
//			final JTextField scenarioTxt = new JTextField();
//			JLabel scenariol = new JLabel("Please choose scenario number between 0-23: ");
//			JButton enter = new JButton("enter");
//			JButton cancel = new JButton("cancel");
//			GridLayout g1 = new GridLayout();
//			g1.setColumns(2);
//			g1.setRows(2);
//
//			window.setLayout(g1);
//			window.add(scenariol);
//			window.add(scenarioTxt);
//			window.add(cancel);
//			window.add(enter);
//			window.setSize(570, 200);
//			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			window.setVisible(true);
//
//			enter.addActionListener(new ActionListener() {
//
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					try {
//						int scenario_num = Integer.parseInt(scenarioTxt.getText());
////						MyGameGUI s = new MyGameGUI(scenario_num);
////						s.game();
//						game(scenario_num);
//						window.setVisible(false);
////						StdDraw.show();
//						selectGameOption(scenario_num);
//					} catch (Exception e2) {
//						JOptionPane.showMessageDialog(null, e2 + "");
//					}
//				}
//			});
//			cancel.addActionListener(new ActionListener() {
//
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					window.setVisible(false);
//				}
//			});
		}
	}

	private void selectGameOption(int scenario_num) {
		int index = JOptionPane.showOptionDialog(StdDraw.frame, "Please choose game option:",
				"Game Option", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, gameOption, gameOption[0]);
		String gameOp = gameOption[index];
		if(gameOp.equals("Manual")) {
			isManualGame = true;
			if (numOfRobots == 1) {
				JOptionPane.showMessageDialog(StdDraw.frame,
						"Please select a location for 1 robot, please note that after the robot location the game will start immediately\r\n",
						"", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(StdDraw.frame, "Please select a location for " + numOfRobots
						+ " robots, please note that after the last robot location the game will start immediately\r\n",
						"", JOptionPane.INFORMATION_MESSAGE);
			}
//			}manualGame();
		}else if(gameOp.equals("Automatic")) {
			automaticGame(scenario_num);
		}
	}

	/**
	 * updates the fruits and robots in the game and draws all parameters of the
	 * game.
	 */
	public void drawGame() {
		updateFruits(game.getFruits());
		updateRobots(game.move());
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
		int timeToEnd = (int) game.timeToEnd() / 1000;
		System.out.println(game.timeToEnd());
		double rX = rx.get_length() / 20;
		double rY = ry.get_length() / 15;
		try {
			JSONObject gameServer = new JSONObject(game.toString()).getJSONObject("GameServer");
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
			while (game.isRunning()) {
				game.move();
				StdDraw.clear();
				drawGame();
				StdDraw.show();
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			results = game.toString();
			isManualGame = false;
		} else {
			while (auto.getGui().game.isRunning()) {
				synchronized (this) {
					auto.autoMoveRobots();
					StdDraw.clear();
					auto.getGui().drawGame();
				}
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			results = auto.getGui().game.toString();
		}
		System.out.println("Game Over: " + results);
		JOptionPane.showMessageDialog(StdDraw.frame, "Game Over: " + results, "Game Over", JOptionPane.INFORMATION_MESSAGE);
	}
}