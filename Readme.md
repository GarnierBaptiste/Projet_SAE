# Smart Grid Server

## Nom du groupe : DEBEAULIEU Anatole, GARNIER Baptiste

## Lien du dépot Github : 
https://github.com/GarnierBaptiste/Projet_SAE

Nous avons crée ce dépot Github que pour le compte rendu, donc les commit ne montrent pas l'avancement de la réalisation du projet.

## Partie Required

### Get : /grids List all grid IDs

Renvoie la liste des IDs des éléments de la classe Grid en utilisant la route Get /grids.

Ce que nous renvoie notre fonction :

![Required Get Grid Response](Annexe/R1R.png)

Ce que la fonction est censée nous renvoyer :

![Required Get Grid Example](Annexe/R1E.png)

### Get : /grid/{id} Get grid details

Renvoie les détail de la Grid dont l'id est rentré en paramètre en utilisant la route Get /grid/{id}.

Ce que nous renvoie notre fonction en mettant en paramère l'id à 1 :

![Required Get Grid Details](Annexe/R2R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 :

![Required Get Grid Details Example](Annexe/R2E.png)

### Get : /persons List all person IDs

Renvoie la liste des IDs des éléments de la classe Person en utilisant la route Get /persons.

Ce que nous renvoie notre fonction :

![Required Get Persons Response](Annexe/R3R.png)

Ce que la fonction est censée nous renvoyer :

![Required Get Persons Example](Annexe/R3E.png)

### Get : /person/{id} Get person by IDs

Renvoie les détails d'un objet de la table Person dont l'id est rentré en paramètre en utilisant la route Get /person/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 1 :

![Required Get Person details Response](Annexe/R4R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 :

![Required Get Person details Example](Annexe/R4E.png)

### Get : /measurement/{id} Get measurement definition

Renvoie les détails d'un objet de la classe Measurement dont l'id est donné en paramètre de la route. La route utilisé est Get /measurement/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 1 :

![Required Get Measurement details Response](Annexe/R5R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 :

![Required Get Measurement details Example](Annexe/R5E.png)

## Partie Medium

### Put : /person Create a new person

Crée une nouvelle personne dans la table Person en utilisant la route Put /person.

Ce que nous renvoie notre fonction après la création de la nouvelle personne si tous les éléments sont présents et qu'il y ait 41 personnes dans la base de données :

![Medium Create Person Response](Annexe/M1R.png)

Ce que la fonction est censée nous renvoyer après la création de la nouvelle personne si tous les éléments sont présents et qu'il y est déjà 41 personnes dans la base de données:

![Medium Create Person Example](Annexe/M1E.png)

Ce que nous renvoie la fonction après la création de la nouvelle personne si le first_name est manquant :

![Medium Create Person Error Response](Annexe/M1XR.png)

Ce que la fonction est censée nous renvoyer après la création de la nouvelle personne si le first_name est manquant :

![Medium Create Person Error Example](Annexe/M1XE.png)

### Post : /person/{id} Update person-values

Met à jour les détails d'un objet de la classe Person pour un id donné en paramètre de la route, en utilisant la route Post /person/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 1 après la mise à jour de la personne en cas de résussite :

![Medium Update Person Response](Annexe/M2R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 après la mise à jour de la personne en cas de résussite :

![Medium Update Person Example](Annexe/M2E.png)

Ce que nous renvoie notre fonction l'id ne correspind à auncune personnes dans la base de données :

![Medium Update Person Error Response](Annexe/M2XR.png)

Ce que la fonction est censée nous renvoyer l'id ne correspind à auncune personnes dans la base de données :

![Medium Update Person Error Example](Annexe/M2XE.png)

### Delete : /person/{id} Delete a person

Supprime une personne de la table Person pour un id donné en paramètre de la route. La route utilisé est Delete /person/{id}.

### Get : /sensor/{kind} List sensors of a given kind

Renvoie la liste des capteurs d'un type donné en paramètre de la route. La route utilisé est Get /sensor/{kind}.

Voici ce que nous renvoie notre fonction en mettant en paramètre le type de capteur à "EVCharger" :

![Medium Get Sensors Response](Annexe/M4R.png)

Voici ce que la fonction est censée nous renvoyer en mettant en paramètre le type de capteur à "EVCharger" :

![Medium Get Sensors Example](Annexe/M4E.png)

### Get : /producers List all producers

Renvoie la liste des producteurs d'énergie de la base de données en utilisant la route Get /producers.

Voici ce que nous renvoie notre fonction :

![Medium Get Producers Response](Annexe/M5R.png)

Voici ce que la fonction est censée nous renvoyer :

![Medium Get Producers Example](Annexe/M5E.png)

### Get : /consumers List all consumers

Renvoie la liste des consommateurs d'énergie de la base de données en utilisant la route Get /consumers.

Voici ce que nous renvoie notre fonction :

![Medium Get Consumers Response](Annexe/M6R.png)

Voici ce que la fonction est censée nous renvoyer :

![Medium Get Consumers Example](Annexe/M6E.png)

## Partie Advanced

### Post : /ingress/windturbine Receive wind turbine measurement

Ajoute une mesure à une élolienne dans la table Measurement en utilisant la route Post /ingress/windturbine.

Ce que nous renvoie notre fonction après l'ajout de la nouvelle mesure en cas de résussite :

![Advanced Post Measurement Response](Annexe/A1R.png)

Ce que la fonction est censée nous renvoyer après l'ajout de la nouvelle mesure en cas de résussite :

![Advanced Post Measurement Example](Annexe/A1E.png)

Ce que nous renvoie notre fonction après l'ajout de la nouvelle mesure en cas d'échec :

![Advanced Post Measurement Error Response](Annexe/A1XR.png)

Ce que la fonction est censée nous renvoyer après l'ajout de la nouvelle mesure en cas d'échec :

![Advanced Post Measurement Error Example](Annexe/A1XE.png)

### Post : /ingress/solarpanel Receive solar panel measurement

Ajoute une mesure à un panneau solaire dans la table Measurement en utilisant la route Post /ingress/solarpanel.

Ce que nous renvoie notre fonction après l'ajout de la nouvelle mesure en cas de résussite :

![Advanced Post Solar Panel Measurement Response](Annexe/A2R.png)

Ce que la fonction est censée nous renvoyer après l'ajout de la nouvelle mesure en cas de résussite :

![Advanced Post Solar Panel Measurement Example](Annexe/A2E.png)

Ce que nous renvoie notre fonction après l'ajout de la nouvelle mesure en cas d'échec :

![Advanced Post Solar Panel Measurement Error Response](Annexe/A2XR.png)

Ce que la fonction est censée nous renvoyer après l'ajout de la nouvelle mesure en cas d'échec :

![Advanced Post Solar Panel Measurement Error Example](Annexe/A2XE.png)

### Get : /sensor/{id} Get sensor detail

Renvoie les détails d'un objet de la classe Sensor pour un id donné en paramètre de la route. La route utilisé est Get /sensor/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 2 :

![Advanced Get Sensor Response](Annexe/A3R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 2 :

![Advanced Get Sensor Example](Annexe/A3E.png)

### Post : /sensor/{id} Update a sensor

Met à jour les détails d'un objet de la classe Sensor pour un id donné en paramètre de la route, en utilisant La route Post /sensor/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 2 après la mise à jour du capteur en cas de résussite :

![Advanced Post Sensor Response](Annexe/A4R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 2 après la mise à jour du capteur en cas de résussite :

![Advanced Post Sensor Example](Annexe/A4E.png)

### Get : /measurements/{id}/values Get measurement values

Renvoie les valeurs d'une mesure pour un id donné en paramètre de la route. La route utilisé est Get /measurements/{id}/values.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 1 :

![Advanced Get Measurement Values Response](Annexe/A5R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 :

![Advanced Get Measurement Values Example](Annexe/A5E.png)

## Partie Hard
