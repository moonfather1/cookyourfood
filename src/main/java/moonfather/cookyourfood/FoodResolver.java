package moonfather.cookyourfood;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class FoodResolver
{
	private static final SimpleContainer inventoryForCheckingRecipes = new SimpleContainer(ItemStack.EMPTY);
	public enum RawFoodRank { NotMapped, NotACookableFood, OkayToEat, Light, Normal, Severe };
	private static final Map<Item, RawFoodRank> foodMap = new HashMap<Item, RawFoodRank>();



	public static void AddCustomRawFoodLight(Item item)
	{
		FoodResolver.foodMap.put(item, RawFoodRank.Light);
	}

	public static void AddCustomRawFoodNormal(Item item)
	{
		FoodResolver.foodMap.put(item, RawFoodRank.Normal);
	}

	public static void AddCustomRawFoodSevere(Item item)
	{
		FoodResolver.foodMap.put(item, RawFoodRank.Severe);
	}

	public static void AddOkToEatRaw(Item item)
	{
		FoodResolver.foodMap.put(item, RawFoodRank.OkayToEat);
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	public static RawFoodRank Resolve(ItemStack stack, Level world)
	{
		final FoodResolver.RawFoodRank[] rank = new RawFoodRank[1]; // because it's used in stupid lambda of stupid Optional
		rank[0] = foodMap.getOrDefault(stack.getItem(), RawFoodRank.NotMapped);
		if (! rank[0].equals(RawFoodRank.NotMapped))
		{
			return rank[0];
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
		inventoryForCheckingRecipes.setItem(0, stack);
		rank[0] = RawFoodRank.NotACookableFood;
		world.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, inventoryForCheckingRecipes, world).ifPresent(
				r ->
				{
					if  (! r.getResultItem(world.registryAccess()).isEmpty() && r.getResultItem(world.registryAccess()).getItem().getFoodProperties() != null)
					{
						rank[0] = RawFoodRank.Normal;
					}
				}
		);
		foodMap.put(stack.getItem(), rank[0]);
		return rank[0];
	}
}
