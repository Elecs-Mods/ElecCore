package elec332.core.grid;

import elec332.core.world.DimensionCoordinate;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 8-11-2017.
 */
public interface IPositionable {

	@Nonnull
	public DimensionCoordinate getPosition();

	default public boolean hasChanged(){
		return false;
	}

}
