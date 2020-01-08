package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.Node;
import dataStructure.graph;
import dataStructure.node_data;
import gui.Graph_Gui;
import utils.Point3D;

class Graph_AlgoTest {
    private static ArrayList<Graph_Algo> gr;
     
    @BeforeAll
	static void initTest() {
    	gr=initialTest();
    	gr.get(2).save("Test");
    }
	
	@Test
	void initFromFileTest() {
		Graph_Algo g= new Graph_Algo();
		g.init("Test");
		for(int i=2; i<6; i++) {
			for(int j=2; j<6; j++) {
				assertEquals((int)gr.get(2).shortestPathDist(j, i), (int)g.shortestPathDist(j, i) );
			}	
		}
	}

	@Test
	void TSPTest() {
		List<Integer> tsp= new ArrayList<Integer>();
		tsp.add(5);
		tsp.add(4);
		tsp.add(3);
		tsp.add(2);
		try {
		List<node_data> t1= gr.get(2).TSP(tsp);
		System.out.println("***TSP***");
		for(node_data n : t1) {
			System.out.print(n.getKey()+" , ");
		}
		System.out.println("\n***END***");
		}catch(Exception e) {e.printStackTrace();}
		
		tsp.clear();
		tsp.add(6);
		tsp.add(8);
		tsp.add(7);
		
		List<node_data> t2= gr.get(3).TSP(tsp);
		System.out.println("***TSP***");
		  for(node_data n : t2) {
			System.out.print(n.getKey()+" , ");
		  }
		System.out.println("\n***END***");
		tsp.add(9);
		try {
	    t2= gr.get(3).TSP(tsp);
		assertEquals(null,t2);
		}
		catch(Exception e) {e.printStackTrace();}
		
		
	}

	@Test
	void copyTest() {
		graph g;
		g=gr.get(2).copy();
		g.addNode(new Node(new Point3D(0,0)));
		Graph_Algo t= new Graph_Algo();
		t.init(g);
		assertFalse(t.isConnected());
		assertTrue(gr.get(2).isConnected());
	}

	@Test
	void shortestPathDistTest() {
		boolean dist=true;
		try {
		gr.get(0).shortestPathDist(3, 1);
		}catch(Exception e) {dist=false;}
		assertFalse(dist);
		dist=true;
		try {
			gr.get(1).shortestPathDist(3, 1);
			}catch(Exception e) {dist=false;}
			assertFalse(dist);
		assertEquals(24,(int)gr.get(2).shortestPathDist(3, 5));
		assertEquals(4,(int)gr.get(3).shortestPathDist(8, 6));
		assertEquals(Integer.MAX_VALUE,(int)gr.get(3).shortestPathDist(7, 9));
	}
	
	@Test
	void shortestPathTest() {
		List<node_data> p1=gr.get(1).shortestPath(1, 1);
		assertEquals(gr.get(1).getGraph().getNode(1), p1.get(0));
		List<node_data> p2=gr.get(2).shortestPath(3, 5);
		assertEquals(3, p2.get(0).getKey());
		assertEquals(5, p2.get(1).getKey());
		List<node_data> p3=gr.get(3).shortestPath(6, 8);
		assertEquals(6, p3.get(0).getKey());
		assertEquals(7, p3.get(1).getKey());
		assertEquals(8, p3.get(2).getKey());
	}

	@Test
	void isConnectedTest() {
		assertTrue(gr.get(0).isConnected());
		assertTrue(gr.get(1).isConnected());
		assertTrue(gr.get(2).isConnected());
		assertFalse(gr.get(3).isConnected());
	}
	public static ArrayList<Graph_Algo> initialTest() {
		ArrayList<Graph_Algo> gr= new ArrayList<Graph_Algo>();
		Graph_Algo g0=new Graph_Algo();
		Graph_Algo g1=new Graph_Algo();
		Graph_Algo g2=new Graph_Algo();
		Graph_Algo g3=new Graph_Algo();
		gr.add(g0);
		gr.add(g1);
		gr.add(g2);
		gr.add(g3);
		gr.get(1).getGraph().addNode(new Node(new Point3D(4,8)));
		graph g=new DGraph();
		for(int i=0; i<4; i++) {
			Point3D p= new Point3D(i,-i);
			node_data n= new Node(p);
			gr.get(2).getGraph().addNode(n);
		}
		for(int i=2; i<6; i++) {
			for(int j=2; j<6; j++) {
				if(i!=j) {
					gr.get(2).getGraph().connect(i, j, (i+j)*i);
				}
			}
		}
		g.addNode(new Node(new Point3D(7,8)));
		g.addNode(new Node(new Point3D(9,10)));
		g.addNode(new Node(new Point3D(11,12)));
		g.addNode(new Node(new Point3D(13,14)));
		g.connect(6, 7, 2);
		g.connect(6, 8, 4);
		g.connect(7, 6, 1);
		g.connect(8, 6, 4);
		g.connect(7, 8, 1);
		gr.get(3).init(g);
		Graph_Gui s=new Graph_Gui(g);
		return gr;
	}
	
}
