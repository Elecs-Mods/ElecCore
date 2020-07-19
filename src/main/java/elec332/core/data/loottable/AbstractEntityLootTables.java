package elec332.core.data.loottable;

import net.minecraft.data.loot.EntityLootTables;

/**
 * Created by Elec332 on 16-7-2020
 */
public abstract class AbstractEntityLootTables extends EntityLootTables {

    @Override
    protected final void addTables() {
        registerEntityTables();
    }

    protected abstract void registerEntityTables();

}
