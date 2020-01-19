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

public class GameManager {
	private MyGameGUI gui;
	private DGraph g;
	private List<Robot> r;
	private List<Fruit> f;
	private game_service game;
	private int numOfRobots, robotsOnGraph = 0;
	private KML_Logger k;
	private int timeForKML=0;
	
	public GameManager(int scenario_num) throws FileNotFoundException {
		game = Game_Server.getServer(scenario_num);
//		System.out.println(game.toString());
		g = new DGraph();
		g.init(game.getGraph());
		k=new KML_Logger();
		k.createPath(g);
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
//				robot = new Robot(ttt.getInt("id"));
				robot = this.getRobot(ttt.getInt("id"));
				robot.getInfoFromJson(ttt);
				robot.setDest(-1);
//				this.r.add(robot);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Robot getRobot(int id) {
		for (int i = 0; i < r.size(); i++) {
			if (r.get(i).getId() == id)
				return r.get(i);
		}
		return null;
	}
	
	public DGraph getGraph() {
		return g;
	}
	
	public List<Fruit> getFruits() {
		return f;
	}

	public game_service getGame() {
		return game;
	}
	
	public List<Robot> getRobots() {
		return r;
	}

	public MyGameGUI getGui() {
		return gui;
	}
	
	public KML_Logger getKML() {
		return k;
	}
	
	public void manualGame(double x, double y) {
			node_data n = findNode(x, y);
			if (n != null) {
				if (robotsOnGraph < numOfRobots) { /*At this point we place the robots on the graph according to the mouse clicks */
					game.addRobot(n.getKey());
					updateRobots(game.getRobots());
					if (robotsOnGraph == numOfRobots - 1) { /*At this point we placed the last robot on the graph so the game starts right away */
						game.startGame();
						MyGameGUI.getThread().start();
					}
					robotsOnGraph++;
				} else { /* At this point all the robots are placed on the graph and we move them according to the mouse clicks */
					for (int i = 0; i < robotsOnGraph; i++) {
						if (r.get(i).getDest() == -1 && g.getEdge(r.get(i).getSrc(), n.getKey()) != null) {
							game.chooseNextEdge(r.get(i).getId(), n.getKey());
							/* This break is located here to avoid a situation where two robots reach the same vertex */
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
			if (p.distance2D(n.getLocation()) <= 0.0003) {// 0.0003
				return n;
			}
		}
		return null;
	}
	
	public void automaticGame(int scenario_num) {
		robotsPlace();
		StdDraw.clear();
		game.startGame();
		MyGameGUI.getThread().start();
	}

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
			System.out.println(tEnd / 1000);
			System.out.println(f);
			if(timeForKML!=tSec) {
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
//			//if have robots that came to src of some fruit, eat the fruit.
//			 for(Robot robot:r) {
//				 if(robot.getDest()==robot.getSrc()) {
//				  for(Fruit fruit:f) {
//					if(fruit.getSrc()!=-1 && robot.getDest()==fruit.getSrc()) {
//						game.chooseNextEdge(robot.getId(),fruit.getDest());
//						fruit.setSrc(-1);
//					}
//				  }
//				 }

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
	 * @param   f-list of fruits
	 * @param i
	 * @param j
	 */
	private void swapFruits(List<Fruit> f, int i, int j) {
		Fruit temp = f.get(j);
		f.set(j, f.get(i));
		f.set(i, temp);
	}
	
}
