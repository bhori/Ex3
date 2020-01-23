package gameClient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.json.JSONObject;

import Server.Game_Server;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Range;
import utils.StdDraw;

/**
 * This class represents a graphical interface that enables to play and view a
 * game in a gui window. In this game there is a server that enables to load a
 * scenario between 0 and 23, In each scenario there is a graph, fruits and
 * robots, each fruit has a value and position on the graph edges and each robot
 * has a position on the graph. The goal of the game is to get as many points as
 * possible by eating fruits by the robots until the game ends.
 * 
 * There are two types of fruit: Apple - is located on a edge in the direction
 * from low vertex to high vertex. Banana - is located on a edge in the
 * direction from high vertex to low vertex.
 * 
 * This class enables to load a scenario from 0 to 23 from the gui window and
 * select one of the following options: Manual or Automatic play.
 * 
 * Manual game - In this option the whole game is managed by the user, the user
 * selects the position of the robots on the graph and moves them from one
 * vertex to another by mouse clicks.
 * 
 * Automatic game - With this option the game is managed automatically and
 * efficiently, the robots are initially positioned near the fruits with the
 * highest value and then moving on each time towards the fruit closest to them.
 * 
 * As long as the game is running you can see in the gui window the time left
 * for the game's score and the result achieved so far, after the game ends, a
 * window opens showing the final result.
 * 
 * This class enables to save the game that ended in a KML file, After the game
 * ends, a window opens asking if you want to save the game as a KML file, This
 * option is only possible after the game is over.
 * 
 * How to use - After creating an object from the class you need to run the
 * program and then a blank gui window opens, to load a game you have to click
 * "Game" on the menu and then "Select scenario", select a scenario from the
 * list and a game option (automatic / manual) and start playing. Just note that
 * when there is a running game there is no way to load a new game until it
 * finishes.
 * 
 * Game objects display - The vertices of the graph are blue, above each vertex
 * appears its serial number. The edges of the graph are black, each edge has a
 * yellow point near the destination vertex that indicates the direction of the
 * edge. Next to the yellow point is the weight of the edge (the time in seconds
 * it will take the robot to walk the edge at normal speed). For a fruit that is
 * on edge from low vertex to high vertex, a picture of an apple appears, and
 * for a fruit that is on edge from high vertex to low vertex, a picture of an
 * banana appears. The robots are shown using large points that are painted in
 * different colors (the options are - red, cyan, orange, pink, magenta).
 * 
 * This class uses the StdDraw library and Range and Point3D classes.
 * 
 * @author ItamarZiv-On, OriBH.
 *
 */
