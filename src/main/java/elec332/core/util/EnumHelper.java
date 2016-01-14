package elec332.core.util;

/**
 * Created by Elec332 on 14-1-2016.
 */
public class EnumHelper {

    //TODO

    public interface IEnumHandler <E extends Enum> {

        public Class<E> getHandlerClass();

        public String getName();

        public E fromString();

        public int getOrdinal();

        public E fromOrdinal();

    }

}
