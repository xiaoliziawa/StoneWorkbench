package com.example.stoneworkbenchblock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("stoneworkbench")
public class StoneWorkbenchMod {
    public static final String MOD_ID = "stoneworkbench";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Block> STONE_WORKBENCH = BLOCKS.register("stone_workbench", StoneWorkbenchBlock::new);
    public static final RegistryObject<Item> STONE_WORKBENCH_ITEM = ITEMS.register("stone_workbench", () -> new BlockItem(STONE_WORKBENCH.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

    public StoneWorkbenchMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }

    public static class StoneWorkbenchBlock extends Block {
        public StoneWorkbenchBlock() {
            super(Properties.of(Material.STONE)
                    .strength(1.5F, 6.0F)
                    .sound(SoundType.STONE));
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                player.openMenu(new StoneWorkbenchMenuProvider(level, pos));
                return InteractionResult.CONSUME;
            }
        }

        private static class StoneWorkbenchMenuProvider implements MenuProvider {
            private final Level level;
            private final BlockPos pos;

            public StoneWorkbenchMenuProvider(Level level, BlockPos pos) {
                this.level = level;
                this.pos = pos;
            }

            @Override
            public Component getDisplayName() {
                return Component.literal("Stone Workbench");
            }

            @Override
            public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int windowId, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
                return new CraftingMenu(windowId, playerInventory, ContainerLevelAccess.create(level, pos)) {
                    @Override
                    public boolean stillValid(Player player) {
                        return level.getBlockState(pos).getBlock() instanceof StoneWorkbenchBlock &&
                                player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
                    }
                };
            }
        }
    }
}
