package authoring;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import sprites.StaticObject;

/**
 * Creates a re-runable HBox for old commands
 * 
 * @author Matt
 */
public class LeftToolBar extends ScrollPane {
	private static final int WIDTH = 300;
	private List<StaticObject> myList;
	private ListView<StaticObject> myListView;
	private AuthorInterface myAuthor;
	private StaticObject myStatic1;
	private StaticObject myStatic2;
	private StaticObject myStatic3;
	private StaticObject myStatic4;
	
	public LeftToolBar(AuthorInterface author) {
		this.setLayoutY(50);
		myAuthor = author;
        myList = new ArrayList<StaticObject>();
        myStatic1 = new StaticObject(1, author);
        myStatic2 = new StaticObject(1, author);        
        myStatic3 = new StaticObject(3, author);        
        myStatic4 = new StaticObject(2, author);
        myList.add(myStatic1);
        myList.add(myStatic2);
        myList.add(myStatic3);
        myList.add(myStatic4);
        ObservableList<StaticObject> items = FXCollections.observableArrayList(myList);
        myListView = new ListView<StaticObject>();
        myListView.setOnMouseClicked(e->myAuthor.clicked(
        		myListView.getSelectionModel().getSelectedItem()));
        myListView.setItems(items);
        this.setContent(myListView);
	}


//	private void drag(MouseEvent e, Rectangle myrec) {
//		Rectangle newRectangle = new Rectangle(myrec.getX(), myrec.getY());
//		newRectangle.setX(e.getSceneX());
//		myrec.setY(e.getSceneY());
//	}

//	public void init() {
//		myCommandHistoryBox = new ScrollPane();
//		commandHistoryView = new ListView<Button>();
//		commandHistory = new ArrayList<Button>();
//		ObservableList<Button> items =FXCollections.observableArrayList(commandHistory);
//        commandHistoryView.setItems(items);
//        commandHistoryView.getSelectionModel();
//        myCommandHistoryBox.setContent(commandHistoryView);
//	}
	
//	public void addCommandToHistoryBox(String command) {
//		Button button = new Button(command);
//		button.addEventHandler(MouseEvent.MOUSE_CLICKED, e->System.out.println(command));
//		commandHistory.add(button);
//		button.setStyle(  "-fx-border-color: transparent; -fx-border-width: 0;-fx-background-radius: 0;-fx-background-color: transparent;");
//		ObservableList<Button> items =FXCollections.observableArrayList(commandHistory);
//        commandHistoryView.setItems(items);
//	}
	
}
