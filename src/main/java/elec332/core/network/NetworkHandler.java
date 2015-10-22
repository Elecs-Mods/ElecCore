package elec332.core.network;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import elec332.core.main.ElecCore;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by Elec332 on 23-2-2015.
 */
public class NetworkHandler {

    public NetworkHandler(String channelName){
        this(channelName, 0);
    }

    public NetworkHandler(String channelName, int start){
        if (start < 0)
            throw new IllegalArgumentException();
        this.channelName = channelName.toLowerCase();
        this.networkWrapper = new SimpleNetworkWrapper(this.channelName);
        this.i = start;
    }

    public void setMessageIndex(int newIndex){
        if (newIndex >= i) {
            this.i = newIndex;
            return;
        }
        throw new IllegalArgumentException();
    }

    private String channelName;
    private int i;

    private SimpleNetworkWrapper networkWrapper;

    public void registerServerPacket(Class<? extends AbstractPacket> packetClass){
        register(packetClass, Side.SERVER);
    }

    public void registerClientPacket(Class<? extends AbstractPacket> packetClass){
        register(packetClass, Side.CLIENT);
    }

    public void registerServerPacket(AbstractPacket packet){
        register(packet, Side.SERVER);
    }

    public void registerClientPacket(AbstractPacket packet){
        register(packet, Side.CLIENT);
    }

    public <M extends IMessage, R extends IMessage> void registerPacket(Class<? extends IMessageHandler<M, R>> messageHandler, Class<M> messageType, Side side){
        networkWrapper.registerMessage(messageHandler, messageType, i, side);
        ++i;
    }

    public void registerPacket(Class packetClass, Side side){
        if (checkValidity(packetClass)){
            register(packetClass, side);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private boolean checkValidity(Class packet){
        if (isValidPacket(packet)){
            if (ElecCore.Debug)
                ElecCore.instance.info("Validated packet: "+packet.getName());
            return true;
        }
        return false;
    }

    public SimpleNetworkWrapper getNetworkWrapper() {
        return networkWrapper;
    }

    public String getChannelName() {
        return channelName;
    }

    @SuppressWarnings("unchecked")
    private void register(Class packetClass, Side side){
        networkWrapper.registerMessage(packetClass, packetClass, i, side);
        ++i;
    }

    private void register(AbstractPacket packet, Side side){
        networkWrapper.registerMessage(packet, packet.getClass(), i, side);
        ++i;
    }

    public static boolean isValidPacket(Class clazz){
        boolean b1 = clazz.getName().contains("Packet") && !Modifier.isAbstract(clazz.getModifiers());
        boolean b2 = false;
        boolean b3 = false;
        List<Class> interfaces = Lists.newArrayList(clazz.getInterfaces());
        Class toCheck = clazz.getSuperclass();
        while (toCheck != null){
            interfaces.addAll(Lists.newArrayList(toCheck.getInterfaces()));
            toCheck = toCheck.getSuperclass();
        }
        for (Class interfaceClass : interfaces){
            if (interfaceClass.equals(IMessage.class))
                b2 = true;
            if (interfaceClass.equals(IMessageHandler.class))
                b3 = true;
        }
        return b1 && b2 && b3;
    }
}
