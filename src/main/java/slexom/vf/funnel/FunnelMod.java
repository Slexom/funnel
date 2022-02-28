package slexom.vf.funnel;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import net.minecraft.util.registry.Registry;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slexom.vf.funnel.block.FunnelBlock;
import slexom.vf.funnel.block.entity.FunnelBlockEntity;
import slexom.vf.funnel.screen.FunnelScreenHandler;

public class FunnelMod implements ModInitializer {

	public static final String MOD_ID = "funnel";
	public static final Logger LOGGER = LoggerFactory.getLogger("Funnel");

	public final Identifier REGISTRY_NAME = new Identifier(FunnelMod.MOD_ID, "funnel");
	public static Block FUNNEL_BLOCK;
	public static BlockEntityType<FunnelBlockEntity> FUNNEL_BLOCK_ENTITY;
	public static BlockItem FUNNEL_BLOCK_ITEM;
	public static ScreenHandlerType<FunnelScreenHandler> FUNNEL_SCREEN_HANDLER;

	@Override
	public void onInitialize() {
		FUNNEL_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(REGISTRY_NAME, FunnelScreenHandler::new);
		FUNNEL_BLOCK = Registry.register(Registry.BLOCK, REGISTRY_NAME, new FunnelBlock(AbstractBlock.Settings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(0.2F).nonOpaque()));
		FUNNEL_BLOCK_ITEM = Registry.register(Registry.ITEM, REGISTRY_NAME, new BlockItem(FUNNEL_BLOCK, new FabricItemSettings().group(ItemGroup.MISC)));
		FUNNEL_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, REGISTRY_NAME, BlockEntityType.Builder.create(FunnelBlockEntity::new, FUNNEL_BLOCK).build(null));
		LOGGER.info("[Funnel] Load Complete! Enjoy :D");
	}

}
