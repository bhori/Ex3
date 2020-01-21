package gameClient;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;
import utils.StdDraw;

/**
 * This class represents a game management system, In this game there is a server that enables to load a scenario between 0 and 23,
 * In each scenario there is a graph, fruits and robots, each fruit has a value and position on the graph edges and each robot has a position on the graph.
 * The goal of the game is to get as many points as possible by eating fruits by the robots.
 * 
 * There are two types of fruit:
 * Apple - is located on a edge in the direction from low vertex to a high vertex.
 * Banana - is located on a edge in the direction from high vertex to low vertex.
 * 
 * This class enables to load a scenario from 0 to 23 and select one of the following options: Manual or Automatic play.
 * 
 * Manual game - In this option the whole game is managed by the user, the user selects the position of the robots on the graph
 * and moves them from one vertex to another by mouse clicks.
 * 
 * Automatic game - With this option the game is managed automatically and efficiently,
 * the robots are initially positioned near the fruits with the highest value and then moving on each time towards the fruit closest to them.
 * 
 * This class enables to save the game that ended in a KML file. 
 * 
 * @author ItamarZiv-On, OriBH.
 *
 */
public class GameManager {
	private DGraph g;
	private List<Robot> r;
	private List<Fruit> f;
	private game_service game;
	private int numOfRobots, robotsOnGraph = 0;
	private KML_Logger k;
	private int timeForKML=0;

	/**
	 * Initializes a game according to the selected scenario.
	 * @param scenario_num - The scenario number.
	 *  @throws FileNotFoundException
	 */
	public GameManager(int scenario_num) throws FileNotFoundException {
		game = Game_Server.getServer(scenario_num);
		g = new DGraph();
		g.init(game.getGraph());
		k=new KML_Logger();
		k.createPath(g);
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
	}
	
