package thaumicperiphery.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import thaumicperiphery.ThaumicPeriphery;

public class PacketHandler {
	
	public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ThaumicPeriphery.MODID);

	private int id = 0;
	
	public static void registerMessages() {
		//INSTANCE.registerMessage(MessageSyncExtendedSlotContents.Handler.class, MessageSyncExtendedSlotContents.class, id++, Side.CLIENT);
	}
	
}