public class MyGameGUI implements ActionListener, MouseListener {
	private GameManager game_manager;
	private Range rx, ry;
	private int scenario;
	// Describes a specific window scale according to the X coordinate range (we
	// choosed pixel = rx.get_length() / 100), Used to draw certain information in
	// the gui window with the same pixel deviation.
	private double pixel;
	// Induction Whether to allow game changes by mouse click, this is only possible
	// for manual game.
	private boolean isManualGame = false;
	// Responsible for updating the game as long as it is still running.
	private static Thread t;
	// scenario list, used for selecting scenario from that list.
	private String[] scenarioList = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14",
			"15", "16", "17", "18", "19", "20", "21", "22", "23" };
	// options for the game, used for the window that opened after the user selected
	// scenario.
	private String[] gameOption = { "Manual", "Automatic" };
	private String[] infoOption = { "All games playd", "Level playd", "Best results", "My place in class"};
	// Colors for the robots.
	public static Color[] Colors = { Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA };
	public static final String jdbcUrl = "jdbc:mysql://db-mysql-ams3-67328-do-user-4468260-0.db.ondigitalocean.com:25060/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
	public static final String jdbcUser = "student";
	public static final String jdbcUserPassword = "OOP2020student";

	/**
	 * Initializes a blank graphic window and opens it.
	 */
	public MyGameGUI() {
		t = new Thread();
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
		StdDraw.text(rx.get_max(), ry.get_max(), "scenario: " + this.scenario);
		drawFruits();
		drawGraph();
		StdDraw.show();
	}

	/**
	 * Creates menu in the gui window with option to start the game.
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		JMenu menu2 = new JMenu("Info");
		menuBar.add(menu);
		menuBar.add(menu2);
//		JMenuItem menuItem1 = new JMenuItem("Select scenario");
		JMenuItem menuItem1 = new JMenuItem("Login");
		JMenuItem menuItem2 = new JMenuItem("My results");
		menuItem1.addActionListener(this);
		menuItem2.addActionListener(this);
		menu.add(menuItem1);
		menu2.add(menuItem2);
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
	 * every edge have a yellow point near to the destination node that show the
	 * direction of the edge, the weight of the edge represented near to the
	 * destination node too.
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

	/**
	 * Draws the robots on the graph.
	 */
	private void drawRobots() {
		double x, y;
		int numOfRobots = game_manager.getGame().getRobots().size();
		for (int a = 0; a < numOfRobots; a++) {
			int c = a % Colors.length;
			StdDraw.setPenColor(Colors[c]);
			x = game_manager.getRobots().get(a).getPos().x();
			y = game_manager.getRobots().get(a).getPos().y();
			StdDraw.setPenRadius(0.05);
			StdDraw.point(x, y);
		}
	}

	/**
	 * Draws the fruits on the graph.
	 */
	private void drawFruits() {
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

	/**
	 * Returns the thread of this class.
	 * 
	 * @return the thread of this class
	 */
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
			GameThread gm = new GameThread(game_manager, this, this.scenario, isManualGame);
			t = new Thread(gm);
			game_manager.manualGame(x_location, y_location);
			if (!(game_manager.getGame().isRunning())) {
				StdDraw.clear();
				drawGame();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	/**
	 * Initializes the game with the selected scenario and display it on the gui
	 * window.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String scenario = "";
		String Id = "";
		int id;
		String action = e.getActionCommand();
//		if (action.equals("Select scenario")) {
		if (action.equals("Login")) {
			if (t.isAlive()) {
				JOptionPane.showMessageDialog(StdDraw.frame,
						"The game is running, please wait until it ends to run another game.");
				return;
			}
			Id = JOptionPane.showInputDialog(StdDraw.frame, "Please insert your id:", "Login",
					JOptionPane.PLAIN_MESSAGE);
			try {
				id = Integer.parseInt(Id);
				Game_Server.login(id);
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(StdDraw.frame, "ERROR: this id is wrong, should be a number.", "ERROR",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			scenario = (String) JOptionPane.showInputDialog(StdDraw.frame, "Please choose scenario number:", "scenario",
					JOptionPane.PLAIN_MESSAGE, null, scenarioList, scenarioList[0]);
			try {
				int scenario_num = Integer.parseInt(scenario);
				this.scenario = scenario_num;
				game_manager = new GameManager(scenario_num);
				initGUI();
				selectGameOption(scenario_num);
			} catch (Exception e2) {
			}
		}
		if (action.equals("My results")) {
			Id = JOptionPane.showInputDialog(StdDraw.frame, "Please insert your id:", "Login",
					JOptionPane.PLAIN_MESSAGE);
			try {
				id = Integer.parseInt(Id);
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(StdDraw.frame, "ERROR: this id is wrong, should be a number.", "ERROR",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			int index = JOptionPane.showOptionDialog(StdDraw.frame, "Please choose option:", "Info Option",
					JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, infoOption, infoOption[0]);
			String info = infoOption[index];
			if (info.equals("All games playd")) {
				JOptionPane.showMessageDialog(StdDraw.frame, "Number of games playd: " + numOfGames(id),
						"All games playd", JOptionPane.INFORMATION_MESSAGE);
			}
			if (info.equals("Level playd")) {
				String level = (String) JOptionPane.showInputDialog(StdDraw.frame, "Please choose level:", "Level",
						JOptionPane.PLAIN_MESSAGE, null, scenarioList, scenarioList[0]);
				try {
					int scenario_num = Integer.parseInt(level);
					JOptionPane.showMessageDialog(StdDraw.frame,
							"Number of games playd in level " + scenario_num + ": " + numOfLevelPlayd(scenario_num, id),
							"All games playd", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e2) {
				}
			}
			if (info.equals("Best results")) {
				int [] score= new int [24];
				bestResults(id, score);
				String res = "";
				for (int i = 0; i < score.length; i++) {
					res+=i+") "+score[i]+"\n";
				}
				JOptionPane.showMessageDialog(StdDraw.frame, res, "results", JOptionPane.INFORMATION_MESSAGE);
			}
			if (info.equals("My place in class")) {
				int [] score= new int [24];
				bestResults(id, score);
				int [] place = new int [24];
				placeInClass(id, score, place);
				String res = "";
				for (int i = 0; i < place.length; i++) {
					res+=i+") "+place[i]+"\n";
				}
				JOptionPane.showMessageDialog(StdDraw.frame, res, "results", JOptionPane.INFORMATION_MESSAGE);
			}	
		}
	}

	/**
	 * enables to select how to play the game - Manual or Automatic.
	 * 
	 * @param scenario_num - The scenario number
	 */
	private void selectGameOption(int scenario_num) {
		int index = JOptionPane.showOptionDialog(StdDraw.frame, "Please choose game option:", "Game Option",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, gameOption, gameOption[0]);
		String gameOp = gameOption[index];
		if (gameOp.equals("Manual")) {
			isManualGame = true;
			if (game_manager.getRobots().size() == 1) {
				JOptionPane.showMessageDialog(StdDraw.frame,
						"Please select a location for 1 robot, please note that after the robot location the game will start immediately\r\n",
						"", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(StdDraw.frame, "Please select a location for "
						+ game_manager.getRobots().size()
						+ " robots, please note that after the last robot location the game will start immediately\r\n",
						"", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (gameOp.equals("Automatic")) {
			isManualGame = false;
			GameThread gm = new GameThread(game_manager, this, this.scenario, isManualGame);
			t = new Thread(gm);
			game_manager.automaticGame(scenario_num);
		}
	}

	/**
	 * updates the fruits and robots in the game and draws all parameters of the
	 * game.
	 */
	public void drawGame() {
		StdDraw.text(rx.get_max(), ry.get_max(), "scenario: " + this.scenario);
		game_manager.updateFruits(game_manager.getGame().getFruits());
		game_manager.updateRobots(game_manager.getGame().getRobots());
		drawFruits();
		drawGraph();
		drawRobots();
		if (game_manager.getGame().isRunning())
			drawInfo();
		StdDraw.show();
	}

	/**
	 * Displays the result achieved in the meantime and the time to end the game.
	 */
	public void drawInfo() {
		int result = 0;
		int timeToEnd = (int) game_manager.getGame().timeToEnd() / 1000;
		double rX = rx.get_length() / 20;
		double rY = ry.get_length() / 15;
		StdDraw.setPenColor(Color.BLACK);
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

	private int numOfGames(int id) {
		int ind = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where userID=" + id;
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			ind = 0;
			while (resultSet.next()) {
				ind++;
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ind;
	}

	private int numOfLevelPlayd(int level, int id) {
		int count = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where levelID=" + level;

			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			while (resultSet.next()) {
				if (resultSet.getInt("UserID") == id)
					count++;
			}
			resultSet.close();
			statement.close();
			connection.close();
		}

		catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return count;
	}

	private void bestResults(int id, int [] score) {
		int[] moves = { 290, 580, Integer.MAX_VALUE, 580, Integer.MAX_VALUE, 500, Integer.MAX_VALUE, Integer.MAX_VALUE,
				Integer.MAX_VALUE, 580, Integer.MAX_VALUE, 580, Integer.MAX_VALUE, 580, Integer.MAX_VALUE,
				Integer.MAX_VALUE, 290, Integer.MAX_VALUE, Integer.MAX_VALUE, 580, 290, Integer.MAX_VALUE,
				Integer.MAX_VALUE, 1140 };
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where userID=" + id;
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			for (int i = 0; i < 24; i++) {
				resultSet = statement.executeQuery(allCustomersQuery);
				while (resultSet.next()) {
					if(resultSet.getInt("levelID")==i && resultSet.getInt("moves")<=moves[i] && resultSet.getInt("score")>score[i]){
						score[i]=resultSet.getInt("score");
					}
				}
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void placeInClass(int id, int [] score, int [] place) {
		int[] moves = { 290, 580, Integer.MAX_VALUE, 580, Integer.MAX_VALUE, 500, Integer.MAX_VALUE, Integer.MAX_VALUE,
				Integer.MAX_VALUE, 580, Integer.MAX_VALUE, 580, Integer.MAX_VALUE, 580, Integer.MAX_VALUE,
				Integer.MAX_VALUE, 290, Integer.MAX_VALUE, Integer.MAX_VALUE, 580, 290, Integer.MAX_VALUE,
				Integer.MAX_VALUE, 1140 };
		int count=1;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcUserPassword);
			Statement statement = connection.createStatement();
			String allCustomersQuery = "SELECT * FROM Logs where levelID=" + 0;
			ResultSet resultSet = statement.executeQuery(allCustomersQuery);
			for (int i = 0; i < 24; i++) {
				allCustomersQuery = "SELECT * FROM Logs where levelID=" + i;
				resultSet = statement.executeQuery(allCustomersQuery);
				while (resultSet.next()) {
					if(resultSet.getInt("UserID")!=id && resultSet.getInt("moves")<=moves[i] && resultSet.getInt("score")>score[i]){
						count++;
					}
				}
				place[i]=count;
				count=1;
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("Vendor Error: " + sqle.getErrorCode());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}