package elec332.core.client.model.template;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Elec332 on 6-12-2015.
 */
public class MutableQuadSidedMap implements ITemplateSidedMap {

    @Nonnull
    public static MutableQuadSidedMap newQuadSidedMap(){
        EnumMap<EnumFacing, List<IQuadTemplate>> param = Maps.newEnumMap(EnumFacing.class);
        for (EnumFacing facing : EnumFacing.VALUES){
            param.put(facing, Lists.<IQuadTemplate>newArrayList());
        }
        return new MutableQuadSidedMap(param);
    }

    private MutableQuadSidedMap(EnumMap<EnumFacing, List<IQuadTemplate>> quads){
        this.quads = quads;
    }

    private EnumMap<EnumFacing, List<IQuadTemplate>> quads;

    @Override
    public void setQuadsForSide(EnumFacing side, @Nonnull List<IQuadTemplate> newQuads) {
        quads.put(side, newQuads);
    }

    @Override
    public void addQuadsForSide(EnumFacing side, List<IQuadTemplate> toAdd) {
        for (IQuadTemplate template : toAdd){
            addQuadForSide(side, template);
        }
    }

    @Override
    public void addQuadForSide(EnumFacing side, IQuadTemplate toAdd) {
        getForSide(side).add(toAdd);
    }

    @Nonnull
    @Override
    public List<IQuadTemplate> getForSide(EnumFacing side) {
        List<IQuadTemplate> ret = quads.get(side);
        if (ret == null){
            ret = Lists.newArrayList();
            quads.put(side, ret);
        }
        return ret;
    }

}
