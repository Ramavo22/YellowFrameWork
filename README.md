# YellowFrameWork

## Description

Creation d'un framework Java constitué de plusieur sprint

## GUIDE

- Vos controllers doit etre annoter par l'annotation:
        Controller_Y
- package: mg.itu.prom16.annotation

- Dans le web.xml il faut que vous declarer:

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
