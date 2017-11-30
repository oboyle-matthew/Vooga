package authoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


import authoring.bottomToolBar.BottomToolBar;
import authoring.customize.AttackDefenseToggle;
import authoring.customize.ColorChanger;
import authoring.customize.ThemeChanger;
import authoring.leftToolBar.LeftToolBar;
import authoring.rightToolBar.RightToolBar;
import authoring.rightToolBar.SpriteImage;
import engine.authoring_engine.AuthoringController;
import engine.play_engine.PlayController;
import interfaces.ClickableInterface;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import main.Main;
import splashScreen.ScreenDisplay;
import sprites.BackgroundObject;
import sprites.InteractiveObject;
import sprites.Sprite;
import sprites.StaticObject;

public class EditDisplay extends ScreenDisplay implements AuthorInterface {
	
	private static final double GRID_X_LOCATION = 620;
	private static final double GRID_Y_LOCATION = 20;
	private AuthoringController controller;
	private LeftToolBar myLeftToolBar;
	private GameArea myGameArea;
	private ScrollableArea myGameEnvironment;
	private RightToolBar myRightToolBar;
	private MainMenuBar myMenuBar;
	private ToggleButton gridToggle;
	private ToggleButton movementToggle;
	private ColorChanger myColorChanger;
	private ThemeChanger myThemeChanger;
	private AttackDefenseToggle myGameChooser;
	private Label attackDefenseLabel;
	private  ReturnButton myReturnButton;
	private Map<String, String> basePropertyMap;
	private BottomToolBar myBottomToolBar;
	private PlayController tester;
	private VBox myLeftBar;
	private VBox myLeftButtonsBar;
	
	
	public EditDisplay(int width, int height) {
//		super(width, height, Color.GREEN);
//		super(width, height);
		super(width, height, Color.BLACK);
//		super(width, height, Color.GRAY);
		myReturnButton = new ReturnButton(this);
		rootAdd(myReturnButton);
		myLeftButtonsBar = new VBox();
		myLeftBar = new VBox();
		addItems();
		formatLeftBar();
		setStandardTheme();
		createGridToggle();
		rootAdd(gridToggle);
		createMovementToggle();
		rootAdd(movementToggle);
		createLabel();
		basePropertyMap = new HashMap<String, String>();
		Button saveButton = new Button("Save");
		saveButton.setLayoutY(600);
		rootAdd(saveButton);
		
		
	}
	
	private void createLabel() {
		attackDefenseLabel = new Label("Defense");
//		styleLabel(attackDefenseLabel);
		attackDefenseLabel.setFont(new Font("Times New Roman", 35));
//		attackDefenseLabel.setFont(new Font("American Typewriter", 40));
//		attackDefenseLabel.setFont(new Font("Cambria", 40));
		attackDefenseLabel.setLayoutX(260);
		attackDefenseLabel.setLayoutY(25);
		rootAdd(attackDefenseLabel);

	}

