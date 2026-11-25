# Forgebound

Fabric-Mod fuer **Minecraft 1.21.1** rund ums Schmieden, einzigartige Ausruestung, Klassen, Skills und Quests. Eigenstaendige, von Schmiede-MMORPGs inspirierte Umsetzung mit eigenen Namen, Texten und Werten.

**Status:** Entwurf, Phase 4 (Prototyp).

## Download

Fertige Jar-Dateien liegen unter [Releases](../../releases). Voraussetzungen: Minecraft 1.21.1, Fabric Loader ab 0.16.14, Fabric API, Java 21.

## Spielablauf

1. **Schmiedestation** herstellen (Handwerkstisch: oben `III` Eisenbarren, Mitte `_S_`, unten `SSS`, S = glatter Stein). Sie sieht aus wie ein Amboss und wird wie einer gedreht.
2. Rechtsklick oeffnet das Schmiedemenue.
3. **Material waehlen:** Stein (nur Schwert), Eisen, Gold, Diamant, Netherit oder Aurorit.
4. **Ausruestungsart waehlen:** Schwert, Helm, Brustplatte, Hose oder Stiefel (Symbole unter dem Minigame). Kosten stehen am Materialknopf; das Schwert braucht zusaetzlich 1 Stock.
5. **Minigame:** 5 Hammerschlaege. Ein Marker wandert ueber die Leiste, schlage zu (Knopf oder Leertaste), wenn er in der Zielzone ist. Perfekt 3, Gut 2, Okay 1, Daneben 0 Punkte.
6. Rechts stehen die **Chancen** auf Normal, Selten, Episch, Legendaer, Einzigartig. Sie steigen mit Treffern, Schmiedelevel und Material.
7. **Enthuellung:** Nach dem Minigame kommt das Stueck nicht ins Inventar. Auf dem Amboss laeuft eine Animation, die mit der Seltenheit groesser wird: Normal nur Funken, Selten und Episch mit farbigem Lichtstrahl, Legendaer mit goldenem Strahl und Blitz aus dem Himmel, Einzigartig mit rotem Strahl, drei Blitzen und Totem-Fanfare. Danach schwebt das Stueck ueber dem Amboss. Rechtsklick sammelt es ein. Die Blitze sind rein kosmetisch.
8. Jedes Schmieden gibt **Schmiede-Erfahrung**. Mit steigendem Level (bis 20) werden die Zielzonen breiter und die Chancen besser.

## Traits

**Waffen** (wirken bei jedem Treffer): Glut (Brand), Lebensraub (Heilung), Frostbiss (Verlangsamung), Verfall (Wither), Wucht (Rueckstoss), Schwung (Tempo fuer dich), Schutzschild (Absorption fuer dich).

**Ruestung** (dauerhafte Attributboni): Vitalitaet (max. Leben), Flinkheit (Lauftempo), Bollwerk (Rueckstossresistenz), Zaehigkeit (Ruestungszaehigkeit), Kraft (Angriffsschaden).

Anzahl und Stufe richten sich nach der Seltenheit: Normal 1x Stufe I, Selten 1x II, Episch 2x III, Legendaer 3x IV, Einzigartig 4x V. Ruestung bekommt zusaetzlich bis zu +2 Ruestungspunkte je Teil. Zahlen stehen in `ForgeTrait`, `ArmorTrait` und `BalanceConfig`.

## Aurorit

Eigenes Erz in der Overworld, tief unter der Erde (Y -64 bis -8) und deutlich seltener als Diamanten. Es braucht mindestens eine Diamantspitzhacke. Rohes Aurorit wird im Ofen oder Schmelzofen zum Barren. Daraus entstehen im Schmiedemenue das **Aurorit-Schwert** und die **Aurorit-Ruestung**, beides ueber Netherit. Das Erz erscheint nur in neu generierten Chunks.

## Technik

- Menue, Treffer, Seltenheit, Traits, Gegenstand und Erfahrung werden nur auf dem Server berechnet; der Client schickt Knopfdruecke und zeigt den Zustand an.
- Das fertige Stueck liegt in einer Block-Entity des Ambosses (gespeichert, mit dem Client synchronisiert); die Animation rechnet der Client aus dem Startzeitpunkt.
- Qualitaet (`forgebound:quality`) und Traits (`forgebound:traits`) sind Data Components.
- Schmiedelevel pro Spieler als Welt-Zustand (ueberlebt Tod, Dimensionswechsel und Neustart).
- Alle Zahlen zum Balancing stehen in `BalanceConfig`.
- Texturen sind selbst gezeichnet (16x16).

## Selbst bauen

```
gradle build
```

Java 21 und Gradle 8.12. Die Jar liegt unter `build/libs/`. Auf GitHub baut der Workflow `build` bei jedem Push; ein Tag wie `v0.4.0` erzeugt zusaetzlich ein Release mit Jar.

## Lizenz

Vorerst `All-Rights-Reserved` (in `fabric.mod.json`).

## Naechste Schritte

- Reparieren und Verbessern von Ausruestung.
- Klassen und erste Skills.
