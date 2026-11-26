package dev.forgebound.block;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.character.CharacterManager;
import dev.forgebound.forging.ForgingEffects;
import dev.forgebound.quality.ItemQuality;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * Haelt das fertig geschmiedete Stueck auf dem Amboss. Server startet die
 * Enthuellungs-Effekte, der Client zeigt das schwebende Stueck (Renderer).
 * Der Zeitpunkt des Starts wird mit dem Block synchronisiert, der Client
 * rechnet die Animation daraus selbst.
 */
public class ForgingStationBlockEntity extends BlockEntity {
	private ItemStack output = ItemStack.EMPTY;
	private long revealStart;
	private int qualityOrdinal;
	private UUID owner;

	public ForgingStationBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.FORGING_STATION, pos, state);
	}

	public boolean hasOutput() {
		return !output.isEmpty();
	}

	public ItemStack getOutput() {
		return output;
	}

	public long getRevealStart() {
		return revealStart;
	}

	/** Legt ein fertiges Stueck auf den Amboss, startet die Enthuellung und gibt dem Schmied Charakter-Erfahrung. */
	public void present(ItemStack stack, ItemQuality quality, UUID ownerId, long now) {
		this.output = stack;
		this.qualityOrdinal = quality.ordinal();
		this.owner = ownerId;
		this.revealStart = now;
		markDirty();
		sync();
		if (world instanceof ServerWorld serverWorld && ownerId != null) {
			ServerPlayerEntity forger = serverWorld.getServer().getPlayerManager().getPlayer(ownerId);
			if (forger != null) {
				int xp = (int) Math.round(BalanceConfig.RARITY_XP[quality.ordinal()] * BalanceConfig.FORGE_TO_CHAR_XP);
				CharacterManager.addXp(forger, xp);
			}
		}
	}

	/** Gibt das Stueck an den Spieler, sobald die Enthuellung vorbei ist. */
	public void tryCollect(PlayerEntity player) {
		if (world == null || output.isEmpty()) {
			return;
		}
		long age = world.getTime() - revealStart;
		if (age < BalanceConfig.REVEAL_ITEM_TICKS) {
			player.sendMessage(Text.translatable("message.forgebound.station_revealing"), true);
			return;
		}
		if (owner != null && !owner.equals(player.getUuid()) && age < BalanceConfig.OWNER_ONLY_TICKS) {
			player.sendMessage(Text.translatable("message.forgebound.not_yours"), true);
			return;
		}
		player.getInventory().offerOrDrop(output);
		output = ItemStack.EMPTY;
		owner = null;
		markDirty();
		sync();
		world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.8F, 1.0F);
	}

	/** Wird beim Abbau des Ambosses aufgerufen und gibt das Stueck frei. */
	public ItemStack takeOutputForDrop() {
		ItemStack stack = output;
		output = ItemStack.EMPTY;
		return stack;
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, ForgingStationBlockEntity entity) {
		if (entity.output.isEmpty() || !(world instanceof ServerWorld serverWorld)) {
			return;
		}
		long t = world.getTime() - entity.revealStart;
		if (t < 0) {
			return;
		}
		ForgingEffects.tick(serverWorld, pos, entity.qualityOrdinal, t);
	}

	private void sync() {
		if (world != null && !world.isClient) {
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (!output.isEmpty()) {
			nbt.put("Output", output.encode(registryLookup));
		}
		nbt.putLong("RevealStart", revealStart);
		nbt.putInt("Quality", qualityOrdinal);
		if (owner != null) {
			nbt.putUuid("Owner", owner);
		}
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		output = nbt.contains("Output")
				? ItemStack.fromNbt(registryLookup, nbt.get("Output")).orElse(ItemStack.EMPTY)
				: ItemStack.EMPTY;
		revealStart = nbt.getLong("RevealStart");
		qualityOrdinal = Math.max(0, Math.min(ItemQuality.values().length - 1, nbt.getInt("Quality")));
		owner = nbt.containsUuid("Owner") ? nbt.getUuid("Owner") : null;
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return createNbt(registryLookup);
	}

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
}
