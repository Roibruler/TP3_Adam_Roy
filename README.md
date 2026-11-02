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

> Remplacez le contenu ci-dessous par vos réponses du TP (voir `ENONCE_TP.md`).

## Question Partie A.1

1. Quelle classe joue le rôle du Modèle, du Contrôleur ? Où se trouve la logique métier ?

C'est la classe "Livre" qui joue le rôle du modèle.
C'est la classe "LivreController" qui s'occupe du rôle de contrôleur.
C'est dans le service que ce trouve la lgoique métier. 

2. Que fait GestionnaireErreurs et quel code HTTP obtient-on pour un livre introuvable ? Et pour un double emprunt ?

"GestionnaireErreurs" s'occupe de répertorier et de gérer la réponse suite aux différentes erreurs qui peuvent subvenir dans l'application.
Lorsqu'un livre est introuvable on reçoit le code "NOT_FOUND".
Lors d'un double emprunt ont reçoit "CONFLIT".

3. Où les données sont-elles stockées ? Que se passe-t-il quand l'application redémarre ?

Les données sont stocké dans le repository.
Lorsque l'application redémarre les données sont récupérer du repository pour être utilisé.

4. Suivez le trajet complet d'un appel POST /api/livres/1/emprunter : quelles classes/méthodes sont appelées, dans quel ordre ?

Dans "LivreController" c'est la méthode "emprunter" qui à été appelé puisque le controlleur ne gère pas la logique métier
Par la suite dans "LivreService" c'est encore la méthode "emprunté" qui est appelé pour vérifier le statut du livre
Ensuite, c'est "LivreRepository" qui est appelé avec la méthode "save" pour sauvegarder le changement du statut du livre en indisponible

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

Code 409

Un code 409 indique qu'une opération ne peut avoir lieu à cause de mauvais paramètre

5. GET /api/livres/2/frais-retard?joursRetard=100

Code 200
Montant: 20

Un code 200 "OK" veut dire que l'information est correctement parvenu