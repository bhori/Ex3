package gameClient;

import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;

public class Fruit {
   
   private Point3D pos;
   private int type, src, dest;
   private double value;

   
   public Fruit(String s) {
	   try {
		JSONObject t= new JSONObject(s);
		JSONObject f= t.getJSONObject("Fruit");
		pos= new Point3D(f.getString("pos"));
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
   public String toString() {
	   return "pos: "+ pos + ", src: " + src + ", dest: " + dest + ", type: " + type + ", value: "+ value;
   }
}
