package engine.behavior.firing;

import engine.behavior.ElementProperty;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author radithya
 *
 */
public class RoundRobinWaveFiringStrategy extends AbstractWaveFiringStrategy {

	Iterator<String> elementChooser;

	public RoundRobinWaveFiringStrategy(
			@ElementProperty(value = "templatesToFire", isTemplateProperty = true) List<String> templatesToFire,
			@ElementProperty(value = "period", isTemplateProperty = true) double period,
			@ElementProperty(value = "totalWaves", isTemplateProperty = true) int totalWaves) {
		super(templatesToFire, period, totalWaves);
		elementChooser = templatesToFire.iterator();
	}

	@Override
	protected String chooseElementToSpawn() {
		if (!elementChooser.hasNext()) {
			elementChooser = getTemplatesToFire().iterator();
		}
		return elementChooser.next();
	}

	@Override
	public String getAudioUrl() {
		return null;
	}

}
