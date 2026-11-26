# Forgebound

Fabric-Mod fuer **Minecraft 1.21.1** rund ums Schmieden, einzigartige Ausruestung, Klassen, Skills und Quests. Eigenstaendige, von Schmiede-MMORPGs inspirierte Umsetzung mit eigenen Namen, Texten und Werten.

**Status:** Entwurf, Phase 5 (Prototyp).

## Download

Fertige Jar-Dateien liegen unter [Releases](../../releases). Voraussetzungen: Minecraft 1.21.1, Fabric Loader ab 0.16.14, Fabric API, Java 21.

## Spielablauf

1. **Schmiedestation** herstellen (Handwerkstisch: oben `III` Eisenbarren, Mitte `_S_`, unten `SSS`, S = glatter Stein). Sie sieht aus wie ein Amboss und wird wie einer gedreht.
2. Rechtsklick oeffnet das Schmiedemenue.
3. **Material waehlen:** Stein (nur Schwert), Eisen, Gold, Diamant, Netherit oder Aurorit.
4. **Ausruestungsart waehlen:** Schwert, Helm, Brustplatte, Hose oder Stiefel (Symbole unter dem Minigame). Kosten stehen am Materialknopf; das Schwert braucht zusaetzlich 1 Stock.
5. **Minigame:** 5 Hammerschlaege. Ein Marker wandert ueber die Leiste, schlage zu (Knopf oder Leertaste), wenn er in der Zielzone ist. Perfekt 3, Gut 2, Okay 1, Daneben 0 Punkte.
6. Rechts stehen die **Chancen** auf Normal, Selten, Episch, Legendaer, Einzigartig. Sie steigen mit Treffern, Schmiedelevel und Material.
7. **Enthuellung:** Nach dem Minigame kommt das Stueck nicht ins Inventar. Auf dem Amboss laeuft eine Animation, die mit der Seltenheit groesser wird (Funken, farbige Lichtstrahlen, Blitze). Danach schwebt das Stueck ueber dem Amboss. Rechtsklick sammelt es ein. Die Blitze sind rein kosmetisch.
8. Jedes Schmieden gibt **Schmiede-Erfahrung**. Mit steigendem Level (bis 20) werden die Zielzonen breiter und die Chancen besser.

## Klassen

Mit dem **Erweckungsstein** (Handwerkstisch: `_L_` / `GDG` / `_L_` mit L = Lapislazuli, G = Goldbarren, D = Diamant) erweckst du eine zufaellige Klasse. Wer schon eine Klasse hat, muss den Stein innerhalb von 10 Sekunden ein zweites Mal benutzen; die alte Klasse geht verloren, die neue ist garantiert eine andere. Keine Klasse ist eine reine Verbesserung: jede hat Staerken und Schwaechen (Attribute) und teils einen eigenen Passiv-Effekt.

| Seltenheit | Chance | Klassen |
|---|---|---|
| Normal | 52 % | Krieger, Bergmann, Spaeher, Schmied |
| Selten | 28 % | Waechter, Assassine, Entdecker |
| Episch | 14 % | Berserker, Duellant |
| Legendaer | 5 % | Titan, Phoenix |
| Einzigartig | 1 % | Essenhueter, Leerenwandler |

Die Chance gilt pro Stufe, innerhalb der Stufe sind alle Klassen gleich wahrscheinlich. Klassen wirken ueber Attribut-Modifikatoren (beim Beitritt, Respawn und Dimensionswechsel neu angewendet) und Server-Ereignisse. Schmied und Essenhueter verbessern zusaetzlich das Schmieden (effektive Schmiedelevel und Erfahrung).

Befehle: `/forgeclass` (deine Klasse), `/forgeclass list` (alle Klassen mit Chance), fuer Operatoren `/forgeclass set <Spieler> <Klasse>` und `/forgeclass reset <Spieler>` zum Testen.

## Traits

**Waffen** (wirken bei jedem Treffer): Glut, Lebensraub, Frostbiss, Verfall, Wucht, Schwung, Schutzschild.

**Ruestung** (dauerhafte Attributboni): Vitalitaet, Flinkheit, Bollwerk, Zaehigkeit, Kraft.

Anzahl und Stufe richten sich nach der Seltenheit: Normal 1x Stufe I, Selten 1x II, Episch 2x III, Legendaer 3x IV, Einzigartig 4x V.

## Aurorit

Eigenes Erz in der Overworld, tief unter der Erde (Y -64 bis -8) und deutlich seltener als Diamanten. Es braucht mindestens eine Diamantspitzhacke. Rohes Aurorit wird im Ofen oder Schmelzofen zum Barren. Daraus entstehen im Schmiedemenue das **Aurorit-Schwert** und die **Aurorit-Ruestung**, beides ueber Netherit. Das Erz erscheint nur in neu generierten Chunks.

## Technik

- Menue, Treffer, Seltenheit, Traits, Gegenstand, Erfahrung und Klassen werden nur auf dem Server berechnet.
- Klasse und Schmiedelevel pro Spieler als Welt-Zustand (ueberlebt Tod, Dimensionswechsel und Neustart).
- Alle Zahlen zum Balancing stehen in `BalanceConfig`, die Klassen in `ForgeClass`.
- Die Phoenix-Abklingzeit wird nur im Speicher gehalten (setzt sich beim Serverneustart zurueck).

## Selbst bauen

```
gradle build
```

Java 21 und Gradle 8.12. Die Jar liegt unter `build/libs/`. Auf GitHub baut der Workflow `build` bei jedem Push; ein Tag wie `v0.5.0` erzeugt zusaetzlich ein Release mit Jar.

## Lizenz

Vorerst `All-Rights-Reserved` (in `fabric.mod.json`).

## Naechste Schritte

- Skills (aktive Faehigkeiten mit Tasten, Abklingzeit, Kosten), passend zu den Klassen.
- Reparieren und Verbessern von Ausruestung.
