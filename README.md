# BlockLogging

BlockLogging is the block logger we run on the Alpha Network in place of CoreProtect. It records block changes, containers, items, kills, signs, chat, commands and sessions, lets staff look any of it up in game, and rolls griefs and raids back without lagging the server. It is written for Minecraft 1.8 on our TacoSpigot fork and Java 25, and stores its logs in SQLite, MySQL or MongoDB.

## Q: "Why not just keep using CoreProtect?"

CoreProtect is a great plugin, but its last release for 1.8 is 2.12.0, which is old now, and a busy HCF server pushes it hard. Raids mean thousands of block changes a minute, and rolling a base back on a full server meant a lag spike every time. We wanted something we could keep improving, that fit how our network runs (MongoDB, our own command and menu libraries, rank colours from Core), and that never touches the disk on the main thread.

## Q: "Do I have to relearn the commands?"

No. `/bl`, `/blocklogging` and `/co` all work, and the filters are the same ones CoreProtect uses (`u:`, `t:`, `r:`, `a:`, `i:`, `e:`, `#preview` and so on). If you know CoreProtect, you already know BlockLogging.

## Q: "Is it actually faster, or does it just say it is?"

We benchmarked both plugins on the same server with the same 100,000 block changes, three runs each. The full numbers are further down. The short version: rollbacks and lookups are about 3x faster, and a 50,000 block rollback only pushed one tick over 60 ms.

## Q: "What happens if the server crashes in the middle of a rollback?"

Every rollback is written to a journal before it starts, and each tick marks the rows it finished. When the server comes back up, a rollback that was still running picks up where it stopped. `/bl undo` also survives a restart, because the last operation is loaded back from the database.

For a direct comparison:

|                        | CoreProtect 2.12.0                        | BlockLogging                                                        |
|------------------------|-------------------------------------------|---------------------------------------------------------------------|
| Storage                | SQLite, MySQL                             | SQLite, MySQL, MongoDB                                              |
| Rollback lag           | 6 ticks over 60 ms, worst 105 ms          | 1 tick over 60 ms, worst 61 ms                                      |
| Crash during rollback  | Not resumed                               | Resumed on the next start                                           |
| Undo after a restart   | No                                        | Yes                                                                 |
| TNT                    | Logged as `#tnt`                          | Credited to the player who placed or lit it                         |
| Radius                 | Number, `#global`, a world                | Same, plus your WorldEdit selection (`r:#we`)                       |
| Names                  | Plain                                     | Coloured by Core rank                                               |
| Staff tools            | Chat                                      | Chat with hover times and click to teleport, plus menus             |


---

## Project Structure

```
BlockLogging/
├── pom.xml
├── libs/
│     └── worldedit-bukkit-6.1.7.3.jar
└── src/main/java/rip/alpha/blocklogging/
      ├── BlockLogging.java     plugin entry point
      ├── admin/                help, status, reload, teleport and the control panel
      ├── common/               shared models, chat styling, permissions, rank colours
      ├── config/               blocklogging.json
      ├── inspect/              the block inspector
      ├── purge/                deleting old logs
      ├── query/                lookup filters, rendering and the results menu
      ├── record/               listeners and the batched writer
      ├── rollback/             planning, applying, undo, previews and the builder menu
      └── storage/              the SQLite/MySQL and MongoDB stores
```

---

## Features

- Logs block breaks and places, interactions, container changes, item pickups and drops, kills, signs, chat, commands, logins and name changes
- Blames explosions, fire, lava, water, pistons, hoppers and decay on a cause, and TNT on whoever placed or lit it
- Inspector: click a block, container, sign or door to see its history
- Lookups with paging, hover timestamps and click to teleport
- Names shown in their Core rank colour
- Commands run by staff are hidden from anyone at the same rank weight or lower, unless they are opped
- Rollback, restore, undo, `#preview` and `#count`
- Rolls back chest contents, sign text, spawners, skulls, banners, item frames, paintings, player items and mobs
- A control panel, a results menu and a rollback builder you can use without typing a single filter
- Purge by age, optionally per world
- Tab completion for every command and filter

---

## Setup instructions

1. Clone the repository:

    ```
    git clone https://github.com/TheAlphaNetwork/BlockLogging.git
    cd BlockLogging
    ```

2. Build it with Java 25 and Maven. Libraries, Core and the server jar need to be in your local Maven repository.

    ```
    mvn package
    ```

3. Drop `target/BlockLogging.jar` into the server's `plugins/` folder. It needs Libraries and packetevents on the server. Core and WorldEdit are optional: without Core names are plain white, and without WorldEdit `r:#we` doesn't work.

4. Start the server once, then pick a database in `plugins/BlockLogging/blocklogging.json`:

    | `storage.type` | When to use it | Settings |
    |----------------|----------------|----------|
    | `sqlite` (default) | One server, nothing to set up | `file` |
    | `mysql` | You already run MySQL or MariaDB | `host`, `port`, `database`, `username`, `password`, `properties` |
    | `mongodb` | You already run MongoDB | `mongoUri`, `database` |

    The rest of the file covers the writer (`flushMillis`, `batchSize`, `maxQueued`), what gets logged (a switch per category and `disabledWorlds`), lookups (`pageSize`, `nearRadius`) and rollbacks (`defaultRadius`, `maxRadius`, `millisPerTick`).

