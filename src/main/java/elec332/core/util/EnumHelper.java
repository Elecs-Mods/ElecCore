package elec332.core.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 14-1-2016.
 */
public class EnumHelper {

    private static final Map<Class<? extends Enum>, IEnumHandler<?>> registry;
    private static final List<IEnumHandler> internalHandlers;

    public static <E extends Enum> void registerHandler(IEnumHandler<E> handler){
        registry.put(handler.getHandlerClass(), handler);
    }

    @SuppressWarnings("unchecked") /* -_- */
    public static <E extends Enum> IEnumHandler<E> getHandlerFor(E e){
        return (IEnumHandler<E>) getHandlerFor(e.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum> IEnumHandler<E> getHandlerFor(Class<E> c){
        return (IEnumHandler<E>) registry.get(c);
    }

    private static <E extends Enum> boolean hasHandler(Class<E> c){
        return registry.keySet().contains(c);
    }

    private static <E extends Enum> void checkIsMC(Class<E> c){
        if (c.getName().startsWith("net.minecraft"))
            throw new IllegalStateException();
    }

    public static <E extends Enum> String getName(E e){
        if (hasHandler(e.getClass())){
            return getHandlerFor(e).getName(e);
        }
        checkIsMC(e.getClass());
        return e.toString();
    }

    public static <E extends Enum> int getOrdinal(E e){
        if (hasHandler(e.getClass())){
            return getHandlerFor(e).getOrdinal(e);
        }
        checkIsMC(e.getClass());
        return e.ordinal();
    }

    @SuppressWarnings("all")
    public static <E extends Enum> E fromString(String s, Class<E> c){
        if (hasHandler(c)){
            return getHandlerFor(c).fromString(s);
        }
        checkIsMC(c);
        return (E) /* <-- Compiler cries if I do not do that... */ Enum.valueOf(c, s);
    }

    public static <E extends Enum> E fromOrdinal(int ordinal, Class<E> c){
        if (hasHandler(c)){
            return getHandlerFor(c).fromOrdinal(ordinal);
        }
        checkIsMC(c);
        return c.getEnumConstants()[ordinal];
    }

    public interface IEnumHandler <E extends Enum> {

        public Class<E> getHandlerClass();

        public String getName(E e);

        public E fromString(String s);

        public int getOrdinal(E e);

        public E fromOrdinal(int i);

    }

    static {
        registry = Maps.newHashMap();
        internalHandlers = Lists.newArrayList();

        internalHandlers.add(new IEnumHandler<EnumFacing>() {

            @Override
            public Class<EnumFacing> getHandlerClass() {
                return EnumFacing.class;
            }

            @Override
            public String getName(EnumFacing facing) {
                switch (facing){
                    case DOWN:
                        return "DOWN";
                    case UP:
                        return "UP";
                    case NORTH:
                        return "NORTH";
                    case SOUTH:
                        return "SOUTH";
                    case WEST:
                        return "WEST";
                    case EAST:
                        return "EAST";
                    default:
                        throw new IllegalArgumentException();
                }
            }

            @Override
            public EnumFacing fromString(String s) {
                if (s.equals("DOWN")){
                    return EnumFacing.DOWN;
                } else if (s.equals("UP")){
                    return EnumFacing.UP;
                } else if (s.equals("NORTH")){
                    return EnumFacing.NORTH;
                } else if (s.equals("SOUTH")){
                    return EnumFacing.SOUTH;
                } else if (s.equals("WEST")){
                    return EnumFacing.WEST;
                } else if (s.equals("EAST")){
                    return EnumFacing.EAST;
                }
                throw new IllegalArgumentException();
            }

            @Override
            public int getOrdinal(EnumFacing facing) {
                switch (facing){
                    case DOWN:
                        return 0;
                    case UP:
                        return 1;
                    case NORTH:
                        return 2;
                    case SOUTH:
                        return 3;
                    case WEST:
                        return 4;
                    case EAST:
                        return 5;
                    default:
                        throw new IllegalArgumentException();
                }
            }

            @Override
            public EnumFacing fromOrdinal(int i) {
                switch (i){
                    case 0:
                        return EnumFacing.DOWN;
                    case 1:
                        return EnumFacing.UP;
                    case 2:
                        return EnumFacing.NORTH;
                    case 3:
                        return EnumFacing.SOUTH;
                    case 4:
                        return EnumFacing.WEST;
                    case 5:
                        return EnumFacing.EAST;
                    default:
                        throw new IllegalArgumentException();
                }
            }

        });

        for (IEnumHandler handler : internalHandlers){
            registerHandler(handler);
        }
    }

}
