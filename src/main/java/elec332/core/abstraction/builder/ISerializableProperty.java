package elec332.core.abstraction.builder;

import net.minecraft.block.properties.IProperty;

/**
 * Created by Elec332 on 22-3-2018.
 */
public interface ISerializableProperty<V extends Comparable<V>> extends IProperty<V> {

    public int getMeta(V value);

    public V getValueFromMeta(int meta);

    default public int getPossibleMetaStates(){
        return getAllowedValues().size();
    }

}
