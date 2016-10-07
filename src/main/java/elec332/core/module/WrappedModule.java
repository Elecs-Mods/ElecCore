package elec332.core.module;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Elec332 on 25-9-2016.
 */
public class WrappedModule implements IModuleContainer {

    WrappedModule(Object o, ElecModule moduleInfo){
        this.module = o;
        this.name = moduleInfo.name();
        this.owner = moduleInfo.owner();
        this.comName = new ResourceLocation(owner, name);
        this.depB = moduleInfo.autoDisableIfRequirementsNotMet();
        this.moduleDependencies = ImmutableList.copyOf(Lists.newArrayList(moduleInfo.moduleDependencies().split(";")));
        this.modDependencies = parseDependencyInfo(moduleInfo.modDependencies());
    }

    private final String name, owner;
    private final ResourceLocation comName;
    private final boolean depB;
    private final Object module;
    private final List<String> moduleDependencies;
    private final List<ArtifactVersion> modDependencies;

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

    @Override
    public void invokeEvent(Object event) throws Exception {
        Class objClass = this.module.getClass();
        for (Method method : objClass.getDeclaredMethods()){
            if (method.isAnnotationPresent(ElecModule.EventHandler.class) || method.isAnnotationPresent(Mod.EventHandler.class)) {

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
    public List<ArtifactVersion> getModDependencies() {
        return this.modDependencies;
    }

    @Nonnull
    @Override
    public List<String> getModuleDependencies() {
        return this.moduleDependencies;
    }

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
