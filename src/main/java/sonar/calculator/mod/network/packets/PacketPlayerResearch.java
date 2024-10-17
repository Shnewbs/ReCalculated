package sonar.calculator.mod.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.calculator.mod.research.ClientResearch;
import sonar.calculator.mod.research.IResearch;
import sonar.calculator.mod.research.PlayerResearchRegistry;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;

import java.util.ArrayList;

public class PacketPlayerResearch implements IMessage {

	public EntityPlayer player;
    public ArrayList<IResearch> research = new ArrayList<>();

    public PacketPlayerResearch() {
    }

	public PacketPlayerResearch(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		research = PlayerResearchRegistry.readPlayerData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		PlayerResearchRegistry.writePlayerData(player, tag, SyncType.SAVE);
		ByteBufUtils.writeTag(buf, tag);
	}

	public static class Handler implements IMessageHandler<PacketPlayerResearch, IMessage> {

        @Override
		public IMessage onMessage(PacketPlayerResearch message, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			if (player != null && ctx.side == Side.CLIENT) {
				ClientResearch.research = ClientResearch.setResearch(message.research);
			}
			return null;
		}
	}
}
