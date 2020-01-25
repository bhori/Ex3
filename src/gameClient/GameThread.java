package gameClient;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import Server.game_service;
import utils.StdDraw;

/**
 * This class represents a thread which responsible for updating the game and the gui window (if the game is displayed in a gui window)
 * while the game is running.
 * 
 * @author OriBH, ItamarZiv-On.
 *
 */
public class GameThread implements Runnable {
	private GameManager gm;
	private MyGameGUI gui = null;
	private int scenario;
	private boolean isManualGame = false;

	/**
	 * Initializes a game with displaying it in a gui window.
	 * 
	 * @param gm           - the game object.
	 * @param gui          - the gui object.
	 * @param isManualGame - Indication whether the game is manual or automatic.
	 */
	public GameThread(GameManager gm, MyGameGUI gui, int scenario, boolean isManualGame) {
		this.gm = gm;
		this.gui = gui;
		this.scenario = scenario;
		this.isManualGame = isManualGame;
	}

	/**
	 * Initializes a game without option to displaying it in a gui window.
	 * 
	 * @param gm - the game object.
	 */
	public GameThread(GameManager gm) {
		this.gm = gm;
	}

	/**
	 * Updates the game while it is running, if gui window is open, updates the
	 * window too. When the game ends, prints the final result and enables to save
	 * the game in KML format.
	 */
	@Override
	public void run() {
		game_service game = gm.getGame();
		String results = "";
		if (isManualGame) {
			while (game.isRunning()) {
				game.move();
				long tEnd = game.timeToEnd();
				int tSec = (int) (tEnd / 500);
				if (gm.getTimeForKML() != tSec) {
					for (Fruit fruit : gm.getFruits())
						gm.getKML().addFruitPlace(fruit.getPos().x(), fruit.getPos().y(), fruit.getType());
					for (Robot robot : gm.getRobots())
						gm.getKML().addRobotPlace(robot.getPos().x(), robot.getPos().y(), robot.getId());
					gm.setTimeForKML(tSec);
				}
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
				if (gui != null) {
					StdDraw.clear();
					gui.drawGame();
				}
				try {
					
				  Thread.sleep(40);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			results = gm.getGame().toString();
		}
		System.out.println("Game Over: " + results);
		JOptionPane.showMessageDialog(StdDraw.frame, "Game Over: " + results, "Game Over",
				JOptionPane.INFORMATION_MESSAGE);
		int ans = JOptionPane.showConfirmDialog(StdDraw.frame, "Save to KML?");
		if (ans == JOptionPane.YES_OPTION) {
			try {
				gm.getKML().save("" + this.scenario);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
