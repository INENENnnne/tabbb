package com.herocraft.herotab.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HeroTabConfig {

    /** Nombre de ticks (20 = 1s) entre deux rafraîchissements du tab. */
    public long updateIntervalTicks = 20;

    /** Format d'un slot pour un joueur donné. Placeholders : %player% %server% %ping% %group% */
    public String playerFormat = "&7[&b%server%&7] &f%player%";

    /** Lignes d'en-tête, une par ligne. Chaque ligne peut avoir plusieurs frames séparées par '||' pour l'animation. */
    public List<String> header = new ArrayList<>(List.of(
            "&b&lHeroCraft &7- &fLe réseau",
            "&7En ligne : &b%online%&7/&b%max%"
    ));

    /** Lignes de pied de page. */
    public List<String> footer = new ArrayList<>(List.of(
            "&7www.herocraft.example",
            "&7Serveur actuel : &b%server%"
    ));

    /** Vitesse d'animation du header/footer si plusieurs frames sont fournies avec '||'. */
    public long animationIntervalTicks = 20;

    /** Tri des joueurs dans la liste : ALPHABETICAL, PING, SERVER, NONE. */
    public String sortMode = "SERVER";

    /**
     * Regroupement optionnel de sous-serveurs sous un même nom affiché.
     * ex: bedwars1, bedwars2 -> "BedWars"
     */
    public Map<String, String> serverGroups = new LinkedHashMap<>();

    /** Si vrai, MiniMessage (&lt;red&gt;, &lt;bold&gt;...) est accepté en plus des codes &. */
    public boolean allowMiniMessage = true;

    public static HeroTabConfig defaults() {
        return new HeroTabConfig();
    }

    @SuppressWarnings("unchecked")
    public static HeroTabConfig fromMap(Map<String, Object> raw) {
        HeroTabConfig c = new HeroTabConfig();

        if (raw.get("update-interval-ticks") instanceof Number n) c.updateIntervalTicks = n.longValue();
        if (raw.get("player-format") instanceof String s) c.playerFormat = s;
        if (raw.get("header") instanceof List<?> l) c.header = toStringList(l);
        if (raw.get("footer") instanceof List<?> l) c.footer = toStringList(l);
        if (raw.get("animation-interval-ticks") instanceof Number n) c.animationIntervalTicks = n.longValue();
        if (raw.get("sort-mode") instanceof String s) c.sortMode = s.toUpperCase();
        if (raw.get("allow-minimessage") instanceof Boolean b) c.allowMiniMessage = b;

        Object groups = raw.get("server-groups");
        if (groups instanceof Map<?, ?> gm) {
            Map<String, String> parsed = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : gm.entrySet()) {
                parsed.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }
            c.serverGroups = parsed;
        }

        return c;
    }

    private static List<String> toStringList(List<?> l) {
        List<String> out = new ArrayList<>();
        for (Object o : l) out.add(String.valueOf(o));
        return out;
    }
}
