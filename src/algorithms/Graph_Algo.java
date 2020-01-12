package algorithms;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import dataStructure.DGraph;
import dataStructure.Edge;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;

import utils.MyMinHeap;
import utils.Point3D;
/**
 * This  class represents the set of graph-theory algorithms.
 *
 */

public class Graph_Algo implements graph_algorithms {
    private DGraph g;
    private boolean isConnected;
    private int mcConnected;
    private int mcSP, src, dest;
    private double dist;
    private List<node_data> path;

    
    public Graph_Algo() {
    	g= new DGraph();
    	path=new ArrayList<node_data>();
    }
    
    public Graph_Algo(graph g) {
    	this.g= new DGraph();
    	this.g = (DGraph) g;
    	path=new ArrayList<node_data>();
    }

    public graph getGraph() {
    	return g;
    }
    
    /**
	 * Init this set of algorithms on the parameter - graph.
	 * @param g
	 */
	@Override
	public void init(graph g) {
		this.g=(DGraph) g;
		
	}

	/**
	 * Init a graph from file
	 * @param file_name
	 */
	@Override
	public void init(String file_name) {
		this.g = (DGraph) this.g.deserialize(file_name);
	}

	/** Saves the graph to a file.
	 * 
	 * @param file_name
	 */
	@Override
	public void save(String file_name) {
		this.g.serialize(file_name);
	}
	
	/**
	 * Returns true if and only if (iff) there is a valid path from EVREY node to each
	 * other node. NOTE: assume directional graph - a valid path (a-->b) does NOT imply a valid path (b-->a).
	 * @return
	 */
	@Override
	public boolean isConnected() {
	    if(g.getMC()==0)//empty graph connected
	    	return true;
		if(mcConnected==g.getMC())
			return isConnected;
		Collection<node_data> v= g.getV();
		Iterator<node_data> itr=v.iterator();
		node_data r= itr.next();
		//paint r and all the connected nodes of r
		g.getNode(r.getKey()).setTag(1);
		isConnectedHelper(r);
		//if some node was not painted, has not way between the start node to this node
		this.isConnected=paintWhite(v);
	    if (this.isConnected== false) {
	    	this.mcConnected=g.getMC();
	    	return this.isConnected;
	    }
	    node_data n= null;
		while(itr.hasNext()) {
			n= itr.next();
			if(this.shortestPath(n.getKey(), r.getKey())==null) {
				this.isConnected =  false;
				mcConnected=g.getMC();
				return this.isConnected;
			}
		}
		this.isConnected =  true;
		mcConnected=g.getMC();
		return this.isConnected;
		
	}
	
    //this method paint all the connected nodes to n.
	private void isConnectedHelper(node_data n) {
		Collection<edge_data> e= g.getE(n.getKey());
		for(edge_data i: e) {
			//all neighbors that not visited paint them and their neighbors 
			if(g.getNode(i.getDest()).getTag()==0) {
				g.getNode(i.getDest()).setTag(1);
				isConnectedHelper(g.getNode(i.getDest()));
			}
		}
	}	

	/**
	 * returns the length of the shortest path between src to dest
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return
	 */
	@Override
	public double shortestPathDist(int src, int dest) {
		if(g.getNode(src)==null || g.getNode(dest)==null)
			throw new RuntimeException("ERR:src/dest is null ");
		if(mcSP==g.getMC() && this.src==src && this.dest==dest) 
			return this.dist;
		shortestPath(src, dest);
		return this.dist;
	}

	/**
	 * returns the the shortest path between src to dest - as an ordered List of nodes:
	 * src--> n1-->n2-->...dest
	 * see: https://en.wikipedia.org/wiki/Shortest_path_problem
	 * @param src - start node
	 * @param dest - end (target) node
	 * @return
	 */
	@Override
	public List<node_data> shortestPath(int src, int dest) {
		if(g.getNode(src)==null || g.getNode(dest)==null)
			throw new RuntimeException("ERR:src/dest is null ");
		//if not have nodes
		if(g.getMC()==0) {
			this.updateSP(Integer.MAX_VALUE,src,dest);
			return null;
		}
		//if the graph was not changed
		if(mcSP==g.getMC() && this.src==src && this.dest==dest) 
			return path;
		Collection<node_data> v= g.getV();
		ArrayList<node_data> l=initForShortestPath(v,src);
		MyMinHeap m=new MyMinHeap(l);
		node_data n=null;
		int i=0;
		//the root of the minheap.
		node_data r=l.get(i);
		while(l.size()>1 && r.getKey()!=dest && r.getWeight()!=Integer.MAX_VALUE ) {
			this.changeNei(r.getKey(),m);
			n=m.extractMin();
			n.setWeight(-1*n.getWeight());
			i=0;
			r=l.get(i);
		}
		if(g.getNode(dest).getWeight()==Integer.MAX_VALUE) {
			this.updateSP(Integer.MAX_VALUE,src,dest);
			this.paintWhite(v);
			return null;
		}
		this.path.clear();
		int p=dest;
		this.path.add(g.getNode(p));
		while(p!=src) {
			p=g.getNode(p).getTag();
			this.path.add(g.getNode(p));
			g.getNode(p).setWeight(-1*g.getNode(p).getWeight());
		}
		this.updateSP(this.path.get(0).getWeight(),src,dest);
		//change the order of path to be from src to dest
		this.reverse(this.path);
		this.paintWhite(v);
		return this.path;
	}
	private void updateSP(double dist, int src, int dest) {
		this.mcSP=g.getMC();
		this.src=src;
		this.dest=dest;
		this.dist=dist;
	}
	private ArrayList<node_data> initForShortestPath(Collection<node_data> v, int src) {
		ArrayList<node_data> t= new ArrayList<node_data>();
		for(node_data n: v) {
			//change all nodes to be max integer except the src node
			g.getNode(n.getKey()).setWeight(Integer.MAX_VALUE);
			g.getNode(n.getKey()).setTag(-1);//Tag use for now from which node the path came to this node
			t.add(n);
			t.get(t.size()-1).setInfo(""+(t.size()-1));
			if(n.getKey()==src) 
				g.getNode(src).setWeight(0);
		}
		
		return t;
	}
	//the method reverse list l
    private void reverse(List<node_data> l) {
    	node_data temp=null;
    	int size=l.size();
    	for(int i=0; i<(int)size/2; i++) {
			temp= l.get(i);
			l.set(i, l.get(size-1-i));
			l.set(size-1-i, temp);
		}
    }
    /* The method is changing the weight of node neighbor of the node, that is key is src, 
     * if the weight of the src node + the weight of the edge between src node to is neighbor 
     * less then the weight of is neighbor weight.   
     */
	private void changeNei(int src,MyMinHeap m ) {
		node_data r=g.getNode(src);
		node_data n=null;
		Collection<edge_data> e= g.getE(src);
		for(edge_data j: e) {
			n=g.getNode(j.getDest());
			//if the dest not remove node
			if(n.getWeight()>0) {
			   if(n.getWeight()>r.getWeight()+j.getWeight()) {
				  m.changePriorety(Integer.parseInt(n.getInfo()), r.getWeight()+j.getWeight());
				  n.setTag(r.getKey());
			   }
			}
		}
	}

