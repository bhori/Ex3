package gameClient;

import org.json.JSONException;
import org.json.JSONObject;

import dataStructure.Edge;
import dataStructure.edge_data;
import utils.Point3D;
/**
 * This class represent a Fruit object, the fruit position is on some edge. 
 * If the type of the fruit is -1, so the src key biggest then dest key, if 1 reverse.
 * 
 * @param type- the type of the fruit: 1 for apple, -1 for banana.
 * @param src- the src of the fruit's edge.
 * @param dest- the dest of the fruit's edge.
 * @param value- the value of the fruit.
 * @param pos- the position of the robot.
 *
 * @author Ori Ben-Hamo and Itamar Ziv-On
 *
 */
public class Fruit {
   
   private Point3D pos;
   private int type, src, dest;
   private double value;
   /**
    * The constructor received a Json String and build the fruit according to this String.
    * @param s
    */
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
   /**
    * The method received src and change the src of the fruit to this src.
    * @param src
    */
   public void setSrc(int src) {
	   this.src=src;
   }
   /**
    * The method received dest and change the dest of the fruit to this dest.
    * @param dest
    */
   public void setDest(int dest) {
	   this.dest=dest;
   }
   /**
    * The method return the src of the fruit.
    * @return
    */
   public int getSrc() {
	   return src;
   }
   /**
    * The method return the dest of the fruit.
    * @return
    */
   public int getDest() {
	   return dest;
   }
   /**
    * The method return the position of the fruit.
    * @return
    */
   public Point3D getPos() {
	   return pos;
   }
   /**
    * The method return the type of the fruit.
    * @return
    */
   public int getType() {
	   return type;
   }
   /**
    * The method return the value of the fruit.
    * @return
    */
   public double  getValue() {
	   return value;
   }
   /**
    * The method return String of the fruit. For example:"pos: [35.33333,32.222222], src: 3, dest: 4, type:1, value: 10".
    */
   public String toString() {
	   return "pos: "+ pos + ", src: " + src + ", dest: " + dest + ", type: " + type + ", value: "+ value;
   }
}
