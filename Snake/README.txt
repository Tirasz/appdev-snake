A Projekt indítása előtt a **\Snake\snake-core\src\main\resources mappában található db.properties fájlban át kell írni a .db fájl elérési útvonalát. 
Amennyiben szükséges, a Tomcat szervert is újra kell konfigurálni.

Megjegyzések:
	- Az adatbázist elérő metódusokban bennehagytam egy 1mp.-es sleepet, így jobban látható a szálkezelés az asztali alkalmazásban. 
		Emiatt sajnos a webes felületen is hoszabbak a töltési idők.
	- Amennyiben a projekt elérési útvonalában található space, az asztali alkalmazás nem fogja tudni betölteni a CSS -ben importált custom fontot. 
		Ez egy ismert bug(/feature?): https://stackoverflow.com/questions/33973921/javafx-font-face-css-error-loadstylesheetunprivileged/41753098
