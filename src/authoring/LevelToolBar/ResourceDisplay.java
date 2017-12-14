package authoring.LevelToolBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import display.factory.TabFactory;
import engine.authoring_engine.AuthoringController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResourceDisplay extends VBox{
	private TabPane resourceTabs;
	private List<ResourceTab> resources;
	private Map<String, Double> resourceEndowments;
	private TextField name;
	private TextField value;
	private AuthoringController myController;
	private TabFactory tabMaker;
	private final int RESOURCE_DISPLAY_MAX_WIDTH = 250;
	private final boolean IS_CLOSABLE = false;
	private final String RESOURCE_NAME_PROMPT_TEXT = "Name";
	private final String TAB_LEVELS_LABEL = "Level ";
	private final String RESOURCE_VALUE_PROMPT_TEXT = "Value";
	private final String ENTER_BUTTON_LABEL = "add!";
	
	public ResourceDisplay(AuthoringController controller){
		myController = controller;
		this.setMaxWidth(RESOURCE_DISPLAY_MAX_WIDTH );
		resourceEndowments = new HashMap<>();
		
		
		resourceTabs = new TabPane();
		tabMaker = new TabFactory();
		resources = new ArrayList<ResourceTab>();
		this.getChildren().add(resourceTabs);
		changeResourceValApparatus();
	}

	private void createResourceTabs() {
		for (int i=0; i<myController.getNumLevelsForGame(); i++) {
//			System.out.println(Integer.toString(myController.getCurrentLevel()));
			Tab newTab = tabMaker.buildTabWithoutContent(TAB_LEVELS_LABEL + Integer.toString(i+1), null, resourceTabs);
			ResourceTab newLv = new ResourceTab(i+1, myController);
			newLv.attach(newTab);
			resources.add(newLv);
			final int j = i+1;
			newTab.setOnSelectionChanged(e->update(j));
			newTab.setClosable(IS_CLOSABLE);
			resourceTabs.getTabs().add(newTab);
		}
		
	}

	private void changeResourceValApparatus() {
		name = new TextField();
		name.setPromptText(RESOURCE_NAME_PROMPT_TEXT);
		value = new TextField();
		value.setPromptText(RESOURCE_VALUE_PROMPT_TEXT);
		Button enter = new Button(ENTER_BUTTON_LABEL);
		enter.setOnAction(e->{
			try {
			if (myController.getResourceEndowments().containsKey(name.getText())) {
				Double d = myController.getResourceEndowments().get(name.getText());
				d = Double.parseDouble(value.getText());
			}
			else {
				resourceEndowments.put(name.getText(), Double.parseDouble(value.getText()));
			}
			try {
				myController.setResourceEndowment(name.getText(), Double.parseDouble(value.getText()));
			} catch(NumberFormatException nfe) {
				//System.out.println("you have to type in a number");
				//TODO ALERT FACTORY
			}
			myController.setResourceEndowment(name.getText(), Double.parseDouble(value.getText()));
			update(myController.getCurrentLevel());
		}catch(Exception nfe) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("Input Not Valid");
			a.setContentText("You need to input a number!");
			a.showAndWait();
			//TODO ALERT FACTORY
		}});
		this.getChildren().addAll(name, value, enter);
		
	}

	private void update(int lv) {
		if (resources.size()!=0) {
		resources.get(lv-1).update();
		}
		name.clear();
		value.clear();
		
	}
	
	void updateCurrentState() {
		resources.clear();
		resourceTabs.getTabs().clear();
		createResourceTabs();
		for(int i=0; i<resources.size(); i++) {
			resources.get(i).update();
		}
		myController.setLevel(1);
	}

	public VBox getRoot() {
		return this;
	}
}
