package engine.behavior.movement;

import engine.game_elements.ElementProperty;
import javafx.geometry.Point2D;

/**
 * Movement strategy for objects that track another sprite.
 *
 * TODO - change to use target's Point2D.Double (prob)
 * 
 * @author mscruggs
 *
 */
public class TrackingMovementStrategy extends TargetedMovementStrategy {

	// can't be set in constructor in order for reflexive creation of sprites to work
	private TrackingPoint targetLocation;
	
	public TrackingMovementStrategy(
			@ElementProperty(value = "startPoint", isTemplateProperty = false) Point2D startPoint,
			@ElementProperty(value = "targetLocation", isTemplateProperty = false) TrackingPoint targetLocation,
			@ElementProperty(value = "velocity", isTemplateProperty = true) double velocity) {
		super(startPoint, targetLocation.getCurrentX(), targetLocation.getCurrentY(), velocity);
		this.targetLocation = targetLocation;
	}

	public void setTargetLocation(TrackingPoint targetLocation) {
		this.targetLocation = targetLocation;
	}
	
	public Point2D move() {
		this.setTargetCoordinates(targetLocation.getCurrentX(), targetLocation.getCurrentY());
		return super.move();
	}
}
