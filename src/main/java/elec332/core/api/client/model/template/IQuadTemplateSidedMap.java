package elec332.core.api.client.model.template;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
public interface IQuadTemplateSidedMap {

    public void setQuadsForSide(EnumFacing side, @Nonnull List<IQuadTemplate> newQuads);

    public void addQuadsForSide(EnumFacing side, List<IQuadTemplate> toAdd);

    public void addQuadForSide(EnumFacing side, IQuadTemplate toAdd);

    @Nonnull
    public List<IQuadTemplate> getForSide(EnumFacing side);

}
