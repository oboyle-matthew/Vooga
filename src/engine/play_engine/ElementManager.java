package engine.play_engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import engine.SpriteQueryHandler;
import javafx.geometry.Point2D;
import sprites.Sprite;
import sprites.SpriteFactory;

/**
 * Single-source of truth for elements and their behavior when in-play
 * 
 * @author radithya
 *
 */
public class ElementManager {

	private SpriteFactory spriteFactory;

	private final String TEAM_NUMBER_KEY = "Numerical \"team\" association", ZERO = "0";

	// Use list to enforce an ordering of elements to facilitate consideration of
	// every element pair only once
	private List<Sprite> gameElements;
	private List<Sprite> newElements;
	private List<Sprite> deadElements;
	
	private SpriteQueryHandler spriteQueryHandler;

	// TODO
	// Reference to GridManager

	/**
	 * Handles the collision-checking and Sprite-specific collision-handling logic
	 * Implements the 'Behavior' interface from the api/doc in the DESIGN_PLAN.md
	 */
	public ElementManager(SpriteFactory spriteFactory) {
		this.spriteFactory = spriteFactory;
		newElements = new ArrayList<>();
		deadElements = new ArrayList<>();
		spriteQueryHandler = new SpriteQueryHandler();
	}

	// Guaranteed to return only active elements (i.e. not dead ones)
	Collection<Sprite> getCurrentElements() {
		// Filter to return only active elements?
		gameElements.removeIf(gameElement -> !gameElement.isAlive());
		return gameElements;
	}

	void setCurrentElements(List<Sprite> newElements) {
		gameElements = newElements;
	}

	/*
	 * AbstractMovementStrategy object should be created with the coordinates Method
	 * might still be necessary but should just do void and put in authoring game
	 * grid
	 */

	void update() {
		newElements.clear();
		for (int elementIndex = 0; elementIndex < gameElements.size(); elementIndex++) {
			Sprite element = gameElements.get(elementIndex);
			element.move();
			if (element.shouldFire()) {
				String elementTemplateName = element.fire();
				List<Sprite> exclusionOfSelf = new ArrayList<>(gameElements);
				exclusionOfSelf.remove(element);
				Map<String, String> spriteToGenerateTemplate = spriteFactory.getTemplateProperties(elementTemplateName);
				int toGenerateId = Integer.parseInt(spriteToGenerateTemplate.getOrDefault(TEAM_NUMBER_KEY, ZERO));
				Map<String, Object> auxiliaryObjects = spriteQueryHandler
						.getAuxiliarySpriteConstructionObjectMap(element.getPlayerId(), new Point2D(element.getX(), element.getY()),
						exclusionOfSelf);
				Sprite projectileSprite = spriteFactory.generateSprite(elementTemplateName,
						new Point2D(element.getX(), element.getY()), auxiliaryObjects);
				System.out.println(element.fire());
				System.out.println("X: " + element.getX());
				System.out.println("Y: " + element.getY());
				newElements.add(projectileSprite);
			}
			processAllCollisionsForElement(elementIndex, element);
		}
		gameElements.forEach(element -> {
			if (!element.isAlive() || (element.reachedTarget() && element.shouldRemoveUponCompletion())) {
				deadElements.add(element);
			}
		});
		gameElements.removeAll(deadElements);
		gameElements.addAll(newElements);
	}

	List<Sprite> getDeadElements() {
		return deadElements;
	}

	void clearDeadElements() {
		deadElements.clear();
	}


	List<Sprite> getNewlyGeneratedElements() {
		return newElements;
	}

	boolean allEnemiesDead() {
		return allElementsFulfillCondition(element -> !element.isEnemy() || !element.isAlive());
	}

	boolean allAlliesDead() {
		return allElementsFulfillCondition(element -> !element.isAlly() || !element.isAlive());
	}

	boolean enemyReachedTarget() {
		return allElementsFulfillCondition(element -> !element.isEnemy() || !element.reachedTarget());
	}

	boolean allElementsFulfillCondition(Predicate<Sprite> condition) {
		for (Sprite element : gameElements) {
			if (!condition.test(element)) {
				return false;
			}
		}
		return true;
	}

	private void processAllCollisionsForElement(int elementIndex, Sprite element) {
		for (int otherIndex = elementIndex + 1; otherIndex < gameElements.size(); otherIndex++) {
			Sprite otherElement = gameElements.get(otherIndex);
			if (element.collidesWith(otherElement)) {
				element.processCollision(otherElement);
				otherElement.processCollision(element);
			}
		}
	}

	/*
	 * Santo's GridManager is not ready, so might as well skip this for now and use
	 * the exact collision-checking logic above // TEMP - SIMPLIFIED CHECKING OF
	 * COLLISIONS, JUST BY GRID POSITION private boolean collidesWith(Sprite
	 * element, Sprite otherElement) { // TODO return false; // TEMP }
	 */

}