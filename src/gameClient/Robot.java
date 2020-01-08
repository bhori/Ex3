package gameClient;



import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;

public class Robot {
   int id, src, dest;
   double value, speed;
   Point3D pos;
   public Robot() {
	 
   }
   public Robot(int id) {
	   this.id=id;
   }
   
   public void getInfoFromJson(String s) {
	   try {
		JSONObject r= new  JSONObject(s);
		id=(r.getInt("id"));
		
	} catch (JSONException e) {
		e.printStackTrace();
	}
   }
   
   public void setSrc(int src) {
	   this.src=src;
   }
   
   public void setDest(int dest) {
	   this.dest=dest;
   }
   
   public void addValue(double value) {
	   this.value+=value;
   }
   
   public void setSpeed(double speed) {
	   this.speed=speed;
   }
   
   public void setPos(String s) {
	   this.pos=new Point3D(s);
   }
   
   public int getId() {
	   return id;
   }
   
   public int getSrc() {
	   return src;
   }
   
   public int getDest() {
	   return dest;
   }
   
   public double getValue() {
	   return value;
   }
   
   public double getSpeed() {
	   return speed;
   }
   
   public Point3D getPos() {
	   return pos;
   }
}
