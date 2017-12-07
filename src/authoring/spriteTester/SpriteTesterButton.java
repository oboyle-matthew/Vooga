package authoring.spriteTester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interfaces.TestingInterface;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class SpriteTesterButton extends Button {
	
	private static String DEFAULT_IMAGE_URL = "https://users.cs.duke.edu/~rcd/images/rcd.jpg\"";

	
	public SpriteTesterButton(TestingInterface tester) {
		this.setLayoutY(700);
		this.setText("PlayGame");
		this.setOnAction(e -> {
			Map<String, String> fun = new HashMap<>();
			fun.put("frequency", "50");
			fun.put("number", "100");
			/*
			 * for (int i=0; i<defaults.size(); i++) { fun.put((String)
			 * defaults.keySet().toArray()[i], (String) defaults.entrySet().toArray()[i]); }
			 */
			fun.put("Collision effects", "Invulnerable to collision damage");
			fun.put("Collided-with effects", "Do nothing to colliding objects");
			fun.put("Move an object", "Object will stay at desired location");
			fun.put("Firing Behavior", "Shoot periodically");
			fun.put("imageUrl", DEFAULT_IMAGE_URL);
			fun.put("imageWidth", "45.0");
			fun.put("imageHeight", "45.0");
			fun.put("Numerical \"team\" association", "0");
			fun.put("Health points", "50");
			fun.put("Damage dealt to colliding objects", "20");
			fun.put("Speed of movement", "5");
			fun.put("initialAngle", "0");
			fun.put("radius", "10");
			fun.put("centerY", "0");
			fun.put("centerX", "0");
			fun.put("Target y-coordinate", "0");
			fun.put("Target x-coordinate", "0");
			fun.put("Projectile Type Name", "Tank");
			fun.put("Attack period", "10");
			List<String> sprites = new ArrayList<String>();
			sprites.add("Tank");
			tester.createTesterLevel(fun, sprites);

		});
	}


}