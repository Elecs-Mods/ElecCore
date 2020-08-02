package elec332.core.grid.multiblock;

import com.google.common.collect.Sets;
import net.minecraft.tileentity.TileEntity;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Elec332 on 1-8-2020
 */
public abstract class AbstractDynamicMultiblock<T extends TileEntity, M extends AbstractDynamicMultiblock<T, M, L>, L extends AbstractDynamicMultiblockTileLink<T, M, L>> {

    public AbstractDynamicMultiblock() {
        this.objects = Sets.newHashSet();
        this.objects_ = Collections.unmodifiableSet(this.objects);
    }

    private final Set<L> objects, objects_;

    protected Set<L> getComponents() {
        return this.objects_;
    }

    protected void tick() {
    }

    protected void onComponentRemoved(L link) {
        onGridChanged();
    }

    protected final void addComponent(L link) {
        this.objects.add(link);
        onComponentAdded(link, false);
        onGridChanged();
    }

    protected void onComponentAdded(L link, boolean mergeAdd) {
    }

    protected abstract boolean canMerge(M other);

    @SuppressWarnings("unchecked")
    protected final void mergeWith(M other) {
        for (L link : other.getComponents()) {
            link.setGrid((M) this);
            objects.add(link);
            onComponentAdded(link, true);
        }
        onMergedWith(other);
        onGridChanged();
    }

    protected void onMergedWith(M other) {
    }

    protected void invalidate() {
        onGridChanged();
    }

    protected abstract void onGridChanged();

}
