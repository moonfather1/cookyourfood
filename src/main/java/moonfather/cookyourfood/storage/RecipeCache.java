package moonfather.cookyourfood.storage;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class RecipeCache
{
    private static final List<CampfireCookingRecipe> recipeList = new LinkedList<>();
    public static void clearAndRecreate(MinecraftServer minecraftServer)
    {
        recipeList.clear();
        Collection<RecipeHolder<?>> c1 = minecraftServer.getRecipeManager().getRecipes();
        for (RecipeHolder<?> holder : c1)
        {
            if (holder.value() instanceof CampfireCookingRecipe recipe)
            {
                recipeList.add(recipe);
            }
        }
    }

    public static Optional<ItemStack> getCookingOutput(ItemStack stack, Level world)
    {
        SingleRecipeInput input = new SingleRecipeInput(stack);
        for (CampfireCookingRecipe recipe : recipeList)
        {
            if (recipe.matches(input, world))
            {
                return Optional.of(recipe.assemble(input));
            }
        }
        return Optional.empty();
    }
}
