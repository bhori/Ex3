package dataStructure;

import java.io.Serializable;
/**
 * This class represents the set of operations applicable on a 
 * directional edge(src,dest) in a (directional) weighted graph.
 *
 */
public class Edge implements edge_data, Serializable {
    private int src;
    private int dest;
    private int tag;
    private double weight;
    private String info;
    
    public Edge(int src, int dest, double weight) {
    	this.src=src;
    	this.dest=dest;
    	this.weight=weight;
    	this.tag=0;
    	this.info="";
    }
    
    /**
	 * The id of the source node of this edge.
	 * @return
	 */
	@Override
	public int getSrc() {
		return src;
	}
	
	/**
	 * The id of the destination node of this edge
	 * @return
	 */
	@Override
	public int getDest() {
		return dest;
	}
	
	/**
	 * @return the weight of this edge (positive value).
	 */
	@Override
	public double getWeight() {
		return weight;
	}

	/**
	 * return the remark (meta data) associated with this edge.
	 * @return
	 */
	@Override
	public String getInfo() {
		
		return info;
	}

	/**
	 * Allows changing the remark (meta data) associated with this edge.
	 * @param s
	 */
	@Override
	public void setInfo(String s) {
		info=s;
		
	}

	/**
	 * Temporal data (aka color: e,g, white, gray, black) 
	 * which can be used be algorithms 
	 * @return
	 */
	@Override
	public int getTag() {
		return tag;
	}

	/** 
	 * Allow setting the "tag" value for temporal marking an edge - common 
	 * practice for marking by algorithms.
	 * @param t - the new value of the tag
	 */
	@Override
	public void setTag(int t) {
		tag=t;
		
	}
	public String toString() {
		return "src: " + src + "dest: "+dest + "weight: " + weight;
	}

}
