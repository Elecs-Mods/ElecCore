package elec332.core.compat.forestry;

import elec332.core.client.ITextureLoader;
import elec332.core.client.model.RenderingRegistry;
import elec332.core.client.model.model.IModelLoader;
import elec332.core.compat.forestry.bee.ForestryBeeEffects;
import elec332.core.java.ReflectionHelper;
import elec332.core.main.ElecCore;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.core.Tabs;
import forestry.api.genetics.*;
import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;

/**
 * Created by Elec332 on 14-8-2016.
 */
public class ForestryInit {

    public static IBeeRoot beeRoot;

    static void init(){
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
        ForestryCompatHandler.tabBees = Tabs.tabApiculture;
        ForestryAlleles.dummyLoad();
        ForestryBeeEffects.init();
    }

    public static void postInit(){
        IndividualDefinitionRegistry.locked = true;
        for (IIndividualTemplate iIndividualTemplate : IndividualDefinitionRegistry.templates){
            if (iIndividualTemplate.isActive()) {
                iIndividualTemplate.registerMutations();
            } else {
                IAlleleSpecies allele;
                try {
                    allele = ((IGenomeTemplate)iIndividualTemplate.getGenomeTemplateType().newInstance()).getSpecies(iIndividualTemplate.getAlleles());
                } catch (Exception e){
                    ElecCore.logger.info("Error invocating class: "+iIndividualTemplate.getGenomeTemplateType().getCanonicalName());
                    ElecCore.logger.info("Attempting backup method to fetch species allele...");
                    allele = (IAlleleSpecies) iIndividualTemplate.getAlleles()[0];
                }
                AlleleManager.alleleRegistry.blacklistAllele(allele.getUID());
            }
        }
    }

}
