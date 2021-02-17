package slexom.vf.funnel.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import slexom.vf.funnel.FunnelMod;
import slexom.vf.funnel.block.FunnelBlock;
import slexom.vf.funnel.inventory.BlockEntityInventory;
import slexom.vf.funnel.screen.FunnelScreenHandler;

public class FunnelBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, BlockEntityInventory, Tickable {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int cooldown;

    public FunnelBlockEntity(BlockEntityType<?> type) {
        super(type);
        this.cooldown = 6;
    }

    public FunnelBlockEntity() {
        this(FunnelMod.FUNNEL_BLOCK_ENTITY);
    }

    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        Inventories.fromTag(tag, this.inventory);
        this.cooldown = tag.getInt("Cooldown");
    }

    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.inventory);
        tag.putInt("Cooldown", this.cooldown);
        return tag;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FunnelScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    private boolean canDropItem() {
        if (world != null && world.getBlockState(this.pos).get(FunnelBlock.ENABLED)) {
            BlockPos down = this.pos.down();
            BlockState state = world.getBlockState(down);
            Block block = state.getBlock();
            return block.getDefaultState().isAir() || state.getCollisionShape(world, down).isEmpty() && !this.inventory.get(0).isEmpty();
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isClient) {
            --this.cooldown;
            if (this.cooldown == 0) {
                this.cooldown = 8;
                if (this.canDropItem()) {
                    ItemStack itemStack = removeStack(0, 1);
                    ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, itemStack.copy());
                    entity.setVelocity(0, 0, 0);
                    world.spawnEntity(entity);
                }
            }
        }
    }

}
