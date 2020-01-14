package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.edge_data;
import dataStructure.graph;

public class autoGame implements Runnable{
	private MyGameGUI g;
	
	public autoGame(int scenario_num) {
		g=new MyGameGUI(scenario_num);
		game_service game= g.getGame();
		game.startGame();
		System.out.println(game.getRobots());
		Thread t= new Thread(this);
		t.start();
	}
	
	private void autoMoveRobots() {
		game_service game= g.getGame();
		List<String> log = game.move();
		System.out.println(log);
		if(log!=null) {
			this.g.updateFruits(game.getFruits());
			this.g.updateRobots(log);
			double minTimeToGet;
			Robot minRob=null;
			double timeToGet;
			sortFruits();
			long tEnd = game.timeToEnd();
			System.out.println(tEnd/1000);
			List<Robot> r=this.g.getRobots();
			List<Fruit> f=this.g.getFruits();
			System.out.println(f);
			
			
			//find the short time between the robots to the fruit(the fruits organize in order to the value of the fruits, the biggest value first)
			 for(Fruit fruit:f) {
			   if(fruit.getSrc()!=-1) {
				minTimeToGet=Integer.MAX_VALUE;
				for(Robot robot:r) {
				  if(robot.getDest()==-1) { 
					timeToGet=(this.g.getGraph().getEdge(fruit.getSrc(), fruit.getDest()).getWeight()
					+new Graph_Algo(this.g.getGraph()).shortestPathDist(robot.getSrc(), fruit.getSrc()))/robot.getSpeed();
					if(timeToGet<minTimeToGet) {
						minTimeToGet=timeToGet;
						minRob=robot;
					}
				  }
				}
				//if left time to eat this fruit
				if(minTimeToGet<=tEnd) {
					//if have robots that came to src of some fruit, eat the fruit.
					if(minRob.getSrc()==fruit.getSrc()) {
						game.chooseNextEdge(minRob.getId(),fruit.getDest());
					}
					else {
					    int dest=new Graph_Algo(this.g.getGraph()).shortestPath(minRob.getSrc(), fruit.getSrc()).get(1).getKey();
					    game.chooseNextEdge(minRob.getId(),dest);
					    minRob.setDest(dest);
					    fruit.setSrc(-1);
					}
				}
			  }
			}
//			//if have robots that came to src of some fruit, eat the fruit.
//			 for(Robot robot:r) {
//				 if(robot.getDest()==robot.getSrc()) {
//				  for(Fruit fruit:f) {
//					if(fruit.getSrc()!=-1 && robot.getDest()==fruit.getSrc()) {
//						game.chooseNextEdge(robot.getId(),fruit.getDest());
//						fruit.setSrc(-1);
//					}
//				  }
//				 }
			 
		}
	}
	/** The method is sorting the fruits according to their value.
	 *  The fruit with the biggest value first.
	 */
	private void sortFruits() {
		List<Fruit> f=g.getFruits();
		if(f.size()>1) {
		  for(int i=0; i<f.size()-1; i++) {
			for(int j=i+1; j>0; j--) {
				if(f.get(j).getValue()>f.get(j-1).getValue())
					swapFruits(f,j,j-1);
			}
		  }
		}
	}
	/** Simple swap method, received two indexes and swap between the fruits that in this indexes in the list.
	 * 
	 * @param f-list of fruits
	 * @param i
	 * @param j
	 */
	private void swapFruits(List<Fruit> f, int i , int j) {
		Fruit temp =f.get(j);
		f.set(j, f.get(i));
		f.set(i, temp);
	}

	@Override
	public void run() {
		while(this.g.getGame().isRunning()) {
			autoMoveRobots();
			try {
				Thread.sleep(80);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		String results = this.g.getGame().toString();
		System.out.println("Game Over: "+results);		
	}
	
}