	/**
	 * The method received a JSONObject and build and add Robots to the list<Robot> r of the class according to this object 
	 * that contain information about number of robots in this game. .
	 * @param r
	 */
	private void initRobots(JSONObject r) {
		try {
			numOfRobots = r.getInt("robots");
			for (int i = 0; i < numOfRobots; i++) {
				this.r.add(new Robot(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The method received a list of Json String and build and add Fruits to the list<Fruit> r of the class according to this list.
	 * @param f
	 */
	private void initFruits(List<String> f) {
		for (int i = 0; i < f.size(); i++) {
			this.f.add(new Fruit(f.get(i)));
		}
	}

	/**
	 * Placing the fruits on their edges.
	 */
	private void fruitsEdges() {
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
	
	/**
	 *  Placing the robots on vertices in the graph adjacent to the fruit with the highest value.
	 *  Used only in automatic game!
	 */
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

	/**
	 * The method received list of Json strings that contain information about the robots in this game from the server, 
	 * and according to the list set the id of the robots of this class. 
	 * @param r
	 */
	private void setIdForRobots(List<String> r) {
		for (int i = 0; i < r.size(); i++) {
			String robot_json = r.get(i);
			try {
				JSONObject line = new JSONObject(robot_json);
				JSONObject ttt = line.getJSONObject("Robot");
			    this.r.get(i).setId(ttt.getInt("id"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The method received a list of Json String and update the fruits  of the class according to this list.
	 * For each update the fruits rebuilding. 
	 * @param f - List of String with the data about the fruits.
	 */
	public void updateFruits(List<String> f) {
		this.f.clear();
		initFruits(f);
		fruitsEdges();
	}

	/**
	 * The method received a list of Json String and update the robots of the class according to this list.
	 * @param r - List of String with the data about the robots.
	 */
	public void updateRobots(List<String> r) {
		Robot robot;
		for (int i = 0; i < r.size(); i++) {
			String robot_json = r.get(i);
			try {
				JSONObject line = new JSONObject(robot_json);
				JSONObject ttt = line.getJSONObject("Robot");
				robot = this.getRobot(ttt.getInt("id"));
				robot.getInfoFromJson(ttt);
				robot.setDest(-1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The method received id and return the robot with this id if exist, if not exist return null.
	 * @param id - id of the robot.
	 * @return the robot with the selected id, null if none.
	 */
	public Robot getRobot(int id) {
		for (int i = 0; i < r.size(); i++) {
			if (r.get(i).getId() == id)
				return r.get(i);
		}
		return null;
	}

	/**
	 * The method return the graph of the class.
	 * @return the graph of this class.
	 */
	public DGraph getGraph() {
		return g;
	}

	/**
	 * The method return the list<Fruit> of the class.
	 * @return return the list<Fruit> of the class.
	 */
	public List<Fruit> getFruits() {
		return f;
	}

	/**
	 * The method return the game_service of the class.
	 * @return return the game_service of the class.
	 */
	public game_service getGame() {
		return game;
	}

	/**
	 * The method return the list<Robot> of the class.
	 * @return the list<Robot> of the class.
	 */
	public List<Robot> getRobots() {
		return r;
	}

	/**
	 * The method return the KML_Logger of the class.
	 * @return
	 */

	public KML_Logger getKML() {
		return k;
	}

	/**
	 * The method return TimeForKML.
	 * @return
	 */
	public int getTimeForKML() {
		return timeForKML;
	}

	/**
	 * The method received time and change the timeForKML to this time.
	 * @param time
	 */
	public void setTimeForKML(int time) {
		timeForKML=time;
	}

	/**
	 * Manual game management system - places the robots on the graph and moves them during the game according to the mouse clicks.
	 * @param x - The x coordinate in the graph according to the mouse click.
	 * @param y - The y coordinate in the graph according to the mouse click.
	 */
	public void manualGame(double x, double y) {
			node_data n = findNode(x, y);
			if (n != null) {
				if (robotsOnGraph < numOfRobots) { /*At this point we place the robots on the graph according to the mouse clicks */
					game.addRobot(n.getKey());
					this.setIdForRobots(game.getRobots());
					updateRobots(game.getRobots());
					if (robotsOnGraph == numOfRobots - 1) { /*At this point we placed the last robot on the graph so the game starts right away */
						updateFruits(game.getFruits());
						for (Fruit fruit : f)
							k.addFruitPlace(fruit.getPos().x(), fruit.getPos().y(), fruit.getType());
						for (Robot robot : r)
						    k.addRobotPlace(robot.getPos().x(), robot.getPos().y(), robot.getId());
						game.startGame();
						MyGameGUI.getThread().start();
					}
					robotsOnGraph++;
				} else { /* At this point all the robots are placed on the graph and we move them according to the mouse clicks */
					updateRobots(game.getRobots());
					updateFruits(game.getFruits());
					for (int i = 0; i < robotsOnGraph; i++) {
						if (r.get(i).getDest() == -1 && g.getEdge(r.get(i).getSrc(), n.getKey()) != null) {
							game.chooseNextEdge(r.get(i).getId(), n.getKey());
							/* This break is located here to avoid a situation where two robots (or more) reach the same vertex */
							break;
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
			if (p.distance2D(n.getLocation()) <= 0.0003) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Automatic game management system without displaying in a gui window - places the 
	 * robots on the graph and moves them automatically during the game.
	 */
	public void automaticGame() {
		robotsPlace();
		GameThread gm = new GameThread(this);
		Thread t = new Thread(gm);
		game.startGame();
		t.start();
	}

	/**
	 * Automatic game management system with gui window display - places the 
	 * robots on the graph and moves them automatically during the game.
	 */
	public void automaticGame(int scenario_num) {
		robotsPlace();
		this.setIdForRobots(game.getRobots());
		System.out.println(game.getRobots());
		StdDraw.clear();
//		Thread t = new Thread(gt);
		game.startGame();
		MyGameGUI.getThread().start();
//		t.start();
	}

	/**
	 * The method first of all doing the next move in the game, than update the fruits and robots of the class 
	 * and prepare the next step of each robot.The method sort the fruit in the list by they value the biggest first
	 * check who the robot that can eat the fruit first, and this robot go to the fruit, 
	 * and the same check for the fruits that left with the robots that left. 
	 */
	public void autoMoveRobots() {
		List<String> log = game.move();
		System.out.println(log);
		if (log != null) {
			updateFruits(game.getFruits());
			updateRobots(log);
			double minTimeToGet;
			Robot minRob = null;
			double timeToGet;
			sortFruits();
			long tEnd = game.timeToEnd();
			int tSec=(int)(tEnd / 500);
			if(timeForKML!=tSec) {//this for the kml that draw the game something like every half second. 
				for (Fruit fruit : f)
					k.addFruitPlace(fruit.getPos().x(), fruit.getPos().y(), fruit.getType());
				for (Robot robot : r)
				    k.addRobotPlace(robot.getPos().x(), robot.getPos().y(), robot.getId());
				timeForKML=tSec;
			}
			// find the short time between the robots to the fruit(the fruits organize in
			// order to the value of the fruits, the biggest value first)
			for (Fruit fruit : f) {
				if (fruit.getSrc() != -1) {
					minTimeToGet = Integer.MAX_VALUE;
					for (Robot robot : r) {
						if (robot.getDest() == -1) {
							timeToGet = (g.getEdge(fruit.getSrc(), fruit.getDest()).getWeight()
									+ new Graph_Algo(g).shortestPathDist(robot.getSrc(),
											fruit.getSrc()))
									/ robot.getSpeed();
							if (timeToGet < minTimeToGet) {
								minTimeToGet = timeToGet;
								minRob = robot;
							}
						}
					}
					// if left time to eat this fruit
					if (minTimeToGet <= tEnd) {
						// if have robots that came to src of some fruit, eat the fruit.
						if (minRob.getSrc() == fruit.getSrc()) {
							game.chooseNextEdge(minRob.getId(), fruit.getDest());
						} else {
							int dest = new Graph_Algo(g).shortestPath(minRob.getSrc(), fruit.getSrc())
									.get(1).getKey();
							game.chooseNextEdge(minRob.getId(), dest);
							minRob.setDest(dest);
							fruit.setSrc(-1);
						}
					}
				}
			}

		}
	}

	/**
	 * The method is sorting the fruits according to their value. The fruit with the
	 * biggest value first.
	 */
	private void sortFruits() {
		if (f.size() > 1) {
			for (int i = 0; i < f.size() - 1; i++) {
				for (int j = i + 1; j > 0; j--) {
					if (f.get(j).getValue() > f.get(j - 1).getValue())
						swapFruits(f, j, j - 1);
				}
			}
		}
	}

	/**
	 * Simple swap method, received two indexes and swap between the fruits that in
	 * this indexes in the list.
	 * 
	 * @param f-list of fruits
	 * @param i
	 * @param j
	 */
	private void swapFruits(List<Fruit> f, int i, int j) {
		Fruit temp = f.get(j);
		f.set(j, f.get(i));
		f.set(i, temp);
	}
	
}
