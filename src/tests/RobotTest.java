package tests;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import gameClient.Robot;

class RobotTest {

	@Test
	void testGetInfoFromJson() {
		game_service game= Game_Server.getServer(23);
		game.addRobot(0);
		game.addRobot(1);
		game.addRobot(2);
		List<Robot> r =new ArrayList();
		for (int i=0; i<3; i++) {
			r.add(new Robot(i));
		}
		for (int i = 0; i < game.getRobots().size(); i++) {
			String robot_json = game.getRobots().get(i);
			try {
				JSONObject line = new JSONObject(robot_json);
				JSONObject ttt = line.getJSONObject("Robot");
				r.get(i).getInfoFromJson(ttt);
				r.get(i).setDest(-1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (int i=0; i<r.size(); i++) {
			assertEquals(r.get(i).getSrc(),i);
		}
	}

}
