# Wiki 1-es feladat

## Task
`hdr.ethernet.dstAddr` mező megkeresése értékadás jobb/bal oldalán.
Sorszám kiíratása.

## Megvalósítás

1. AssignmentOrMethodCallStatementContext osztályú node-ok megkeresése (ezek jelzik, hogy értékadás következik)
2. Bal oldali értékadás akkor van, ha `lvalue` élen megyünk tovább a gráfban
3. Jobb oldali értékadás akkor van, ha `expression` élen megyünk tovább a gráfban
4. `TerminalNodeImpl` osztályú `dstAddr` értékű node-ok megkeresése

## Továbbfejlesztés
Paraméterként legyen megadható a fejléc (fent: ethernet), illetve a mező (fent: dstAddr) neve. A kimenetnél adja vissza melyik oldalon szerepelt, illetve hogy az értékadás másik oldalán mi volt a kifejezés.
