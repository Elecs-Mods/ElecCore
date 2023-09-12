package elec332.core.api.registration;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.tileentity.TileEntityType;

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
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface RegisteredTileEntity {

    public String value();

    /**
     * Use this to set the mod owner of this tile when auto-detection fails
     *
     * @return The mod owner
     */
    public String mod() default "";

    /**
     * Use this interface if you want the {@link TileEntityType} to be set automatically
     */
    public interface TypeSetter {

        public void setTileEntityType(TileEntityType<?> type);

    }

}
