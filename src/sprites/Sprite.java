package sprites;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import engine.behavior.collision.CollisionVisitable;
import engine.behavior.collision.CollisionVisitor;
import engine.behavior.firing.FiringStrategy;
import engine.behavior.movement.MovementStrategy;

// TODO - Just a basic outline for reference from Behavior
// Schwen please feel free to add / change / remove as necessary
// Could be sub-classed by -> MortalSprite -> MovingSprite etc
public abstract class Sprite {

	// Flag to facilitate clean-up of 'dead' elements - only active elements
	// displayed by front end
	private boolean isActive;
	// ^ I (Schwen) think this will be part of the State object once I make it

	private String templateName;
	// These fields should be set through setProperties
	private MovementStrategy movementStrategy;
	private CollisionVisitor collisionVisitor;
	private CollisionVisitable collisionVisitable;
	private FiringStrategy firingStrategy;

	public Sprite(Map<String, ?> properties, String templateName) {
		setProperties(properties);
		this.templateName = templateName;
	}

	/**
	 * Move one cycle in direction of current velocity vector
	 */
	public void move() {
		movementStrategy.move();
	}

	/**
	 * Attack in whatever way necessary Likely called by interaction_engine in
	 * event-handlers for keys / clicks
	 */
	public void attack() {
		firingStrategy.fire();
	}

	public double getX() {
		return movementStrategy.getX();
	}

	public double getY() {
		return movementStrategy.getY();
	}

	// TODO - Can avoid exposing strategies through public methods? Currently using
	// public methods since sprites package is different from behavior package and
	// play_engine package
	public CollisionVisitor getCollisionVisitor() {
		return collisionVisitor;
	}

	public CollisionVisitable getCollisionVisitable() {
		return collisionVisitable;
	}
	
	public MovementStrategy getMovementStrategy() {
		return movementStrategy;
	}


	public String getTemplateName() {
		return templateName;
	}

	public boolean isActive() {
		return isActive;
	}

	/**
	 * Will cause Sprite to be displayed by front end Can be used to differentiate
	 * between Sprites in game area and those not (dead, off-screen???, etc)
	 */
	public void setActive() {
		isActive = true;
	}

	/**
	 * When the Sprite dies - will facilitate removal of element from
	 * ElementManager's collection of active sprites
	 */
	public void deactivate() {
		isActive = false;
	}

	/**
	 * Set the properties of this sprite.
	 *
	 * @param properties
	 *            - maps instance variables of this sprite to properties, as strings
	 */
	private void setProperties(Map<String, ?> properties) {
		List<Field> fields = getAllFieldsInInheritanceHierarchy();
		for (Field field : fields) {
			field.setAccessible(true);
			if (properties.containsKey(field.getName())) {
				setField(properties, field);
			} else {
				// TODO - throw custom exception? set to a default value?
				// System.out.println(String.format("%s: warning, %s was not set",
				// this.getClass().getName(), field
				// .getName()));
			}
		}
	}

	private List<Field> getAllFieldsInInheritanceHierarchy() {
		List<Field> fields = new ArrayList<>(Arrays.asList(this.getClass().getDeclaredFields()));
		Class superClass = this.getClass().getSuperclass();
		while (superClass != null && !superClass.equals(Object.class)) {
			fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
			superClass = superClass.getSuperclass();
		}
		return fields;
	}

	private void setField(Map<String, ?> properties, Field field) {
		try {
			field.set(this, properties.get(field.getName()));
		} catch (IllegalAccessException e) {
			// TODO - because of setAccessible above this won't happen (?), so remove print
			// statement
			System.out.println("Sprite reflection exception: this should never happen");
		}
	}

	/**
	 * Get all the field names of this sprite instance, for creating a property map.
	 *
	 * @return a list of all field names (regardless of accessibility) in this
	 *         instance's inheritance hierarchy
	 */
	public List<String> getFieldNames() {
		return getAllFieldsInInheritanceHierarchy().stream().map(Field::getName).collect(Collectors.toList());
	}
}
