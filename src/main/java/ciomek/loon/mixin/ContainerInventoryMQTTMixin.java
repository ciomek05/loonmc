package ciomek.loon.mixin;

import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.player.inventory.PlayerInventoryPayload;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(value = ContainerInventory.class, remap = false)
public class ContainerInventoryMQTTMixin {
	@Shadow
	public Player player;
	@Shadow
	public ItemStack[] mainInventory;

	@Shadow
	public @Nullable ItemStack @NotNull [] armorInventory;

	@Unique
	private void sendFullInventory() {
		if (player == null) {
			return;
		}

		new PlayerInventoryPayload(
			new PlayerIdentity(player.uuid, player.username),
			Arrays.stream(mainInventory).map(ItemStackData::from).toList(),
			Arrays.stream(armorInventory).map(ItemStackData::from).toList()
		).send();
	}

	@Inject(method = {
		"setItem",
		"insertItem",
		"sort",
		"dropAllItems",
		"transferAllContents",
		"load"
	}, at = @At("RETURN"))
	private void onInventoryChanged(CallbackInfo ci) {
		sendFullInventory();
	}

	@Inject(method = {
		"removeItem",
		"consumeInventoryItem"
	}, at = @At("RETURN"))
	private void onInventoryChangedReturn(CallbackInfoReturnable<?> cir) {
		sendFullInventory();
	}
}
