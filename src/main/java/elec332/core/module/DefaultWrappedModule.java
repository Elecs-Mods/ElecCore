package elec332.core.module;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import elec332.core.api.module.ElecModule;
import elec332.core.api.module.IModuleContainer;
import elec332.core.api.module.IModuleController;
import elec332.core.api.module.IModuleInfo;
import elec332.core.util.FMLHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.VersionRange;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Elec332 on 25-9-2016.
 */
public class DefaultWrappedModule implements IModuleContainer {

    public DefaultWrappedModule(Object o, IModuleInfo moduleInfo) {
        this.module = o;
        this.name = moduleInfo.getName();
        this.owner = moduleInfo.getOwner();
        this.comName = new ResourceLocation(moduleInfo.getCombinedName().toString().toLowerCase());
        this.depB = moduleInfo.autoDisableIfRequirementsNotMet();
        this.moduleDependencies = ImmutableList.copyOf(moduleInfo.getModuleDependencies());
        this.modDependencies = ImmutableList.copyOf(moduleInfo.getModDependencies());
        this.clazz = moduleInfo.getModuleClass();
        this.alwaysEnabled = moduleInfo.alwaysEnabled();
        this.moduleController = moduleInfo.getModuleController();
        ModContainer mc = null;
        for (ModContainer m : FMLHelper.getMods()) {
            if (m.getModId().equals(owner)) {
                mc = m;
                break;
            }
        }
        this.mod = Preconditions.checkNotNull(mc);
    }

    private final String name, owner, clazz;
    private final ResourceLocation comName;
    private final boolean depB, alwaysEnabled;
    private final Object module;
    private final List<String> moduleDependencies;
    private final List<Pair<String, VersionRange>> modDependencies;
    private final IModuleController moduleController;
    private final ModContainer mod;

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getOwner() {
        return this.owner;
    }

    @Nonnull
    @Override
    public ResourceLocation getCombinedName() {
        return this.comName;
    }

    @Override
    public boolean autoDisableIfRequirementsNotMet() {
        return this.depB;
    }

    @Nonnull
    @Override
    public Object getModule() {
        return this.module;
    }

    @Nonnull
    @Override
    public ModContainer getOwnerMod() {
        return mod;
    }

    @Override
    public void invokeEvent(Object event) throws Exception {
        Class objClass = this.module.getClass();
        for (Method method : objClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ElecModule.EventHandler.class)/* || method.isAnnotationPresent(Mod.EventHandler.class)*/) {

                if (method.getParameterTypes().length != 1) {
                    continue;
                }
                if (!method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                    continue;
                }
                method.invoke(this.module, event);
            }
        }
    }

    @Nonnull
    @Override
    public List<Pair<String, VersionRange>> getModDependencies() {
        return modDependencies;
    }

    @Nonnull
    @Override
    public List<String> getModuleDependencies() {
        return this.moduleDependencies;
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

    @Override
    @Nonnull
    public IModuleController getModuleController() {
        return moduleController;
    }

}
