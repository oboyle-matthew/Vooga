package authoring.leftToolBar;

import java.util.List;

import interfaces.ClickableInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import sprites.StaticObject;

public class SimpleTab extends ScrollPane{
	private ClickableInterface clickable;
	private List<StaticObject> myList;
	private ListView<StaticObject> myListView;
	private ObservableList<StaticObject> items;
	
	public SimpleTab(ClickableInterface clickable, List<StaticObject> defaults) {
		this.clickable = clickable;
		addDefaultImages(defaults);
	}

	private void addDefaultImages(List<StaticObject> defaults) {
		myList = defaults;
		items = FXCollections.observableArrayList(myList);
		myListView = new ListView<>();
		myListView.setOnMouseClicked(e->clickable.clicked(
      		myListView.getSelectionModel().getSelectedItem()));
		myListView.setItems(items);
		this.setContent(myListView);
	}
	
	protected void addItem(int size, String imageString) {
		items.add(new StaticObject(size, clickable, imageString));
	}
}
