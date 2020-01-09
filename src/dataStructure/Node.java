package dataStructure;

import java.io.Serializable;
import java.util.HashMap;

import utils.Point3D;

/**
 * This class represents the set of operations applicable on a 
 * node (vertex) in a (directional) weighted graph.
 * @author 
 *
 */

public class Node implements node_data, Serializable {
	private int key;
	private double weight;
	private Point3D p;
	private String Info;
	private int Tag;
	public static int size=0;
	
	// constructors
	
	public Node() {
		p=null;
		key=++size;
		weight=Double.MAX_VALUE;
		Tag=0;
		Info="";
	}
	
	public Node(Point3D p) {
		this.p=new Point3D(p);
		key=++size;
		weight=Integer.MAX_VALUE;
		Tag=0;
		Info="";
	}
	public Node(int key, Point3D p) {
		this.p=new Point3D(p);
		this.key=key;
		weight=Integer.MAX_VALUE;
		Tag=0;
		Info="";
		size++;
	}
	
	public Node(Node n) {
		this.key = n.getKey();
		this.Tag=n.getTag();
		this.p=new Point3D(n.getLocation());
		this.weight=n.getWeight();
		this.Info=n.getInfo();	
	}
	
	public Node(node_data n) {
		this(n.getLocation());

	}

	// methods
	
	/**
	 * Return the key (id) associated with this node.
	 */
	public int getKey() {
		return this.key;
	}

	/** Return the location (of applicable) of this node, if
	 * none return null.
	 */
	public Point3D getLocation() {
		return this.p;
	}

	/** Allows changing this node's location.
	 * @param p - new new location  (position) of this node.
	 */
	public void setLocation(Point3D p) {
		this.p=new Point3D(p);
	}

	/**
	 * Return the weight associated with this node.
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * Allows changing this node's weight.
	 * @param w - the new weight
	 */
	public void setWeight(double w) {
		this.weight=w;
	}

	/**
	 * return the remark (meta data) associated with this node.
	 */
	public String getInfo() {
		return Info;
	}

	/**
	 * Allows changing the remark (meta data) associated with this node.
	 * @param s
	 */
	public void setInfo(String s) {
		this.Info=s;
		
	}

	/**
	 * Temporal data (aka color: e,g, white, gray, black) 
	 * which can be used by algorithms. 
	 * @return the tag of this node.
	 */
	public int getTag() {
		return this.Tag;
	}

	/** 
	 * Allow setting the "tag" value for temporal marking an node - common 
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag.
	 */
	public void setTag(int t) {
		this.Tag=t;
	}
	
	public String toString() {
		return "key: " + key + "\npoint: "+p;
	}
	
}
