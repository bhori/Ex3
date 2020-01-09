package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import algorithms.Graph_Algo;

import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.Node;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;

class DGraphTest {
	private static ArrayList<DGraph> gr;

	@BeforeEach
	void initTest() {
		gr = initialTest();
	}

	@Test
	void testEfficiency() {
		DGraph g = new DGraph();
		for(int i=1;i<=1000000;i++) {
			Node n = new Node(new Point3D(i, i));
			g.addNode(n);
		}
		for (node_data i : g.getV()) {
			int sum=0;
			for (node_data j : g.getV()) {
				if(sum==10) {
					break;
				}
				if((j.getKey() != i.getKey()) && (sum<10)) {
					g.connect(i.getKey(), j.getKey(), 5.2);
					sum++;
				}
			}
		}
	}

	@Test
	void testAddNode() {
		Node n = new Node(new Point3D(5.3, 1.2));
		for (DGraph i : gr) {
			assertEquals(null, i.getNode(n.getKey()));
			i.addNode(n);
			assertNotEquals(null, i.getNode(n.getKey()));
		}
	}

	@Test
	void testRemoveNode() {
		ArrayList<Integer> node_id = new ArrayList<Integer>();
		for (node_data i : gr.get(0).getV()) {
			node_id.add(i.getKey());
		}
		for (Integer i : node_id) {
			assertNotEquals(null, gr.get(0).removeNode(i));
			assertEquals(null, gr.get(0).removeNode(i));
			for (Integer j : node_id) {
				if (i != j) {
					assertEquals(null, gr.get(0).removeEdge(i, j));
					assertEquals(null, gr.get(0).removeEdge(j, i));
				}
			}
		}
	}
	
	@Test
	void testConnect() {
		for (DGraph i : gr) {
			ArrayList<Integer> node_id = new ArrayList<Integer>();
			for (node_data n : i.getV()) {
				node_id.add(n.getKey());
			}
			try {
				int dest = 10;
				while(node_id.contains(dest))
					dest++;
				i.connect(node_id.get(0), dest, 86);
				fail("this edge cannot be created, the dest node isnt exist.");
			} catch (Exception e) {
				i.connect(node_id.get(0), node_id.get(1), 86);
				assertEquals(86, i.getEdge(node_id.get(0), node_id.get(1)).getWeight());
			}
		}		
	}

	@Test
	void testRemoveEdge() {
		for (node_data i : gr.get(0).getV()) {
			for (node_data j : gr.get(0).getV()) {
				if (i.getKey() != j.getKey()) {
					assertNotEquals(null, gr.get(0).removeEdge(i.getKey(), j.getKey()));
					assertEquals(null, gr.get(0).removeEdge(i.getKey(), j.getKey()));
				}
			}
		}
	}

	@Test
	void testNodeSize() {
		DGraph s = new DGraph();
		int sumOfNodes = 0;
		for (double i = 0; i <= 5; i = i + 0.5) {
			Node n = new Node(new Point3D(i, i));
			s.addNode(n);
			sumOfNodes++;
		}
		assertEquals(sumOfNodes, s.nodeSize());
		assertEquals(10, gr.get(0).nodeSize());
		assertEquals(5, gr.get(1).nodeSize());
		assertEquals(3, gr.get(2).nodeSize());
	}

	@Test
	void testEdgeSize() {
		DGraph s = new DGraph();
		int sumOfEdges = 0;
		for (double i = 0; i <= 5; i = i + 0.5) {
			Node n = new Node(new Point3D(i, i));
			s.addNode(n);
		}
		Iterator<node_data> itr = s.getV().iterator();
		while (itr.hasNext()) {
			int keySrc = itr.next().getKey();
			if (itr.hasNext()) {
				s.connect(keySrc, itr.next().getKey(), 3);
				sumOfEdges++;
			}
		}
		assertEquals(sumOfEdges, s.edgeSize());
		for (node_data i : s.getV()) {
			for (edge_data j : s.getE(i.getKey())) {
				s.removeEdge(j.getSrc(), j.getDest());
				sumOfEdges--;
				assertEquals(sumOfEdges, s.edgeSize());
			}
		}
		assertEquals(0, s.edgeSize());
		assertEquals(90, gr.get(0).edgeSize());
		assertEquals(10, gr.get(1).edgeSize());
		assertEquals(0, gr.get(2).edgeSize());
	}
	
	@Test
	void testGetMC() {
		assertEquals(100, gr.get(0).getMC());
		assertEquals(15, gr.get(1).getMC());
		assertEquals(0, gr.get(2).getMC());
	}

	public static ArrayList<DGraph> initialTest() {
		ArrayList<DGraph> gr = new ArrayList<DGraph>();
		DGraph g0 = new DGraph();
		DGraph g1 = new DGraph();
		DGraph g2;
		gr.add(g0);
		gr.add(g1);
		for (int i = 1; i <= 10; i++) {
			Point3D p = new Point3D(i, i);
			Node n = new Node(p);
			gr.get(0).addNode(n);
		}
		for (node_data i : gr.get(0).getV()) {
			for (node_data j : gr.get(0).getV()) {
				if (i.getKey() != j.getKey()) {
					gr.get(0).connect(i.getKey(), j.getKey(), j.getKey() * 2);
				}
			}
		}
		for (int i = 1; i <= 5; i++) {
			Point3D p = new Point3D(i, -i);
			Node n = new Node(p);
			gr.get(1).addNode(n);
		}
		for (node_data i : gr.get(1).getV()) {
			int numOfEdges = 0;
			for (node_data j : gr.get(1).getV()) {
				if ((i.getKey() != j.getKey()) && (numOfEdges < 2)) {
					gr.get(1).connect(i.getKey(), j.getKey(), j.getKey() * 2);
					numOfEdges++;
				}
			}
		}
		DGraph g = new DGraph();
		Node n1 = new Node(new Point3D(4, 8.9));
		Node n2 = new Node(new Point3D(-1.6, -7.5));
		Node n3 = new Node(new Point3D(0.3, 5.1));
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);
		g2 = new DGraph(g);
		gr.add(g2);
		return gr;
	}

}
