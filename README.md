# What is Theatrical?
Theatrical is a **utility mod for Minecraft roleplayers**, built for Fabric 1.20.1.<br>
It is made for roleplay servers in order to make some parts of it better / easier.<br>

---

# Features

### Item handover
You can "hand" items to people by holding them out, and having them take it from you.
- Holding down **G** (configurable) will make you hold whatever item is in your hand out (or your empty hand)
- Pressing the same key with an empty hand while looking at someone else who has their hand held out will take that item from them.

### Variable Walk Speed
Slow down your walking speed below default:
- Hold **Alt** and scroll to change speed
- Hold **Shift** + **Alt** and scroll to change speed in larger steps
- You can change these keybinds in controls
- By default, the mod shows you the current walk speed when you change it - this can be disabled in the config

---

### Countdown System
Start a synchronized countdown for nearby players or specific players. Every player can configure what things they need to have enabled for the countdown to be able to start. There are currently these possible checks:
- Verify if **ReplayMod** is recording.
- Verify if **Simple Voice Chat** is recording.

You can also enable a feature (per-check), which makes it so that if the check it is related to fails, the check gets "fixed" (like a recording being auto-started).

On the server side, you can configure how many people's checks are allowed to fail before starting the countdown fails.

Players with the `director` role (or server operators) can run countdown commands:

- `/countdown radius <radius> <duration>`
- `/countdown players <duration> <targets (List of player names)>`
- `/countdown selector <duration> <targets (Default minecraft player selector)>`

---

### Plot Armor
Change how damage and healing works for players:
- **Positive plot armor:** Makes them harder to kill (e.g., set minimum health, faster regeneration).
- **Negative plot armor:** Makes them more vulnerable (e.g., higher damage taken, slower regen).

Use the `/director` command to configure:
- `/director plot_armor <player> <type>` – set plot armor type.
- `/director director <player> <true|false>` – toggle director role for a player.

---

# Configuration
Theatrical has both **client-side** and **server-side** configs.<br>
On a local world (or if you are an operator), you can modify the **server-side** config from the client.

---

# Installation

There are two **required** mods for theatrical:

- [Cardinal Components](https://modrinth.com/mod/cardinal-components-api)
- [Composer Reloaded](https://modrinth.com/mod/composer-reloaded)

You can optionally also install these for theatrical to check if they are running:

- [ReplayMod](https://modrinth.com/mod/replaymod)
- [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat)

Currently, theatrical is only officially available for 1.20.1 fabric, but it should inherently work on quilt.

---

# License

Theatrical is licensed under **CC-BY-NC-SA 4.0**. (See [LICENSE](LICENSE.txt))
