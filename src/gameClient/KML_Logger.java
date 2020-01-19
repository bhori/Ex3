package gameClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Icon;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Style;
/**
 * This class represents a client's game conversion to a KML file and 
 * allows you to look at the game on Google Earth. 
 * 
 */
public class KML_Logger {
	final Kml kml;
	Document doc;
	Folder folder;
	/**
	 * The constructor build the Kml, Document and Folder objects and open the Document and Folder objects.
	 * @throws FileNotFoundException
	 */
	public KML_Logger() throws FileNotFoundException {
	kml = new Kml();
	// create a Document
	doc = kml.createAndSetDocument().withName("Game").withOpen(true);
	// create a Folder
	folder = doc.createAndAddFolder();
	folder.withName("Game in Ariel").withOpen(true);
	}
	/**
	 * The method received a point and id and create placemark with name like the id .  
	 * @param longitude
	 * @param latitude
	 * @param id
	 */
	public void addNode( double longitude, double latitude, int id) {
		createPlacemark(longitude,latitude, ""+id, 0,false);
	}
	/**
	 * The method received a point of a robot and his id and create placemark with name like the id.  
	 * @param longitude
	 * @param latitude
	 * @param id
	 */
	public void addRobotPlace( double longitude, double latitude, int id) {
		createPlacemark(longitude,latitude, ""+id, 0,true);
	}
	/**
	 * The method received a point of a fruit and his type and create placemark with icon match to the type.
	 * @param longitude
	 * @param latitude
	 * @param type
	 */
	public void addFruitPlace( double longitude, double latitude, int type) {
		createPlacemark(longitude,latitude, "", type,true);
	}
	/**
	 * The method received a graph and add the graph to the kml file according to his coordinates, and mark his nodes with they key number.
	 * @param g
	 */
	public void createPath(graph g) {
		for(node_data n:g.getV()) {
			addNode(n.getLocation().x(),n.getLocation().y(), n.getKey());
		}
		Placemark p= doc.createAndAddPlacemark();
		p.setName("Path");
		p.createAndAddStyle().createAndSetLineStyle().withColor("ff0000ff").setWidth(2);; 
		LineString ls =p.createAndSetLineString();
		ls.withTessellate(true);
		for(node_data n:g.getV()) {
			for(edge_data e: g.getE(n.getKey())) {
				ls.addToCoordinates(n.getLocation().toString()+ ",0");
				ls.addToCoordinates(g.getNode(e.getDest()).getLocation().toString()+ ",0");
			}
			ls.addToCoordinates(n.getLocation().toString()+ ",0");
		}
	}
	/**
	 * The createPlacemark ()-method generates and set a placemark object. The placemark is created and set to the folder of the class.
	 * 
	 * @param type for fruit: 1 for apple, -1 for banana, else 0
	 * @param longitude of the point
	 * @param latitude of the point
	 * @param id  name of the placemark
	 * @param time true if needs to add to the placemark TimeStamp false if not
	 */
	private void createPlacemark( double longitude, double latitude, String id, int type, boolean time) {
		Icon icon = new Icon();
		Style style = doc.createAndAddStyle();
		Placemark placemark = folder.createAndAddPlacemark();
		//if fruit
		if(type!=0) {
			if(type>0) {
				id="-2";
				icon.withHref("http://pngimg.com/uploads/apple/apple_PNG12507.png");
			}
			else {
				id="-1";
				icon.withHref("http://pngimg.com/uploads/banana/banana_PNG841.png");
			}
			
			style.withId("style_" +id ) // set the stylename to use this style from the placemark
			    .createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
			placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
			// use the style for each fruit
			placemark.withStyleUrl("#style_" + id).createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(12000000);// coordinates and distance (zoom level) of the viewer
		}
		//if robot
		else if(time==true){
			icon.withHref("http://pngimg.com/uploads/robot/robot_PNG3"+ id+ ".png");
			style.withId("style_" + id) 
		    .createAndSetIconStyle().withScale(1).withIcon(icon); 
			style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1); //set color and size of the name
			placemark.createAndSetPoint().addToCoordinates(longitude, latitude); 
			// use the style for each robot
			placemark.withName(id).withStyleUrl("#style_" + id).createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(12000000);// coordinates and distance (zoom level) of the viewer
		}
		//node..
		else {
			
			icon.withHref("http://maps.google.com/mapfiles/kml/paddle/red-circle.png");
			style.withId("style_") .createAndSetIconStyle().withScale(0).withIcon(icon); 
			style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1); 
			placemark.createAndSetPoint().addToCoordinates(longitude, latitude); 
			// use the style for each node
			placemark.withName(id).withStyleUrl("#style_").createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(12000000);// coordinates and distance (zoom level) of the viewer
		}
		//if fruit or robot should be TimeStamp.
        if(time==true){
				Date d=new Date();
				placemark.createAndSetTimeStamp().withWhen(""+d.toInstant());
		}
	}
	/**
	 * The method received a file name and save the kml file with fileName.kml. 
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public void save(String fileName) throws FileNotFoundException {
		// print and save
		kml.marshal(new File(fileName + ".kml"));
	}
}
