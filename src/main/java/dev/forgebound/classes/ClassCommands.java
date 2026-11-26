package dev.forgebound.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.forgebound.quality.ItemQuality;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * /forgeclass            zeigt deine Klasse
 * /forgeclass list       zeigt alle Klassen mit Seltenheit und Chance
 * /forgeclass set <spieler> <klasse>   (Operator) setzt eine Klasse zum Testen
 * /forgeclass reset <spieler>          (Operator) entfernt die Klasse
 */
public final class ClassCommands {
	private ClassCommands() {
	}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> build(dispatcher));
	}

	private static void build(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("forgeclass")
				.executes(context -> info(context.getSource()))
				.then(CommandManager.literal("list")
						.executes(context -> list(context.getSource())))
				.then(CommandManager.literal("reset")
						.requires(source -> source.hasPermissionLevel(2))
						.then(CommandManager.argument("player", EntityArgumentType.player())
								.executes(context -> reset(context.getSource(), EntityArgumentType.getPlayer(context, "player")))))
				.then(CommandManager.literal("set")
						.requires(source -> source.hasPermissionLevel(2))
						.then(CommandManager.argument("player", EntityArgumentType.player())
								.then(CommandManager.argument("class", StringArgumentType.word())
										.suggests((context, builder) -> CommandSource.suggestMatching(ForgeClass.ids(), builder))
										.executes(context -> set(context.getSource(),
												EntityArgumentType.getPlayer(context, "player"),
												StringArgumentType.getString(context, "class")))))));
	}

	private static int info(ServerCommandSource source) throws CommandSyntaxException {
		ServerPlayerEntity player = source.getPlayerOrThrow();
		ForgeClass cls = ClassManager.get(player);
		if (cls == null) {
			source.sendFeedback(() -> Text.translatable("message.forgebound.class_none"), false);
			return 0;
		}
		for (Text line : ClassInfo.describe(cls)) {
			source.sendFeedback(() -> line, false);
		}
		return 1;
	}

	private static int list(ServerCommandSource source) {
		source.sendFeedback(() -> Text.translatable("message.forgebound.class_list_header"), false);
		for (ItemQuality rarity : ItemQuality.values()) {
			for (ForgeClass cls : ForgeClass.ofRarity(rarity)) {
				Text line = Text.translatable("message.forgebound.class_list_entry",
						rarity.symbol(),
						Text.translatable(cls.translationKey()),
						Text.translatable(rarity.translationKey()),
						ClassInfo.fmt(Math.round(ClassManager.chancePercent(cls) * 10.0) / 10.0)).formatted(rarity.color());
				source.sendFeedback(() -> line, false);
			}
		}
		return ForgeClass.values().length;
	}

	private static int set(ServerCommandSource source, ServerPlayerEntity target, String id) {
		ForgeClass cls = ForgeClass.byId(id);
		if (cls == null) {
			source.sendError(Text.translatable("message.forgebound.class_unknown", id));
			return 0;
		}
		ClassManager.set(target, cls);
		source.sendFeedback(() -> Text.translatable("message.forgebound.class_set",
				target.getDisplayName(), Text.translatable(cls.translationKey())), true);
		return 1;
	}

	private static int reset(ServerCommandSource source, ServerPlayerEntity target) {
		ClassManager.set(target, null);
		source.sendFeedback(() -> Text.translatable("message.forgebound.class_reset", target.getDisplayName()), true);
		return 1;
	}
}
