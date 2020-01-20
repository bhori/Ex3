package gameClient;

import javax.swing.JOptionPane;

import Server.game_service;
import utils.StdDraw;

public class GameThread implements Runnable {
	private GameManager gm;
	private MyGameGUI gui=null;
	private boolean isManualGame=false;
	
	public GameThread(GameManager gm, MyGameGUI gui, boolean isManualGame) {
		this.gm=gm;
		this.gui=gui;
		this.isManualGame=isManualGame;
	}
	
	public GameThread(GameManager gm) {
		this.gm=gm;
	}

	@Override
	public void run() {
//		game_service game = gm.getGame();
		String results="";
		if (isManualGame) {
			while (gm.getGame().isRunning()) {
				gm.getGame().move();
				StdDraw.clear();
				gui.drawGame();
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			results = gm.getGame().toString();
			isManualGame = false;
		} else {
			while (gm.getGame().isRunning()) {
					gm.autoMoveRobots();
					if(gui!=null) {
						StdDraw.clear();
						gui.drawGame();
					}
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			results = gm.getGame().toString();
		}
		System.out.println("Game Over: " + results);
		JOptionPane.showMessageDialog(StdDraw.frame, "Game Over: " + results, "Game Over", JOptionPane.INFORMATION_MESSAGE);		
	}

}
