package moonfather.cookyourfood;

import moonfather.cookyourfood.storage.RecipeCache;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FoodResolver
{
	public enum RawFoodRank { NotMapped, NotACookableFood, OkayToEat, Light, Normal, Severe };
	private static final Map<Item, RawFoodRank> foodMap = new HashMap<Item, RawFoodRank>();

	//////////////////////////////////////////////////////////////////////////////////////////

	public static RawFoodRank Resolve(ItemStack stack, Level world)
	{
		RawFoodRank rank = foodMap.getOrDefault(stack.getItem(), RawFoodRank.NotMapped);
		if (! rank.equals(RawFoodRank.NotMapped))
		{
			return rank;
		}
		///...///
		if (stack.is(Constants.Tags.OK_TO_EAT_RAW))
		{
			foodMap.put(stack.getItem(), RawFoodRank.OkayToEat);
			return RawFoodRank.OkayToEat;
		}
		if (stack.is(Constants.Tags.RAW_FOOD_SEVERE))
		{
			foodMap.put(stack.getItem(), RawFoodRank.Severe);
			return RawFoodRank.Severe;
		}
		if (stack.is(Constants.Tags.RAW_FOOD_LIGHT))
		{
			foodMap.put(stack.getItem(), RawFoodRank.Light);
			return RawFoodRank.Light;
		}
		if (stack.is(Constants.Tags.RAW_FOOD_NORMAL))
		{
			foodMap.put(stack.getItem(), RawFoodRank.Normal);
			return RawFoodRank.Normal;
		}
		///...///
		rank = RawFoodRank.NotACookableFood;
		Optional<ItemStack> output = RecipeCache.getCookingOutput(stack, world); // returns empty on client
		if (output.isPresent())
		{
			if (! output.get().isEmpty() && output.get().get(DataComponents.FOOD) != null)
			{
				rank = RawFoodRank.Normal;
			}
		}
		foodMap.put(stack.getItem(), rank);
		return rank;
	}
}
