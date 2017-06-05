package org.halvors.quantum.common.item;


import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import org.halvors.quantum.Quantum;
import org.halvors.quantum.common.Reference;

public class ItemBucketToxicWaste extends ItemBucket {
    public ItemBucketToxicWaste() {
        super(Quantum.blockToxicWaste);

        setUnlocalizedName("bucketToxicWaste");
        setTextureName(Reference.PREFIX + "bucketToxicWaste");
        setContainerItem(Items.bucket);
        setCreativeTab(Quantum.getCreativeTab());
    }
}