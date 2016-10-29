package elec332.core.api.module;

/**
 * Created by Elec332 on 24-10-2016.
 */
public interface IDefaultModuleImplementations {

    public IModuleInfo newDefaultModuleInfo(String owner, String name, String modDependencies, String moduleDependencies, boolean autoDisableIfRequirementsNotMet, boolean alwaysOn, String mainClazz, IModuleController moduleController);

    public IModuleContainer newDefaultModuleContainer(Object moduleInstance, IModuleInfo moduleInfo);

}
