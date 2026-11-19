# Forgebound

Fabric-Mod fuer **Minecraft 1.21.1** rund ums Schmieden, einzigartige Ausruestung, Klassen, Skills und Quests. Eigenstaendige, von Schmiede-MMORPGs inspirierte Umsetzung mit eigenen Namen, Texten und Werten.

**Status:** Entwurf, Phase 3 (Prototyp).

## Download

Fertige Jar-Dateien liegen unter [Releases](../../releases). Voraussetzungen: Minecraft 1.21.1, Fabric Loader ab 0.16.14, Fabric API, Java 21.

## Spielablauf

1. **Schmiedestation** herstellen (Handwerkstisch: oben `III` Eisenbarren, Mitte `_S_`, unten `SSS`, S = glatter Stein). Sie sieht aus wie ein Amboss und wird wie einer gedreht.
2. Rechtsklick oeffnet das Schmiedemenue.
3. **Material waehlen:** Stein, Eisen, Gold, Diamant, Netherit oder Aurorit (jeweils plus 1 Stock; Kosten stehen am Knopf).
4. **Minigame:** 5 Hammerschlaege. Ein Marker wandert ueber die Leiste, schlage zu (Knopf oder Leertaste), wenn er in der Zielzone ist. Perfekt 3, Gut 2, Okay 1, Daneben 0 Punkte.
5. Rechts stehen die **Chancen** auf Normal, Selten, Episch, Legendaer, Einzigartig. Sie steigen mit Treffern, Schmiedelevel und Material.
6. Das Ergebnis ist ein Schwert mit Seltenheit und **Traits**. Je seltener, desto mehr und desto staerkere Traits: Normal 1x Stufe I, Selten 1x II, Episch 2x III, Legendaer 3x IV, Einzigartig 4x V. Dazu kommt ein kleiner Schadensbonus je Seltenheit.
7. Jedes Schmieden gibt **Schmiede-Erfahrung**. Mit steigendem Level (bis 20) werden die Zielzonen breiter und die Chancen besser.

## Traits

Glut (Brand), Lebensraub (Heilung), Frostbiss (Verlangsamung), Verfall (Wither), Wucht (Rueckstoss), Schwung (Tempo fuer dich), Schutzschild (Absorption fuer dich). Sie wirken bei jedem Treffer; Zahlen stehen in `ForgeTrait` und `BalanceConfig`.

## Aurorit

Eigenes Erz in der Overworld, tief unter der Erde (Y -64 bis -8) und deutlich seltener als Diamanten. Es braucht mindestens eine Diamantspitzhacke. Rohes Aurorit wird im Ofen oder Schmelzofen zum Barren. Aus 2 Barren und 1 Stock entsteht im Schmiedemenue das **Aurorit-Schwert**, das ueber Netherit liegt. Das Erz erscheint nur in neu generierten Chunks.

## Technik

- Menue, Treffer, Seltenheit, Traits, Gegenstand und Erfahrung werden nur auf dem Server berechnet; der Client schickt Knopfdruecke und zeigt den Zustand an.
- Qualitaet (`forgebound:quality`) und Traits (`forgebound:traits`) sind Data Components.
- Schmiedelevel pro Spieler als Welt-Zustand (ueberlebt Tod, Dimensionswechsel und Neustart).
- Alle Zahlen zum Balancing stehen in `BalanceConfig`.
- Texturen sind selbst gezeichnet (16x16).

## Selbst bauen

```
gradle build
```

Java 21 und Gradle 8.12. Die Jar liegt unter `build/libs/`. Auf GitHub baut der Workflow `build` bei jedem Push; ein Tag wie `v0.3.0` erzeugt zusaetzlich ein Release mit Jar.

## Lizenz

Vorerst `All-Rights-Reserved` (in `fabric.mod.json`).

## Naechste Schritte

- Mehr Ausruestung (Ruestung, Werkzeuge) und Reparieren/Verbessern.
- Klassen und erste Skills.
