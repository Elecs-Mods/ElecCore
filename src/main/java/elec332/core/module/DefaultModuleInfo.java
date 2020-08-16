package elec332.core.module;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import elec332.core.api.module.IModuleController;
import elec332.core.api.module.IModuleInfo;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.VersionRange;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Elec332 on 8-10-2016.
 */
public class DefaultModuleInfo implements IModuleInfo {

    public DefaultModuleInfo(IModuleInfo info) {
        this(info.getOwner(), info.getName(), info.getModDependencies(), info.getModuleDependencies(), info.autoDisableIfRequirementsNotMet(), info.alwaysEnabled(), info.getModuleClass(), info.getModuleController(), info.getCombinedName());
    }

    @SuppressWarnings("UnstableApiUsage")
    public DefaultModuleInfo(String owner, String name, String modDeps, String moduleDeps, boolean ADRIM, boolean alwaysOn, String mainClazz, IModuleController moduleController) {
        this(owner, name, IModuleInfo.parseDependencyInfo(modDeps), Strings.isNullOrEmpty(moduleDeps) ? ImmutableSet.of() : Arrays.stream(moduleDeps.split(";")).map(ResourceLocation::new).collect(ImmutableSet.toImmutableSet()), ADRIM, alwaysOn, mainClazz, moduleController, new ResourceLocation(owner, name.toLowerCase()));
    }

    public DefaultModuleInfo(String owner, String name, Set<Pair<String, VersionRange>> modDeps, Set<ResourceLocation> moduleDeps, boolean ADRIM, boolean alwaysOn, String mainClazz, IModuleController moduleController, ResourceLocation combinedName) {
        this.owner = Objects.requireNonNull(owner);
        this.name = Objects.requireNonNull(name);
        this.modDeps = Collections.unmodifiableSet(Objects.requireNonNull(modDeps));
        this.moduleDeps = Collections.unmodifiableSet(Objects.requireNonNull(moduleDeps));
        this.autoDIRNM = ADRIM;
        this.alwaysEnabled = alwaysOn;
        this.clazz = Objects.requireNonNull(mainClazz);
        this.moduleController = Objects.requireNonNull(moduleController);
        this.combinedName = Objects.requireNonNull(combinedName);
    }

    private final String owner, name, clazz;
    private final ResourceLocation combinedName;
    private final boolean autoDIRNM, alwaysEnabled;
    private final Set<Pair<String, VersionRange>> modDeps;
    private final Set<ResourceLocation> moduleDeps;
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
    public Set<Pair<String, VersionRange>> getModDependencies() {
        return modDeps;
    }

    @Nonnull
    @Override
    public Set<ResourceLocation> getModuleDependencies() {
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

    public static class Builder implements IModuleInfo.Builder {

        public Builder(String owner, IModuleController moduleController, String name, String clazz) {
            this.owner = owner;
            this.name = name;
            this.clazz = clazz;
            this.moduleController = moduleController;
            this.modDeps = Sets.newHashSet();
            this.moduleDeps = Sets.newHashSet();
        }

        private final String owner, name, clazz;
        private final Set<Pair<String, VersionRange>> modDeps;
        private final Set<ResourceLocation> moduleDeps;
        private final IModuleController moduleController;

        private boolean adrim = true;
        private boolean ae = false;

        @Override
        public IModuleInfo.Builder noAutoDisableIfRequirementsNotMet() {
            this.adrim = false;
            return this;
        }

        @Override
        public IModuleInfo.Builder addModDependency(String name, VersionRange version) {
            return this;
        }

        @Override
        public IModuleInfo.Builder addModuleDependency(ResourceLocation moduleName) {
            return this;
        }

        @Override
        public IModuleInfo.Builder alwaysEnabled() {
            this.ae = true;
            return this;
        }

        @Override
        public IModuleInfo build() {
            return new DefaultModuleInfo(owner, name, modDeps, moduleDeps, adrim, ae, clazz, moduleController, new ResourceLocation(owner, name.toLowerCase()));
        }

    }

}