	private void createGridToggle() {
		gridToggle = new ToggleButton();
		gridToggle.setLayoutX(GRID_X_LOCATION);
		gridToggle.setLayoutY(GRID_Y_LOCATION);
		gridToggle.setSelected(true);
		gridToggle.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("grid_icon.png"))));
		gridToggle.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
			myGameArea.toggleGridVisibility(gridToggle.isSelected());
		});
	}
	
	private void createMovementToggle() {
		movementToggle = new ToggleButton();
		movementToggle.setLayoutX(GRID_X_LOCATION - 40);
		movementToggle.setLayoutY(GRID_Y_LOCATION);
		movementToggle.setSelected(false);
		movementToggle.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("scroll_arrow_icon.png"))));
		movementToggle.addEventHandler(MouseEvent.MOUSE_CLICKED, e->toggleMovement(movementToggle));
	}
	
	private void toggleMovement(ToggleButton movement) {
		myGameArea.toggleMovement(movementToggle.isSelected());
		if(movement.isSelected()) {
			this.getScene().setCursor(new ImageCursor(new Image(getClass().getClassLoader().getResourceAsStream("scroll_arrow_icon.png"))));
		}else {
			this.getScene().setCursor(Cursor.DEFAULT);
		}
	}
	
	private void addToLeftButtonsBar() {
		myColorChanger = new ColorChanger(this);
		myLeftButtonsBar.getChildren().add(myColorChanger);
		myGameChooser = new AttackDefenseToggle(this);
		myLeftButtonsBar.getChildren().add(myGameChooser);
	}
	
	private void addToLeftBar() {
		myLeftToolBar = new LeftToolBar(this, controller);
		myLeftBar.getChildren().add(myLeftToolBar);
		addToLeftButtonsBar();
		myLeftBar.getChildren().add(myLeftButtonsBar);
	}

	private void addItems() {
		controller = new AuthoringController();
		myGameArea = new GameArea(controller);
		myGameEnvironment = new ScrollableArea(myGameArea);
		rootAdd(myGameEnvironment);
		this.setDroppable(myGameArea);
		addToLeftBar();
		rootAdd(myLeftBar);
		myRightToolBar = new RightToolBar(this, controller);
		rootAdd(myRightToolBar);
		myThemeChanger = new ThemeChanger(this);
		rootAdd(myThemeChanger);
		myMenuBar = new MainMenuBar(this, controller);
		rootAdd(myMenuBar);
		myBottomToolBar = new BottomToolBar(this, controller, myGameEnvironment);
		rootAdd(myBottomToolBar);
	}
	
	public void listItemClicked(ImageView clickable) {
		StaticObject object = (StaticObject) clickable;
		Button addNewButton = new Button("New");
		Button incrementButton = new Button("+");
		Button decrementButton = new Button("-");
		addNewButton.setLayoutY(20);
		incrementButton.setLayoutY(20);
		decrementButton.setLayoutY(20);
		incrementButton.setLayoutX(50);
		decrementButton.setLayoutX(85);
		addNewButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->addObject(object));
		incrementButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->object.incrementSize());
		decrementButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->object.decrementSize());
		rootAdd(addNewButton);
		rootAdd(incrementButton);
		rootAdd(decrementButton);
	}

	private void addObject(InteractiveObject object) {
		InteractiveObject newObject;
		if (object instanceof BackgroundObject) {
			newObject = new BackgroundObject(object.getSize(), this, object.getElementName());
		} else {
			newObject = new StaticObject(object.getSize(), this, object.getElementName());
		}
		myGameArea.addBackObject(newObject);
//		newObject.setElementId(controller.placeElement(object.getImageString(), new Point2D(object.getX(),object.getY())));
	}

	@Override
	public void newTowerSelected(ImageView myImageView) {
		
	}

	@Override
	public void clicked(SpriteImage imageView) {
		SelectionWindow mySelectionWindow = new SelectionWindow(imageView, this, controller);
	}

	@Override
	public void changeColor(String color) {
		myGameArea.changeColor(color);
	}
	
	@Override
	public void save(File saveName) {
		controller.saveGameState(saveName);
		myGameArea.savePath();
	}

	public void changeTheme(String theme) {
		rootStyleAndClear(myThemeChanger.getThemePath(theme));
		myRightToolBar.getStyleClass().add("borders");
		myLeftToolBar.getStyleClass().add("borders");
		myLeftBar.getStyleClass().add("outer-border");
		myLeftButtonsBar.getStyleClass().add("borders");
	}

	private void setStandardTheme() {
		changeTheme(ThemeChanger.STANDARD);
	}
	
	private void formatLeftBar() {
		myLeftBar.setLayoutY(30);
		myLeftBar.setSpacing(30);
		myLeftButtonsBar.setSpacing(20);
	}

	public void attack() {
		attackDefenseLabel.setText("Defense");
	}

	public void defense() {
		attackDefenseLabel.setText("Attack");
	}

	@Override
	public void doSomething() {
		// TODO Auto-generated method stub
				
	}

	@Override
	public String[] getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void returnButtonPressed() {
		VBox newProject = new VBox();
		Scene newScene = new Scene(newProject, 400, 400);
		Stage myStage = new Stage();
		myStage.setScene(newScene);
		myStage.show();
		Main restart = new Main();
		restart.start(myStage);
	}

	@Override
	public void imageSelected(SpriteImage imageView) {
		imageView.addBasePropertyMap(basePropertyMap);
		imageView.createInitialProperties(controller.getAuxiliaryElementConfigurationOptions(basePropertyMap));
		myRightToolBar.imageSelected(imageView);
		controller.defineElement(imageView.getName(), imageView.getAllProperties());
		controller.addElementToInventory(imageView.getName());
		
//		System.out.println(tester.getAllDefinedTemplateProperties());

	}

	@Override
	public void addToMap(String baseProperty, String value) {
		basePropertyMap.put(baseProperty, value);
//		myRightToolBar.addToMap(baseProperty, value);
		
	}
	
	public void setGameArea(GameArea game) {
		this.myGameArea = game;
	}
}
