package net.quelfth.gigatubes.block.entities;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.quelfth.gigatubes.block.GigaBlockEntities;
import net.quelfth.gigatubes.block.blocks.TubeBlock;
import net.quelfth.gigatubes.block.tube_parts.FilterModule;
import net.quelfth.gigatubes.block.tube_parts.TubeModule;
import net.quelfth.gigatubes.item.GigaItems;
import net.quelfth.gigatubes.util.BlockFace;
import net.quelfth.gigatubes.util.Comparison;
import net.quelfth.gigatubes.util.Couple;
import net.quelfth.gigatubes.util.Wrapper;
import net.quelfth.gigatubes.util.routing.PathData;
import net.quelfth.gigatubes.util.routing.PathDatum;
import net.quelfth.gigatubes.util.NullUtil;


public class TubeBlockEntity extends BlockEntity {

    protected boolean[] intakes;
    protected boolean[] outputs;
    protected TubeModule[] modules;

    public TubeBlockEntity(BlockPos pos, BlockState state) {
        super(GigaBlockEntities.TUBE.get(), pos, state);
        intakes = new boolean[Direction.values().length];
        outputs = new boolean[Direction.values().length];
        modules = new TubeModule[Direction.values().length];
    }

    public boolean getIO(Direction dir, boolean in) {
        return (in ? intakes : outputs)[dir.get3DDataValue()];
    }

    @Nullable
    public TubeModule getModule(Direction dir) {
        return modules[dir.get3DDataValue()];
    }

    public boolean doesIntake(BlockState state, Direction dir) {
        return getIO(dir, true) && state.getValue(TubeBlock.tubeProp(dir));
    }

    public boolean doesOutput(BlockState state, Direction dir) {
        return getIO(dir, false) && state.getValue(TubeBlock.tubeProp(dir));
    }



    

    public void setIO(Direction dir, boolean in, boolean value) {
        (in ? intakes : outputs)[dir.get3DDataValue()] = value;
        setChanged();
    }

    public void setModule(Direction dir, @Nullable TubeModule module) {
        modules[dir.get3DDataValue()] = module;
        setChanged();
    }

    public boolean tryAddIO(Direction dir, boolean in) {
        boolean can = !getIO(dir, in);
        if (can)
            setIO(dir, in, true);
        return can;
    }

    public boolean tryAddModule(Direction dir, TubeModule module) {
        boolean can = !anyModule(dir);
        if (can)
            setModule(dir, module);
        return can;
    }

    public boolean anyIO(Direction dir) {
        return intakes[dir.get3DDataValue()] || outputs[dir.get3DDataValue()];
    }

    public boolean anyModule(Direction dir) {
        return modules[dir.get3DDataValue()] != null;
    }

    public boolean onlyIntake(Direction dir) {
        return intakes[dir.get3DDataValue()] && !outputs[dir.get3DDataValue()];
    }

    public boolean onlyOutput(Direction dir) {
        return !intakes[dir.get3DDataValue()] && outputs[dir.get3DDataValue()];
    }

    public boolean fullIO(Direction dir) {
        return intakes[dir.get3DDataValue()] && outputs[dir.get3DDataValue()];
    }

    public int numIntakes() {
        int num = 0;
        for (boolean intake : intakes) 
            if (intake)
                num++;
        return num;
    }

    public int numOutputs() {
        int num = 0;
        for (boolean output : outputs) 
            if (output)
                num++;
        return num;
    }

    public boolean shouldExist() {
        for (Direction dir : Direction.values()) 
            if (anyIO(dir) || anyModule(dir))
                return true;
        
        return false;
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.intakes = dirDataFromListTag(tag.getList("Intakes", Tag.TAG_BYTE));
        this.outputs = dirDataFromListTag(tag.getList("Outputs", Tag.TAG_BYTE));
        this.modules = moduleDataFromListTag(tag.getList("Modules", Tag.TAG_COMPOUND));
        
    }

    private static boolean[] dirDataFromListTag(ListTag tag) {
        boolean[] result = new boolean[Direction.values().length];
        if (tag.size() >= result.length) 
            for (int i = 0; i < result.length; i++) {
                ByteTag b = (ByteTag) tag.get(i);
                result[i] = b.getAsByte() != 0;
            }
        return result;
    }

