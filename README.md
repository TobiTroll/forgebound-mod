# Forgebound

Fabric-Mod fuer **Minecraft 1.21.1** rund ums Schmieden, einzigartige Ausruestung, Klassen, Skills und Quests. Eigenstaendige, von Schmiede-MMORPGs inspirierte Umsetzung mit eigenen Namen, Texten und Werten.

**Status:** Entwurf, Phase 7 (Prototyp).

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

## Reparieren und Verbessern

**Schleichen + Rechtsklick** auf die Schmiedestation mit einem geschmiedeten Stueck in der Hand oeffnet das Reparatur- und Verbesserungsmenue (mit Knopf zurueck zum Schmieden).

- **Reparieren:** stellt die volle Haltbarkeit her. Kosten: Anteil des Schadens mal Herstellungskosten (mindestens 1 Stueck des Materials).
- **Verbessern:** bis zu 3 Stufen, jede gibt +1 Angriffsschaden (Schwert) bzw. +1 Ruestung (Ruestung). Stufe n braucht Schmiedelevel 4 mal n und halbe Herstellungskosten mal n. Die Stufe steht im Tooltip.
- **Klassenrabatt:** Schmied 25 %, Schmiedemeister und Essenhueter 50 % guenstiger.

## Benannte Relikt-Waffen

Geschmiedete **Schwerter** koennen zu einem benannten Relikt mit eigener Geschichte und besonderer Faehigkeit werden: Einzigartige Schwerter immer, legendaere mit 20 % Chance. Es gibt sieben (alle selbst erfunden): Aschenfeder (Glutstoss), Donnerzahn (Blitz und Zusatzschaden), Nebelspalter (jeder 5. Treffer blendet und trifft hart), Seelendurst (heilt bei Treffern und Kills), Frostzunge (verlangsamt und vereist), Koenigsfall (Zusatzschaden nach maximalem Leben) und Windschneide (Temposchub, schleudert Ziele manchmal hoch). Name, dreizeilige Geschichte und Faehigkeit stehen im Tooltip.

## Charakter und Stats

Taste **K** oeffnet das Charaktermenue. Du bekommst **Charakter-Erfahrung** fuer das Toeten feindlicher Mobs (Tiere zaehlen nicht, Bosse geben viel) und fuers Schmieden. Pro Level (bis 50) gibt es 2 **Stat-Punkte**, die du frei verteilst (je Stat maximal 100):

| Stat | Wirkung pro Punkt |
|---|---|
| Staerke | +0,05 Angriffsschaden |
| Durability | +0,3 max. Leben, +0,05 Ruestung |
| Speed | +0,3 % Lauftempo |
| Mana | +5 max. Mana, +0,05 Mana pro Sekunde |
| Geschick | +0,3 % Angriffstempo |
| Widerstand | +0,5 % Rueckstossresistenz |
| Regeneration | +0,01 Leben pro Sekunde |
| Glueck | +0,1 Glueck (wirkt auf Beute) |

**Mana** (Grundwert 30, +1 pro Sekunde) wird als blaue Leiste ueber der Hungeranzeige gezeigt. Es ist fuer die spaeteren Skills vorbereitet; Skills gibt es noch nicht.

## Klassen

Mit dem **Erweckungsstein** (Handwerkstisch: `_L_` / `GDG` / `_L_` mit L = Lapislazuli, G = Goldbarren, D = Diamant) erweckst du eine zufaellige Klasse. Wer schon eine Klasse hat, muss den Stein innerhalb von 10 Sekunden ein zweites Mal benutzen; die alte Klasse geht verloren, die neue ist garantiert eine andere. Keine Klasse ist eine reine Verbesserung: jede hat Staerken und Schwaechen und teils einen eigenen Passiv-Effekt.

| Seltenheit | Chance | Klassen |
|---|---|---|
| Normal | 46 % | Krieger, Bergmann, Spaeher, Schmied |
| Selten | 28 % | Waechter, Assassine, Entdecker |
| Episch | 16 % | Berserker, Duellant |
| Legendaer | 7 % | Titan, Phoenix, Schmiedemeister |
| Einzigartig | 3 % | Essenhueter, Leerenwandler |

Die Chance gilt pro Stufe, innerhalb der Stufe sind alle Klassen gleich wahrscheinlich. Phoenix und Essenhueter sind immun gegen Feuer- und Lavaschaden (serverseitig durchgesetzt). Schmied, Schmiedemeister und Essenhueter verbessern das Schmieden (effektive Schmiedelevel, Erfahrung) und verbilligen Reparaturen und Verbesserungen.

Befehle: `/forgeclass` (deine Klasse), `/forgeclass list` (alle Klassen mit Chance), fuer Operatoren `/forgeclass set <Spieler> <Klasse>` und `/forgeclass reset <Spieler>`.

## Operator-Menue

`/forge` (Berechtigungsstufe 2, z. B. `/op`) oeffnet ein Menue, in dem du Klasse, Schmiedelevel, Charakter-Level, freie Punkte, alle acht Stats und Mana aendern oder alles zuruecksetzen kannst (Umschalttaste: Stats um 10). Der Server prueft die Berechtigung bei jeder Aktion. Das Menue wirkt auf den eigenen Spieler.

## Traits

**Waffen** (wirken bei jedem Treffer): Glut, Lebensraub, Frostbiss, Verfall, Wucht, Schwung, Schutzschild.

**Ruestung** (dauerhafte Attributboni): Vitalitaet, Flinkheit, Bollwerk, Zaehigkeit, Kraft.

Anzahl und Stufe richten sich nach der Seltenheit: Normal 1x Stufe I, Selten 1x II, Episch 2x III, Legendaer 3x IV, Einzigartig 4x V.

## Aurorit

Eigenes Erz in der Overworld, tief unter der Erde (Y -64 bis -8) und deutlich seltener als Diamanten. Es braucht mindestens eine Diamantspitzhacke. Rohes Aurorit wird im Ofen oder Schmelzofen zum Barren. Daraus entstehen im Schmiedemenue das **Aurorit-Schwert** und die **Aurorit-Ruestung**, beides ueber Netherit. Das Erz erscheint nur in neu generierten Chunks.

## Technik

- Menue, Treffer, Seltenheit, Traits, Relikte, Reparatur, Gegenstand, Erfahrung, Klassen, Charakter und Mana werden nur auf dem Server berechnet; der Client bekommt Anzeigedaten per Netzwerknachricht und schickt nur Wuensche (Stat-Punkte, Operator-Aktionen).
- Klasse, Schmiedelevel und Charakterdaten pro Spieler als Welt-Zustand (ueberlebt Tod, Dimensionswechsel und Neustart).
- Alle Zahlen zum Balancing stehen in `BalanceConfig`, die Klassen in `ForgeClass`, die Relikte in `Relic`.
- Die Phoenix-Abklingzeit wird nur im Speicher gehalten (setzt sich beim Serverneustart zurueck).

## Selbst bauen

```
gradle build
```

Java 21 und Gradle 8.12. Die Jar liegt unter `build/libs/`. Auf GitHub baut der Workflow `build` bei jedem Push; ein Tag wie `v0.7.0` erzeugt zusaetzlich ein Release mit Jar.

## Lizenz

Vorerst `All-Rights-Reserved` (in `fabric.mod.json`).

## Naechste Schritte

- Skills (aktive Faehigkeiten mit Tasten, Abklingzeit, Manakosten), passend zu den Klassen.
- Quests und Belohnungen.
