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

    /** Connexion à la base "grades_db" de GradePlugin (table player_grades + grades). */
    public MySQLTarget gradesMysql = new MySQLTarget();

    /** Connexion à la base "herocraft" de FactionPlugin (table faction_tab_sync). */
    public MySQLTarget factionsMysql = new MySQLTarget();

    /**
     * Un bloc de connexion MySQL en lecture seule, utilisé pour récupérer les
     * grades et/ou factions déjà stockés par les plugins Paper correspondants.
     */
    public static class MySQLTarget {
        public boolean enabled = false;
        public String host = "127.0.0.1";
        public int port = 3306;
        public String database = "";
        public String user = "";
        public String password = "";
        /** Intervalle entre deux rechargements complets depuis MySQL, en secondes. */
        public int refreshIntervalSeconds = 15;

        public String jdbcUrl() {
            return "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
        }

        @SuppressWarnings("unchecked")
        static MySQLTarget fromMap(Object raw) {
            MySQLTarget t = new MySQLTarget();
            if (!(raw instanceof Map<?, ?> m)) return t;
            if (m.get("enabled") instanceof Boolean b) t.enabled = b;
            if (m.get("host") instanceof String s) t.host = s;
            if (m.get("port") instanceof Number n) t.port = n.intValue();
            if (m.get("database") instanceof String s) t.database = s;
            if (m.get("user") instanceof String s) t.user = s;
            if (m.get("password") instanceof String s) t.password = s;
            if (m.get("refresh-interval-seconds") instanceof Number n) t.refreshIntervalSeconds = n.intValue();
            return t;
        }
    }

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

        if (raw.get("grades-mysql") != null) c.gradesMysql = MySQLTarget.fromMap(raw.get("grades-mysql"));
        if (raw.get("factions-mysql") != null) c.factionsMysql = MySQLTarget.fromMap(raw.get("factions-mysql"));

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
