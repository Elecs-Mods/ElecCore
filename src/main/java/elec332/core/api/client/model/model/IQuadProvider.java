package elec332.core.api.client.model.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 11-3-2016.
 */
public interface IQuadProvider {

    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> getBakedQuads(@Nullable BlockState state, Direction side, Random random);

}
