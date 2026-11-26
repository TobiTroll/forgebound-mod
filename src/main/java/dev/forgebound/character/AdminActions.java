package dev.forgebound.character;

import dev.forgebound.balance.BalanceConfig;
import dev.forgebound.classes.ClassManager;
import dev.forgebound.classes.ForgeClass;
import dev.forgebound.forging.ForgeProgressState;
import dev.forgebound.network.ModPayloads.AdminActionPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

/**
 * Aktionen des Operator-Menues (/forge). Der Server prueft die Berechtigung (Stufe 2)
 * bei jeder einzelnen Aktion; ein manipulierter Client bewirkt nichts.
 */
public final class AdminActions {
	private AdminActions() {
	}

	public static final int SET_CLASS = 0;
	public static final int FORGE_LEVEL_DELTA = 1;
	public static final int FORGE_LEVEL_SET = 2;
	public static final int CHAR_LEVEL_DELTA = 3;
	public static final int STAT_DELTA = 4;
	public static final int POINTS_DELTA = 5;
	public static final int RESET_STATS = 6;
	public static final int FILL_MANA = 7;
	public static final int RESET_ALL = 8;
	public static final int CHAR_XP_ADD = 9;

	public static void handle(ServerPlayerEntity player, AdminActionPayload payload) {
		if (!player.hasPermissionLevel(2)) {
			player.sendMessage(Text.translatable("message.forgebound.admin_denied"), true);
			return;
		}
		MinecraftServer server = player.getServer();
		if (server == null) {
			return;
		}
		UUID id = player.getUuid();
		CharacterState.Data data = CharacterManager.data(player);
		ForgeProgressState forge = ForgeProgressState.get(server);

		switch (payload.action()) {
			case SET_CLASS -> ClassManager.set(player, ForgeClass.fromProperty(payload.a()));
			case FORGE_LEVEL_DELTA -> forge.setLevel(id, forge.level(id) + payload.a());
			case FORGE_LEVEL_SET -> forge.setLevel(id, payload.a());
			case CHAR_LEVEL_DELTA -> CharacterManager.setLevel(player, data.level + payload.a());
			case STAT_DELTA -> CharacterManager.adminStat(player, payload.a(), payload.b());
			case POINTS_DELTA -> data.points = Math.max(0, data.points + payload.a());
			case RESET_STATS -> CharacterManager.resetStats(player);
			case FILL_MANA -> data.curMana = CharacterManager.maxMana(data);
			case CHAR_XP_ADD -> CharacterManager.addXp(player, payload.a());
			case RESET_ALL -> {
				ClassManager.set(player, null);
				forge.setLevel(id, 1);
				CharacterManager.setLevel(player, 1);
				CharacterManager.resetStats(player);
				data.points = 0;
				data.curMana = BalanceConfig.BASE_MANA;
			}
			default -> {
				return;
			}
		}
		CharacterManager.markDirty(player);
		CharacterManager.apply(player);
		CharacterManager.sync(player);
	}
}
