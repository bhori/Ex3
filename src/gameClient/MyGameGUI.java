package gameClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import dataStructure.DGraph;


public class MyGameGUI {
	DGraph g;
	ArrayList<Robot> r;
	ArrayList<Fruit> f;
	
	
	public MyGameGUI(DGraph t, List<String> r, List<String> f ) {
		initRobots(r);
		initFruits(f);
		fruitsEdges();
		robotsPlace();
	}
	
	private void initRobots(List<String> r) {
		JSONObject o;
		for(int i=0; i<r.size(); i++) {
			try {
				o= new JSONObject(r.get(i));
			    this.r.add(new Robot(o.getInt("id")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	} 
	
    private void initFruits(List<String> f) {
    	for(int i=0; i<f.size(); i++) {
			    this.f.add(new Fruit(f.get(i)));
		}
	}
    
    private void fruitsEdges() {
    	
    }
    
    private void robotsPlace() {
    	
    }
	
}
