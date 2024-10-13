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
```
si vous vous demander ce qu'est le **controller package**, c'est juste le dossier où la classe controller sera déclarer

par exemple si votre controller est dans le package mg.itu.mycontroller

votre package sera mg.itu.mycontroller

les principal classe que vous allez utilisé pour votre projet java EE sera les annotations

- Controller_Y
- Url
- Get
- Post
- Param
- RestAPI

### Utilisation

#### Controller_Y

L'annotation *Controller_Y* permet de faire savoir au frameWork que le 

- Vos controllers doit etre annoter par l'annotation:
        Controller_Y
- package: mg.itu.prom16.annotation

