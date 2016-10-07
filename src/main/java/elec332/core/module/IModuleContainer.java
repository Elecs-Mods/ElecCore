package elec332.core.module;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 25-9-2016.
 */
public interface IModuleContainer {

    @Nonnull
    public String getName();

    @Nonnull
    public String getOwner();

    @Nonnull
    public ResourceLocation getCombinedName();

    @Nonnull
    public Object getModule();

    public void invokeEvent(Object event) throws Exception;

    @Nonnull
    public List<ArtifactVersion> getModDependencies();

    public boolean autoDisableIfRequirementsNotMet();

    @Nonnull
    public List<String> getModuleDependencies();

}
