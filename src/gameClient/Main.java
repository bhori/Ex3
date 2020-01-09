package gameClient;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;

public class Main {

	public static void main(String[] args) {
		game_service game = Game_Server.getServer(2);
		DGraph g = new DGraph();
		MyGameGUI s = new MyGameGUI(g, game.getRobots(), game.getFruits());
//		MyGameGUI s = new MyGameGUI();
		s.game(2);
	}

}
