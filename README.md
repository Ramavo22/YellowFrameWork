# YellowFrameWork

## Description

Creation d'un framework Java constitué de plusieur sprint

## GUIDE

### Introduction

pour votre projet web, il faut que vous importer le jar du framework, YellowFrameWork dans le lib du projet. Aussi il devra être présent dans le webApp (WEB-INF/lib)

Puis dans votre web.xml vous devez déclarer le servlet FrontController

```xml
<servlet>
    <servlet-name>FrontController</servlet-name>
    <servlet-class>mg.itu.prom16.controller.FrontController</servlet-class>
    <init-param>
        <param-name>controllerName</param-name>
        <param-value><!-- votre controller package --></param-value>
    </init-param>
</servlet>
<servlet-mapping>
    <servlet-name>FrontController</servlet-name>
    <url-pattern>/</url-pattern>
</servlet-mapping>
```

si vous vous demander ce qu'est le **controller package**, c'est juste le dossier où la classe controller sera déclarer. **Se réferer ci dessous**

par exemple si votre controller est dans le package mg.itu.mycontroller

votre package sera mg.itu.mycontroller

les principal classe que vous allez utilisé pour votre projet java EE sera les annotations

- *Controller_Y*
- ModelViewY
- *Url*
- *Get* et *Post*
- *Param*
- *RestAPI*


### Utilisation

#### 1. Controller_Y

L'annotation *Controller_Y* permet de faire savoir au frameWork que le

- l'annotation se fait sur la classe. les classes annotée par l'annotation sera les controlleur de votre projets

- il doit être dans le package que vous allez déclarer dans le web xml. Se référencer si dessus

- Vos controllers doit etre annoter par l'annotation: *Controller_Y*

- les classes annoté par *Controller_Y* doit être dans les même packages;

Exemple

```java
@Controller_Y
public class MonController{
    // votre logique
}
```

### ModelViewY

ModelViewY est la classe qui interagit entre le client est le serveur.

elle a comme attribut:
- url (String): utiliser pour la redirection vers un autre méthode ou à une page
- data (map<String, Object>): les data à envoyer dans la pages

Fonction Important: la fonction `addObject(key,data)` permet d'ajouter un donnée 
Exemple: 

```java

public ModelView_Y getIndex(){
    /*
     * Votre logique
    */
    ModelView_Y modelViewY = new ModelView_Y("index.jsp");
    modelViewY.addObject ("message", "Ceci est un message!");
    modelViewY.addObject ("data",myObject);
    ...
    
    return modelViewY;
}
```

les donnéer ajouter par `addObject` seront disponible sur jsp avec `request.getAttribute(key)`

Pour la redirection vers un autre controller il faut preciser la methode (Verb) de la fonction en ajoutant comme donnée dans ModelView_Y de la manière suivant:
```java
public ModelView_Y getIndex(){
    /*
     * Votre logique
    */
    ModelView_Y modelViewY = new ModelView_Y("lien/controller");
    modelViewY.addObject ("message", "Ceci est un message!");
    modelViewY.addObject ("data",myObject);
    ...

    // if faut ajouter ceci...
    modelViewY.addObject("METHOD",VERB) // GET, POST
    
    return modelViewY;
}
``` 

#### 2. Url

L'annotation *Url* permet de associer une action à un Url donné

- L'annotation se fait sur les actions (fonction)
- l'action doit être dans un controller
- déclarez l'url dans l'attribut name de l'url

Exemple

```java
@Url(name = "monurl")
public ModelView_Y methode(){
    // votre logique
}
```

 **NB:** <span style = "color:orange"> L'url ne dois pas avoir de slash "/".<span>


#### 3. Get & Post

Les annotations *Get* et *Post* permet d'initialisé la méthode de l'action

- L'annotation se fait sur les actions (fonction)
- les actions annotées doit être aussi annoté par l'annotation *Url* sinon, l'action ne sera pas vu.

Exemple

```java
@Url(name = "monUrl")
@Post
public ModelView_Y methode(){
    // votre logique
}

@Url(name = "monUrl")
@Get
public ModelView_Y methode(){
    // votre logique
}
```

**NB**: Si une action n'est pas annotée par une methode (Get ou Post), La méthode par defaut sera Get

#### 4. Param

L'annoation *Param* permet de mapper l'argument d'une action qui est associé à un formulaire

- L'annotation se fait sur l'argument de l'action 
- mettre ,sur l'attribut name de l'annotation, le nom du champs destiné pour l'object

Exemple

Sur une page html on a

```html
<form action ="testform" methode = "post"> 
    <input type="text" name="anarana">
    <input type ="number" name = "taona">
    <button type="submit">Valider </button>
</form>
```

Dans votre controller, pour traiter les donnée, vous avez:

```java
@Url(name = "testform")
@Post
public ModelView_Y formulaire(@Param(name = "anarana") String nom, @Param(name = "taona") int age){

    // votre logique 
}
```

- Il est aussi possible d'associer un object Personnalisé sur en tant que argument

Exemple,

Nous avons une classe suivant:

```java
public class Personne{
    String nom
    int age;
}
```

dans le controlleur, on peut avoir l'action suivant

```java
@Url(name = "testform")
@Post
public ModelView_Y formulaire(@Param("NewPerson") Personne personne){

    // votre logique 
}
```

et dans le formulaire, les valeur de l'attribut name sera légèrement modifier

```html
<form action ="testform" methode = "post"> 
    <input type="text" name="NewPerson.nom">
    <input type ="number" name = "NewPerson.age">
    <button type="submit">Valider </button>
</form>
```

- L'annotation *Param* n'est pas obligatoire, la valeur sera juste remplacer par le nom de argument