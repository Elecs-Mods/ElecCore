package elec332.core.util;

import elec332.core.ElecCore;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Elec332 on 19-1-2020
 */
public class ResourceHelper {

    /**
     * Gets the {@link InputStream} from the provided {@link ResourceLocation},
     * also works on the {@link net.minecraftforge.api.distmarker.Dist#DEDICATED_SERVER}
     * (MC's method doesn't)
     *
     * @param resourceLocation The location of the resource
     * @return The {@link InputStream} from the provided {@link ResourceLocation}
     */
    @Nonnull
    public static InputStream getInputStreamFromResource(@Nonnull ResourceLocation resourceLocation) throws IOException {
        String location = "/assets/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath();
        InputStream ret = ElecCore.class.getResourceAsStream(location);
        if (ret != null) {
            return ret;
        }
        throw new FileNotFoundException(location);
    }

    public static ResourceLocation getItemModelLocation(@Nonnull ResourceLocation name) {
        return getPrefixedLocation(name, "item/");
    }

    public static ResourceLocation getBlockModelLocation(@Nonnull ResourceLocation name) {
        return getPrefixedLocation(name, "block/");
    }

    public static ResourceLocation getPrefixedLocation(@Nonnull ResourceLocation name, @Nonnull String prefix) {
        return new ResourceLocation(name.getNamespace(), prefix + name.getPath());
    }

}
