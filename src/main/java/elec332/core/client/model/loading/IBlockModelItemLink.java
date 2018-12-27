package elec332.core.client.model.loading;

/**
 * Created by Elec332 on 22-3-2017.
 */
public interface IBlockModelItemLink {

    default public boolean itemInheritsModel() {
        return true;
    }

}
