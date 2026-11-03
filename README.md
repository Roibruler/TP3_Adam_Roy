# biblio-api

API REST de gestion d'une petite bibliothèque (Spring Boot 3.5, Java 25, architecture MVC).

## Démarrer

```bash
./mvnw spring-boot:run
```

- Swagger UI : http://localhost:8080/swagger-ui.html
- Les données sont gardées **en mémoire** (perdues à l'arrêt).

## Structure

| Package | Rôle |
|---|---|
| `model` | Le modèle : `Livre` |
| `repository` | Accès aux données (en mémoire) |
| `service` | Logique métier |
| `controller` | Contrôleur REST (+ `LivreRequest`, corps JSON des requêtes) |
| `exception` | Exceptions et traduction en codes HTTP |

## Endpoints

| Méthode | URL | Description |
|---|---|---|
| GET | `/api/livres` | Lister les livres |
| GET | `/api/livres/{id}` | Obtenir un livre |
| POST | `/api/livres` | Ajouter un livre |
| PUT | `/api/livres/{id}` | Modifier un livre |
| DELETE | `/api/livres/{id}` | Supprimer un livre |
| POST | `/api/livres/{id}/emprunter` | Emprunter un livre |
| POST | `/api/livres/{id}/retourner` | Retourner un livre |
| GET | `/api/livres/{id}/frais-retard?joursRetard=N` | Frais de retard en $ |

---

## Question Partie A.1

1. Quelle classe joue le rôle du Modèle, du Contrôleur ? Où se trouve la logique métier ?

C'est la classe "Livre" qui joue le rôle du modèle.
C'est la classe "LivreController" qui s'occupe du rôle de contrôleur.
C'est dans le service que ce trouve la lgoique métier. 

2. Que fait GestionnaireErreurs et quel code HTTP obtient-on pour un livre introuvable ? Et pour un double emprunt ?

"GestionnaireErreurs" s'occupe de répertorier et de gérer la réponse suite aux différentes erreurs qui peuvent subvenir dans l'application.
Lorsqu'un livre est introuvable on reçoit le code "404 NOT_FOUND".
Lors d'un double emprunt ont reçoit "409 CONFLIT".

3. Où les données sont-elles stockées ? Que se passe-t-il quand l'application redémarre ?

Les données sont stocké dans un 'Map' du repository.
Lorsque l'application redémarre les données sont perdus, mais les 3 livres de bases sont initialisé.

4. Suivez le trajet complet d'un appel POST /api/livres/1/emprunter : quelles classes/méthodes sont appelées, dans quel ordre ?

Dans "LivreController" c'est la méthode "emprunter" qui est appelée puisque le contrôleur ne gère pas la logique métier.
Par la suite dans "LivreService" c'est encore la méthode "emprunter" qui est appelée. Elle utilise "trouver" qui appelle "findById" dans "LivreRepository" pour aller chercher le livre (404 si introuvable).
Si le livre est déjà emprunté, une exception est lancée et "GestionnaireErreurs" renvoie un code 409.
Sinon, c'est "LivreRepository" qui est appelé avec la méthode "save" pour sauvegarder le changement du statut du livre en indisponible.

## Question Partie A.3

1. GET /api/livres

Code 200

Un code 200 "OK" veut dire que l'information est correctement parvenu

2. POST /api/livres avec un nouveau livre

Code 201

Un code 201 signifie que le post d'information c'est fait correctemment

3. POST /api/livres avec un titre vide

Code 400 

Un code 400 "Bad Request" veut dire que la requête est invalide. Dans notre cas c'est parce qu'il manque le titre.

4. POST /api/livres/1/emprunter deux fois de suite

Premier appel : Code 200 (le livre est emprunté)
Deuxième appel : Code 409

Un code 409 "Conflict" indique que la requête entre en conflit avec l'état actuel de la ressource.

5. GET /api/livres/2/frais-retard?joursRetard=100

Code 200
Montant: 20

Un code 200 "OK" veut dire que l'information est correctement parvenu

## Partie C — Couverture JaCoCo

| Package | Instructions | Branches |
|---|---|---|
| **Total** | **33 %** | **16 %** |
| repository | 85 % | 50 % |
| model | 48 % | n/a |
| service | 27 % | 15 % |
| controller | 0 % | n/a |
| exception | 0 % | n/a |

La couverture totale est de 33 %.
Le "repository" est bien couvert grâce au test fourni.
Le "service" est à 27 % parce qu'on teste seulement "emprunter" et "calculerFraisRetard", et seulement la catégorie "SCIENCE" pour les frais.
Le "controller" et "GestionnaireErreurs" sont à 0 % parce que nos tests unitaires ne passent pas par HTTP.
Il faudrait ajouter des tests pour les autres catégories et les autres méthodes du service.

## Partie D — Code smells

| Smell | Fichier | Lignes | Problème |
|---|---|---|---|
| Feature envy | `LivreController.java` | 82-86 | Le contrôleur fait un calcul qui devrait être dans le service. |
| Enchaînements conditionnels répétés | `LivreService.java` | 84-105 | Beaucoup de `if/else` et de nombres magiques pour les frais. |
| Duplication de code | `LivreService.java` | 29-37 et 44-52 | La même validation est copiée dans `creer` et `modifier`. |
| Liste de paramètres trop longue | `LivreService.java` | 28 et 42 | `creer` et `modifier` ont trop de paramètres. |

## Partie E.2 — Test du Dockerfile dans Codespaces

```bash
chmod +x mvnw
./mvnw -B package
docker build -t biblio-api:local .
docker run -d -p 8080:8080 --name biblio biblio-api:local
docker ps
curl http://localhost:8080/api/livres
```

`curl` retourne la liste des 3 livres en JSON. Swagger a été testé avec le port 8080.

## Partie E.5 — Versions

- **v1.0.0** : première livraison (pipeline CI, tests, JaCoCo, Docker, GHCR, Release).
- **v1.0.1** : *CORRECTIF*. Les réusinages de la partie D ne changent pas le comportement de l'API. Aucun endpoint ajouté ou retiré, mêmes requêtes et mêmes réponses. Ce n'est donc pas un changement cassant ou une nouvelle fonctionnalité.

Note : les réusinages de la partie D ont été fusionnés avant la pose du tag v1.0.0. Le numéro v1.0.1 est celui qui correspond à ces changements selon SemVer.

Vérification dans Codespaces :
```bash
docker login ghcr.io -u roibruler
docker pull ghcr.io/roibruler/biblio-api:v1.0.1
```

## Liens

- Exécution réussie du workflow (matrice 21 et 25) : https://github.com/Roibruler/TP3_Adam_Roy/actions/runs/41601128554
- Pull Requests fusionnées :
  - https://github.com/Roibruler/TP3_Adam_Roy/pull/1
  - https://github.com/Roibruler/TP3_Adam_Roy/pull/3
  - https://github.com/Roibruler/TP3_Adam_Roy/pull/4
  - https://github.com/Roibruler/TP3_Adam_Roy/pull/5
  - https://github.com/Roibruler/TP3_Adam_Roy/pull/6
  - https://github.com/Roibruler/TP3_Adam_Roy/pull/7
  - https://github.com/Roibruler/TP3_Adam_Roy/pull/8
- Releases : https://github.com/Roibruler/TP3_Adam_Roy/releases
- Packages (GHCR) : https://github.com/users/Roibruler/packages/container/package/biblio-api