Megvalósítás:
    Első feltételként ellenőrzöm, hogy értékadásról van-e szó, tehát 'AssignmentOrMethodCallStatementContext' osztályú node leszármazottja-e a keresett node.
    Ha igen, először balra indulok fában, míg 'LValueContext' osztályig érek, ami bal oldali értékadást jelöl. Ez alatt a 'dstAddr' értékű mezőt megkeresve megtalálunk egy bal oldali értékadást.
    Jobb oldali értékadás esetén ugyanezek a lépések tükörirányban, 'LValueContext' helyett 'ExpressionContext' osztállyal.

Paraméterezés:
    "Általánosítás: Paraméterként legyen megadható a fejléc (fent: ethernet), illetve a mező (fent: dstAddr) neve. A kimenetnél adja vissza melyik oldalon szerepelt, illetve hogy az értékadás másik oldalán mi volt a kifejezés."
    A későbbiekben az analízis paraméterezése lesz a cél, illetve az új programverzióba való integrálás.