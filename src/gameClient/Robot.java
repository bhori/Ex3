package gameClient;



import org.json.JSONException;
import org.json.JSONObject;

import utils.Point3D;
/**
 * This class represent a Robot object, the robot move from node to node to eat fruits.
 * 
 * @param id- the id of the robot.
 * @param src- the node that robot find.
 * @param dest- the node destination of the robot.
 * @param value- grows according to the fruits that the robot eats.
 * @param speed- the speed of the robot.
 * @param pos- the position of the robot.
 *
 * @author Ori Ben-Hamo and Itamar Ziv-On
 *
 */
public class Robot {
   private int id, src, dest;
   private double value, speed;
   private Point3D pos;
   
   public Robot() {
	 
   }
   /**
    * The constructor received id and build robot with this id. the id maybe change depend the id robot in the server. 
    * @param id
    */
   public Robot(int id) {
	   this.id=id;
   }
   /**
    * The method received a JSONObject and update the robot according to this object.
    * @param r
    */
   public void getInfoFromJson(JSONObject r) {
	   try {
		setSrc(r.getInt("src"));
		setDest(r.getInt("dest"));
		setValue(r.getDouble("value"));
		setPos(r.getString("pos"));
		setSpeed(r.getDouble("speed"));
		
	} catch (JSONException e) {
		e.printStackTrace();
	}
   }
   /**
    * The method received id and change the id of the robot to this id.
    * Needed just for the start game to now the id of the robot in the server.
    * @param id
    */
   public void setId(int id) {
	   this.id=id;
   }
   /**
    * The method received src and change the src of the robot to this src.  
    * @param src
    */
   public void setSrc(int src) {
	   this.src=src;
   }
   /**
    * The method received dest and change the dest of the robot to this dest.
    * @param dest
    */
   public void setDest(int dest) {
	   this.dest=dest;
   }
   /**
    * The method received value and change the value of the robot to this value.
    * @param value
    */
   public void setValue(double value) {
	   this.value=value;
   }
   /**
    * The method received speed and change the speed of the robot to this speed.
    * @param speed
    */
   public void setSpeed(double speed) {
	   this.speed=speed;
   }
   /**
    * The method received string of point and change the position of the robot to the point in the string.  .
    * @param s
    */
   public void setPos(String s) {
	   this.pos=new Point3D(s);
   }
   /**
    * The method received point and change the position of the robot to the point.
    * @param p
    */
   public void setPos(Point3D p) {
	   this.pos=new Point3D(p);
   }
   /**
    * The method return the id of the robot.
    * @return
    */
   public int getId() {
	   return id;
   }
   /**
    * The method return the src of the robot.
    * @return
    */
   public int getSrc() {
	   return src;
   }
   /**
    * The method return the dest of the robot.
    * @return
    */
   public int getDest() {
	   return dest;
   }
   /**
    * The method return the value of the robot.
    * @return
    */
   public double getValue() {
	   return value;
   }
   /**
    * The method return the speed of the robot.
    * @return
    */
   public double getSpeed() {
	   return speed;
   }
   /**
    * The method return the point of the robot.
    * @return
    */
   public Point3D getPos() {
	   return pos;
   }
   /**
    * The method return String of the robot. For example:"id: 1, pos: [35.33333,32.222222]", src: 3, dest: 4, value: 105, speed: 5".
    */
   public String toString() {
	   return "id: " +id +  ", pos: " + pos+  ", src: "+ src + ", dest: "+ dest + ", value: " + value+ ", speed: "+ speed;   
   }
}
