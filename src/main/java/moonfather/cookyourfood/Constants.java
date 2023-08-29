package moonfather.cookyourfood;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class Constants
{ 
    public static final String MODID = "cookyourfood";

    public static class Tags
    {
        public static final TagKey<Item> RAW_FOOD_LIGHT = TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MODID, "raw_food_light"));
        public static final TagKey<Item> RAW_FOOD_NORMAL = TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MODID, "raw_food_normal"));
        public static final TagKey<Item> RAW_FOOD_SEVERE = TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MODID, "raw_food_severe"));
        public static final TagKey<Item> OK_TO_EAT_RAW = TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MODID, "ok_to_eat_raw"));
    }
} 
