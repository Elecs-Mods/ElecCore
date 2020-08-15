package elec332.core.api.module;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.VersionRange;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

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
    String getOwner();

    /**
     * @return The name of this module
     */
    @Nonnull
    String getName();

    /**
     * @return The ResourceLocation of this module, so: owner:name
     */
    @Nonnull
    ResourceLocation getCombinedName();

    /**
     * When true, this module will load even if some of the dependencies are missing,
     * when false, this module will not be loaded when some dependencies are missing
     *
     * @return Whether this module should automatically disable when some of the
     * dependencies are missing.
     */
    boolean autoDisableIfRequirementsNotMet();

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
    Set<Pair<String, VersionRange>> getModDependencies();

    /**
     * Returns an array of module names this module depends on.
     * Format: modId:moduleName
     * Splitter: ;
     *
     * @return The mod dependencies
     */
    @Nonnull
    Set<ResourceLocation> getModuleDependencies();

    /**
     * The main module class that needs to be invoked to load the module.
     *
     * @return The main module class
     */
    @Nonnull
    String getModuleClass();

    /**
     * If true, this module cannot be disabled
     * by the module controller
     *
     * @return Whether the module-controller can disable this module.
     */
    boolean alwaysEnabled();

    /**
     * @return The module-controller for this module
     */
    @Nonnull
    IModuleController getModuleController();

    static Set<Pair<String, VersionRange>> parseDependencyInfo(String modDependencies) {
        try {
            List<Pair<String, VersionRange>> ret = Lists.newArrayList();
            if (Strings.isNullOrEmpty(modDependencies)) {
                return ImmutableSet.of();
            }
            String[] parts = modDependencies.split(";");
            for (String s : parts) {
                String[] split = s.split("@");
                if (split.length > 2) {
                    throw new IllegalArgumentException(s);
                }
                VersionRange version = IModInfo.UNBOUNDED;
                if (split.length == 2) {
                    version = VersionRange.createFromVersionSpec(split[1]);
                }
                ret.add(Pair.of(split[0], version));
            }
            return ImmutableSet.copyOf(ret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse dependency info!", e);
        }
    }

    interface Builder {

        Builder noAutoDisableIfRequirementsNotMet();

        Builder addModDependency(String name, VersionRange version);

        Builder addModuleDependency(ResourceLocation moduleName);

        Builder alwaysEnabled();

        IModuleInfo build();

    }

}
