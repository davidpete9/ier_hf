# ier_hf
Csoportos szekvenciális árverés és kommmunikáció

1. ágens eldönti, hogy közvetlenül tudja-e célba juttatni a csomagot.

 ha igen, akkor annak az útnak az idejével licitál
 ha nem, akkor futtat egy 'körbekérdezős' protokollt, hogy ki tudná őt kisegíteni. 
 Az így kelentkező összidővel licitál a kezdeményező ágens.
 
 összes ágensre ugyanez, a kegkisebb idő nyer?
 
 Egész rendszer célja:
 
 MIN(km/perc kritérium)
 
 MIN(szum kiszállítási idő)
 
