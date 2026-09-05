package com.herocraft.herotab;

import com.herocraft.herotab.config.ConfigManager;
import com.herocraft.herotab.config.HeroTabConfig;
import com.herocraft.herotab.integration.FactionInfo;
import com.herocraft.herotab.integration.FactionSync;
import com.herocraft.herotab.integration.GradeInfo;
import com.herocraft.herotab.integration.GradeSync;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.player.TabList;
import com.velocitypowered.api.proxy.player.TabListEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Construit et pousse le header/footer + les noms affichés dans le tab pour
 * chaque joueur connecté au proxy, quel que soit le sous-serveur/monde sur
 * lequel il se trouve. Le tab est donc unifié sur tout le réseau HeroCraft.
 *
 * Les grades (GradePlugin) et factions (FactionPlugin) sont lus directement
 * depuis MySQL via GradeSync / FactionSync — voir com.herocraft.herotab.integration.
 */
public class TabListManager {

    private final HeroTabPlugin plugin;
    private final ProxyServer server;
    private final ConfigManager configManager;
    private final Logger logger;

    private volatile GradeSync gradeSync;
    private volatile FactionSync factionSync;

    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public TabListManager(HeroTabPlugin plugin, ProxyServer server, ConfigManager configManager, Logger logger,
                           GradeSync gradeSync, FactionSync factionSync) {
        this.plugin = plugin;
        this.server = server;
        this.configManager = configManager;
        this.logger = logger;
        this.gradeSync = gradeSync;
        this.factionSync = factionSync;
    }

    /** Permet à /herotab reload de brancher de nouvelles instances (nouvelle config MySQL) sans recréer le manager. */
    public void setIntegrations(GradeSync gradeSync, FactionSync factionSync) {
        this.gradeSync = gradeSync;
        this.factionSync = factionSync;
    }

    public void reloadAnimationState() {
        // Rien à réinitialiser : l'index d'animation est dérivé de l'horloge système.
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        updateAll();
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        // Léger délai pour laisser Velocity retirer le joueur de sa liste interne avant de rafraîchir.
        server.getScheduler().buildTask(plugin, this::updateAll)
                .delay(java.time.Duration.ofMillis(150))
                .schedule();
    }

    public void updateAll() {
        HeroTabConfig cfg = configManager.getConfig();
        List<Player> online = new ArrayList<>(server.getAllPlayers());
        if (online.isEmpty()) return;

        // Nombre de joueurs par sous-serveur, calculé une seule fois par cycle
        // pour alimenter %server_online% sans reparcourir la liste pour chaque viewer.
        Map<String, Integer> countsByServer = new HashMap<>();
        for (Player p : online) {
            String name = p.getCurrentServer().map(sc -> sc.getServerInfo().getName()).orElse("?");
            countsByServer.merge(name, 1, Integer::sum);
        }

        for (Player viewer : online) {
            updateHeaderFooter(viewer, cfg, online.size(), countsByServer);
            updateEntries(viewer, cfg, online);
        }
    }

    private void updateHeaderFooter(Player viewer, HeroTabConfig cfg, int totalOnline, Map<String, Integer> countsByServer) {
        Component header = joinLines(cfg.header, viewer, cfg, totalOnline, countsByServer);
        Component footer = joinLines(cfg.footer, viewer, cfg, totalOnline, countsByServer);
        viewer.sendPlayerListHeaderAndFooter(header, footer);
    }

