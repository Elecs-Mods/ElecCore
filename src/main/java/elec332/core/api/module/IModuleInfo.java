package elec332.core.api.module;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 8-10-2016.
 */
public interface IModuleInfo {

    /**
     * The mod ID of the mod that "owns" this module.
     *
     * @return The modId
     */
    @Nonnull
    public String getOwner();

    /**
     * @return The name of this module
     */
    @Nonnull
    public String getName();

    /**
     * @return The ResourceLocation of this module, so: owner:name
     */
    @Nonnull
    public ResourceLocation getCombinedName();

    /**
     * When true, this module will load even if some of the dependencies are missing,
     * when false, this module will not be loaded when some dependencies are missing
     *
     * @return Whether this module should automatically disable when some of the
     *          dependencies are missing.
     */
    public boolean autoDisableIfRequirementsNotMet();

    /**
     * Returns an array of modId's this module depends on
     * (owner mod does not count).
     * This gets parsed the same way as in the
     * {@link net.minecraftforge.fml.common.Mod} annotation.
     * (Keep in mind that "before" and "after" does not work)
     *
     * @return The mod dependencies
     */
    @Nonnull
    public List<ArtifactVersion> getModDependencies();

    /**
     * Returns an array of module names this module depends on.
     * Format: modId:moduleName
     * Splitter: ;
     *
     * @return The mod dependencies
     */
    @Nonnull
    public List<String> getModuleDependencies();

    /**
     * The main module class that needs to be invoked to load the module.
     *
     * @return The main module class
     */
    @Nonnull
    public String getModuleClass();

    /**
     * If true, this module cannot be disabled
     * by the module controller
     *
     * @return Whether the module-controller can disable this module.
     */
    public boolean alwaysEnabled();

    /**
     * @return The module-controller for this module
     */
    @Nonnull
    public IModuleController getModuleController();

    public static List<ArtifactVersion> parseDependencyInfo(String modDependencies){
        List<ArtifactVersion> ret = Lists.newArrayList();
        if (Strings.isNullOrEmpty(modDependencies)){
            return ImmutableList.of();
        }
        String[] parts = modDependencies.split(";");
        for (String s : parts){
            ret.add(VersionParser.parseVersionReference(s));
        }
        return ImmutableList.copyOf(ret);
    }

}
