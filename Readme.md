# Smart Grid Server

## Nom du groupe : DEBEAULIEU Anatole, GARNIER Baptiste

## Partie Required

### Get : /grids List all grid IDs

Renvoie la liste des IDs des éléments de la classe Grid en utilisant la route Get /grids.

Ce que nous renvoie notre fonction :

![Première_image](Annexe/R1R.png)

Ce que la fonction est censée nous renvoyer :

![Deuxième_image](Annexe/R1E.png)

### Get : /grid/{id} Get grid details

Renvoie les détail de la Grid dont l'id est rentré en paramètre en utilisant la route Get /grid/{id}.

Ce que nous renvoie notre fonction en mettant en paramère l'id à 1 :

![Troisième_image](Annexe/R2R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 :

![Quatrième_image](Annexe/R2E.png)

### Get : /persons List all person IDs

Renvoie la liste des IDs des éléments de la classe Person en utilisant la route Get /persons.

Ce que nous renvoie notre fonction :

![Cinquième_image](Annexe/R3R.png)

Ce que la fonction est censée nous renvoyer :

![Sixième_image](Annexe/R3E.png)

### Get : /person/{id} Get person by IDs

Renvoie les détails d'un objet de la table Person dont l'id est rentré en paramètre en utilisant la route Get /person/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 1 :

![Septième_image](Annexe/R4R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 :

![Huitième_image](Annexe/R4E.png)

### Get : /measurement/{id} Get measurement definition

Renvoie les détails d'un objet de la classe Measurement dont l'id est donné en paramètre de la route. La route utilisé est Get /measurement/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 1 :

![Neuvième_image](Annexe/R5R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 :

![Dixième_image](Annexe/R5E.png)

## Partie Medium

## Partie Advanced

### Post : /ingress/windturbine Receive wind turbine measurement

Ajoute une mesure à une élolienne dans la table Measurement en utilisant la route Post /ingress/windturbine.

Ce que nous renvoie notre fonction après l'ajout de la nouvelle mesure en cas de résussite :

![Onzième_image](Annexe/A1R.png)

Ce que la fonction est censée nous renvoyer après l'ajout de la nouvelle mesure en cas de résussite :

![Douzième_image](Annexe/A1E.png)

Ce que nous renvoie notre fonction après l'ajout de la nouvelle mesure en cas d'échec :

![Treizième_image](Annexe/A1XR.png)

Ce que la fonction est censée nous renvoyer après l'ajout de la nouvelle mesure en cas d'échec :

![Quatorzième_image](Annexe/A1XE.png)

### Post : /ingress/solarpanel Receive solar panel measurement

Ajoute une mesure à un panneau solaire dans la table Measurement en utilisant la route Post /ingress/solarpanel.

Ce que nous renvoie notre fonction après l'ajout de la nouvelle mesure en cas de résussite :

![Quinzième_image](Annexe/A2R.png)

Ce que la fonction est censée nous renvoyer après l'ajout de la nouvelle mesure en cas de résussite :

![Seizième_image](Annexe/A2E.png)

Ce que nous renvoie notre fonction après l'ajout de la nouvelle mesure en cas d'échec :

![Dix-septième_image](Annexe/A2XR.png)

Ce que la fonction est censée nous renvoyer après l'ajout de la nouvelle mesure en cas d'échec :

![Dix-huitième_image](Annexe/A2XE.png)

### Get : /sensor/{id} Get sensor detail

Renvoie les détails d'un objet de la classe Sensor pour un id donné en paramètre de la route. La route utilisé est Get /sensor/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 2 :

![Dix-neuvième_image](Annexe/A3R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 2 :

![Vingtième_image](Annexe/A3E.png)

### Post : /sensor/{id} Update a sensor

Met à jour les détails d'un objet de la classe Sensor pour un id donné en paramètre de la route, en utilisant La route Post /sensor/{id}.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 2 après la mise à jour du capteur en cas de résussite :

![Vingt-et-unième_image](Annexe/A4R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 2 après la mise à jour du capteur en cas de résussite :

![Vingt-deuxième_image](Annexe/A4E.png)

### Get : /measurements/{id}/values Get measurement values

Renvoie les valeurs d'une mesure pour un id donné en paramètre de la route. La route utilisé est Get /measurements/{id}/values.

Ce que nous renvoie notre fonction en mettant en paramètre l'id à 1 :

![Vingt-troisième_image](Annexe/A5R.png)

Ce que la fonction est censée nous renvoyer en mettant en paramètre l'id à 1 :

![Vingt-quatrième_image](Annexe/A5E.png)

## Partie Hard
