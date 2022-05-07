# ier_hf
Csoportos szekvenciális árverés és kommmunikáció

Ágens eldönti, hogy közvetlenül tudja-e célba juttatni a csomagot.
Igen, akkor annak az útnak az idejével licitál
Nem, akkor futtat egy 'körbekérdezős' protokollt, hogy ki tudná őt kisegíteni. (Ez mehetne tovább rekúrzívan)

Az így keletkező összidővel licitál a kezdeményező ágens.
 
Összes ágensre ugyanez, a kegkisebb idő nyer?
 
Egész rendszer célja?

MIN(leghosszab rendelés kiszállítási ideje) = MINIMAX
 
MIN(megadható km/perc kritérium) = MINIAVE
 
**MIN(szum kiszállítási idő) = MINISUM**

Ágensek tudása:

Minden tud mindenkiről - direktbe kerese másik ágenseket -  Master-Slave

Körbekérdezés - Mindenkitól - Vállalhozási hálók - A többi ágensről csak azt tudja hogy létezik

**Körbekérdezés - Klaszterben - Vállalkozás hálók - A többi ágensnek tudja a helyzetét (és sebességét? és foglaltságát?)**

Körbekérdezés protokollja

**Alárverés: Mindenkit megkérdez, hogy mennyi idő alatt vinné el. - ugyanaz mint a 'fő' árverés**

Vállalhozási hálók 

Ágens memóriája

Honnan hova. FIFO sorrendiség, nem felülbírálható - MINISUM

Felülbírálás

**Árverésre lehessen bocsájtani az éppen teljesítés alatt álló rendelést, ha minisum szempontjából egy újonnan érkezett rendeléssel jobb eredmény száítható.**

Ilyet ne lehessen. Ha valami megynyert bármi is van, nem lehet változtatni. 



 
