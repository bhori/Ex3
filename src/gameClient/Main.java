package gameClient;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;

public class Main {

	public static void main(String[] args) {
//		game_service game = Game_Server.getServer(2);
//		DGraph g = new DGraph();
//		MyGameGUI s = new MyGameGUI(g, game.getRobots(), game.getFruits());
	    MyGameGUI s = new MyGameGUI(2);
	    s.game();
		//System.out.println(game.getRobots());
		//s.game(2);
		System.out.println(s.getGraph().getEdge(9, 10));
		System.out.println(s.getGraph().getEdge(10, 9));
		System.out.println(s.getGraph().getEdge(4, 5));
		System.out.println(s.getGraph().getEdge(2, 1));
		System.out.println(s.getFruits());
	}

}
