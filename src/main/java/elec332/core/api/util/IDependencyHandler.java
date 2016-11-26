package elec332.core.api.util;

/**
 * Created by Elec332 on 19-11-2016.
 */
public interface IDependencyHandler {

    default public String getRequiredForgeVersion(String mcVersion){
        return null;
    };

    default public String getRequiredElecCoreVersion(String mcVersion){
        return null;
    };

    default public String getOtherDependencies(String mcVersion){
        return null;
    };

}
