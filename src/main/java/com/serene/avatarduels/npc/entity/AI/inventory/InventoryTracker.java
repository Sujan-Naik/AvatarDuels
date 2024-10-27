package com.serene.avatarduels.npc.entity.AI.inventory;

import com.serene.avatarduels.npc.entity.HumanEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class InventoryTracker {

    private final HumanEntity npc;
    private Inventory inventory;

    public InventoryTracker(Inventory inventory, HumanEntity humanEntity) {

        this.inventory = inventory;
        this.npc = humanEntity;
    }


    public void tick() {

        this.inventory = npc.getInventory();

        List<ItemStack> heldArmor = inventory.getContents().stream().filter(itemStack -> itemStack.getItem() instanceof ArmorItem && !inventory.getArmorContents().contains(itemStack)).toList();
        for (ItemStack item : heldArmor) {
            ArmorItem armorItem = (ArmorItem) item.getItem();
            EquipmentSlot equipmentSlot = armorItem.getEquipmentSlot();
            boolean shouldSwitch = false;
            if (inventory.getArmor(equipmentSlot.getIndex()).getItem() instanceof ArmorItem currentArmor) {
                if (armorItem.getToughness() > currentArmor.getToughness()) {
                    shouldSwitch = true;
                }
            } else {
                shouldSwitch = true;
            }
            if (shouldSwitch) {
                npc.setItemSlot(equipmentSlot, item.copy());
                npc.getInventory().removeItem(item);

//                for (Player player : Bukkit.getOnlinePlayers()) {
//                    NPCUtils.updateEquipment(npc, player);
//                }
            }
        }
    }

    private List<ItemStack> getOfType(Predicate<ItemStack> condition) {
        return inventory.getContents().stream().filter(condition).toList();
    }

}

