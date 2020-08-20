package elec332.core.api.util;

import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * Created by Elec332 on 20-8-2020
 */
public interface ISimpleResourcePack {

    InputStream getResourceStream(@Nonnull ResourcePackType type, @Nonnull ResourceLocation location);

    boolean resourceExists(@Nonnull ResourcePackType type, @Nonnull ResourceLocation location);

}
