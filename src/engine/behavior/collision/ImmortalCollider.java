package engine.behavior.collision;

import engine.behavior.ParameterName;

/**
 * Represents immortal collider behavior - does not take any damage, always
 * alive
 * Could be used by immortal units - like in GodMode
 * @author radithya
 *
 */
public class ImmortalCollider extends GenericCollider {

	public ImmortalCollider(@ParameterName("playerId") int playerId) {
		super(playerId);
	}
	
	// Immortal colliders don't take any damage
	@Override
	public void visit(DamageDealingCollisionVisitable visitable) {
	}

	@Override
	public boolean isAlive() {
		return true; // ImmortalColliders (Obstacles, etc) are always alive
	}

}