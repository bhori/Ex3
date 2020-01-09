package gameClient;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;

public class Fruit {
   Point3D pos;
   int type, src, dest;
   double value;
   
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
   
   public void setSrc(int src) {
	   this.src=src;
   }
   
   public void setDest(int dest) {
	   this.dest=dest;
   }
   
   public int getSrc() {
	   return src;
   }
   
   public int getDest() {
	   return dest;
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
