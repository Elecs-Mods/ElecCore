package elec332.core.util;

import net.minecraft.world.World;

/**
 * Created by Elec332 on 23-8-2016.
 * <p>
 * Taken from MagicBees, original code can be found here:
 * https://github.com/MagicBees/MagicBees/blob/master/src/main/java/magicbees/main/utils/MoonPhase.java
 */
public enum MoonPhase {

    FULL("full"),
    WANING_GIBBOUS("gibbousWaning"),
    WANING_HALF("halfWaning"),
    WANING_CRESCENT("crescentWaning"),
    NEW("new"),
    WAXING_CRESCENT("crescentWaxing"),
    WAXING_HALF("halfWaxing"),
    WAXING_GIBBOUS("gibbousWaxing");

    private String phaseName;

    MoonPhase(String name) {
        this.phaseName = name;
    }

    public boolean isBetween(MoonPhase first, MoonPhase second) {
        if (first.ordinal() <= second.ordinal()) {
            return first.ordinal() <= this.ordinal() && this.ordinal() <= second.ordinal();
        } else {
            return first.ordinal() <= this.ordinal() && this.ordinal() <= WAXING_GIBBOUS.ordinal() || FULL.ordinal() <= this.ordinal() && this.ordinal() <= second.ordinal();
        }
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal("moon." + this.phaseName);
    }

    public static MoonPhase getMoonPhase(World w) {
        return getMoonPhaseFromTime(w.getWorldTime());
    }

    public static MoonPhase getMoonPhaseFromTime(long time) {
        return values()[(int) ((time - 6000L) / 24000L) % 8];
    }

}
