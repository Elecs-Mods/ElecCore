package elec332.core.api.registration;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 13-8-2018.
 *
 * Used on a {@link TileEntity} to bind the specified {@link TileEntitySpecialRenderer} to it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HasSpecialRenderer {

    Class<? extends TileEntitySpecialRenderer<?>> value();

}
