package ciomek.loon.mqtt.payload.data;

import net.minecraft.core.item.ItemStack;

public record ItemStackData(
	String itemName,
	int itemID,
	int itemCount
	) {
	public static ItemStackData from(ItemStack stack) {
		if (stack == null) {
			return null;
		}

		return from(stack, stack.stackSize);
	}

	public static ItemStackData from(ItemStack stack, int itemCount) {
		if (stack == null) {
			return null;
		}

		return new ItemStackData(stack.getDisplayName(), stack.itemID, itemCount);
	}
}
