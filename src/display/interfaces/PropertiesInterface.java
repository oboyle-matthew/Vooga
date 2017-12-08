package display.interfaces;

import authoring.rightToolBar.SpriteImage;
import display.tabs.SimpleTab;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public interface PropertiesInterface {
	
	public void clicked(MouseEvent e, ImageView imageView, SimpleTab tab);

	public void imageSelected(SpriteImage imageView);

	public void addToWave();

}
