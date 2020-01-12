package dataStructure;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

//import javax.json.Json;
//import javax.json.JsonArray;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
//import javax.json.JsonValue;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Point3D;

/**
 * This interface represents a directional weighted graph.
 * @author 
 *
 */

public class DGraph implements graph, Serializable {
	private HashMap<Integer, node_data> nodes;
	private HashMap<Integer, HashMap<Integer,edge_data>> edges;
	private int mc;
	
	// constructors
	
	public DGraph() {
		nodes = new HashMap<Integer, node_data>();
		edges = new HashMap<Integer, HashMap<Integer,edge_data>>();
		mc=0;
	}
	
	public DGraph(graph g) {
		nodes = new HashMap<Integer, node_data>();
		edges = new HashMap<Integer, HashMap<Integer,edge_data>>();
		for (node_data i : g.getV()) {
			this.addNode(new Node((Node) i));
		}
		for (node_data i : g.getV()) {
			for (edge_data j : g.getE(i.getKey())) {
				this.connect(j.getSrc(), j.getDest(), j.getWeight());
			}
		}
		mc=0;
	}
	
	public void init(String g) {
//		nodes = new HashMap<Integer, node_data>();
//		edges = new HashMap<Integer, HashMap<Integer,edge_data>>();
		try {
			JSONObject jsonObject = new JSONObject(g);
			JSONArray jsonArrayNodes = jsonObject.getJSONArray("Nodes");
			JSONArray jsonArrayEdges = jsonObject.getJSONArray("Edges");
			 for (int i = 0; i < jsonArrayNodes.length(); i++) {
				 JSONObject node = jsonArrayNodes.getJSONObject(i);
				    String s= node.getString("pos");
				    String s1[]=s.split(",");
				    //nodes.put(node.getInt("id"), new Node(new Point3D(Double.parseDouble(s1[0]), Double.parseDouble(s1[1]), Double.parseDouble(s1[2]))));
				    this.addNode(new Node(node.getInt("id"), new Point3D(s)));
			 }
			 for (int i = 0; i < jsonArrayEdges.length(); i++) {
				    JSONObject edge = jsonArrayEdges.getJSONObject(i);
				    this.connect(edge.getInt("src"), edge.getInt("dest"), edge.getDouble("w"));
			 }
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}	
		mc++;
	}

	// methods
	
	/**
	 * return the node_data by the node_id, null if none.
	 * @param key - the node_id
	 * @return the node_data by the node_id, null if none.
	 */
	public node_data getNode(int key) {
		if(!nodes.containsKey(key))
			return null;
		return nodes.get(key);
	}

	/**
	 * return the data of the edge (src,dest), null if none.
	 * @param src - the node_id of the source node.
	 * @param dest - the node_id of the destination node.
	 * @return the data of the edge (src,dest), null if none.
	 */
	public edge_data getEdge(int src, int dest) {
		if(!(edges.containsKey(src)) || !(edges.get(src).containsKey(dest))){
			return null;
		}
		return edges.get(src).get(dest);
	}

	/**
	 * add a new node to the graph with the given node_data.
	 * Note: if this node is already exist, the method throws exception.
	 * @param n
	 */
	public void addNode(node_data n) {
		if(nodes.containsKey(n.getKey())) {
			throw new RuntimeException("this node is already exist.");
		}
		nodes.put(n.getKey(),new Node ((Node) n));
		edges.put(n.getKey(), new HashMap<Integer,edge_data>());
		mc++;
	}
	

	/**
	 * Connect an edge with weight w between node src to node dest.
	 * @param src - the node_id of the source of the edge.
	 * @param dest - the node_id of the destination of the edge.
	 * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
	 * Note: If the variable w received is not positive the function throws exception.
	 */
	public void connect(int src, int dest, double w) {
		if(!(nodes.containsKey(src)) || !(nodes.containsKey(dest))) {
			throw new RuntimeException("cannot connect this edge, at least one of this nodes isn't exist.");
		}
		if(w<=0) {
			throw new RuntimeException("cannot connect this edge, the weight must be positive.");
		}
		edge_data e = new Edge(src, dest, w);
		edges.get(src).put(dest, e);
		mc++;
	}

	/**
	 * This method return a pointer (shallow copy) for the
	 * collection representing all the nodes in the graph. 
	 * @return Collection<node_data>
	 */
	public Collection<node_data> getV() {
		return nodes.values();
	}

	/**
	 * This method return a pointer (shallow copy) for the
	 * collection representing all the edges getting out of 
	 * the given node (all the edges starting (source) at the given node). 
	 * @return Collection<edge_data>
	 */
	public Collection<edge_data> getE(int node_id) {
		return edges.get(node_id).values();
	}

	/**
	 * Delete the node (with the given ID) from the graph -
	 * and removes all edges which starts or ends at this node.
	 * @return the data of the removed node (null if none). 
	 * @param key - the node_id of the node that should be removed.
	 */
	public node_data removeNode(int key) {
		if(!(nodes.containsKey(key))) {
			return null;
		}
		node_data n = nodes.get(key);
		nodes.remove(key);
		edges.remove(key);
		for(HashMap<Integer,edge_data> i : edges.values()) {
			if(i.containsKey(key)) {
				i.remove(key);
			}
		}
		mc++;
		return n;
	}

	/**
	 * Delete the edge between node src to node dest from the graph. 
	 * @param src - the node_id of the source of the edge.
	 * @param dest - the node_id of the destination of the edge.
	 * @return the data of the removed edge (null if none).
	 */
	public edge_data removeEdge(int src, int dest) {
		if(!(edges.containsKey(src)) || !(edges.get(src).containsKey(dest))) {
			return null;
		}
		mc++;
		return edges.get(src).remove(dest);
	}

	/**
	 *  return the number of vertices (nodes) in the graph. 
	 */
	public int nodeSize() {
		return nodes.size();
	}

	/** 
	 * return the number of edges in the graph (assume directional graph).
	 */
	public int edgeSize() {
		int numOfEdges=0;
		for(HashMap<Integer,edge_data> i : edges.values()) {
			numOfEdges+=i.size();
		}
		return numOfEdges;
	}

	/**
	 * return the Mode Count - for testing changes in the graph.
	 */
	public int getMC() {
		return mc;
	}
	
	/**
	 * Saves the graph to a file.
	 * @param file_name - the name of the file within which the graph will be saved.
	 */
	public void serialize(String file_name) 
	{ 
          
        try
        {    
            FileOutputStream file = new FileOutputStream(file_name); 
            ObjectOutputStream out = new ObjectOutputStream(file); 
              
            out.writeObject(this); 
              
            out.close(); 
            file.close(); 
              
            System.out.println("Object has been serialized"); 
        }   
        catch(IOException ex) 
        { 
            System.out.println("IOException is caught"); 
        } 
  		
	} 
	
	/**
	 * Init a graph from file.
	 * @param file_name
	 * @return the new graph that accepted from the file.
	 */
	public graph deserialize(String file_name)
	{
       graph g = new DGraph();
        try
        {    
            FileInputStream file = new FileInputStream(file_name); 
            ObjectInputStream in = new ObjectInputStream(file); 
              
            g = (DGraph) in.readObject();
              
            in.close(); 
            file.close(); 
            
              
            System.out.println("Object has been deserialized"); 
            System.out.println(g.nodeSize());
            return g;
        } 
          
        catch(IOException ex) 
        { 
            System.out.println("IOException is caught"); 
        } 
          
        catch(ClassNotFoundException ex) 
        { 
            System.out.println("ClassNotFoundException is caught"); 
        }
        return null;
	}

}
