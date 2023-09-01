package moonfather.cookyourfood;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class FoodResolver
{
	private static final SimpleInventory inventoryForCheckingRecipes = new SimpleInventory(ItemStack.EMPTY);
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

	public static RawFoodRank Resolve(ItemStack stack, World world)
	{
		final FoodResolver.RawFoodRank[] rank = new RawFoodRank[1]; // because it's used in stupid lambda of stupid Optional
		rank[0] = foodMap.getOrDefault(stack.getItem(), RawFoodRank.NotMapped);
		if (! rank[0].equals(RawFoodRank.NotMapped))
		{
			return rank[0];
		}
		///...///
		if (stack.isIn(Constants.Tags.OK_TO_EAT_RAW))
		{
			foodMap.put(stack.getItem(), RawFoodRank.OkayToEat);
			return RawFoodRank.OkayToEat;
		}
		if (stack.isIn(Constants.Tags.RAW_FOOD_SEVERE))
		{
			foodMap.put(stack.getItem(), RawFoodRank.Severe);
			return RawFoodRank.Severe;
		}
		if (stack.isIn(Constants.Tags.RAW_FOOD_LIGHT))
		{
			foodMap.put(stack.getItem(), RawFoodRank.Light);
			return RawFoodRank.Light;
		}
		if (stack.isIn(Constants.Tags.RAW_FOOD_NORMAL))
		{
			foodMap.put(stack.getItem(), RawFoodRank.Normal);
			return RawFoodRank.Normal;
		}
		///...///
		inventoryForCheckingRecipes.setStack(0, stack);
		rank[0] = RawFoodRank.NotACookableFood;
		world.getRecipeManager().getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventoryForCheckingRecipes, world).ifPresent(
				r ->
				{
					if (! r.getOutput().isEmpty() && r.getOutput().getItem().getFoodComponent() != null)
					{
						rank[0] = RawFoodRank.Normal;
					}
				}
		);
		foodMap.put(stack.getItem(), rank[0]);
		return rank[0];
	}
}
