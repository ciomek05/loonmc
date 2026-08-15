package ciomek.loon.mixin;

import ciomek.loon.mqtt.payload.data.ItemStackData;
import ciomek.loon.mqtt.payload.data.PlayerIdentity;
import ciomek.loon.mqtt.payload.player.PlayerDiedPayload;
import ciomek.loon.mqtt.payload.player.PlayerHurtPayload;
import ciomek.loon.mqtt.payload.player.inventory.PlayerDropItemPayload;
import ciomek.loon.mqtt.payload.player.inventory.PlayerInventoryPayload;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(value = Player.class, remap = false)
public class PlayerMQTTMixin {
	@Unique
	DamageType lastDamageType = null;

	@Inject(method = "hurt", at = @At(value = "HEAD"))
	public void onHurtStart(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> cir) {
		lastDamageType = type;
	}

	@Inject(method = "hurt", at = @At(value = "TAIL"))
	public void onHurt(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> cir) {
		Player player = (Player) (Object) this;

		PlayerHurtPayload payload = new PlayerHurtPayload(new PlayerIdentity(player.uuid, player.username), damageTypeName(type));

		payload.send();
	}

	@Inject(method = "onDeath", at = @At(value = "TAIL"))
	public void onDeath(Entity entityKilledBy, CallbackInfo ci) {
		Player player = (Player) (Object) this;

		String killerName;
		if (entityKilledBy instanceof Player killer) {
			killerName = killer.username;
		} else if (entityKilledBy != null) {
			killerName = Entity.getNameFromEntity(entityKilledBy, true);
		} else {
			killerName = null;
		}

		String damageType = lastDamageType == null ? null : damageTypeName(lastDamageType);
		PlayerDiedPayload payload = new PlayerDiedPayload(new PlayerIdentity(player.uuid, player.username), killerName, damageType);

		payload.send();
	}

	@Inject(method = "dropItem", at = @At("TAIL"))
	public void onDropItem(ItemStack stack, boolean randomDirection, CallbackInfo ci) {
		if (stack == null) {
			return;
		}

		Player player = (Player) (Object) this;
		PlayerDropItemPayload payload = new PlayerDropItemPayload(
			new PlayerIdentity(player.uuid, player.username),
			ItemStackData.from(stack)
		);

		payload.send();
	}

	@Inject(method = "setItemInArmorSlot", at = @At("TAIL"))
	private void onArmorChanged(CallbackInfo ci) {
		Player player = (Player) (Object) this;

		new PlayerInventoryPayload(
			new PlayerIdentity(player.uuid, player.username),
			Arrays.stream(player.inventory.mainInventory).map(ItemStackData::from).toList(),
			Arrays.stream(player.inventory.armorInventory).map(ItemStackData::from).toList()
		).send();
	}

	@Unique
	private static String damageTypeName(DamageType type) {
		String key = type.getLanguageKey();
		return key.substring(key.lastIndexOf('.') + 1);
	}
}
