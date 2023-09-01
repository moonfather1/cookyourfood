package moonfather.cookyourfood.mixin;

import moonfather.cookyourfood.EventHandlers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class FoodMixin
{
	@Inject(method = "finishUsing", at = @At("RETURN"))
	public void onFood(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {

		EventHandlers.onItemUse(world, user, ((Item)(Object) this));
	}
}
