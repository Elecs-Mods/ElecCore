package elec332.core.data.custom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import elec332.core.client.ClientHelper;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 28-7-2020
 */
public abstract class AbstractDataReader extends JsonReloadListener {

    public AbstractDataReader(String folder, LogicalSide loadSide) {
        this(GSON, folder, loadSide);
    }

    public AbstractDataReader(Gson gson, String folder, LogicalSide loadSide) {
        super(gson, folder);
        this.gson = gson;
        this.loadSide = loadSide;
        this.register();
    }

    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    protected final Gson gson;
    private final LogicalSide loadSide;

    @Override
    protected final void apply(@Nonnull Map<ResourceLocation, JsonElement> objectIn, @Nonnull IResourceManager resourceManagerIn, @Nonnull IProfiler profilerIn) {
        read(objectIn, resourceManagerIn, profilerIn);
    }

    protected abstract void read(@Nonnull Map<ResourceLocation, JsonElement> objects, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler);

    protected void register() {
        if (loadSide.isClient()) {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientHelper.registerReloadListener(this);
            });
        } else {
            MinecraftForge.EVENT_BUS.addListener((Consumer<FMLServerAboutToStartEvent>) event -> ((IReloadableResourceManager) event.getServer().getDataPackRegistries().getResourceManager()).addReloadListener(AbstractDataReader.this));
        }
    }

}
