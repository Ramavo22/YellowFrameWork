* Sprint 0 :
    - Tous les requetes demandees par l'utilisateur doit entrer en premier dans le frontController

* Sprint 1 :
    - on doit lister tous les controllers que le developpeur a annote de @Controller

* Sprint 2 :
    - Montrer la classe et la methode corespondant a l'url tapee

* Sprint 3 :
   - Execution de la methode appelee dans l'url 
        - type de retour : Spring

* Sprint 4 :
    - Passer des informations du controller vers les vues
        - si le type de retour est String : on retourne tout simplement
        - si c'est de type ModelAndView : on recupere la cle et les valeurs correspondantes 
     
* Sprint 5 : 
     - Gestion des erreurs :
         * si les packages declares sont vides
         * si un url est duplique 
         * si une methde du developpeur retourne autre que String ou ModelAndView

* Sprint 6 : 
    * Envoyer des donnees du vue vers le controller
         - utilisation de l'annotation @RequestParam pour matcher les valeurs du formulaire avec les parametres de la fonction 
         - Les parametres sont de type natifs

* Sprint 7 :
      * Envoyer des donnees du vue vers le controller
         - utilisation de l'annotation @RequestParam pour matcher les valeurs du formulaire avec les parametres de la fonction 
         - Les parametres sont de types Objets ou natifs en meme temps


    
* Sprint 8 : 
     * gestion des sessions : 
        - utiliser l'attribut MySession pour stocker la valeur de notre session
        - Au niveau classe : attribuer MySession comme fields de votre classe
        - Au niveau methode : Attribuer le comme argument de votre fonction 

* Sprint 9 : Response API 
     * Annoter la fonction avec @RestApi pour convertir le retour de votre fonction en JSON

* Sprint 10 : 
     * nouvelle annotation @Url pour attribuer la valeur de la methode a appeler
     * annotation du verbe d'action 
     * on peut avoir le meme url mais avec des methodes d'action differentes

* Sprint 11 :
     * Au lieu de throws Exception seulement , om montre l'erreur avec une page jsp personnalise avec Printwriter

* Sprint 12 :
     * Traitement de fichier : Si vous vouler faire un input de type date, passer en argument de la fonction la classe Multipart et setter les valeurs dedans

* Sprint 13:
     * Validation : au niveau des attibuts d'une methode, utilisation de divers annotations pour verifie le valeurs, si jamais une valeur enfreint les methodes de validation on renvoie une exception 

* Sprint 14:
     * Suite de la validation : mais en plus on renvoie l'utilisateur vers le formulaire de depart avec les messages d'erreurs et les valeurs saisies

* Sprint 15:
     * Autorisation : au niveau des methodes , on verifie si une methode necessite une authentification avant le login et si une methode est specifiee par un utilisateur specifique


* Sprint 16:
     * Autorisation suite: au niveau des classes , on verifie si une classe necessite une authentification avant le login et si une methode est specifiee par un utilisateur specifique 

