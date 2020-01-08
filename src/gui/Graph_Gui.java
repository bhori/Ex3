package gui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
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

import algorithms.Graph_Algo;

import dataStructure.DGraph;
import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Range;
import utils.StdDraw;

public class Graph_Gui implements ActionListener, Runnable {
	private Graph_Algo g;
	private graph g2;
    private int mc;
    private Thread t;
	
    public Graph_Gui(graph g) {
		this.g= new Graph_Algo();
		g2=g;
		this.mc = g.getMC();
		this.g.init(g2);
	    t=new Thread(this);
		initGUI();
		t.start();
		
	}

	private void initGUI() {
		StdDraw.setCanvasSize(1000, 600);
		StdDraw.setJMenuBar(createMenuBar());
		Range rx = get_x_Range();
		Range ry = get_y_Range();
		StdDraw.setXscale(rx.get_min() - 5, rx.get_max() + 5);
		StdDraw.setYscale(ry.get_min() - 5, ry.get_max() + 5);
		drawGraph();
	}
	
	private Range get_x_Range() {
		double minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
		Collection<node_data> nodes = g.getGraph().getV();
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
		Collection<node_data> nodes = g.getGraph().getV();
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
	 * draw the graph, the nodes color is blue, the edge color is black
	 * and on every edge there is a yellow point near to the dest node to show the direction of the edge.
	 */
	public void drawGraph() {
		double x0, x1, y0, y1, directX, directY;
		Collection<node_data> nodes = g.getGraph().getV();
		for (node_data i : nodes) {
			x0 = g.getGraph().getNode(i.getKey()).getLocation().x();
			y0 = g.getGraph().getNode(i.getKey()).getLocation().y();
			StdDraw.setPenColor(Color.BLACK);
			StdDraw.setPenRadius(0.003);
			StdDraw.text(x0, y0 + 0.5, "" + i.getKey());
			StdDraw.setPenColor(Color.BLUE);
			StdDraw.setPenRadius(0.03);
			StdDraw.point(x0, y0);
			Collection<edge_data> edges = g.getGraph().getE(i.getKey());
			for (edge_data j : edges) {
				x1 = g.getGraph().getNode(j.getDest()).getLocation().x();
				y1 = g.getGraph().getNode(j.getDest()).getLocation().y();
				directX = (9 * x1 + x0) / 10;
				directY = (9 * y1 + y0) / 10;
				StdDraw.setPenColor(Color.YELLOW);
				StdDraw.point(directX, directY);
				StdDraw.setPenColor(Color.BLACK);
				StdDraw.setPenRadius(0.003);
				StdDraw.line(x0, y0, x1, y1);
				StdDraw.text((x0 + x1) / 2, (y0 + y1) / 2 + 0.3, "" + j.getWeight());
				this.mc= this.g2.getMC();
			}
		}
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu1 = new JMenu("File");
		JMenu menu2 = new JMenu("Algorithms");
		menuBar.add(menu1);
		menuBar.add(menu2);
		JMenuItem menuItem1 = new JMenuItem("Save");
		JMenuItem menuItem2 = new JMenuItem("Load");
		JMenuItem menuItem3 = new JMenuItem("ShortestPath");
		JMenuItem menuItem4 = new JMenuItem("TSP");
		JMenuItem menuItem5 = new JMenuItem("IsConnected");
		menuItem1.addActionListener(this);
		menuItem2.addActionListener(this);
		menuItem3.addActionListener(this);
		menuItem4.addActionListener(this);
		menuItem5.addActionListener(this);
		menu1.add(menuItem1);
		menu1.add(menuItem2);
		menu2.add(menuItem5);
		menu2.add(menuItem3);
		menu2.add(menuItem4);
		return menuBar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Save")) {
			FileDialog chooser = new FileDialog(StdDraw.frame, null, FileDialog.SAVE);
			chooser.setVisible(true);
			String filename = chooser.getFile();
			if (filename != null) {
				g.save(chooser.getDirectory() + File.separator + chooser.getFile());
			}
		}
		if (action.equals("Load")) {
			FileDialog chooser2 = new FileDialog(StdDraw.frame, null, FileDialog.LOAD);
			chooser2.setVisible(true);
			String filename2 = chooser2.getFile();
			if (filename2 != null) {
				g.init(chooser2.getDirectory() + File.separator + chooser2.getFile());
				this.initGUI();
			}
		}
		if (action.equals("ShortestPath")) {
			final JFrame window = new JFrame("ShortestPath");
			final JTextField srcTxt = new JTextField();
			final JTextField destTxt = new JTextField();
			JLabel srcl = new JLabel("src: ");
			JLabel destl = new JLabel("dest: ");
			JButton enter = new JButton("enter");
			JButton cancel = new JButton("cancel");
			GridLayout g1 = new GridLayout();
			g1.setColumns(2);
			g1.setRows(3);

			window.setLayout(g1);
			window.add(srcl);
			window.add(srcTxt);
			window.add(destl);
			window.add(destTxt);
			window.add(cancel);
			window.add(enter);
			window.setSize(300, 200);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);

			enter.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						int src = Integer.parseInt(srcTxt.getText());
						int dest = Integer.parseInt(destTxt.getText());
						shortPathDraw(src, dest);
						window.setVisible(false);
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
		if (action.equals("TSP")) {
			final JFrame window = new JFrame("TSP");
			final JTextField targetsText = new JTextField();
			JLabel targetsLabel = new JLabel("Please inset List of targets in the shape 1,4,8...\n Targets: ");
			JButton enter = new JButton("Enter");
			JButton cancel = new JButton("Cancel");

			GridLayout g1 = new GridLayout();
			g1.setColumns(1);
			g1.setRows(2);

			window.setLayout(g1);
			window.add(targetsLabel);
			window.add(targetsText);
			window.add(cancel);
			window.add(enter);
			window.setSize(650, 150);
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			window.setVisible(true);

			enter.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						String tar = targetsText.getText();
						List<Integer> targets = new ArrayList<Integer>();
						while (!tar.isEmpty()) {
							int target;
							if (!tar.contains(",")) {
								target = Integer.parseInt(tar.substring(0));
								tar = "";
							} else {
								target = Integer.parseInt(tar.substring(0, tar.indexOf(",")));
								tar = tar.substring(tar.indexOf(",") + 1);
							}
							targets.add(target);
						}
						TSPDraw(targets);
						window.setVisible(false);
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
		if (action.equals("IsConnected")) {
			String result = "";
			if (g.isConnected() == true) {
				result = "the graph is connected";
			} else {
				result = "the graph is not connected";
			}
			JOptionPane.showMessageDialog(StdDraw.frame, result, "IsConnected", JOptionPane.PLAIN_MESSAGE);
		}
	}

	public void shortPathDraw(int src, int dest) {
		drawGraph();
		List<node_data> shortPath = new ArrayList<node_data>();
		shortPath = g.shortestPath(src, dest);
		if (shortPath != null) {
			for (int i = 0; i < shortPath.size() - 1; i++) {
				double x0 = shortPath.get(i).getLocation().x();
				double y0 = shortPath.get(i).getLocation().y();
				if (shortPath.get(i + 1) != null) {
					double x1 = shortPath.get(i + 1).getLocation().x();
					double y1 = shortPath.get(i + 1).getLocation().y();
					StdDraw.setPenColor(StdDraw.GREEN);
					StdDraw.line(x0, y0, x1, y1);
				}
			}
		}
	}

	public void TSPDraw(List<Integer> targets) {
		drawGraph();
		List<node_data> TSP = new ArrayList<node_data>();
		TSP = g.TSP(targets);
		if (TSP != null) {
			for (int i = 0; i < TSP.size() - 1; i++) {
				double x0 = TSP.get(i).getLocation().x();
				double y0 = TSP.get(i).getLocation().y();
				if (TSP.get(i + 1) != null) {
					double x1 = TSP.get(i + 1).getLocation().x();
					double y1 = TSP.get(i + 1).getLocation().y();
					StdDraw.setPenRadius(0.006);
					StdDraw.setPenColor(StdDraw.PINK);
					StdDraw.line(x0, y0, x1, y1);
				}
			}
		}
	}

	@Override
	public void run() {
		int i=3;
		
		while(true) {
			synchronized(this){
			     if(this.mc!=g2.getMC()) {
			    	this.mc=g2.getMC();
		            this.drawGraph();
			}
			}
			try {
				Thread.sleep(500);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		
		}
		
	}


}
