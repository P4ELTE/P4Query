# Fejtörők 4-es feladat

## Feladatleírás

Milyen kulcsokra illesztünk hdr.ethernet.etherType mezőt select kifejezésben?<br />
Teszt kimenet:
	- TYPE_IPV4 (pl. nodeId=3723)
	- default (pl. nodeId=3733)

## Megvalósítás

Azokat a csúcsokat gyűjtük ki, amelyeknek a class-a `SelectExpressionContext`, és az `ExpressionList` élen keresztül olyan csúcsba jutunk melyből a `TerminalNodeImpl` class-ú csúcsokig eljutva a value mezője rendre megegyezik a hdr, ethernet, és etherType értékekkel.
Az így megkapott csúcshalmazból a `SelectCaseList` éleken továbbhaladva kikeressük a `SelectCaseContext` csúcsokat, amelyekből elérhetőek lesznek a `KeySetExpression` élek, amelyek már a feladatban keresett `TerminalNodeImpl` csúcsokat tartalmazzák. Ezek value értékei lesznek a keresett mezők. <br />
A feladat általánosítása is megvalósításra került, a függvénynek paraméterben átadott fejléc és mező értékeknek megfelelően működik.