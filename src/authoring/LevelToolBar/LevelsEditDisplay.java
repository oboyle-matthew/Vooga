package authoring.LevelToolBar;

import authoring.EditDisplay;
import engine.authoring_engine.AuthoringController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LevelsEditDisplay {
	private static final int SIZE = 800;
	
	private Stage myStage;
	private Scene myScene;
	private AuthoringController myController;
	private BorderPane myRoot;
	private ResourceDisplay resourceEditor;
	private GameEnder gameEnder;
	private EditDisplay myDisplay;
	private GameEnderRecorder recorder;
	
	public LevelsEditDisplay(AuthoringController controller, EditDisplay display) {
//		myDisplay = display;
		myController = controller;
		myStage = new Stage();
		myRoot = new BorderPane();
		myScene = new Scene(myRoot, SIZE, SIZE);
		resourceEditor = new ResourceDisplay(controller);
		gameEnder = new GameEnder(controller, display);
		recorder = new GameEnderRecorder(gameEnder.getSelectedLevels());
		gameEnder.setRecorder(recorder);
		myRoot.setLeft(gameEnder);
		myRoot.setRight(resourceEditor);
		myRoot.setCenter(recorder);
		myStage.setScene(myScene);

//		myStage.show();
	}
	
	
	public void open() {
		myStage.show();
	}
}
