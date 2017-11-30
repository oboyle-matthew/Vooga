package player;

import java.util.ArrayList;
import java.util.List;

import interfaces.ClickableInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import splashScreen.ScreenDisplay;
import sprites.BackgroundObject;
import sprites.InteractiveObject;
import sprites.Sprite;
import sprites.StaticObject;

/**
 * Creates a re-runable HBox for old commands
 * 
 * @author Matt
 */
public class GenericToolBar extends ScrollPane {
	private static final int WIDTH = 300;
	private List<InteractiveObject> myList;
	private ListView<InteractiveObject> myListView;
	protected ObservableList<InteractiveObject> items;
	private ScreenDisplay myDisplay;
	private StaticObject myStatic1;
	private StaticObject myStatic2;
	private BackgroundObject myBackground3;
	private BackgroundObject myBackground4;
	private Class<?> myCls;
	
	public GenericToolBar(ScreenDisplay display, Class<?> cls) {
		myCls = cls;
		this.setLayoutY(50);
		myDisplay = display;
		init();
//		if (cls == StaticObject.class) addToStaticToolbar();
//		else System.out.println("Fails.");
	}
	
	public void addToStaticToolbar() {
        items = FXCollections.observableArrayList(myList);
        myListView = new ListView<>();
        myListView.setOnMouseClicked(e->myDisplay.listItemClicked(
        		myListView.getSelectionModel().getSelectedItem(), null));
        myListView.setItems(items);
        this.setContent(myListView);
	}
	
	public void init() {
		createDefaultObjects();
		addToList();
        addToToolbar();
	}

	public void createDefaultObjects() {
		myStatic1 = createNewStatic(3, "black_square.png");
		myStatic2 = createNewStatic(1, "black_square2.png");
		myBackground3 = createNewBackground(1, "green_tank.png");
		myBackground4 = createNewBackground(2, "red_balloon.png");
	}

	public void addToList() {
		myList = new ArrayList<>();
        myList.add(myStatic1);
        myList.add(myStatic2);
        myList.add(myBackground3);
        myList.add(myBackground4);
	}
	
	public void addToToolbar() {
        items = FXCollections.observableArrayList(myList);
        myListView = new ListView<>();
        myListView.setOnMouseClicked(e->myDisplay.listItemClicked(
        		myListView.getSelectionModel().getSelectedItem(), null));
        myListView.setItems(items);
        this.setContent(myListView);
	}
	
	private StaticObject createNewStatic(int size, String imageString) {
		return new StaticObject(size, myDisplay, imageString);
	}
	
	private BackgroundObject createNewBackground(int size, String imageString) {
		return new BackgroundObject(size, myDisplay, imageString);
	}
}