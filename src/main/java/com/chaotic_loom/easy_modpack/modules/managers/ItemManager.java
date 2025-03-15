package com.chaotic_loom.easy_modpack.modules.managers;

import com.chaotic_loom.easy_modpack.modules.ConfigManager;
import com.chaotic_loom.easy_modpack.modules.Utils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager extends BaseManager {
    private static List<ResourceLocation> disabledUsageItems = new ArrayList<>();

    public void register() {
        reload("items");
    }

    @Override
    public void reload(String configCategory) {
        super.reload(configCategory);

        List<String> disabledUsageRaw = ConfigManager.getConfigArray(configCategory, "disabled_usage");
        for (String rawID : disabledUsageRaw) {
            disabledUsageItems.add(new ResourceLocation(rawID));
        }
    }

    public boolean isDisabled(Item result) {
        for (ResourceLocation item : this.disabledObjects) {
            if (BuiltInRegistries.ITEM.getKey(result).equals(item)) {
                return true;
            }
        }

        return false;
    }

    public boolean isDisabledUsage(Item result) {
        for (ResourceLocation item : disabledUsageItems) {
            if (BuiltInRegistries.ITEM.getKey(result).equals(item)) {
                return true;
            }
        }

        return false;
    }

    public void removeDisabledItems(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);

            if (this.isDisabled(itemStack.getItem())) {
                container.removeItem(i, itemStack.getCount());
            }
        }
    }

    public void replaceItems(Container container) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack itemStack = container.getItem(i);
            ResourceLocation itemID = Utils.getItemLocation(itemStack.getItem());

            if (this.hasReplacement(itemID)) {
                Item item = Utils.getItem(getReplacement(itemID));

                ItemStack newItemStack = new ItemStack(item);
                Utils.copyItemStackProperties(itemStack, newItemStack);

                container.setItem(i, newItemStack);
            }
        }
    }
}
