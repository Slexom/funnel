package slexom.vf.funnel.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import slexom.vf.funnel.FunnelMod;
import slexom.vf.funnel.block.FunnelBlock;
import slexom.vf.funnel.inventory.BlockEntityInventory;
import slexom.vf.funnel.screen.FunnelScreenHandler;

public class FunnelBlockEntity extends LootableContainerBlockEntity implements NamedScreenHandlerFactory, BlockEntityInventory {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    private int cooldown;

    public FunnelBlockEntity(BlockPos pos, BlockState state) {
        super(FunnelMod.FUNNEL_BLOCK_ENTITY, pos, state);
        this.cooldown = 6;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (!this.deserializeLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory);
        }
        this.cooldown = nbt.getInt("Cooldown");
    }

    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory);
        }
        nbt.putInt("Cooldown", this.cooldown);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new FunnelScreenHandler(syncId, playerInventory, this);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    private boolean isEnabled() {
        return this.world.getBlockState(this.pos).get(FunnelBlock.ENABLED);
    }

    private boolean haveSpace() {
        BlockPos down = this.pos.down();
        BlockState blockState = this.world.getBlockState(down);
        Block block = blockState.getBlock();
        return block.getDefaultState().isAir() || blockState.getCollisionShape(world, down).isEmpty();

    }

    private boolean haveItems() {
        return !this.inventory.get(0).isEmpty();
    }

    private boolean canDropItem() {
        return this.haveItems() && this.isEnabled() && this.haveSpace();
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, FunnelBlockEntity blockEntity) {
        if (world != null && !world.isClient()) {
            --blockEntity.cooldown;
            if (blockEntity.cooldown == 0) {
                blockEntity.cooldown = 8;
                if (blockEntity.canDropItem()) {
                    ItemStack itemStack = blockEntity.removeStack(0, 1);
                    ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, itemStack.copy());
                    entity.setVelocity(0, 0, 0);
                    world.spawnEntity(entity);
                }
            }
        }
    }

}