    private Component joinLines(List<String> lines, Player viewer, HeroTabConfig cfg, int totalOnline, Map<String, Integer> countsByServer) {
        Component result = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            String frame = pickFrame(lines.get(i), cfg.animationIntervalTicks);
            String replaced = applyPlaceholders(frame, viewer, cfg, totalOnline, countsByServer);
            result = result.append(parse(replaced, cfg));
            if (i < lines.size() - 1) {
                result = result.append(Component.newline());
            }
        }
        return result;
    }

    /** Une ligne peut contenir plusieurs frames séparées par '||' pour être animée. */
    private String pickFrame(String line, long animationIntervalTicks) {
        if (!line.contains("||")) return line;
        String[] frames = line.split("\\|\\|");
        if (frames.length <= 1) return line;
        long millisPerFrame = Math.max(50L, animationIntervalTicks * 50L);
        int index = (int) ((System.currentTimeMillis() / millisPerFrame) % frames.length);
        return frames[index].trim();
    }

    private void updateEntries(Player viewer, HeroTabConfig cfg, List<Player> online) {
        TabList tabList = viewer.getTabList();

        List<Player> sorted = new ArrayList<>(online);
        applySort(sorted, cfg.sortMode);

        for (Player target : sorted) {
            TabListEntry entry = tabList.getEntry(target.getUniqueId()).orElse(null);
            if (entry == null) continue; // Velocity ajoute/retire déjà les entrées de base à la connexion/déconnexion.

            String serverName = target.getCurrentServer().map(sc -> sc.getServerInfo().getName()).orElse("?");
            String group = cfg.serverGroups.getOrDefault(serverName, serverName);
            long ping = target.getPing();

            GradeInfo grade = gradeSync != null ? gradeSync.get(target.getUniqueId()) : null;
            FactionInfo faction = factionSync != null ? factionSync.get(target.getUniqueId()) : null;

            String formatted = cfg.playerFormat
                    .replace("%player%", target.getUsername())
                    .replace("%server%", serverName)
                    .replace("%group%", group)
                    .replace("%ping%", String.valueOf(Math.max(0, ping)))
                    .replace("%grade%", grade != null && grade.displayName() != null ? grade.displayName() : "")
                    .replace("%grade_prefix%", grade != null && grade.prefix() != null ? grade.prefix() : "")
                    .replace("%grade_suffix%", grade != null && grade.suffix() != null ? grade.suffix() : "")
                    .replace("%grade_color%", grade != null && grade.color() != null ? grade.color() : "&f")
                    .replace("%faction%", faction != null ? faction.factionName() : "")
                    .replace("%faction_rank%", faction != null && faction.rankName() != null ? faction.rankName() : "")
                    .replace("%faction_tag%", buildFactionTag(faction));

            entry.setDisplayName(parse(formatted, cfg));
        }
    }

    /** Construit un petit tag lisible du type " &7[&e★ Or - MaFaction]" — vide si le joueur n'a pas de faction. */
    private String buildFactionTag(FactionInfo faction) {
        if (faction == null || faction.factionName() == null || faction.factionName().isBlank()) {
            return "";
        }
        String color = faction.rankColor() != null && !faction.rankColor().isBlank() ? faction.rankColor() : "&7";
        String icon = faction.rankIcon() != null ? faction.rankIcon() + " " : "";
        return " &7[" + color + icon + faction.factionName() + "&7]";
    }

    private void applySort(List<Player> players, String sortMode) {
        Comparator<Player> comparator = switch (sortMode.toUpperCase(Locale.ROOT)) {
            case "ALPHABETICAL" -> Comparator.comparing(Player::getUsername, String.CASE_INSENSITIVE_ORDER);
            case "PING" -> Comparator.comparingLong(Player::getPing);
            case "SERVER" -> Comparator.comparing(
                    p -> p.getCurrentServer().map(sc -> sc.getServerInfo().getName()).orElse("~"),
                    String.CASE_INSENSITIVE_ORDER
            );
            default -> null; // NONE : on garde l'ordre d'itération de Velocity
        };
        if (comparator != null) {
            players.sort(comparator);
        }
    }

    private String applyPlaceholders(String text, Player viewer, HeroTabConfig cfg, int totalOnline, Map<String, Integer> countsByServer) {
        String serverName = viewer.getCurrentServer().map(sc -> sc.getServerInfo().getName()).orElse("?");
        String group = cfg.serverGroups.getOrDefault(serverName, serverName);
        int serverOnline = countsByServer.getOrDefault(serverName, 0);

        GradeInfo grade = gradeSync != null ? gradeSync.get(viewer.getUniqueId()) : null;
        FactionInfo faction = factionSync != null ? factionSync.get(viewer.getUniqueId()) : null;

        return text
                .replace("%player%", viewer.getUsername())
                .replace("%server%", serverName)
                .replace("%group%", group)
                .replace("%ping%", String.valueOf(Math.max(0, viewer.getPing())))
                // %online% = total sur TOUT le réseau (tous les sous-serveurs confondus)
                .replace("%online%", String.valueOf(totalOnline))
                .replace("%max%", String.valueOf(server.getConfiguration().getShowMaxPlayers()))
                // %server_online% = uniquement les joueurs sur le sous-serveur actuel du viewer
                .replace("%server_online%", String.valueOf(serverOnline))
                .replace("%grade%", grade != null && grade.displayName() != null ? grade.displayName() : "")
                .replace("%grade_prefix%", grade != null && grade.prefix() != null ? grade.prefix() : "")
                .replace("%faction%", faction != null ? faction.factionName() : "")
                .replace("%faction_rank%", faction != null && faction.rankName() != null ? faction.rankName() : "");
    }

    private Component parse(String text, HeroTabConfig cfg) {
        if (cfg.allowMiniMessage && text.indexOf('<') >= 0) {
            try {
                return miniMessage.deserialize(text);
            } catch (Exception e) {
                // Retombe sur les codes & si le MiniMessage est invalide.
            }
        }
        return legacy.deserialize(text);
    }
}
