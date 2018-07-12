package elec332.core.abstraction.builder;

import net.minecraft.block.properties.IProperty;

/**
 * Created by Elec332 on 22-3-2018.
 */
public interface IPropertySerializer<V extends Comparable<V>> {

    public <P extends IProperty<V>> int getMeta(V value, P property);

    public <P extends IProperty<V>> V getValueFromMeta(int meta, P property);

    default public <P extends IProperty<V>> int getPossibleMetaStates(P property){
        return property.getAllowedValues().size();
    }

}
