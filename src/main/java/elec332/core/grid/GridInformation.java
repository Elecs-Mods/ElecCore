package elec332.core.grid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 9-10-2016.
 * <p>
 * Can be used on field in {@link net.minecraft.tileentity.TileEntity}'s
 * to give a tile information about the grid it is in.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GridInformation {

    public Class value();

}
