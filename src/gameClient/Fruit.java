package gameClient;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;

public class Fruit {
   private Point3D pos;
   private int type;
   private double value;
   
   public Fruit(String s) {
	   try {
		JSONObject f= new JSONObject(s);
		pos= new Point3D(s);
		type= f.getInt("type");
		value= f.getDouble("value");
	   } catch (JSONException e) {
		  e.printStackTrace();
	   }
   }
   
   public Point3D getPos() {
	   return pos;
   }
   
   public int getType() {
	   return type;
   }
   
   public double  getValue() {
	   return value;
   }
}
