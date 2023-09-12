package elec332.core.api.registration;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 13-8-2018.
 * <p>
 * Used on a {@link TileEntity} to bind the specified {@link TileEntityRenderer} to it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HasSpecialRenderer {

    Class<? extends TileEntityRenderer<?>> value();

}
