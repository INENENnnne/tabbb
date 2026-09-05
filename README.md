# 🎮 HeroTab

**Tab list unifié et personnalisable pour tout le réseau HeroCraft (proxy Velocity).**

HeroTab est un plugin pour **Velocity** qui remplace le tab (liste de joueurs), header et footer de tous les joueurs connectés au proxy — quel que soit le sous-serveur ou le monde sur lequel ils se trouvent. Plus besoin d'installer quoi que ce soit sur chaque serveur de jeu : tout est géré depuis le proxy, pour une expérience cohérente sur tout le réseau.

---

## ✨ Fonctionnalités

- 🌐 **Tab unifié sur tout le réseau** : le header, le footer et les noms affichés sont identiques pour tous les joueurs, même s'ils sont répartis sur plusieurs sous-serveurs.

- 🎨 **Header / footer personnalisables** : plusieurs lignes, codes couleur `&` classiques, hexadécimaux `&#RRGGBB`, et même des balises **MiniMessage** (`<gradient>`, `<bold>`…) si tu les autorises dans la config.

- 🔄 **Lignes animées** : sépare plusieurs frames d'une ligne avec `||` pour créer une animation en boucle (ex: `"&b&lHeroCraft||&3&lHeroCraft"`). La vitesse se règle dans `config.yml`.


- 📊 **Placeholders riches** : affiche dans ton tab le nombre de joueurs sur tout le réseau (`%online%`), sur ton sous-serveur actuel (`%server_online%`), ton serveur, ton groupe, ton ping…
 
- 🗂️ **Regroupement de sous-serveurs** : affiche un joli nom commun pour tout un groupe de serveurs (ex: `bedwars1`, `bedwars2`, `bedwars3` → `BedWars`) grâce à `server-groups`.
 
- 📋 **Tri des joueurs** : trie la liste des joueurs par ordre alphabétique, par ping, par serveur, ou laisse l'ordre par défaut.

- ⚡ **Léger et efficace** : mis à jour périodiquement par un scheduler Velocity, avec des compteurs réseau calculés une seule fois par cycle; aucun impact sur les serveurs de jeu.

- 🔒 **Rechargement à chaud** : `/herotab reload` (permission `herotab.admin`) sans redémarrer le proxy.



## 📥 Installation

1. Télécharge le fichier **`.jar`** depuis la dernière [release] (https://github.com/INENENnnne/tabbb/releases).
2. Dépose-le dans le dossier `plugins/` de ton proxy **Velocity 3.3+**.
3. Redémarre le proxy (ou utilise un plugin de rechargement). Le plugin génère alors son fichier `config.yml` dans `plugins/herotab/`.


## ⚙️ Configuration

Tous les réglages se font dans `plugins/herotab/config.yml`. Exemple de base :

```yaml
update-interval-ticks: 20          # intervalle de rafraîchissement (20 =  seconde)
animation-interval-ticks: 20      # vitesse des lignes animées
player-format: "&7[&b%server%&7] &f%player%"
sort-mode: "SERVER"                # ALPHABETICAL, PING, SERVER, NONE
allow-minimessage: true

header:
  - "&b&lHeroCraft &7- &fLe réseau"
  - "&7Réseau : &b%online%&7/&b%max% &8| &7Ici : &b%server_online%"

footer:
  - "&7www.herocraft.example"
  - "&7Serveur actuel : &b%server%"

server-groups:
  bedwars1: "BedWars"
  bedwars2: "BedWars"
```

### Placeholders disponibles

| Placeholder | Description |
|---|---|
| `%player%` | Nom du joueur qui regarde le tab |
| `%server%` | Sous-serveur actuel du joueur |
| `%group%` | Nom de groupe défini dans `server-groups`(sinon le nom du serveur) |
| `%ping%` | Ping du joueur (en ms) |
| `%online%` | Nombre total de joueurs sur tout le réseau |
| `%server_online%` | Nombre de joueurs sur le sous-serveur actuel |
| `%max%` | Nombre maximal de joueurs autorisé par la config du proxy |

### Commandes

| Commande | Description | Permission |
|---|---|---|
| `/herotab reload`(ou `/htab reload`) | Recharge la configuration et relance les mises à jour | `herotab.admin` |

---

## 🧩 Nouveautés

### Version 1.0.0 — Première version
- Première publication officielle du plugin sur le proxy Velocity
- Header, footer et noms de joueurs unifiés au niveau du proxy
- Animations de header/footer multi-frames avec `||`
- Support MiniMessage optionnel
- Ruches placeholders (`%online%`, `%server_online%`, `%server%`, `%group%`, `%ping%`, `%max%`)
- Regroupement de sous-serveurs via `server-groups`
- Tri des joueurs (alphabétique, ping, serveur, aucun)
- Commande de rechargement à chaud avec permission

---

## 🛠️ Compilation depuis les sources

```bash
mvn clean package
```

Le `.jar` final se trouve dans `target/` (la lib SnakeYAML est incluse et déplacée dans `com.herocraft.herotab.libs` pour éviter tout conflit).

---

## 📄 Licence

Ce plugin est fourni pour le réseau HeroCraft. Utilisation et modification libres pour un usage interne.