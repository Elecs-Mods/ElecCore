package elec332.core.module;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import elec332.core.api.module.IModuleController;
import elec332.core.api.module.IModuleInfo;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.VersionRange;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by Elec332 on 8-10-2016.
 */
public class DefaultModuleInfo implements IModuleInfo {

    public DefaultModuleInfo(IModuleInfo info) {
        this(info.getOwner(), info.getName(), info.getModDependencies(), info.getModuleDependencies(), info.autoDisableIfRequirementsNotMet(), info.alwaysEnabled(), info.getModuleClass(), info.getModuleController(), info.getCombinedName());
    }

    public DefaultModuleInfo(String owner, String name, String modDeps, String moduleDeps, boolean ADRIM, boolean alwaysOn, String mainClazz, IModuleController moduleController) {
        this(owner, name, IModuleInfo.parseDependencyInfo(modDeps), Strings.isNullOrEmpty(moduleDeps) ? ImmutableList.of() : ImmutableList.copyOf(Lists.newArrayList(moduleDeps.split(";"))), ADRIM, alwaysOn, mainClazz, moduleController, new ResourceLocation(owner, name.toLowerCase()));
    }

    public DefaultModuleInfo(String owner, String name, List<Pair<String, VersionRange>> modDeps, List<String> moduleDeps, boolean ADRIM, boolean alwaysOn, String mainClazz, IModuleController moduleController, ResourceLocation combinedName) {
        this.owner = requireNonNull(owner);
        this.name = requireNonNull(name);
        this.modDeps = Collections.unmodifiableList(requireNonNull(modDeps));
        this.moduleDeps = Collections.unmodifiableList(requireNonNull(moduleDeps));
        this.autoDIRNM = ADRIM;
        this.alwaysEnabled = alwaysOn;
        this.clazz = requireNonNull(mainClazz);
        this.moduleController = requireNonNull(moduleController);
        this.combinedName = requireNonNull(combinedName);
    }

    private final String owner, name, clazz;
    private final ResourceLocation combinedName;
    private final boolean autoDIRNM, alwaysEnabled;
    private final List<Pair<String, VersionRange>> modDeps;
    private final List<String> moduleDeps;
    private final IModuleController moduleController;

    @Nonnull
    @Override
    public String getOwner() {
        return owner;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public ResourceLocation getCombinedName() {
        return combinedName;
    }

    @Override
    public boolean autoDisableIfRequirementsNotMet() {
        return autoDIRNM;
    }

    @Nonnull
    @Override
    public List<Pair<String, VersionRange>> getModDependencies() {
        return modDeps;
    }

    @Nonnull
    @Override
    public List<String> getModuleDependencies() {
        return moduleDeps;
    }

    @Nonnull
    @Override
    public String getModuleClass() {
        return clazz;
    }

    @Override
    public boolean alwaysEnabled() {
        return alwaysEnabled;
    }

    @Nonnull
    @Override
    public IModuleController getModuleController() {
        return moduleController;
    }

}
