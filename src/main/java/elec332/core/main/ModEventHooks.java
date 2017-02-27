package elec332.core.main;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import elec332.core.api.IElecCoreMod;
import elec332.core.api.module.IModuleInfo;
import elec332.core.api.util.IDependencyHandler;
import elec332.core.compat.ModNames;
import elec332.core.util.FMLUtil;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 17-11-2016.
 */
class ModEventHooks {

    ModEventHooks(FMLModContainer modContainer){
        this.modContainer = Validate.notNull(modContainer);
        this.elecCoreMod = modContainer.getMod() instanceof IElecCoreMod ? (IElecCoreMod) modContainer.getMod() : null;
        this.dependencyHandler = elecCoreMod != null ? elecCoreMod.getDependencyHandler() : null;
    }

    @Nonnull
    private final FMLModContainer modContainer;
    @Nullable
    private final IElecCoreMod elecCoreMod;
    @Nullable
    private final IDependencyHandler dependencyHandler;
    private static final ArtifactVersion actualForge, actualElecCore;
    private static MissingModsException ex;

    private static final List<ModContainer> mc = Lists.newArrayList();

    @Subscribe
    @SuppressWarnings("all")
    public void onConstuct(FMLConstructionEvent event){
        if (dependencyHandler != null) {
            String mcVersion = ForgeVersion.mcVersion;
            String forge = dependencyHandler.getRequiredForgeVersion(mcVersion);
            Set<ArtifactVersion> missing = Sets.newHashSet();
            if (!Strings.isNullOrEmpty(forge)) {
                if (!forge.startsWith("(") || !forge.startsWith("[")) {
                    forge = "[" + forge + ",)";
                }
                ArtifactVersion reqForgeVer = VersionParser.parseVersionReference(ModNames.FORGE + "@" + forge);
                if (!reqForgeVer.containsVersion(actualForge)) {
                    missing.add(reqForgeVer);
                }
            }
            String elecCore = dependencyHandler.getRequiredElecCoreVersion(mcVersion);
            if (!Strings.isNullOrEmpty(elecCore)){
                if (!elecCore.startsWith("(") || !elecCore.startsWith("[")) {
                    elecCore = "[" + elecCore + ",)";
                }
                ArtifactVersion reqElecVer = VersionParser.parseVersionReference(ElecCore.MODID + "@" + elecCore);
                if (!reqElecVer.containsVersion(actualElecCore)) {
                    missing.add(reqElecVer);
                }
            }
            String other = dependencyHandler.getOtherDependencies(mcVersion);
            if (!Strings.isNullOrEmpty(other)){
                List<ArtifactVersion> deps = IModuleInfo.parseDependencyInfo(other);
                for (ArtifactVersion version : deps){
                    ModContainer mc = FMLUtil.findMod(version.getLabel());
                    if (mc == null || !version.containsVersion(mc.getProcessedVersion())){
                        missing.add(version);
                    }
                }
            }
            if (!missing.isEmpty()){
                onVersionsNotFound(missing);
            }
        }/*
        if (!mc.isEmpty()){
            try {
                Field f = LoadController.class.getDeclaredField("activeModList");
                f.setAccessible(true);
                List<ModContainer> l = Lists.newArrayList(((List<ModContainer>) f.get(FMLUtil.getLoadController())));
                ModContainer m = l.get(l.size()-1);
                if (modContainer == m){
                    l.removeAll(mc);
                }
                f.set(FMLUtil.getLoadController(), l);
            } catch (Exception e1){
                throw new RuntimeException(e1);
            }
        }*/
    }

    private void onVersionsNotFound(Set<ArtifactVersion> missing){
        //FMLUtil.getLoadController().transition();
        ElecCore.logger.error("The mod %s (%s) requires mod versions %s to be available", modContainer.getModId(), modContainer.getName(), missing);
        MissingModsException e = new MissingModsException(missing, modContainer.getModId(), modContainer.getName());
        /*if (FMLCommonHandler.instance().getSide().isClient()) {
            try {
                Field f = FMLClientHandler.class.getDeclaredField("modsMissing");
                f.setAccessible(true);
                f.set(FMLClientHandler.instance(), e);
            } catch (Exception exeption) {
                //NBC
            }
        }
        try {
            Field f = LoadController.class.getDeclaredField("activeModList");
            f.setAccessible(true);
            ///((List<ModContainer>) f.get(FMLUtil.getLoadController())).remove(modContainer);
        } catch (Exception e1){
            throw new RuntimeException(e1);
        }
        mc.add(modContainer);*/
        LogManager.getLogger(modContainer.getName()).info("Missing Mods Exception: ", e);
        ex = e;//throw e;
    }


    static {
        actualForge = new DefaultArtifactVersion(ModNames.FORGE, ForgeVersion.getVersion());
        actualElecCore = new DefaultArtifactVersion(ElecCore.MODID, ElecCore.ElecCoreVersion);
        FMLUtil.getMainModBus().unregister(FMLUtil.getLoadController());
        FMLUtil.getMainModBus().register(new Object(){

            @Subscribe
            @SuppressWarnings("all")
            public void onConstuct(FMLConstructionEvent event){
                if (ex != null){
                    throw ex;
                }
            }

        });
        FMLUtil.getMainModBus().register(FMLUtil.getLoadController());
    }

}
