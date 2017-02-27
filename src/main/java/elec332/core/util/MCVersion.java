package elec332.core.util;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-1-2017.
 */
public enum MCVersion {

    OTHER(""),
    MC_1_10("1.10"),
    MC_1_10_2("1.10.2"),
    MC_1_11("1.11"),
    MC_1_11_2("1.11.2");

    MCVersion(String ver){
        this.name = ver;
    }

    private final String name;

    @Nonnull
    public static MCVersion getCurrentVersion(){
        if (currentVer == null){
            for (MCVersion m : MCVersion.values()){
                if (m.name.equals(FMLUtil.getMcVersion())){
                    currentVer = m;
                    break;
                }
            }
            if (currentVer == null){
                currentVer = OTHER;
            }
        }
        return currentVer;
    }

    private static MCVersion currentVer;

}
