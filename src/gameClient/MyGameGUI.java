package gameClient;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI {
	DGraph g;
	ArrayList<Robot> r;
	ArrayList<Fruit> f;
	
//	public MyGameGUI() {
//		g=new DGraph();
//	}

	public MyGameGUI(DGraph t, List<String> r, List<String> f) {
		g=new DGraph(t);
		initRobots(r);
//		initFruits(f);

	}

	private void initRobots(List<String> r) {
		JSONObject o;
		for (int i = 0; i < r.size(); i++) {
			try {
				o = new JSONObject(r.get(i));
				this.r.add(new Robot(o.getInt("id")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void initFruits(List<String> f) {
		for (int i = 0; i < f.size(); i++) {
			this.f.add(new Fruit(f.get(i)));
		}
	}

	private void fruitsEdges() {

	}

	private void robotsPlace() {

	}
	
	public void game(int scenario_num) {
		game_service game = Game_Server.getServer(scenario_num);
		g.init(game.getGraph());
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
	}
	
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
	
	public void drawGraph() {
		double x0, x1, y0, y1, directX, directY;
		Collection<node_data> nodes = g.getV();
		for (node_data i : nodes) {
			x0 = g.getNode(i.getKey()).getLocation().x();
			y0 = g.getNode(i.getKey()).getLocation().y();
			StdDraw.setPenColor(Color.BLACK);
			StdDraw.setPenRadius(0.003);
			StdDraw.text(x0, y0 + 0.5, "" + i.getKey());
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setPenRadius(0.03);
			StdDraw.point(x0, y0);
			Collection<edge_data> edges = g.getE(i.getKey());
			for (edge_data j : edges) {
				x1 = g.getNode(j.getDest()).getLocation().x();
				y1 = g.getNode(j.getDest()).getLocation().y();
				directX = (9 * x1 + x0) / 10;
				directY = (9 * y1 + y0) / 10;
				StdDraw.setPenColor(Color.YELLOW);
				StdDraw.point(directX, directY);
				StdDraw.setPenColor(Color.BLACK);
				StdDraw.setPenRadius(0.003);
				StdDraw.line(x0, y0, x1, y1);
				StdDraw.text((x0 + x1) / 2, (y0 + y1) / 2 + 0.3, "" + j.getWeight());
			}
		}
	}

}	