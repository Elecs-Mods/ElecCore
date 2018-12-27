package elec332.core.api.registration;

import net.minecraft.tileentity.TileEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 14-1-2016.
 * <p>
 * Used to register {@link TileEntity}'s, annotate your TileEntity class to register it
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisteredTileEntity {

    public String value();

}
