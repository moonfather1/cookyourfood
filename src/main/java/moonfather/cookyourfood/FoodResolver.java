package moonfather.cookyourfood;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class FoodResolver
{
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

	public static RawFoodRank Resolve(ItemStack stack, Level world, LivingEntity player)
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
		rank[0] = RawFoodRank.NotACookableFood;
		if (world instanceof ServerLevel serverLevel)
		{
			SingleRecipeInput input = new SingleRecipeInput(stack);
			serverLevel.recipeAccess().getRecipeFor(RecipeType.CAMPFIRE_COOKING, input, world).ifPresent(
					r ->
					{
						ItemStack result = r.value().assemble(input, world.registryAccess());
						if (! result.isEmpty() && result.has(DataComponents.FOOD))
						{
							rank[0] = RawFoodRank.Normal;
						}
					}
			);
		}
		else
		{
			if (world.recipeAccess().propertySet(RecipePropertySet.CAMPFIRE_INPUT).test(stack))   // dumber client version. inaccurate for things that cook into non-food
			{
				rank[0] = RawFoodRank.Normal;
			}
		}
		foodMap.put(stack.getItem(), rank[0]);
		return rank[0];
	}
}
