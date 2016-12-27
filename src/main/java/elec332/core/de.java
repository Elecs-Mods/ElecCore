package elec332.core;

/**
 * Created by Elec332 on 21-12-2016.
 */
public interface de {

    public boolean jo(int i, int e);

    public int i(boolean jo, Object o, int i);

    public void v1(boolean a, int b, Object c);

    public static class DEI implements de {

        public DEI(Object v){
            this.o2 = ov3(instance);
        }

        private Object instance;
        private final Object o2;

        public int y;

        @Override
        public boolean jo(int i, int e) {
            return i == e;
        }

        @Override
        public int i(boolean jo, Object o, int i) {
            return i + (jo ? 2 : 0);
        }

        @Override
        public void v1(boolean a, int b, Object c) {
            ((de) instance).v1(a, b, c);
        }

        private void av1(boolean a, int b, Object c) {

        }

        private static Object ov3(Object o){
            return o;
        }

    }

    public abstract class beep implements de {

    }

}
