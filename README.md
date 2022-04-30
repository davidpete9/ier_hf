# ier_hf
Csoportos szekvenciális árverés és kommmunikáció

Ágens eldönti, hogy közvetlenül tudja-e célba juttatni a csomagot.
Igen, akkor annak az útnak az idejével licitál
Nem, akkor futtat egy 'körbekérdezős' protokollt, hogy ki tudná őt kisegíteni. (Ez mehetne tovább rekúrzívan)

Az így keletkező összidővel licitál a kezdeményező ágens.
 
Összes ágensre ugyanez, a kegkisebb idő nyer?
 
Egész rendszer célja?

MIN(leghosszab rendelés kiszállítási ideje) = MINIMAX
 
MIN(km/perc kritérium) = MINIAVE
 
MIN(szum kiszállítási idő) = MINISUM
 
