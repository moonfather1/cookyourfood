package moonfather.cookyourfood.mixin;

import moonfather.cookyourfood.EventHandlers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class FoodMixin
{
	@Inject(method = "finishUsingItem", at = @At("RETURN"))
	public void onFood(ItemStack stack, Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir)
	{
		EventHandlers.onItemUse(world, user, stack.isEmpty() ? ((Item) (Object) this).getDefaultInstance() : stack); // yes it can be empty, sometimes, don't know why; meanwhile, item itself is raw cod or whatever and can probably give a valid itemstack.
	}
}