    private static TubeModule[] moduleDataFromListTag(ListTag tag) {
        TubeModule[] result = new TubeModule[Direction.values().length];
        if (tag.size() >= result.length)
            for (int i = 0; i < result.length; i++) {
                CompoundTag m = (CompoundTag) tag.get(i);
                result[i] = TubeModule.deserialize(m);
            }
        return result;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        
        super.saveAdditional(tag);

        tag.put("Intakes", dirDataToListTag(intakes));
        tag.put("Outputs", dirDataToListTag(outputs));
        tag.put("Modules", moduleDataToListTag(modules));
    }

    private static ListTag dirDataToListTag(boolean[] dirData) {
        ListTag tag = new ListTag();
        for (boolean dir : dirData)
            tag.add(ByteTag.valueOf(dir));
        return tag;
    }

    private static ListTag moduleDataToListTag(TubeModule[] modules) {
        ListTag tag = new ListTag();
        for (TubeModule module : modules)
            tag.add(module == null ? new CompoundTag() : module.serialize());
        return tag;
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }


    public void drops() {

        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(GigaItems.TUBE_INTAKE.get(), numIntakes()));
        items.add(new ItemStack(GigaItems.TUBE_OUTPUT.get(), numOutputs()));
        for (Direction dir : Direction.values()) {
            final TubeModule module = getModule(dir);
            if (module != null)
                items.add(module.asItem());
        }

        final @Nonnull SimpleContainer container = new SimpleContainer(items.size());
        for (int i = 0; i < items.size(); i++) 
            container.setItem(i, items.get(i));

