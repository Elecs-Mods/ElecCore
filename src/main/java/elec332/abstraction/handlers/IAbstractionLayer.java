package elec332.abstraction.handlers;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-1-2017.
 */
public interface IAbstractionLayer {

    @Nonnull
    public IAbstractedClassProvider getClassProvider();

    @Nonnull
    public Class<? extends IWorldAbstraction> getWorldAbstraction();

    @Nonnull
    public Class<? extends IInventoryAbstraction> getInventoryAbstraction();

    @Nonnull
    public Class<? extends IEntityAbstraction> getEntityAbstraction();

    @Nonnull
    public Class<? extends IGeneralAbstraction> getGeneralAbstraction();

}
