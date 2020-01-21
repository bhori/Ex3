package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import gameClient.Fruit;

class FruitTest {

	@Test
	void FruitTest() {
		game_service game= Game_Server.getServer(23);
		List<String> s=game.getFruits();
		List<Fruit> f= new ArrayList<Fruit>();
		for (int i = 0; i < s.size(); i++) {
			f.add(new Fruit(s.get(i)));
		}
		assertEquals(6, f.size());
		int countBanana=0, countApple=0;
		for (int i = 0; i < f.size(); i++) {
			if(f.get(i).getType()==-1)
				countBanana++;
			else countApple++;
		}
		assertEquals(4, countBanana);
		assertEquals(2, countApple);
	}

}
