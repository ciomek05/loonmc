package ciomek.loon.mixin;

import ciomek.loon.mqtt.MQTTClient;
import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.player.inventory.PlayerPickUpItemPayload;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityItem.class, remap = false)
public class EntityItemMQTTMixin {
	@Shadow
	public ItemStack item;

	@Unique
	private int originalStackSize;

	@Inject(method = "playerTouch", at = @At("HEAD"))
	public void onPlayerTouchHead(Player player, CallbackInfo ci) {
		originalStackSize = item != null ? item.stackSize : 0;
	}

	@Inject(method = "playerTouch", at = @At("TAIL"))
	public void onPlayerTouch(Player player, CallbackInfo ci) {
		if (item == null || item.stackSize >= originalStackSize) {
			return;
		}

		PlayerPickUpItemPayload payload = new PlayerPickUpItemPayload(
			new PlayerIdentity(player.uuid, player.username),
			ItemStackData.from(item, originalStackSize - item.stackSize)
		);

		payload.send();
	}
}
