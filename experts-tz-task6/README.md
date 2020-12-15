# Fejtörők 6-os feladat

## Feladatleírás

A hdr.ipv4.dstAddr mező mely táblákban szerepel kulcsként a lekérdezéseknél?<br />
Teszt kimenet: ipv4_lpm (pl. nodeId=4122)

## Megvalósítás

A megvalósítás során a Gremlin ismeretem finomítása volt a cél, ezáltal három megoldást készítettem el a feladatra, melyekhez különböző Gremlin tudást használtam fel. Bővebben csak az első és utolsó megoldásomat fejtem ki.

### 1. Megoldás

1. Kigyűjtöm az összes **TableDeclarationContext** `class`-szal rendelkező csúcsnak a `nodeId`-ját
1. Az 1. lépésben kigyűjtött csúcsokból kiindulva megkeresem a **KeyElementContext** `class`-szal rendelkező csúcsokat és azokból az **expression** élen lemegyek a következő csúcsig.
1. A 2. lépésben kigyűjtött csúcsokból meegkeresem a **TerminalNodeImpl** `class`-szal rendelkező csúcsnak a `nodeId`-ját
1. A 3. lépésben kigyűjtött csúcsoknak lekérdezem a `value` értékét is és egy kulcs-érték pár halmazt hozok létre (kulcs: `value`, érték: `nodeId`) úgy, hogy azokat a csúcsokat ignorálom melyeknél a `value` értéke "." .
1. A 4. lépésben előállított halmazra egy ellenőrzést hajtok végre, hogy illeszkedik-e a **hdr.ipv4.dstAddr** értékre. Amennyiben illeszkedik, akkor az 1. lépésben kigyűjött `nodeId`-t hozzáadom egy listához.
1. Az 5. lépésben használt listákban megtalálhatóak azon **TableDeclarationContext** `nodeId` mezői, melyeket keresek. Ezek után ezekből a csúcsokból kiindulok a **name** élen és lemegyek **TerminalNodeImpl** `class`-szal rendelkező csúcsig, melynek kiveszem a `value` mezőjét.

### 3. Megoldás

1. Kigyűjtöm a **KeyElementContext** `class`-szal rendelkező csúcsok **TerminalNodeImpl** `class`-szal rendelkező csúcsinak a `value` és `nodeId` mezőjét.
1. Az 1. lépésben kigyűjtött adatokból az 1. Megoldáshoz hasonlóan kulcs-érték pár halmazt csinálok, majd egy ellenőrzést hajtok végre rajta és kigyűjtöm a megfelelő **KeyElementContext** `nodeId`-kat.
1. A **TableDeclarationContext** `class`-szal rendelkező csúcsokból kiszűröm azokat melyek relkeznek olyan gyerek csúccsal, ami **KeyElementContext** `class`-szal rendelkezik és a `nodeId`-ja szerepel a 2. lépésben kigyűjtött listában. Majd a megmaradt **TableDeclarationContext** csúcsoknak kikeresem a nevét az 1. Megoldás 6. lépéséhez hasonlóan.

## Általánosítás

1. Paraméterként legyen megadható a fejléc (fent: ipv4), illetve a mező (fent: dstAddr) neve.
    - A 3. Megoldásban implementálásra került.
1. A kimenetnél adja vissza azt is, hogy milyen módon szerepel a kulcs a lookup során (a fenti példánál lpm), illetve, hogy az adott kulcs mellett milyen más kulcsok szerepelnek még ugyanezen táblánál.
    - Minden szükséges adat rendelkezésre áll. Az implementáció még nem készült el.
