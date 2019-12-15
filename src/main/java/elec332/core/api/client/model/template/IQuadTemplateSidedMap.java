package elec332.core.api.client.model.template;

import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 6-12-2015.
 */
@OnlyIn(Dist.CLIENT)
public interface IQuadTemplateSidedMap {

    public void setQuadsForSide(Direction side, @Nonnull List<IQuadTemplate> newQuads);

    public void addQuadsForSide(Direction side, List<IQuadTemplate> toAdd);

    public void addQuadForSide(Direction side, IQuadTemplate toAdd);

    @Nonnull
    public List<IQuadTemplate> getForSide(Direction side);

}
