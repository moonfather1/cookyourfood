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
	private static boolean staticInitializationDone = false;



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

	private static void CheckStaticInitialization()
	{
		// because stupid java does not support static constructors
		if (!staticInitializationDone)
		{
			// Raw Salmon:  dmg==1,   Raw Cod:  dmg==0,  Pufferfish:  dmg==3,  Clownfish:  dmg==2
			AddCustomRawFoodNormal(Items.TROPICAL_FISH); // clownfish - not cookable but edible

			AddCustomRawFoodLight(Items.POTATO); // normal raw potato
			AddCustomRawFoodLight(Items.SALMON); // salmon

			AddCustomRawFoodNormal(Items.POISONOUS_POTATO);
			AddCustomRawFoodSevere(Items.ROTTEN_FLESH);

			staticInitializationDone = true;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	public static RawFoodRank Resolve(ItemStack stack, Level world)
	{
		FoodResolver.CheckStaticInitialization();
		final FoodResolver.RawFoodRank[] rank = new RawFoodRank[1]; // because it's used in stupid lambda of stupid Optional
		rank[0] = foodMap.getOrDefault(stack.getItem(), RawFoodRank.NotMapped);
		if (! rank[0].equals(RawFoodRank.NotMapped))
		{
			return rank[0];
		}
		inventoryForCheckingRecipes.setItem(0, stack);
			/*if (event.getEntity().level.getRecipeManager().getRecipeFor(IRecipeType.CAMPFIRE_COOKING, inventoryForCheckingRecipes, event.getEntity().level).isPresent())
			{
				foodMap.put(item, RawFoodRank.Normal);
				rank = RawFoodRank.Normal;
			}
			else
			{
				foodMap.put(item, RawFoodRank.NotACookableFood);
				return;
			}*/
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
