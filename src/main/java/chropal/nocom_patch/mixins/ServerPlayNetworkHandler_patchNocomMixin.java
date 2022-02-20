package chropal.nocom_patch.mixins;

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandler_patchNocomMixin {
	@Shadow
	public ServerPlayerEntity player;
	private int distantBlockInteractions;
	private boolean hasLoggedWarning;
	private static final Logger NOCOM_LOGGER = LoggerFactory.getLogger("nocom_patch");

	@Inject(method = "onPlayerInteractBlock", at = @At("HEAD"), cancellable = true)
	private void patch(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
		if (player.getBlockPos().getManhattanDistance(packet.getBlockHitResult().getBlockPos()) > 32) {
			distantBlockInteractions++;
			if (distantBlockInteractions > 400 && !hasLoggedWarning) {
				NOCOM_LOGGER.warn(player.getName().getString() + " might be trying to crash the server using the nocom exploit.");
				hasLoggedWarning = true;
			}
			ci.cancel();
		}
	}
}
