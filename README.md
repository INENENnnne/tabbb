# 🎮 HeroTab

> **Version actuelle : 1.4.0** — placeholders de grade/faction étendus au header & footer, sources republiées à jour. [Voir les nouveautés](#-nouveautés) · [Télécharger la release](https://github.com/INENENnnne/tabbb/releases/latest)

**Tab list unifié et personnalisable pour tout le réseau HeroCraft (proxy Velocity.)**

HeroTab est un plugin pour **Velocity 3.3+** qui remplace le tab (liste de joueurs, header et footer) de **tous** les joueurs connectés au proxy — quel que soit le sous-serveur ou le monde sur lequel ils se trouvent. **Une seule installation sur le proxy suffit** : plus rien à installer ni à configurer sur chaque serveur de jeu. Le résultat est une expérience cohérente et soignée sur tout le réseau..

Le plugin sait aussi afficher le **grade** (GradePlugin) et la **faction** (FactionPlugin) de chaque joueur, en lisant directement leurs bases MySQL — sans dépendre des plugins Paper eux-mêmes..

---

## ✨ Fonctionnalités

- 🌐 **Tab unifié sur tout le réseau** : le header, le footer et les noms affichés sont identiques pour tous les joueurs, même s'ils sont répartis sur plusieurs sous-serveurs..

- 🎨 **Header / footer personnalisables** : plusieurs lignes, codes couleur `&` classiques, hexadécimaux `&#RRGGBB`, et même des balises **MiniMessage** (`<gradient>`, `<bold>`…) si tu les autorises dans la config..

- 🔄 **Lignes animées** : sépare plusieurs frames d'une ligne avec `||` pour créer une animation en boucle (ex: `"&b&lHeroCraft||&3&lHeroCraft"`). La vitesse se règle dans `config.yml`.

- 🏷️ **Grades intégrés (GradePlugin)** : le nom, préfixe, suffixe et couleur du grade le plus prioritaire de chaque joueur, lus directement depuis la base `grades_db` (tables `player_grades` + `grades`)— aucune installation supplémentaire sur les serveurs de jeu..

- ⚔️ **Factions intégrées (FactionPlugin)** : le nom et le rang de faction de chaque joueur, avec un tag prêt à l'emploi, lus depuis la table `faction_tab_sync` dans la base `herocraft`..

- 🎨 **Thème de couleurs global** : deux couleurs de décoration (`theme-primary` / `theme-secondary`) réglables dans `config.yml` et réutilisables partout— header, footer, format joueur et séparateur de groupe—via les placeholders `%primary%` et `%secondary%`..

- 🏠 **Ton serveur d'abord** : mode de tri `SERVER_SELF_FIRST`(activé par défaut) qui affiche d'abord les joueurs de ton sous-serveur actuel, puis les autres regroupés par serveur, avec un **séparateur décoratif** entre les deux— un vrai réordonnancement du tab, pas juste un tri cosmétique..

- 📊 **Placeholders riches** : affiche dans ton tab le nombre de joueurs sur tout le réseau (`%online%`), sur ton sous-serveur actuel (`%server_online%`), ton serveur, ton groupe, ton ping, ton grade, ta faction, l'adresse du réseau et du site…

- 🗂️ **Regroupement de sous-serveurs** : affiche un joli nom commun pour tout un groupe de serveurs(ex: `bedwars1`, `bedwars2`, `bedwars3` → `BedWars` grâce à `server-groups`.

- 📋 **Tri des joueurs** : trie la liste des joueurs par ordre alphabétique, par ping, par serveur, **ton serveur d'abord + séparateur**, ou laisse l'ordre par défaut..

- ⚡ **Léger et efficace** : mis à jour périodiquement par un scheduler Velocity, avec des compteurs réseau calculés une seule fois par cycle; aucun impact sur les serveurs de jeu..

- 🔒 **Rechargement à chaud** : `/herotab reload`(permission `herotab.admin`) sans redémarrer le proxy.


## 📥 Installation

1. Télécharge le fichier **`.jar`** depuis la dernière [release](https://github.com/INENENnnne/tabbb/releases).
2. Dépose-le dans le dossier `plugins/` de ton proxy **Velocity 3.3+**..
3. Redémarre le proxy(ou utilise un plugin de rechargement). Le plugin génère alors son fichier `config.yml` dans `plugins/herotab/`.


## ⚙️ Configuration

Tous les réglages se font dans `plugins/herotab/config.yml`. Exemple de base :

```yaml
update-interval-ticks: 20          # intervalle de rafraîchissement(20 =≈ une seconde)
animation-interval-ticks: 20      # vitesse des lignes animées

theme-primary: "&b"             # couleur de décoration principale (%primary%)
theme-secondary: "&e"            # couleur de décoration secondaire (%secondary%)
network-address: "herocraft.servegame.com"   # IP/domaine affiché dans le footer
website-address: "herocraft.servegame.com"  # site web affiché dans le footer

player-format: "&7[%primary%%server%&7] %grade_prefix%&f%player%%faction_tag% &8•&7 %ping%ms"
sort-mode: "SERVER_SELF_FIRST"   # ALPHABETICAL, PING, SERVER, SERVER_SELF_FIRST, NONE
group-spacer-enabled: true
group-spacer-text: "%secondary%&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"
allow-minimessage: true

header:
  - "%primary%&m                                        "
  - ""
  - "%primary%&lHeroCraft %secondary%&l• %primary%Le réseau"
  - "&7Réseau : %primary%%online%&7/%primary%%max% &8| &7Ici : %secondary%%server_online%"
  - ""
  - "%primary%&m                                        "

footer:
  - "%secondary%&m                                        "
  - ""
  - "&7Serveur : %primary%%network_address%"
  - "&7Site : %primary%%website_address%"
  - ""
  - "%secondary%&m                                        "

# Intégrations MySQL(GradePlugin et FactionPlugin) — à activer si tu veux
# afficher les grades/factions dans le tab。
grades-mysql:
  enabled: false
  host: "127.0.0.1"
  port: 3306
  database: "grades_db"
  user: "grades_user"
  password: "mot-de-passe"
  refresh-interval-seconds: 15

factions-mysql:
  enabled: false
  host: "127.0.0.1"
  port: 3306
  database: "herocraft"
  user: "herocraft_user"
  password: "mot-de-passe"
  refresh-interval-seconds: 15

server-groups:
  bedwars1: "BedWars"
  bedwars2: "BedWars"
```

> 💡 **Lecture seule** : HeroTab ne modifie jamais ces bases MySQL — il se contente de lire le grade de chaque joueur et la table `faction_tab_sync` (créée par la classe `FactionTabSync` de FactionPlugin.)

>

### Placeholders disponibles

**Dans `player-format`** — tous les placeholders :

| Placeholder | Description |
|---|---|
| `%player%` | Nom du joueur affiché dans la liste |
| `%server%` | Sous-serveur actuel du joueur |
| `%group%` | Nom de groupe défini dans `server-groups`(sinon le nom du serveur) |
| `%ping%` | Ping du joueur (en ms) |
| `%grade%` | Nom du grade le plus prioritaire (ex: `VIP`)— vide si aucun ou si MySQL désactivé |
| `%grade_prefix%` | Préfixe brut du grade, déjà coloré (ex: `&6[VIP] `)— vide si aucun |
| `%grade_suffix%` | Suffixe du grade (ex: `&7`)— vide si aucun |
| `%grade_color%` | Code couleur brut du grade (ex: `&6`)— `&f` par défaut |
| `%faction%` | Nom de la faction du joueur — vide si aucune |
| `%faction_rank%` | Nom du rang de faction (ex: `Or`)— vide si aucun |
| `%faction_tag%` | Tag prêt à l'emploi `&7[&e★ Or - MaFaction&7]` — vide si pas de faction |
| `%primary%` | Couleur de décoration principale (`theme-primary`) |
| `%secondary%` | Couleur de décoration secondaire (`theme-secondary`) |

**Dans header/footer** — tous les placeholders ci-dessous, plus `%online%`, `%server_online%`, `%max%`, `%network_address%` et `%website_address%` :

| Placeholder | Description |
|---|---|
| `%online%` | Nombre total de joueurs sur tout le réseau |
| `%server_online%` | Nombre de joueurs sur le sous-serveur actuel |
| `%max%` | Nombre maximal de joueurs autorisé par la config du proxy |
| `%network_address%` | IP/domaine de connexion au réseau (`network-address`) |
| `%website_address%` | Adresse du site web (`website-address`) |
| `%grade%`, `%grade_prefix%`, `%grade_suffix%`, `%grade_color%`, `%faction%`, `%faction_rank%`, `%faction_tag%` | Disponibles aussi dans le header/footer |

### Commandes

| Commande | Description | Permission |
|---|---|---|
| `/herotab reload`(ou `/htab reload`) | Recharge la configuration et relance les mises à jour | `herotab.admin` |

---

## 🧩 Nouveautés

### Version 1.4.0 — Placeholders étendus, publication des sources à jour
- 🏷️ **`%grade_suffix%` et `%grade_color%` dans le header/footer** : le suffixe et la couleur du grade le plus prioritaire sont désormais disponibles dans l'en-tête et le pied de page, en plus du nom (`%grade%`) et du préfixe (`%grade_prefix%`) déjà supportés..
- ⚔️ **`%faction_tag%` dans le header/footer** : le tag de faction prêt à l'emploi (`&7[&e★ Or - MaFaction&7]`) peut maintenant s'afficher aussi dans l'en-tête et le pied de page, pas seulement dans le format joueur..
- 🔄 **Parité complète des placeholders** : les placeholders de grade et de faction se comportent désormais de la même façon partout— `player-format`, header et footer accepts les mêmes variables pour une composition libre du tab..
- 📦 **Publication republiée** : le `.jar` compilé (shadé) et le code source complet sont régénérés et attachés à cette release pour une traçabilité parfaite..

### Version 1.3.0 — Correctif du driver MySQL (connexions grades/factions fiabilisées)
- 🐛 **Correctif « No suitable driver found »** : sur Velocity, chaque plugin vit dans son propre classloader isolé— le mécanisme automatique de découverte du driver JDBC (utilisé en interne par `DriverManager` n'arrivait pas toujours à charger `mysql-connector-j` depuis une tâche planifiée du proxy, ce qui cassait les intégrations GradePlugin/FactionPlugin dès que l'on les activait..
- ⚙️ **Chargement explicite du driver** : nouvelle classe interne `JdbcDriverLoader` qui force le chargement du driver MySQL une fois pour toutes avant toute connexion— le bloc statique du driver s'enregistre alors durablement auprès de `DriverManager`..
- ✅ **GradeSync & FactionSync sécurisés** : si le driver ne peut toujours pas être chargé, le cache existant est conservé et un avertissement clair est journalisé plutôt qu'une exception silencieuse— plus de spam de stacktrace ni de tab qui se fige..

### Version 1.2.0 — Tri réel, séparateur de groupe& thème de couleurs
- 🏠 **Ton serveur d'abord** : nouveau mode de tri `SERVER_SELF_FIRST`, activé par défaut— chaque joueur voit d'abord les joueurs de **son** sous-serveur, puis les autres groupés par serveur..
- ➖ **Séparateur de groupe** : en mode `SERVER_SELF_FIRST`, une ligne décorative (`group-spacer-text`, désactivable avec `group-spacer-enabled`) sépare « ton serveur » des « autres »..
- 🔀 **Vrai réordonnancement du tab** : le protocole Minecraft n'ayant pas de notion de « position », HeroTab retire puis ré-ajoute les entrées du tab dans l'ordre voulu— en **conservant le skin et le mode de jeu** de chaque joueur (profil original réutilisé)..
- 🎨 **Thème de couleurs global** : nouvelles options `theme-primary` (bleu par défaut) et `theme-secondary` (jaune par défaut), utilisables partout via `%primary%` et `%secondary%`— plus besoin de changer chaque ligne pour recolorer tout le tab..
- 🌍 **Adresses du réseau dans le footer** : nouveaux placeholders `%network_address%` et `%website_address%`, réglables via `network-address` / `website-address`, avec un footer par défaut remanié..
- 🧹 **Format joueur par défaut enrichi** : le nom affiché montre désormais le serveur coloré, le préfixe de grade, la faction, et le ping (`&7[&b%server%&7] %grade_prefix%&f%player%%faction_tag% &8•&7 %ping%ms`)..
- ✨ **Header par défaut redessiné** : lignes décoratives, espacements et placeholders de réseau pour une présentation soignée dès la première installation..
- 🔧 **Refactor interne** : centralisation du nom de serveur et du remplacement des placeholders de thème— code plus lisible et maintenable..

### Version 1.1.0 — Intégrations GradePlugin & FactionPlugin
- 🏷️ **Grades affichés dans le tab** : lecture directe de la base MySQL `grades_db` de GradePlugin(tables `player_grades` + `grades`), avec `%grade%`, `%grade_prefix%`, `%grade_suffix%` et `%grade_color%` dans `player-format`..
- ⚔️ **Factions affichées dans le tab** : lecture de la table `faction_tab_sync` de FactionPlugin(base `herocraft`), avec `%faction%`, `%faction_rank%` et le tag prêt à l'emploi `%faction_tag%`..
- 📦 **Driver MySQL embarqué** : `mysql-connector-j` est inclus et shadé dans le `.jar` — aucune dépendance à installer sur le proxy..
- ⚙️ **Configuration enrichie** : nouvelles sections `grades-mysql` et `factions-mysql`(activation, identifiants, intervalle de rechargement) dans `config.yml`..
- 🔄 **Synchronisation périodique** : les caches grades/factions sont rechargés automatiquement(par défaut toutes les15 secondes, réglable) sans redémarrer le proxy..
- 🧹 **Classe `FactionTabSync` déplacée** : le code de synchronisation des factions vit désormais dans FactionPlugin, HeroTab se contente de lire la table partagée..

### Version 1.0.1 — Correctifs et publication des artefacts
- Publication de la release avec le **.jar compilé** et le **code source** attachés directement au dépôt..
- Description du plugin enrichie et mise à jour (page principale et release)..
- Version propagée dans `pom.xml`, l'annotation `@Plugin` et la release pour une traçabilité claire..

### Version 1.0.0 — Première version
- Première publication officielle du plugin sur le proxy Velocity..
- Header, footer et noms de joueurs unifiés au niveau du proxy..
- Animations de header/footer multi-frames avec `||`..
- Support MiniMessage optionnel..
- Ruches placeholders(`%online%`, `%server_online%`, `%server%`, `%group%`, `%ping%`, `%max%`)..
- Regroupement de sous-serveurs via `server-groups`..
- Tri des joueurs(alphabétique, ping, serveur, aucun)..
- Commande de rechargement à chaud avec permission..


---

## 🛠️ Compilation depuis les sources

```bash
mvn clean package
```

Le `.jar` final se trouve dans `target/` (SnakeYAML et le driver MySQL sont inclus et déplacés dans `com.herocraft.herotab.libs` pour éviter tout conflit).

---

## 📄 Licence

Ce plugin est fourni pour le réseau HeroCraft. Utilisation et modification libres pour un usage interne..
