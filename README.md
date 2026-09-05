# 🎮 HeroTab

**Tab list unifié et personnalisable pour tout le réseau HeroCraft (proxy Velocity.)**

HeroTab est un plugin pour **Velocity 3.3+** qui remplace le tab (liste de joueurs, header et footer) de **tous** les joueurs connectés au proxy — quel que soit le sous-serveur ou le monde sur lequel ils se trouvent. **Une seule installation sur le proxy suffit** : plus rien à installer ni à configurer sur chaque serveur de jeu. Le résultat est une expérience cohérente et soignée sur tout le réseau..

À partir de la version **1.1.0**, HeroTab sait aussi afficher le **grade** (GradePlugin) et la **faction** (FactionPlugin) de chaque joueur, en lisant directement leurs bases MySQL — sans dépendre des plugins Paper eux-mêmes..

---

## ✨ Fonctionnalités

- 🌐 **Tab unifié sur tout le réseau** : le header, le footer et les noms affichés sont identiques pour tous les joueurs, même s'ils sont répartis sur plusieurs sous-serveurs..

- 🎨 **Header / footer personnalisables** : plusieurs lignes, codes couleur `&` classiques, hexadécimaux `&#RRGGBB`, et même des balises **MiniMessage** (`<gradient>`, `<bold>`…) si tu les autorises dans la config..

- 🔄 **Lignes animées** : sépare plusieurs frames d'une ligne avec `||` pour créer une animation en boucle (ex: `"&b&lHeroCraft||&3&lHeroCraft"`). La vitesse se règle dans `config.yml`.

- 🏷️ **Grades intégrés (GradePlugin)** : le nom, préfixe et couleur du grade le plus prioritaire de chaque joueur, lus directement depuis la base `grades_db` (tables `player_grades` + `grades`)— aucune installation supplémentaire sur les serveurs de jeu..

- ⚔️ **Factions intégrées (FactionPlugin)** : le nom et le rang de faction de chaque joueur, avec un tag prêt à l'emploi, lus depuis la table `faction_tab_sync` dans la base `herocraft`..

- 📊 **Placeholders riches** : affiche dans ton tab le nombre de joueurs sur tout le réseau (`%online%`), sur ton sous-serveur actuel (`%server_online%`), ton serveur, ton groupe, ton ping, ton grade, ta faction…

- 🗂️ **Regroupement de sous-serveurs** : affiche un joli nom commun pour tout un groupe de serveurs(ex: `bedwars1`, `bedwars2`, `bedwars3` → `BedWars` grâce à `server-groups`.

- 📋 **Tri des joueurs** : trie la liste des joueurs par ordre alphabétique, par ping, par serveur, ou laisse l'ordre par défaut..

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
player-format: "&7[&b%server%&7] %grade_prefix%&f%player%%faction_tag%"
sort-mode: "SERVER"                # ALPHABETICAL, PING, SERVER, NONE
allow-minimessage: true

header:
  - "&b&lHeroCraft &7- &fLe réseau"
  - "&7Réseau : &b%online%&7/&b%max% &8| &7Ici : &b%server_online%"

footer:
  - "&7www.herocraft.example"
  - "&7Serveur actuel : &b%server%"

# Intégrations MySQL(GradePlugin et FactionPlugin) — à activer si tu veux
# afficher les grades/factions dans le tab.
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

> 💡 **Lecture seule** : HeroTab ne modifie jamais ces bases MySQL — il se contente de lire le grade de chaque joueur et la table `faction_tab_sync` (créée par la classe `FactionTabSync` de FactionPlugin).

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

**Dans header/footer** — tous les placeholders ci-dessous, plus `%online%`, `%server_online%` et `%max%` :

| Placeholder | Description |
|---|---|
| `%online%` | Nombre total de joueurs sur tout le réseau |
| `%server_online%` | Nombre de joueurs sur le sous-serveur actuel |
| `%max%` | Nombre maximal de joueurs autorisé par la config du proxy |
| `%grade%`, `%grade_prefix%`, `%faction%`, `%faction_rank%` | Disponibles aussi dans le header/footer |

### Commandes

| Commande | Description | Permission |
|---|---|---|
| `/herotab reload`(ou `/htab reload`) | Recharge la configuration et relance les mises à jour | `herotab.admin` |

---

## 🧩 Nouveautés

### Version 1.1.0 — Intégrations GradePlugin & FactionPlugin
- 🏷️ **Grades affichés dans le tab** : lecture directe de la base MySQL `grades_db` de GradePlugin(tables `player_grades` + `grades`), avec `%grade%`, `%grade_prefix%`, `%grade_suffix%` et `%grade_color%` dans `player-format`..
- ⚔️ **Factions affichées dans le tab** : lecture de la table `faction_tab_sync` de FactionPlugin(base `herocraft`), avec `%faction%`, `%faction_rank%` et le tag prêt à l'emploi `%faction_tag%`..
- 📦 **Driver MySQL embarqué** : `mysql-connector-j` est inclus et shadé dans le `.jar` — aucune dépendance à installer sur le proxy..
- ⚙️ **Configuration enrichie** : nouvelles sections `grades-mysql` et `factions-mysql`(activation, identifiants, intervalle de rechargement) dans `config.yml`..
- 🔄 **Synchronisation périodique** : les caches grades/factions sont rechargés automatiquement (par défaut toutes les 15 secondes, réglable) sans redémarrer le proxy..
- 🧹 **Classe `FactionTabSync` déplacée** : le code de synchronisation des factions vit désormais dans FactionPlugin, HeroTab se contente de lire la table partagée.



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
