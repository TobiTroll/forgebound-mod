# Forgebound

Fabric-Mod fuer **Minecraft 1.21.1** rund ums Schmieden, einzigartige Ausruestung, Klassen, Skills und Quests. Eigenstaendige, von Schmiede-MMORPGs inspirierte Umsetzung mit eigenen Namen, Texten und Werten.

**Status:** Entwurf, Phase 2 (Prototyp). Texturen sind Vanilla-Platzhalter.

## Download

Fertige Jar-Dateien liegen unter [Releases](../../releases). Voraussetzungen: Minecraft 1.21.1, Fabric Loader ab 0.16.14, Fabric API, Java 21.

## Spielablauf

1. **Schmiedestation** herstellen (Handwerkstisch: oben `III` Eisenbarren, Mitte `_S_`, unten `SSS`, S = glatter Stein).
2. Rechtsklick auf die Station oeffnet das Schmiedemenue.
3. **Material waehlen:** Stein, Eisen, Gold, Diamant oder Netherit (jeweils plus 1 Stock; Kosten stehen am Knopf).
4. **Minigame:** 5 Hammerschlaege. Ein Marker wandert ueber die Leiste, schlage zu (Knopf oder Leertaste), wenn er in der Zielzone ist. Perfekt 3, Gut 2, Okay 1, Daneben 0 Punkte.
5. Rechts stehen die **Chancen** auf Normal, Selten, Episch, Legendaer, Einzigartig. Sie steigen mit deinen Treffern, deinem Schmiedelevel und dem Material.
6. Das Ergebnis ist ein Schwert mit Seltenheit (Tooltip mit Text und Symbol) und einem kleinen Schadensbonus je Seltenheit.
7. Jedes Schmieden gibt **Schmiede-Erfahrung**. Mit steigendem Level (bis 20) werden die Zielzonen breiter und die Chancen besser. Besseres Material gibt mehr Erfahrung, billiges weniger.

Wer das Menue mitten im Schmieden schliesst, verliert nichts: Der Vorgang wird mit den bisherigen Punkten abgeschlossen.

## Technik

- Menue, Treffer, Seltenheit, Gegenstand und Erfahrung werden nur auf dem Server berechnet; der Client schickt Knopfdruecke und zeigt den Zustand an.
- Qualitaet als Data Component `forgebound:quality`, Schadensbonus ueber die Attribut-Komponente.
- Schmiedelevel pro Spieler als Welt-Zustand (ueberlebt Tod, Dimensionswechsel und Neustart).
- Alle Zahlen zum Balancing stehen in `BalanceConfig`.

## Selbst bauen

```
gradle build
```

Java 21 und Gradle 8.12. Die Jar liegt unter `build/libs/`. Auf GitHub baut der Workflow `build` bei jedem Push; ein Tag wie `v0.2.0` erzeugt zusaetzlich ein Release mit Jar.

## Lizenz

Vorerst `All-Rights-Reserved` (in `fabric.mod.json`).

## Naechste Schritte

- Eigene Texturen statt Vanilla-Platzhalter.
- Mehr Ausruestung (Rüstung, Werkzeuge) und Reparieren/Verbessern.
- Klassen und erste Skills.
