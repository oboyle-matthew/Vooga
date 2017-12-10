package util.io;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import engine.Bank;
import engine.behavior.collision.CollisionVisitable;
import engine.behavior.collision.CollisionVisitor;
import engine.behavior.collision.DamageDealingCollisionVisitable;
import engine.behavior.collision.ImmortalCollider;
import engine.behavior.firing.FiringStrategy;
import engine.behavior.movement.MovementStrategy;
import engine.game_elements.GameElement;
import engine.game_elements.GameElementFactory;
import util.AnnotationExclusionStrategy;
import util.InterfaceAdapter;

import java.io.StringReader;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SerializationUtils {

	public static final String DESCRIPTION = "description";
	public static final String CONDITIONS = "conditions";
	public static final String BANK = "bank";
	public static final String STATUS = "status";
	public static final String SPRITES = "gameElements";
	public static final String INVENTORY = "inventory";
	public static final String WAVE = "wave";
	public static final String DELIMITER = "\n";
	// Description, Status, Sprites
	public static final int NUM_SERIALIZATION_SECTIONS = 7;
	public static final int DESCRIPTION_SERIALIZATION_INDEX = 0;
	public static final int CONDITIONS_SERIALIZATION_INDEX = 1;
	public static final int BANK_SERIALIZATION_INDEX = 2;
	public static final int STATUS_SERIALIZATION_INDEX = 3;
	public static final int SPRITES_SERIALIZATION_INDEX = 4;
	public static final int INVENTORY_SERIALIZATION_INDEX = 5;
	public static final int WAVE_SERIALIZATION_INDEX = 6;
	private GsonBuilder gsonBuilder;

	public SerializationUtils() {
		gsonBuilder = new GsonBuilder();
		gsonBuilder.setExclusionStrategies(new AnnotationExclusionStrategy());
		gsonBuilder.serializeSpecialFloatingPointValues();
		InterfaceAdapter interfaceAdapter = new InterfaceAdapter();
		gsonBuilder.registerTypeAdapter(CollisionVisitable.class, interfaceAdapter);
		gsonBuilder.registerTypeAdapter(CollisionVisitor.class, interfaceAdapter);
		gsonBuilder.registerTypeAdapter(MovementStrategy.class, interfaceAdapter);
		gsonBuilder.registerTypeAdapter(FiringStrategy.class, interfaceAdapter);
		gsonBuilder.setLenient();
	}

	/**
	 * Serialize all game data for the given level - description, status and
	 * collection of sprites, nest serialized data within level identifier
	 *
	 * @param gameDescription
	 *            (level-specific) description of game
	 * @param gameConditions
	 *            map of result to string identifier for a boolean function, e.g.
	 *            {"victory" : "allEnemiesDead", "defeat" : "allTowersDestroyed"},
	 *            etc.
	 * @param level
	 *            the level corresponding to the status, elements and description
	 *            data
	 * @param status
	 *            top-level game status from Heads-Up-Display, i.e. all game state
	 *            other than the Sprites
	 * @param levelGameElements
	 *            the cache of generated sprites for a level
	 * @param levelWaves
	 * 			the waves for this level
	 * @return serialization of map of level to serialized level data
	 */
	public String serializeGameData(String gameDescription, Map<String, String> gameConditions, Bank gameBank,
									int level, Map<String, Double> status, List<GameElement> levelGameElements,
									Set<String> levelInventories, List<GameElement> levelWaves) {
		Map<String, String> serializedLevelData = new HashMap<>();
		serializedLevelData.put(Integer.toString(level), serializeLevelData(gameDescription, gameConditions, gameBank,
				status, levelGameElements, levelInventories, levelWaves, level));
		return gsonBuilder.create().toJson(serializedLevelData);
	}

	/**
	 * Serialize map of levels to serialized level data to a multi-level game
	 * serialization
	 *
	 * @param serializedLevelsData
	 *            map of level to serialized data for that level
	 * @return serialization of this map, useful for storing data for multiple
	 *         levels of a game in a single file
	 */
	public String serializeLevelsData(Map<Integer, String> serializedLevelsData) {
		Map<String, String> serializedLevelsDataMap = new HashMap<>();
		for (Integer level : serializedLevelsData.keySet()) {
			serializedLevelsDataMap.put(Integer.toString(level), serializedLevelsData.get(level));
		}
		return gsonBuilder.create().toJson(serializedLevelsDataMap);
	}

	/**
	 * Serialize data for a specific level - description, status and collection of
	 * sprites
	 *
	 * @param gameDescription
	 *            (level-specific) description of game
	 * @param levelConditions
	 *            map of result to string identifier for a boolean function, e.g.
	 *            {"victory" : "allEnemiesDead", "defeat" : "allTowersDestroyed"},
	 *            etc.
	 * @param status
	 *            top-level game status from Heads-Up-Display, i.e. all game state
	 *            other than the Sprites
	 * @param levelGameElements
	 *            the cache of generated sprites for a level
	 * @return serialization of level data
	 */
	public String serializeLevelData(String gameDescription, Map<String, String> levelConditions, Bank bank,
			Map<String, Double> status, List<GameElement> levelGameElements, Set<String> levelInventories,
			List<GameElement> levelWaves, int level) {
		StringBuilder gameDataStringBuilder = new StringBuilder();
		gameDataStringBuilder.append(serializeGameDescription(gameDescription));
		gameDataStringBuilder.append(DELIMITER);
		gameDataStringBuilder.append(serializeConditions(levelConditions));
		gameDataStringBuilder.append(DELIMITER);
		gameDataStringBuilder.append(serializeBank(bank));
		gameDataStringBuilder.append(DELIMITER);
		gameDataStringBuilder.append(serializeStatus(status));
		gameDataStringBuilder.append(DELIMITER);
		gameDataStringBuilder.append(serializeSprites(levelGameElements, level));
		gameDataStringBuilder.append(DELIMITER);
		gameDataStringBuilder.append(serializeInventories(levelInventories, level));
		gameDataStringBuilder.append(DELIMITER);
		gameDataStringBuilder.append(serializeWaves(levelWaves, level));
		return gameDataStringBuilder.toString();

	}

	// TODO - for all deserialization methods : take level as parameter

	/**
	 * Deserialize previously serialized game description into a string
	 *
	 * @param serializedGameData
	 *            string of serialized game data, both top-level game status and
	 *            elements
	 * @param level
	 *            the level whose description is to be deserialized
	 * @return string corresponding to game description
	 * @throws IllegalArgumentException
	 *             if serialization is ill-formatted
	 */
	public String deserializeGameDescription(String serializedGameData, int level) throws IllegalArgumentException {
		String[] serializedSections = retrieveSerializedSectionsForLevel(serializedGameData, level);
		return deserializeDescription(serializedSections[DESCRIPTION_SERIALIZATION_INDEX]);
	}

	/**
	 * Deserialize previously serialized game conditions into a string
	 * 
	 * @param serializedGameData
	 *            string of serialized game conditions
	 * @param level
	 *            the level whose description is to be deserialized
	 * @return map corresponding to game conditions
	 * @throws IllegalArgumentException
	 *             if serialization is ill-formatted
	 */
	public Map<String, String> deserializeGameConditions(String serializedGameData, int level)
			throws IllegalArgumentException {
		String[] serializedSections = retrieveSerializedSectionsForLevel(serializedGameData, level);
		return deserializeConditions(serializedSections[CONDITIONS_SERIALIZATION_INDEX]);
	}

	/**
	 * Deserialize previously serialized bank into a Bank object
	 * 
	 * @param serializedGameData
	 *            string of serialized game conditions
	 * @param level
	 *            the level whose description is to be deserialized
	 * @return Bank instance corresponding to serialized bank data
	 * @throws IllegalArgumentException
	 *             if serialization is ill-formatted
	 */
	public Bank deserializeGameBank(String serializedGameData, int level) throws IllegalArgumentException {
		String[] serializedSections = retrieveSerializedSectionsForLevel(serializedGameData, level);
		return deserializeBank(serializedSections[BANK_SERIALIZATION_INDEX]);
	}

	/**
	 * Deserialize previously serialized game data into a game status map
	 *
	 * @param serializedGameData
	 *            string of serialized game data, both top-level game status and
	 *            elements
	 * @param level
	 *            the level whose description is to be deserialized
	 * @return map corresponding to top-level status
	 * @throws IllegalArgumentException
	 *             if serialization is ill-formatted
	 */
	public Map<String, Double> deserializeGameStatus(String serializedGameData, int level)
			throws IllegalArgumentException {
		String[] serializedPortions = retrieveSerializedSectionsForLevel(serializedGameData, level);
		return deserializeStatus(serializedPortions[STATUS_SERIALIZATION_INDEX]);
	}

	/**
	 * Deserialize previously serialized game data into a sprite map where sprite
	 * name is key and a list of sprites is its value
	 *
	 * @param serializedGameData
	 *            string of serialized game data, both top-level game status and
	 *            elements
	 * @param level
	 *            the level whose description is to be deserialized
	 * @return map of sprite name to list of sprites of that name / type
	 * @throws IllegalArgumentException
	 *             if serialization is ill-formatted
	 */
	public List<GameElement> deserializeGameSprites(String serializedGameData, int level)
			throws IllegalArgumentException {
		String[] serializedSections = retrieveSerializedSectionsForLevel(serializedGameData, level);
		return deserializeSprites(serializedSections[SPRITES_SERIALIZATION_INDEX]);
	}

	public Set<String> deserializeGameInventories(String serializedGameData, int level)
			throws IllegalArgumentException {
		String[] serializedSections = retrieveSerializedSectionsForLevel(serializedGameData, level);
		return deserializeInventories(serializedSections[INVENTORY_SERIALIZATION_INDEX]);
	}

	public List<GameElement> deserializeGameWaves(String serializedGameData, int level)
			throws IllegalArgumentException {
		String[] serializedSections = retrieveSerializedSectionsForLevel(serializedGameData, level);
		return deserializeWaves(serializedSections[WAVE_SERIALIZATION_INDEX]);
	}

	/**
	 * The number of levels that exist in this game currently, as set by the
	 * authoring env
	 * 
	 * @param serializedGameData
	 *            string of serialized game data, both top-level game status and
	 *            elements
	 * @return the number of levels that currently exist in this game
	 * @throws IllegalArgumentException
	 *             if serialization is ill-formatted
	 */
	@SuppressWarnings("unchecked")
	public int getNumLevelsFromSerializedGame(String serializedGameData) throws IllegalArgumentException {
		Map<String, String> serializedLevelData = gsonBuilder.create().fromJson(serializedGameData, Map.class);
		return serializedLevelData.keySet().size();
	}

	public Map<String, String> serializeWaveProperties(Map<String, ?> waveProperties) {
		Map<String, String> stringifiedWaveProperties = new HashMap<>();
		waveProperties.entrySet().forEach(
				entry -> stringifiedWaveProperties.put(entry.getKey(), gsonBuilder.create().toJson(entry.getValue())));
		return stringifiedWaveProperties;
	}

	private String serializeGameDescription(String gameDescription) {
		Map<String, String> descriptionMap = new HashMap<>();
		descriptionMap.put(DESCRIPTION, gameDescription);
		return gsonBuilder.create().toJson(descriptionMap);
	}

	private String serializeConditions(Map<String, String> gameConditions) {
		Map<String, Map<String, String>> conditionsMap = new HashMap<>();
		conditionsMap.put(CONDITIONS, gameConditions);
		return gsonBuilder.create().toJson(conditionsMap);
	}

	private String serializeBank(Bank bank) {
		Map<String, Bank> bankMap = new HashMap<>();
		bankMap.put(BANK, bank);
		return gsonBuilder.create().toJson(bankMap);
	}

	private String serializeStatus(Map<String, Double> status) {
		Map<String, Map<String, Double>> statusMap = new HashMap<>();
		statusMap.put(STATUS, status);
		return gsonBuilder.create().toJson(status);
	}

	// Collect multiple sprites into a top-level map
	private String serializeSprites(List<GameElement> levelGameElements, int level) {
		Map<String, List<GameElement>> spritesMap = new HashMap<>();
		spritesMap.put(SPRITES, levelGameElements);
		return gsonBuilder.create().toJson(spritesMap);
	}

	private String serializeInventories(Set<String> levelInventories, int level) {
		Map<String, Set<String>> inventoriesMap = new HashMap<>();
		inventoriesMap.put(INVENTORY, levelInventories);
		return gsonBuilder.create().toJson(inventoriesMap);
	}

	private String serializeWaves(List<GameElement> levelWaves, int level) {
		Map<String, List<GameElement>> wavesMap = new HashMap<>();
		wavesMap.put(WAVE, levelWaves);
		return gsonBuilder.create().toJson(wavesMap);
	}

	private String deserializeDescription(String serializedDescription) {
		Type mapType = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> descriptionMap = gsonBuilder.create().fromJson(serializedDescription, mapType);
		return descriptionMap != null ? descriptionMap.get(DESCRIPTION) : new String();
	}

	private Map<String, String> deserializeConditions(String serializedConditions) {
		Type mapType = new TypeToken<Map<String, Map<String, String>>>() {
		}.getType();
		Map<String, Map<String, String>> conditionsMap = gsonBuilder.create().fromJson(serializedConditions, mapType);
		return conditionsMap != null ? conditionsMap.get(CONDITIONS) : new HashMap<>();
	}

	private Bank deserializeBank(String serializedBank) {
		Type mapType = new TypeToken<Map<String, Bank>>() {
		}.getType();
		Map<String, Bank> bankMap = gsonBuilder.create().fromJson(serializedBank, mapType);
		return bankMap != null ? bankMap.get(BANK) : new Bank();
	}

	private Map<String, Double> deserializeStatus(String serializedStatus) {
		Type mapType = new TypeToken<Map<String, Map<String, Double>>>() {
		}.getType();
		Map<String, Map<String, Double>> statusMap = gsonBuilder.create().fromJson(serializedStatus, mapType);
		return statusMap != null ? statusMap.get(STATUS) : new HashMap<>();
	}

	// Return a map of sprite name to list of elements, which can be used by
	// ElementFactory to construct sprite objects
	private List<GameElement> deserializeSprites(String serializedSprites) {
		Type mapType = new TypeToken<Map<String, List<GameElement>>>() {
		}.getType();
		Map<String, List<GameElement>> spritesMap = gsonBuilder.create().fromJson(serializedSprites, mapType);
		return spritesMap != null ? spritesMap.get(SPRITES) : new ArrayList<>();
	}

	private Set<String> deserializeInventories(String serializedInventories) {
		Type mapType = new TypeToken<Map<String, Set<String>>>() {
		}.getType();
		Map<String, Set<String>> inventoriesMap = gsonBuilder.create().fromJson(serializedInventories, mapType);
		return inventoriesMap != null ? inventoriesMap.get(INVENTORY) : new HashSet<>();
	}

	private List<GameElement> deserializeWaves(String serializedWaves) {
		Type mapType = new TypeToken<Map<String, List<GameElement>>>() {
		}.getType();
		Map<String, List<GameElement>> wavesMap = gsonBuilder.create().fromJson(serializedWaves, mapType);
		return wavesMap != null ? wavesMap.get(WAVE) : new ArrayList<>();
	}

	private String[] retrieveSerializedSectionsForLevel(String serializedGameData, int level)
			throws IllegalArgumentException {
		Map<String, String> serializedLevelData = gsonBuilder.create()
				.fromJson(new JsonReader(new StringReader(serializedGameData)), Map.class);
		String levelString = Integer.toString(level);
		if (!serializedLevelData.containsKey(levelString)) {
			throw new IllegalArgumentException();
		}
		String[] serializedSections = serializedLevelData.get(levelString).split(DELIMITER);
		System.out.println("Number of serialized sections: " + serializedSections.length);
		Arrays.asList(serializedSections).forEach(section -> {
			System.out.println("SECTION");
			System.out.println(section);
		});
		if (serializedSections.length < NUM_SERIALIZATION_SECTIONS) {
			throw new IllegalArgumentException();
		}
		return serializedSections;
	}

	public String serializeElementProperty(Object propertyValue) {
		return gsonBuilder.create().toJson(propertyValue, propertyValue.getClass());
	}

	public Object deserializeElementProperty(String propertySerialization, Class propertyClass) {
		if (propertyClass != String.class) {
			return gsonBuilder.create().fromJson(propertySerialization, propertyClass);
		} else {
			return propertySerialization;
		}
	}

	private final String COMMA = ",";
	private final int VALUE_INDEX = 0, CLASS_INDEX = 1;

	public Map<String, String> serializeElementTemplate(Map<String, ?> elementTemplate) {

		Map<String, String> serializedTemplate = new HashMap<>();
		for (String propertyName : elementTemplate.keySet()) {
			Class propertyClass = elementTemplate.get(propertyName).getClass();
			String serializedProperty = elementTemplate.get(propertyName) + COMMA + propertyClass.toString();
			serializedTemplate.put(propertyName, serializedProperty);
		}
		return serializedTemplate;
	}

	public Map<String, Object> deserializeElementTemplate(Map<String, String> elementTemplate) {
		Map<String, Object> serializedTemplate = new HashMap<>();
		for (String propertyName : elementTemplate.keySet()) {
			String[] serializedProperty = elementTemplate.get(propertyName).split(COMMA);
			Class propertyClass;
			try {
				propertyClass = Class.forName(serializedProperty[CLASS_INDEX]);
			} catch (ClassNotFoundException exception) {
				propertyClass = String.class;
			}
			serializedTemplate.put(propertyName,
					deserializeElementProperty(serializedProperty[VALUE_INDEX], propertyClass));
		}
		return serializedTemplate;
	}
}
