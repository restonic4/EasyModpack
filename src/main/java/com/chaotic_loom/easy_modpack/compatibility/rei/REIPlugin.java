package com.chaotic_loom.easy_modpack.compatibility.rei;

import com.chaotic_loom.easy_modpack.EasyModpack;
import com.chaotic_loom.easy_modpack.modules.managers.ItemManager;
import me.shedaniel.rei.api.client.entry.filtering.base.BasicFilteringRule;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;

import java.util.stream.Stream;

/*
Hides disabled items on REI
 */
public class REIPlugin implements REIClientPlugin {
    @Override
    public void registerBasicEntryFiltering(BasicFilteringRule<?> rule) {
        Stream<EntryStack<?>> stacks = EntryRegistry.getInstance().getEntryStacks();

        stacks.forEach(entryStack -> {
            if (EasyModpack.ITEM_MANAGER.isDisabled(entryStack.getIdentifier()) | EasyModpack.ITEM_MANAGER.hasReplacement(entryStack.getIdentifier())) {
                rule.hide(entryStack);
            }
        });
    }
}
