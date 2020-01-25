package tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


import Server.Game_Server;
import gameClient.GameManager;
import gameClient.MyGameGUI;

public class GameTest {


	public static void main(String[] args) throws FileNotFoundException  {
		Game_Server.login(305688111);
		//MyGameGUI s = new MyGameGUI();
		GameManager g=new GameManager(-31);
		g.automaticGame();
	}
		

}
