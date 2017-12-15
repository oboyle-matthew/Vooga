package authoring.levelEditor;

import engine.AuthoringModelController;
import engine.authoring_engine.AuthoringController;
import javafx.scene.layout.VBox;

public class GameEnder extends VBox{
	private AuthoringModelController myController;
	private GameEnderConditions conditions;
	private GameHealthSelector health;
	private GamePointSelector points;
	
	
	public GameEnder(AuthoringModelController controller) {
		myController = controller; 
		conditions = new GameEnderConditions(controller);
		health = new GameHealthSelector(controller);
		points = new GamePointSelector(controller);
		this.getChildren().addAll(conditions, health, points);
		this.setPrefWidth(300);
		this.setSpacing(100);
		}
	
	public void update() {
		conditions.update();
	}
	
	public void setRecorder(GameEnderRecorder r) {
		conditions.setRecorder(r);
		conditions.setPointRecorder(points);
	}
	
}