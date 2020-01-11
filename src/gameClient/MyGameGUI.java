package gameClient;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI implements MouseListener {
	private DGraph g;
	private List<Robot> r;
	private List<Fruit> f;
	private game_service game;
	public static Color[] Colors = { Color.RED, Color.CYAN, Color.ORANGE, Color.PINK, Color.MAGENTA, Color.GREEN,Color.BLUE };

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
		for (Fruit fruit : f) {

		}
	}

	private void robotsPlace() {
		int i = 0;
		int j = 0;
		ArrayList<Integer> node_id = new ArrayList<Integer>();
		for (node_data n : g.getV()) {
			node_id.add(n.getKey());
		}
		while (i < r.size()) { // it is just to try the gui, might be changed..
			if (j < node_id.size()) {
				r.get(i).setPos(g.getNode(node_id.get(j)).getLocation());
			} else {
				j = 0;
				r.get(i).setPos(g.getNode(node_id.get(j)).getLocation());
			}
			i++;
			j++;
		}
	}

	public void game(int scenario_num) {
		g = new DGraph();
		game_service game = Game_Server.getServer(scenario_num);
		g.init(game.getGraph());
		initGUI();
	}

	public void game() {
		initGUI();
	}

	private void initGUI() {
		StdDraw.setCanvasSize(1000, 600);
//		StdDraw.setJMenuBar(createMenuBar());
		Range rx = get_x_Range();
		Range ry = get_y_Range();
		StdDraw.setXscale(rx.get_min() - 5, rx.get_max() + 5);
		StdDraw.setYscale(ry.get_min() - 5, ry.get_max() + 5);
		drawGraph();
		drawRobots();
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
			StdDraw.setPenRadius(0.03);
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
				StdDraw.text((x0 + 2 * x1) / 3, (y0 + 2 * y1) / 3 + 0.3,"" + new DecimalFormat("0.0").format(j.getWeight()));
			}
		}
	}

	private void drawRobots() {
		double x, y;
		robotsPlace();
		for (int a = 0; a < r.size(); a++) {
			int c = a % Colors.length;
			StdDraw.setPenColor(Colors[c]);
			x = r.get(a).getPos().x() * 2500;
			y = r.get(a).getPos().y() * 2500;
			StdDraw.setPenRadius(0.05);
			StdDraw.point(x, y);
		}
	}

	public DGraph getGraph() {
		return g;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		

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

}