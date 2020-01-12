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
import javax.swing.JTextField;

import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Node;
import dataStructure.Edge;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI implements ActionListener, MouseListener {
	private DGraph g;
	private List<Robot> r;
	private List<Fruit> f;
	private game_service game;
	private double nodePenRadius = 0.03;
	private boolean isManualGame;
	public static Color[] Colors = { Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.GREEN,
			Color.BLUE };

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
//		robotsPlace();
	}
//	public MyGameGUI(DGraph t, List<String> r, List<String> f) {
//		g=new DGraph(t);
//		initRobots(r);
////		initFruits(f);
//
//	}
//	public MyGameGUI(DGraph t, List<String> r, List<String> f) {
//		g=new DGraph(t);
//		initRobots(r);
////		initFruits(f);
//	}

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
		Graph_Algo graph=new Graph_Algo(g);
		for(Fruit fruit: f) {
			e=graph.findEdgeToPoint(fruit.getPos());
			System.out.println(e);
			if(fruit.getType()==-1) {
				fruit.setSrc(Math.max(e.getSrc(), e.getDest()));
				fruit.setDest(Math.min(e.getSrc(), e.getDest()));
			}
			else {
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
			System.out.println(j.getValue() + ", " + j.getSrc());
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
			initRobots(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initFruits(game.getFruits());
		fruitsEdges();
		game();
	}

	public void game() {
		initGUI();
	}

	private void initStartGUI() {
		StdDraw.setCanvasSize(1000, 600);
		StdDraw.setJMenuBar(createMenuBar());
	}

	private void initGUI() {
		StdDraw.setCanvasSize(1000, 600);
		StdDraw.setJMenuBar(createMenuBar());
		StdDraw.addMouseListener(this);
		Range rx = get_x_Range();
		Range ry = get_y_Range();
		StdDraw.setXscale(rx.get_min() - 5, rx.get_max() + 5);
		StdDraw.setYscale(ry.get_min() - 5, ry.get_max() + 5);
		drawFruits();
		drawGraph();
//		StdDraw.addMouseListener(this);
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Algorithms");
		JMenu menu3 = new JMenu("Game");
		menuBar.add(menu1);
		menuBar.add(menu2);
		menuBar.add(menu3);
		JMenuItem menuItem1 = new JMenuItem("Save");
		JMenuItem menuItem2 = new JMenuItem("Load");
		JMenuItem menuItem3 = new JMenuItem("ShortestPath");
		JMenuItem menuItem4 = new JMenuItem("TSP");
		JMenuItem menuItem5 = new JMenuItem("IsConnected");
		JMenuItem menuItem6 = new JMenuItem("Select scenario");
		menuItem1.addActionListener(this);
		menuItem2.addActionListener(this);
		menuItem3.addActionListener(this);
		menuItem4.addActionListener(this);
		menuItem5.addActionListener(this);
		menuItem6.addActionListener(this);
		menu1.add(menuItem1);
		menu1.add(menuItem2);
		menu2.add(menuItem5);
		menu2.add(menuItem3);
		menu2.add(menuItem4);
		menu3.add(menuItem6);
		return menuBar;
	}

	private Range get_x_Range() {
		double minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		Collection<node_data> nodes = g.getV();
		for (node_data n : nodes) {
			if (n.getLocation().x() * 2500 < minX) {
				minX = n.getLocation().x() * 2500;
			} else if (n.getLocation().x() * 2500 > maxX) {
				maxX = n.getLocation().x() * 2500;
			}
		}
		Range rx = new Range(minX, maxX);
		return rx;
	}

	private Range get_y_Range() {
		double minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
		Collection<node_data> nodes = g.getV();
		for (node_data n : nodes) {
			if (n.getLocation().y() * 2500 < minY) {
				minY = n.getLocation().y() * 2500;
			} else if (n.getLocation().y() * 2500 > maxY) {
				maxY = n.getLocation().y() * 2500;
			}
		}
		Range ry = new Range(minY, maxY);
		return ry;
	}

	private void manualGame() {
//		StdDraw.addMouseListener(this);
	}

	private void automaticGame() {
		robotsPlace();
		drawRobots();
	}

	private void drawGraph() {
		double x0, x1, y0, y1, directX, directY;
		Collection<node_data> nodes = g.getV();
		for (node_data i : nodes) {
			x0 = g.getNode(i.getKey()).getLocation().x() * 2500;
			y0 = g.getNode(i.getKey()).getLocation().y() * 2500;
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setPenRadius(0.003);
			StdDraw.text(x0, y0 + 0.5, "" + i.getKey());
//			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setPenRadius(nodePenRadius);
			StdDraw.point(x0, y0);
			Collection<edge_data> edges = g.getE(i.getKey());
			for (edge_data j : edges) {
				x1 = g.getNode(j.getDest()).getLocation().x() * 2500;
				y1 = g.getNode(j.getDest()).getLocation().y() * 2500;
				directX = (9 * x1 + x0) / 10;
				directY = (9 * y1 + y0) / 10;
				StdDraw.setPenColor(Color.YELLOW);
				StdDraw.setPenRadius(0.02);
				StdDraw.point(directX, directY);
				StdDraw.setPenColor(Color.BLACK);
				StdDraw.setPenRadius(0.003);
				StdDraw.line(x0, y0, x1, y1);
				StdDraw.text((x0 + 2 * x1) / 3, (y0 + 2 * y1) / 3 + 0.3,
						"" + new DecimalFormat("0.0").format(j.getWeight()));
			}
		}
	}

	private void drawRobots() {
		double x, y;
		for (int a = 0; a < r.size(); a++) {
			int c = a % Colors.length;
			StdDraw.setPenColor(Colors[c]);
			x = r.get(a).getPos().x() * 2500;
			y = r.get(a).getPos().y() * 2500;
			StdDraw.setPenRadius(0.05);
			StdDraw.point(x, y);
		}
	}

	private void drawFruits() {
		double x, y;
		for (Fruit fruit : f) {
			x = fruit.getPos().x() * 2500;
			y = fruit.getPos().y() * 2500;
			if (fruit.getType() == 1) {
//				StdDraw.picture(x, y, "C:\\Users\\OriBH\\Desktop\\Apple.jpg", 2, 2);
				StdDraw.picture(x, y, "src\\Apple.jpg", 2, 2);
			} else {
//				StdDraw.picture(x, y, "C:\\Users\\OriBH\\Desktop\\banana.jpeg", 2, 2);
				StdDraw.picture(x, y, "src\\banana.jpeg", 2, 2);
			}
		}
	}

	public DGraph getGraph() {
		return g;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		double x_location = StdDraw.userX(e.getX())/2500;
		double y_location = StdDraw.userY(e.getY())/2500;
//		double x_location = StdDraw.mouseX();
//		double y_location = StdDraw.mouseY();
		node_data n = new Node();
		System.out.println(x_location + ", " + y_location);
		if (isManualGame) {
			for (Robot robot : r) {
				n = findNode(x_location, y_location);
				if(n!=null) {
					robot.setPos(n.getLocation());
					drawRobot(robot.getPos().x(), robot.getPos().y(), robot.getId());
				}
			}
			isManualGame=false;
		}

	}
	
	private void drawRobot(double x, double y, int id) {
		int c = id % Colors.length;
		StdDraw.setPenColor(Colors[c]);
		x = x * 2500;
		y = y * 2500;
		StdDraw.setPenRadius(0.05);
		StdDraw.point(x, y);
	}

	private node_data findNode(double x, double y) {
		Point3D p = new Point3D(x, y);
//		double radius = nodePenRadius;
		for (node_data n : g.getV()) {
			System.out.println(n.getLocation().x()+", "+n.getLocation().y());
			if (p.distance2D(n.getLocation()) <= nodePenRadius) {
				return n;
			}
		}
		return null;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public List<Robot> getRobots() {
		return r;
	}

	public List<Fruit> getFruits() {
		return f;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Select scenario")) {
			final JFrame window = new JFrame("Select scenario");
			final JTextField scenarioTxt = new JTextField();
			JLabel scenariol = new JLabel("Please choose scenario number between 0-23: ");
			JButton enter = new JButton("enter");
			JButton cancel = new JButton("cancel");
			GridLayout g1 = new GridLayout();
			g1.setColumns(2);
			g1.setRows(2);

			window.setLayout(g1);
			window.add(scenariol);
			window.add(scenarioTxt);
			window.add(cancel);
			window.add(enter);
			window.setSize(570, 200);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);

			enter.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						int scenario_num = Integer.parseInt(scenarioTxt.getText());
//						MyGameGUI s = new MyGameGUI(scenario_num);
//						s.game();
						game(scenario_num);
						window.setVisible(false);
						selectGameOption();
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, e2 + "");
					}
				}
			});
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					window.setVisible(false);
				}
			});
		}
	}

	private void selectGameOption() {
		final JFrame window = new JFrame("Select game option");
		JLabel gameOptionl = new JLabel("Please choose game option: ");
		JLabel empty = new JLabel();
		JButton manual = new JButton("Manual game");
		JButton Automatic = new JButton("Automatic game");
		GridLayout g1 = new GridLayout();
		g1.setColumns(2);
		g1.setRows(2);
		window.setLayout(g1);
		window.add(gameOptionl);
		window.add(empty);
		window.add(manual);
		window.add(Automatic);
		window.setSize(400, 200);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);

		manual.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				isManualGame = true;
				manualGame();
				window.setVisible(false);
			}
		});
		Automatic.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				automaticGame();
				window.setVisible(false);
			}
		});
	}

}