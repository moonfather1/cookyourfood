package moonfather.cookyourfood;

import com.mojang.logging.LogUtils;
import moonfather.cookyourfood.extra.EventForTooltips;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

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
        InterModComms.sendTo("cookyourfood", "ok-to-eat-raw", () -> { return "minecraft:apple"; });
        InterModComms.sendTo("cookyourfood", "ok-to-eat-raw", () -> { return "minecraft:carrot"; });
    }

    private void processIMC(final InterModProcessEvent event)
    {
        event.getIMCStream().forEach(
                m -> {
                    if (m.getMethod() != null)
                    {
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
                                System.out.println("Error: InterModComms message to mod CookYourFood was invalid. Sender: " + m.getSenderModId() + ". Valid method names are raw-food-light, raw-food-normal, raw-food-severe, ok-to-eat-raw.");
                            }
                        }
                    }
                });
    }
}