5. Restart, then run `/bl status` to check that it connected.

---

## Commands

| Command | What it does |
|---------|--------------|
| `/bl menu` | Control panel: inspector, nearby changes, rollback builder, undo and status |
| `/bl inspect` or `/bl i` | Toggle the inspector |
| `/bl lookup <filters>` or `/bl l` | Search the logs. `/bl l 2` goes to page 2 of the last search |
| `/bl gui [page]` | Show the last search as a menu |
| `/bl near [radius]` | What changed around you |
| `/bl rollback <filters>` or `/bl rb` | Undo matching changes |
| `/bl restore <filters>` or `/bl rs` | Put rolled back changes back |
| `/bl undo [player]` | Revert your last rollback or restore. Admins can revert someone else's |
| `/bl apply` / `/bl cancel` | Confirm or throw away a `#preview` |
| `/bl purge t:<time> [r:#world]` | Delete old logs |
| `/bl status`, `/bl consumer`, `/bl reload` | Writer stats, pause the writer, reload the config |

### Filters

| Filter | Example | Meaning |
|--------|---------|---------|
| `u:` | `u:Notch,#tnt` | Players or causes (`#tnt`, `#creeper`, `#fire`, `#lava`, `#water`, `#piston`, `#hopper`, `#decay`). Old names match too |
| `t:` | `t:2w5d`, `t:1h-2h` | Newer than a time, or between two |
| `r:` | `r:15`, `r:#we`, `r:#global`, `r:#world_nether` | A radius, your WorldEdit selection, everywhere, or one world |
| `a:` | `a:-block,+container` | `block`, `click`, `container`, `item`, `kill`, `sign`, `chat`, `command`, `session`, `username`, with `+` or `-` for placed or removed |
| `i:` / `e:` | `i:diamond_block` / `e:stone,Notch` | Only include, or leave out, these blocks, items, entities or players |
| flags | `#preview`, `#count`, `#verbose`, `#silent` | Change how a rollback runs |

A rollback needs a `t:`. If you leave out `r:` it uses 10 blocks around you. `r:#global` and `r:#<world>` rollbacks need `blocklogging.rollback.global`, so someone with the normal rollback permission can't wipe the whole map.

### Permissions

| Node | Lets you |
|------|----------|
| `blocklogging.inspect` | Use the inspector |
| `blocklogging.lookup` | Look things up, and use `near`, `gui` and `menu` |
| `blocklogging.lookup.chat` | Look up chat and commands |
| `blocklogging.rollback` | Roll back, undo and preview, up to the max radius |
| `blocklogging.rollback.global` | Roll back a whole world, the whole server, or a WorldEdit selection bigger than the max radius |
| `blocklogging.undo.others` | Undo another player's last rollback or restore with `/bl undo <player>` |
| `blocklogging.restore` | Restore |
| `blocklogging.purge` | Purge old logs |
| `blocklogging.status`, `.consumer`, `.reload`, `.teleport` | The matching admin command |

---

## Benchmark

Both plugins ran on the same TacoSpigot 1.8.8 server with Java 25 and SQLite. A bench plugin fired 100,000 real block break and place events, waited until every row was in the database, timed lookups through each plugin's own API, then rolled the 50,000 placed blocks back and checked every one. These are the medians of three runs.

| | BlockLogging | CoreProtect |
|---|---:|---:|
| Rolling back 50,000 blocks | **0.91 s** | 3.00 s |
| Single block lookup (p50) | **0.17 ms** | 0.50 ms |
| 21x21 radius lookup (p50) | **19.4 ms** | 63.9 ms |
| Time for the last write to land | **0.11 s** | 1.43 s |
| Main thread time for 100,000 events | **93 ms** | 130 ms |
| Rows written per second | **107,160** | 48,817 |
| Ticks over 60 ms during the rollback | **1** | 6 |
| Worst tick during the rollback | **61 ms** | 105 ms |
| Blocks restored correctly | 100% | 100% |
| Database size afterwards | **8.64 MB** | 8.77 MB |
| Disk used during the run | 9.66 MB | **8.77 MB** |

The one row CoreProtect wins is disk use while the test is running. SQLite's write-ahead log holds up to about a megabyte before it gets folded back into the database, and once it is, BlockLogging ends up slightly smaller. One BlockLogging run had a 785 ms main thread spike (the other two were 76 and 93 ms), which the median leaves out.

---

## Known limitations

- Player items only roll back for players who are online when the rollback runs
- A respawned mob gets its name, armour, colours and variant back, but not its health or potion effects
- There's no importer for existing CoreProtect data yet

---

## Future improvements

- An importer for CoreProtect databases
- Rolling back an offline player's items the next time they log in
- A web panel for lookups

---

## License

BlockLogging is licensed under the [GNU General Public License v3.0](LICENSE), with a few extra terms under section 7 that are listed in [NOTICE](NOTICE). In short: you can use it, change it and share it, as long as your version stays under the GPL with its source available. Keep the Alpha Development Team credit, mark your changes, and don't use the Alpha Network name for your fork or to say we made or endorse it. Calling your fork BlockLogging is fine.
