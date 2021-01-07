# Il Paroliere - Applicazione lato client

Porzione del progetto "Il Paroliere" lato client.  
L'applicazione lato client si compone principalmente della GUI, sviluppata con OpenJFX 15 e dei moduli core per
la comunicazione con il server.

## Il menu principale
Il menu principale consiste di una schermata con 4 pulsanti:

* Login: permette di effettuare il login di un utente che si è già registrato tramite la piattaforma e
permette inoltre di richedere una password dimenticata e la cancellazione del proprio profilo
* Nuovo giocatore: permette la registrazione di un nuovo profilo utente
* Opzioni: la porzione dell'applicazione dove è possibile personalizzare alcune opzioni, come la risoluzine
e l'aspect ratio, la lingua dell'interfaccia e la configurazione dei server

![mainMenu](/readmeimg/mainmenu.png)

## La schermata home

Nella schermata principale si trovano:

* Immagine del profilo e nome utente, in alto a sinistra. Cliccando sull'immagine è possibile aprire la 
schermata di opzioni del profilo, dove è possibile personalizzare la propria immagine, cambiare alcune informazioni 
e richiedere cambio di nickname e cambio di password.
* In alto a destra è presente un menu minimale ad icone, ogni icona, sebbene intuitiva, descrive la propria
funzione in un tooltip che compare sostando con il mouse sopra di essa per 1 secondo. L'icona più a destra
apre il menù a tendina da cui è possibile effettuare il logout e uscire dall'applicazione.
* La parte centrale è costituita da una serie di tab, la prima di cui mostra la lista delle lobby di gioco
disponibili e dove è possibile creare/joinare suddette lobby. Le altre tab sono tutte relative alle statistiche
di gioco.

![home1](/readmeimg/home1.png)

![home2](/readmeimg/home2.png)

![home3](/readmeimg/home3.png)

## La schermata di gioco
La schermata di gioco è suddivisa in:

* Porzione superiore: in questo spazio vengono visualizzati il numero del match in corso, il timer del match e
il proprio punteggio di gioco (cumulativo). Subito al di sotto si trova un campo di testo che viene popolato
man mano che si compongono parole sulla griglia di gioco con 2 pulsanti laterali che consentono rispettivamente
l'inserimento di una parola nella lista e la deselezione rapida di tutti i dadi selezionati.
* Porzione inferiore: a sinistra viene visualizzato un brevissimo reminder del tutorial di gioco (comandi),
al centro c'è la vera e propria griglia di gioco e a destra vengono visualizzate la lista delle parole individuate
e il riassunto dei punteggi per ciascun giocatore.

![loading](/readmeimg/screen.gif)

![match](/readmeimg/match.gif)

