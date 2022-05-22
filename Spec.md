Elosztott rendszerek előzetes specifikáció
Tagok:
Gál Botond (HUQ0EJ)
Kalmár László Dániel (I7BEID)
Pete Dávid (GVJ529)
Feladat:
Rövid leírás:
Bizonyos termékek kiszállítására alkalmas drón flotta megtervezése.
A termékek különböző tulajdonságokkal rendelkeznek, ami miatt nem minden drón
alkalmas minden termék kiszállítására. A kiszállítási pontok különböző távolságokra
vannak a raktáraktól. Ezen felül a drónok különböző távot tudnak egy töltéssel
megtenni, és különböző terhet bírnak el.
Lehetséges protokollok:
● Kombinatorikus árverés - lehet licitálni rendelések csoportjára
● Vállalkozási hálók - Drónok tovább oszthatják maguk között a feladatot
(Valamelyik elviszi egy lerakatig, majd onnan valaki továbbviszi, ha
szükséges)
Környezet:
● Egy központi raktár, az összes termék ott megtalálható, a raktár
rendszerezésével nem, a drónok töltésének csak az idő igényével
foglalkozunk.
● Pár (~ 2 db) regionális lerakat, csak olyan termékek találhatóak meg ott,
melyeket előzőleg, egy drón a központi raktárból szállított oda
● Kiszállítási pont (falu), a rendelések célállomása, majd GUI-ról választható. kb
6-8 db.
Ágensek:
Drónok, különböző paraméterekkel (hatótáv, elbírt áru mennyisége, sebesség)
● pl. (10 km, 1 kg, 20 km/h)
○ Vagyis 10 km megtétele után mindenképpen fel kell tölteni. A töltési idő
arányos az előző töltés óta megtett kilométerrel.
Árvereztető ágens (Ha ezt a kommunikációs protokollt használjuk)
