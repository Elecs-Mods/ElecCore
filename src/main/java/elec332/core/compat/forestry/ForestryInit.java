package elec332.core.compat.forestry;

import elec332.core.client.ITextureLoader;
import elec332.core.client.model.RenderingRegistry;
import elec332.core.client.model.model.IModelLoader;
import elec332.core.compat.forestry.testMod.TestMod;
import elec332.core.java.ReflectionHelper;
import elec332.core.main.ElecCore;
import forestry.api.apiculture.*;
import forestry.api.genetics.*;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;

/**
 * Created by Elec332 on 14-8-2016.
 */
public class ForestryInit {

    public static IAlleleBeeEffect forestryEffectNone;
    public static IBeeRoot beeRoot;

    static void init(){
        forestryEffectNone = (IAlleleBeeEffect) AlleleManager.alleleRegistry.getAllele("forestry.none");
        beeRoot = BeeManager.beeRoot;
        //ForestryAlleles.init();
        if (FMLCommonHandler.instance().getSide().isClient()) {
            AlleleManager.alleleRegistry.registerAlleleHandler(new IAlleleHandler() {

                @Override
                public void onRegisterAllele(IAllele allele) {
                    if (allele instanceof IModelLoader){
                        RenderingRegistry.instance().registerLoader((IModelLoader) allele);
                    }
                    if (allele instanceof ITextureLoader){
                        RenderingRegistry.instance().registerLoader((ITextureLoader) allele);
                    }
                    if (allele instanceof AlleleBeeSpecies){
                        try {
                            Field f = ReflectionHelper.makeFieldAccessible(AlleleBeeSpecies.class.getDeclaredField("beeModelProvider"));
                            IBeeModelProvider modelProvider = (IBeeModelProvider) f.get(allele);
                            if (modelProvider instanceof IModelLoader){
                                RenderingRegistry.instance().registerLoader((IModelLoader) allele);
                            }
                            if (modelProvider instanceof ITextureLoader){
                                RenderingRegistry.instance().registerLoader((ITextureLoader) allele);
                            }
                        } catch (Exception e){
                            ElecCore.logger.error("Error checking bee ModelProvider");
                        }
                    }
                }

                @Override
                public void onRegisterClassification(IClassification classification) {
                }

                @Override
                public void onRegisterFruitFamily(IFruitFamily family) {
                }

            });
        }
        TestMod.init();
    }

    public static void postInit(){
        IndividualDefinitionRegistry.locked = true;
        for (IIndividualTemplate iIndividualTemplate : IndividualDefinitionRegistry.templates){
            iIndividualTemplate.registerMutations();
        }
        TestMod.postInit();
    }

}
