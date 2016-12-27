package elec332.core.inventory.window;

/**
 * Created by Elec332 on 2-12-2016.
 */
public interface ISimpleWindowFactory extends IWindowFactory, IWindowModifier {

    @Override
    default public Window createWindow(Object... args){
        return new Window(getXSize(), getYSize(), this);
    }

    default public int getXSize(){
        return -1;
    }

    default public int getYSize(){
        return -1;
    }

}
