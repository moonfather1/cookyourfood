package moonfather.cookyourfood;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.MODID)
public class ModCookYourFood
{
    public ModCookYourFood()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OptionsHolder.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, OptionsHolder.CLIENT_SPEC);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
    }


    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // example for pre-2023 versions. use tags in new versions.
        //InterModComms.sendTo("cookyourfood", "ok-to-eat-raw", () -> { return "minecraft:apple"; });
        //InterModComms.sendTo("cookyourfood", "ok-to-eat-raw", () -> { return "minecraft:carrot"; });
        // there two are here so that if someone adds a grilled carrot, we don't start treating normal carrot as raw food.
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // this will go away in 1.20.2 and forward
        event.getIMCStream().forEach(
                m -> {
                    if (m.getMethod() != null)
                    {
                        LogUtils.getLogger().warn("Warning: InterModComms message to mod CookYourFood received from " + m.getSenderModId() + ". IMC will be removed in 1.20.2 in favor of tags.");
                        Object value = m.getMessageSupplier().get();
                        ResourceLocation itemCode = new ResourceLocation(value != null ? value.toString() : "x:x");
                        if (ForgeRegistries.ITEMS.containsKey(itemCode))
                        {
                            if (m.getMethod().equals("ok-to-eat-raw"))
                            {
                                FoodResolver.AddOkToEatRaw(ForgeRegistries.ITEMS.getValue(itemCode));
                            }
                            else if (m.getMethod().equals("raw-food-light"))
                            {
                                FoodResolver.AddCustomRawFoodLight(ForgeRegistries.ITEMS.getValue(itemCode));
                            }
                            else if (m.getMethod().equals("raw-food-normal"))
                            {
                                FoodResolver.AddCustomRawFoodNormal(ForgeRegistries.ITEMS.getValue(itemCode));
                            }
                            else if (m.getMethod().equals("raw-food-severe"))
                            {
                                FoodResolver.AddCustomRawFoodSevere(ForgeRegistries.ITEMS.getValue(itemCode));
                            }
                            else
                            {
                                LogUtils.getLogger().error("Error: InterModComms message to mod CookYourFood was invalid. Sender: " + m.getSenderModId() + ". Valid method names are raw-food-light, raw-food-normal, raw-food-severe, ok-to-eat-raw.");
                            }
                        }
                    }
                });
    }
}
