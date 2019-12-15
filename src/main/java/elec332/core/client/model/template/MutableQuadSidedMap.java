package elec332.core.client.model.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplateSidedMap;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Elec332 on 6-12-2015.
 */
@OnlyIn(Dist.CLIENT)
public class MutableQuadSidedMap implements IQuadTemplateSidedMap {

    @Nonnull
    public static MutableQuadSidedMap newQuadSidedMap() {
        EnumMap<Direction, List<IQuadTemplate>> param = Maps.newEnumMap(Direction.class);
        for (Direction facing : Direction.values()) {
            param.put(facing, Lists.<IQuadTemplate>newArrayList());
        }
        return new MutableQuadSidedMap(param);
    }

    private MutableQuadSidedMap(EnumMap<Direction, List<IQuadTemplate>> quads) {
        this.quads = quads;
    }

    private EnumMap<Direction, List<IQuadTemplate>> quads;

    @Override
    public void setQuadsForSide(Direction side, @Nonnull List<IQuadTemplate> newQuads) {
        quads.put(side, newQuads);
    }

    @Override
    public void addQuadsForSide(Direction side, List<IQuadTemplate> toAdd) {
        for (IQuadTemplate template : toAdd) {
            addQuadForSide(side, template);
        }
    }

    @Override
    public void addQuadForSide(Direction side, IQuadTemplate toAdd) {
        getForSide(side).add(toAdd);
    }

    @Nonnull
    @Override
    public List<IQuadTemplate> getForSide(Direction side) {
        List<IQuadTemplate> ret = quads.get(side);
        if (ret == null) {
            ret = Lists.newArrayList();
            quads.put(side, ret);
        }
        return ret;
    }

}