	/**
	 * computes a relatively short path which visit each node in the targets List.
	 * Note: this is NOT the classical traveling salesman problem, 
	 * as you can visit a node more than once, and there is no need to return to source node - 
	 * just a simple path going over all nodes in the list. 
	 * @param targets
	 * @return if targets =null throw run time exception. 
	 */
	@Override
	public List<node_data> TSP(List<Integer> targets) {
		if(targets==null)
			throw new RuntimeException("ERR:the list is null ");
		if(targets.size()==0) 
			return null;
		List<node_data> tsp=new ArrayList<node_data>();
		List<node_data> tn=new ArrayList<node_data>();
		List<Integer> temp= new ArrayList<Integer>(targets);
		if(targets.size()==1) {
			tsp.add(g.getNode(targets.get(0)));
			return tsp;
		}
		int z=1;
		double min=Integer.MAX_VALUE;
		while(temp.size()>2) {
			dijkstras(temp.get(0));
			for(int j=1;j<temp.size(); j++) {
				//the nodes not connected
				if(g.getNode(temp.get(j)).getWeight()==Integer.MAX_VALUE)
                    return null;
				if(min>Math.abs(g.getNode(temp.get(j)).getWeight())) {
					min=Math.abs(g.getNode(temp.get(j)).getWeight());
					z=j;
				}
			}
			int key=temp.get(z);
			while(key!=temp.get(0)) {
				key=g.getNode(key).getTag();
				tn.add(g.getNode(key));
				g.getNode(key).setWeight(-1*g.getNode(key).getWeight());
			}
			reverse(tn);
			tsp.addAll(tn);
			tn.clear();
			temp.set(0, temp.get(z));
			temp.remove(z);
			
		}
		if(this.shortestPathDist(temp.get(0), temp.get(1))==Integer.MAX_VALUE)
			return null;
		tsp.addAll(this.shortestPath(temp.get(0), temp.get(1)));
		return tsp;
	}
	
	private void dijkstras(int src) {
		Collection<node_data> v= g.getV();
		ArrayList<node_data> l=initForShortestPath(v,src);
		MyMinHeap m=new MyMinHeap(l);
		node_data n=null;
		int i=0;
		//the root of the minheap.
		node_data r=l.get(i);
		while(l.size()>1  && r.getWeight()!=Integer.MAX_VALUE ) {
			this.changeNei(r.getKey(),m);
			n=m.extractMin();
			n.setWeight(-1*n.getWeight());
			i=0;
			r=l.get(i);
		}
	}
	
	/** 
	 * Compute a deep copy of this graph.
	 * @return graph
	 */
	@Override
	public graph copy() {
		String fileName="Graph";
		this.save(fileName);
		Graph_Algo t= new Graph_Algo();
		t.init(fileName);
		return t.getGraph();

	}
	///the method is changing all value tag in each node to 0
	private boolean paintWhite(Collection<node_data> v) {
		//this flag to now if all nodes changed 
		boolean allNodesChanged=true;
		 for(node_data n: v) {
		    	if(n.getTag()==0)
		    		allNodesChanged=false;
		    	else n.setTag(0);
		    }
		 return allNodesChanged;
	}
	public edge_data findEdgeToPoint(Point3D p){
		Collection<node_data> n=this.g.getV();
		Collection<edge_data> e;
		int dest;
		for(node_data t:n) {
			e=this.g.getE(t.getKey());
			for(edge_data j:e) {
				dest= j.getDest();
//				if(p.x()<= t.getLocation().x() && p.x()>= this.getNode(dest).getLocation().x()
//						|| (p.x()>= t.getLocation().x() && p.x()<= this.getNode(dest).getLocation().x() )) {
//					if()
//				}
				if((Math.abs((t.getLocation().distance2D(p)+this.g.getNode(dest).getLocation().distance2D(p))
					- t.getLocation().distance2D(this.g.getNode(dest).getLocation()))<=Point3D.EPS)) {
					return j;
				}
				
			}
		}
	    return null;
	}


}