        if(level != null)
            Containers.dropContents(level, worldPosition, container);
    }


    public void tick(final Level level, final BlockPos pos, final BlockState state) {

        for (Direction dir : Direction.values()) {
            if (!doesIntake(state, dir)) continue; 

            transferOutOf(new BlockFace(pos, dir));
        }
    }


    /**
     * {@code position} must be the position of a tube block (entity not necessary).
    */
    private static NonNullList<Destination> getAllDestinations(final Level level, final BlockPos position, final Direction intakeDir) {
        final @Nonnull NonNullList<Destination> destinations = NonNullList.create();
        final @Nonnull ArrayDeque<Couple<BlockPos, PathDatum>> pending = new ArrayDeque<>();
        final @Nonnull Map<BlockPos, PathData> searched = new HashMap<>();

        @Nonnull PathDatum initialDatum = PathDatum.ofSpeed(getTransferSpeed(level, position));
        if (level.getBlockEntity(position) instanceof TubeBlockEntity entity) {
            TubeModule module = entity.getModule(intakeDir);
            if (module != null)
                if (module instanceof FilterModule filter)
                    initialDatum = initialDatum.then(PathDatum.ofFilter(filter.getPredicate()));
        }

            

        pending.add(new Couple<>(position, initialDatum));

        while (!pending.isEmpty()) {
            final @Nonnull Couple<BlockPos, PathDatum> current = pending.removeFirst();
            final BlockPos pos = current.$0;
            final PathDatum datum = current.$1;
            boolean replace = true;
            if (searched.containsKey(pos)) {
                Comparison comparison = searched.get(pos).compare(datum);
                if (comparison == null)
                    replace = false;
                else if (comparison != Comparison.LESS)
                    continue;
            }

            final TubeBlockEntity entity = TubeBlock.getTubeBlockEntity(level, pos);

            for (final @Nonnull Direction dir : Direction.values()) {

                final BlockPos nextPos = pos.relative(dir);
                PathDatum nextDatum = PathDatum.ofSpeed(getTransferSpeed(level, nextPos));
                if (entity != null && entity.getModule(dir) instanceof FilterModule filter)
                    nextDatum = nextDatum.then(PathDatum.ofFilter(filter.getPredicate()));
                final TubeBlockEntity nextEntity = TubeBlock.getTubeBlockEntity(level, nextPos);
                if (nextEntity != null && nextEntity.getModule(dir.getOpposite()) instanceof FilterModule filter)
                    nextDatum = nextDatum.then(PathDatum.ofFilter(filter.getPredicate()));


                if (!TubeBlock.interconnecting(level, pos, dir))
                    continue;
                
                pending.addLast(new Couple<>(nextPos, datum.then(nextDatum)));
            }

            if (!replace)
                searched.get(pos).add(datum);
            else
                searched.put(pos, new PathData(datum));
        }

        for (final @Nonnull BlockPos pos : searched.keySet())
            for (final @Nonnull Direction dir : allOutputDirs(level, pos))
                destinations.add(new Destination(pos, dir, searched.get(pos)));

        return destinations;
    }

    private static int getTransferSpeed(final Level level, final BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof final TubeBlock tube)
            return tube.transferSpeed();
        return 0;
    }

    public static NonNullList<Direction> allOutputDirs(final Level level, final BlockPos pos) {
        final @Nonnull NonNullList<Direction> dirs = NonNullList.create();

        final @Nonnull BlockState block = level.getBlockState(pos);
        if (!(
            block.getBlock() instanceof TubeBlock && 
            level.getBlockEntity(pos) instanceof final TubeBlockEntity entity
        )) 
            return dirs;

        for (final @Nonnull Direction dir : Direction.values()) 
            if (entity.doesOutput(block, dir))
                dirs.add(dir);

        return dirs;
    }

    private void transferFromTo(final IItemHandler source, final IItemHandler dest, final Wrapper<Integer> count, final PathDatum path, final boolean backflowCheck) {
        for (int i = 0; i < source.getSlots(); i++) {
            if (count.value >= path.speed)
                return;
            
            @Nonnull ItemStack items = source.extractItem(i, path.speed - count.value, true);
            //System.out.println("" + items + " ~ " + path.filter.toString() + " ~ " + path.filter.allows(items));
            if (!path.filter.allows(items))
                continue;
            if (backflowCheck && source.isItemValid(i, items))
                continue;

            if (items.isEmpty()) continue;
            for (int j = 0; j < dest.getSlots(); j++) {
                
                final @NotNull ItemStack leftover = dest.insertItem(j, items, true);
                final int transfer = items.getCount() - leftover.getCount();
                if (transfer < 1) continue;
                
                dest.insertItem(j, source.extractItem(i, transfer, false), false);
                count.value += transfer;
                items = leftover;
                if(items.isEmpty())
                    break;
            }
        }
    }

    

    

    private void transferOutOf(final BlockFace sourceFace) {
        if (this.level == null) return;
        final @Nonnull Level level = NullUtil.forgiveNull(this.level);

        final IItemHandler source = getItemHandlerOnFace(level, sourceFace);

        if (source == null)
            return;

        for (final @Nonnull Destination dest : getAllDestinations(level, worldPosition, sourceFace.dir.getOpposite())) {
            final IItemHandler destination = getItemHandlerOnFace(level, dest.face);

            if (destination != null) {
                final Wrapper<Integer> itemCount = new Wrapper<>(0);
                for (PathDatum datum : dest.data.data)
                    transferFromTo(source, destination, itemCount, datum, sourceFace.equals(dest.face));
            }
        }
    
    }




    @Nullable
    public static IItemHandler getItemHandlerOnFace(final Level level, final BlockFace face) {
        return getItemHandlerInDir(level, face.pos, face.dir);
    }

    @Nullable
    public static IItemHandler getItemHandlerInDir(final Level level, final BlockPos pos, final Direction dir) {
        return getItemHandlerAt(level, pos.relative(dir), dir.getOpposite());
    }

    @Nullable
    public static IItemHandler getItemHandlerAt(final Level level, final BlockPos pos, final Direction face) {
        final BlockEntity entity = level.getBlockEntity(pos);
        if (entity == null)
            return null;
        
        final @Nonnull LazyOptional<IItemHandler> capability = entity.getCapability(ForgeCapabilities.ITEM_HANDLER, face);
        if (!capability.isPresent())
            return null;

        return capability.resolve().get();
    }

    private static final class Destination {
        public final BlockFace face;
        public final PathData data;

        public Destination(final BlockPos pos, final Direction dir, final PathData data) {
            this(new BlockFace(pos, dir), data);
        }

        public Destination(final BlockFace face, final PathData data) {
            this.face = face;
            this.data = data;
        }
    }


}