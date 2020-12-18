# Fejtörők 2-es feladat

## Feladatleírás
A kód mely soraiban kezdődnek olyan elágazások, melyekben az `hdr.ipv4.isValid()` kifejezés szerepel?

Teszt kimenet: 538. sor (pl. nodeId=4227)

## Megvalósítás

1. Megkeresem az összes ConditionalStatementContext osztályú csúcsot.
2. Ellenőrzöm, hogy ez egy "if-es" kifejezés.
3. Ellenőrzöm, hogy ez egy megfelelő kifejezés:
   * `hdr.ipv4` megkeresése
   * `hdr.ipv4.isValid()` megkeresése
   * Mindkettő egy `and()` parancscsal teljesíthető, és ezt az if-es csúcsnál két szinttel lejjebb található `ExpressionContext` osztályú node-ból indul.
   * Az `and()`-en belül addig lépünk a kifelé vezető csúcson, amíg a keresett `value`-t meg nem találtuk
4. Végül visszadja a talált csúcsok sorának a számát

## Általánosítás
Általánosítás: Paraméterként legyen megadható a fejléc (fent: ipv4) neve. Az eredményben szerepeljen melyik kontrolhoz tartozik az elágazás (a basic.p4-nél a fenti példa esetén ez a MyIngress).
