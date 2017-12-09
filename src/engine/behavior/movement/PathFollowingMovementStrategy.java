package engine.behavior.movement;

import java.util.ArrayList;

import authoring.path.PathList;
import engine.behavior.ParameterName;
import javafx.geometry.Point2D;

/**
 * Movement strategy for objects that move along a defined path
 * 
 * @author mscruggs
 *
 */
public class PathFollowingMovementStrategy extends TargetedMovementStrategy {

	private PathList coordinates;
	private Point2D target;

	public PathFollowingMovementStrategy(@ParameterName("velocity") double velocity, PathList coordinates) {
		super(coordinates.next(),new Point2D(0, 0), velocity);
		setPathCoordinates(coordinates);
	}

	public Point2D move() {
		super.move();
		checkIfLocationReached();
		return getCurrentCoordinates();
	}

	public void setPathCoordinates(PathList coordinates) {
		this.coordinates = coordinates;
		this.target = this.coordinates.next();
		setTargetCoordinates(target.getX(), target.getY());
	}

	public boolean targetReached() {
		return (target == null);
	}

	/**
	 * Check to see if one point in the coordinates was reached
	 */
	private void checkIfLocationReached() {
		if (super.targetReached()) {
			target = coordinates.next();
			if (target == null) {
				stop();
			} else {
				this.setTargetCoordinates(target.getX(), target.getY());
			}
		}
	}
}
