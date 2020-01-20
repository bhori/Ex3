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
		game_service game = gm.getGame();
		String results="";
		if (isManualGame) {
			while (game.isRunning()) {
				game.move();
//				if(gui!=null) {
				long tEnd = game.timeToEnd();
				int tSec=(int)(tEnd / 500);
				if(gm.getTimeForKML()!=tSec) {
					for (Fruit fruit : gm.getFruits())
						gm.getKML().addFruitPlace(fruit.getPos().x(), fruit.getPos().y(), fruit.getType());
					for (Robot robot : gm.getRobots())
						gm.getKML().addRobotPlace(robot.getPos().x(), robot.getPos().y(), robot.getId());
					gm.setTimeForKML(tSec);
				}
				StdDraw.clear();
				gui.drawGame();
//				}
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			results = game.toString();
			isManualGame = false;
		} else {
			while (game.isRunning()) {
					gm.autoMoveRobots();
					if(gui!=null) {
						StdDraw.clear();
						gui.drawGame();
					}
				try {
					Thread.sleep(193);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			results = game.toString();
		}
		System.out.println("Game Over: " + results);
		JOptionPane.showMessageDialog(StdDraw.frame, "Game Over: " + results, "Game Over", JOptionPane.INFORMATION_MESSAGE);		
	}

}
