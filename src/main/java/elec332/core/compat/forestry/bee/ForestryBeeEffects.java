package elec332.core.compat.forestry.bee;

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.genetics.AlleleManager;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by Elec332 on 25-8-2016.
 */
public class ForestryBeeEffects {

    public static IAlleleBeeEffect effectNone;
    public static IAlleleBeeEffect effectAggressive;
    public static IAlleleBeeEffect effectHeroic;
    public static IAlleleBeeEffect effectBeatific;
    public static IAlleleBeeEffect effectMiasmic;
    public static IAlleleBeeEffect effectMisanthrope;
    public static IAlleleBeeEffect effectGlacial;
    public static IAlleleBeeEffect effectRadioactive;
    public static IAlleleBeeEffect effectCreeper;
    public static IAlleleBeeEffect effectIgnition;
    public static IAlleleBeeEffect effectExploration;
    public static IAlleleBeeEffect effectFestiveEaster;
    public static IAlleleBeeEffect effectSnowing;
    public static IAlleleBeeEffect effectDrunkard;
    public static IAlleleBeeEffect effectReanimation;
    public static IAlleleBeeEffect effectResurrection;
    public static IAlleleBeeEffect effectRepulsion;
    public static IAlleleBeeEffect effectFertile;
    public static IAlleleBeeEffect effectMycophilic;

    public static void init(){
        effectNone = getEffect("none");
        effectAggressive = getEffect("aggressive");
        effectHeroic = getEffect("heroic");
        effectBeatific = getEffect("beatific");
        effectMiasmic = getEffect("miasmic");
        effectMisanthrope = getEffect("misanthrope");
        effectGlacial = getEffect("glacial");
        effectRadioactive = getEffect("radioactive");
        effectCreeper = getEffect("creeper");
        effectIgnition = getEffect("ignition");
        effectExploration = getEffect("exploration");
        effectFestiveEaster = getEffect("festiveEaster");
        effectSnowing = getEffect("snowing");
        effectDrunkard = getEffect("drunkard");
        effectReanimation = getEffect("reanimation");
        effectResurrection = getEffect("resurrection");
        effectRepulsion = getEffect("repulsion");
        effectFertile = getEffect("fertile");
        effectMycophilic = getEffect("mycophilic");
    }

    private static IAlleleBeeEffect getEffect(String name){
        return (IAlleleBeeEffect) AlleleManager.alleleRegistry.getAllele("forestry.effect"+WordUtils.capitalize(name));
    }

}
