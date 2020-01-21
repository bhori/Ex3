package tests;

import static org.junit.Assert.assertEquals;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gameClient.Fruit;
import gameClient.GameManager;

class GameManagerTest {
  public static	GameManager gm;
	@BeforeAll
	public static void testInit() throws FileNotFoundException {
		 gm= new GameManager(23);
		 gm.getGame().addRobot(0);
		 gm.getGame().addRobot(1);
		 gm.getGame().addRobot(2);
	}
	@Test
	void testUpdateFruits() {
		List<Fruit>f=new ArrayList<Fruit>(gm.getFruits());
		gm.getFruits().remove(5);
		gm.getFruits().add(f.get(3));
		gm.updateFruits(gm.getGame().getFruits());
		for (int i = 0; i < f.size(); i++) {
			assertEquals(f.get(i).getSrc(), gm.getFruits().get(i).getSrc());
		}
	}

	@Test
	void testUpdateRobots() {
		gm.updateRobots(gm.getGame().getRobots());
		for (int i=0; i<3; i++) {
			assertEquals(gm.getRobots().get(i).getSrc(),i);
		}
	}

	@Test
	void testGetRobot() {
		gm.updateRobots(gm.getGame().getRobots());
		for (int i=0; i<3; i++) {
			assertEquals(gm.getRobot(i).getSrc(),i);
		}
	}

}
