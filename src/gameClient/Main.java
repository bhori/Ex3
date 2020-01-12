package gameClient;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;

public class Main {

	public static void main(String[] args) {
//		game_service game = Game_Server.getServer(2);
//		DGraph g = new DGraph();
//		MyGameGUI s = new MyGameGUI(g, game.getRobots(), game.getFruits());
<<<<<<< HEAD
//		MyGameGUI s = new MyGameGUI(17);
//		for (Fruit f : s.getFruits()) {
//			System.out.println("src: "+f.getSrc()+"dest: "+f.getDest());
//		}
//		System.out.println(s.getFruits().size());
//		System.out.println(game.getRobots());
//		s.game(6);
//		s.game();
		MyGameGUI s = new MyGameGUI();
//		System.out.println(s.getGraph().getEdge(9, 10));
//		System.out.println(s.getGraph().getEdge(10, 9));
//		System.out.println(s.getGraph().getEdge(4, 5));
//		System.out.println(s.getGraph().getEdge(2, 1));
//		System.out.println(s.getFruits());
=======
	    MyGameGUI s = new MyGameGUI(2);
	    s.game();
		//System.out.println(game.getRobots());
		//s.game(2);
		System.out.println(s.getGraph().getEdge(9, 10));
		System.out.println(s.getGraph().getEdge(10, 9));
		System.out.println(s.getGraph().getEdge(4, 5));
		System.out.println(s.getGraph().getEdge(2, 1));
		System.out.println(s.getFruits());
		System.out.println(s.getRobots());
>>>>>>> 14597f9e37c603ee8c434d8b15c2255d06ce47d1
	}

}
