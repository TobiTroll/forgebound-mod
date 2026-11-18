# Forgebound

Fabric-Mod fuer **Minecraft 1.21.1** rund ums Schmieden, einzigartige Ausruestung, Klassen, Skills und Quests. Die Mod ist eine eigenstaendige, von Schmiede-MMORPGs inspirierte Umsetzung mit eigenen Namen, Texten und Werten.

**Status:** Entwurf, Phase 1 (Prototyp). Der Build laeuft auf GitHub durch, in Minecraft getestet ist die Mod noch nicht. Texturen sind Vanilla-Platzhalter.

## Download

Fertige Jar-Dateien liegen unter [Releases](../../releases). Voraussetzungen: Minecraft 1.21.1, Fabric Loader ab 0.16.14, Fabric API, Java 21.

## Was Phase 1 enthaelt

- Block **Schmiedestation** (Rezept im Handwerkstisch: 3x Eisenbarren oben, 4x glatter Stein darunter in T-Form: Reihe 1 `III`, Reihe 2 `_S_`, Reihe 3 `SSS`).
- Rechtsklick auf die Station: 3 Eisenbarren und 1 Stock werden verbraucht, du erhaeltst ein **Geschmiedetes Eisenschwert**. Im Kreativmodus kostenlos.
- Das Schwert traegt eine **Qualitaet** (Datenkomponente) und zeigt sie im Tooltip mit Text und Symbol.
- Die Herstellung laeuft nur serverseitig. Zahlen stehen zentral in `BalanceConfig`.

## Selbst bauen

```
gradle build
```

Java 21 und Gradle 8.12 werden benoetigt. Die Jar liegt danach unter `build/libs/`. Auf GitHub baut der Workflow `build` bei jedem Push automatisch; ein Tag wie `v0.1.0` erzeugt zusaetzlich ein Release mit Jar.

## Technische Entscheidungen

- Yarn-Mappings und Loom mit `remapJar`, passend zu 1.21.1.
- Item-Daten als eigene Data Component (`forgebound:quality`) mit Codec und Packet-Codec, damit sie gespeichert und zum Client synchronisiert werden.
- Lizenz vorerst `All-Rights-Reserved` (in `fabric.mod.json`), bis eine Lizenz gewaehlt wird.

## Naechste Schritte

1. Eigenes Schmiedemenue mit Materialanzeige und Vorschau.
2. Spielerfortschritt (Handwerksstufe), der nach Neustart erhalten bleibt.
3. Qualitaet abhaengig von Materialien und Fortschritt, begrenzter Zufall.
