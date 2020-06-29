package elec332.core.client.model;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.client.model.data.ModelProperty;

/**
 * Created by Elec332 on 3-1-2020
 */
public class ModelProperties {

    public static final ModelProperty<ILightReader> WORLD = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();

}
