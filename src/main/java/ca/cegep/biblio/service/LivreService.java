package ca.cegep.biblio.service;

import ca.cegep.biblio.exception.LivreIntrouvableException;
import ca.cegep.biblio.model.Livre;
import ca.cegep.biblio.repository.LivreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Logique métier de la bibliothèque. */
@Service
public class LivreService {

    private final LivreRepository repository;

    private static final int ANNEE_MIN = 1450;
    private static final int ANNEE_MAX = 2100;

    public LivreService(LivreRepository repository) {
        this.repository = repository;
    }

    public List<Livre> lister() {
        return repository.findAll();
    }

    public Livre trouver(Long id) {
        return repository.findById(id).orElseThrow(() -> new LivreIntrouvableException(id));
    }

    public Livre creer(String titre, String auteur, String isbn, int anneePublication, String categorie) {
        valider(titre, auteur, anneePublication);
        Livre livre = new Livre(null, titre, auteur, isbn, anneePublication, categorie);
        return repository.save(livre);
    }

    public Livre modifier(Long id, String titre, String auteur, String isbn, int anneePublication, String categorie) {
        Livre livre = trouver(id);
        valider(titre, auteur, anneePublication);
        livre.setTitre(titre);
        livre.setAuteur(auteur);
        livre.setIsbn(isbn);
        livre.setAnneePublication(anneePublication);
        livre.setCategorie(categorie);
        return repository.save(livre);
    }

    public void supprimer(Long id) {
        trouver(id);
        repository.deleteById(id);
    }

    public Livre emprunter(Long id) {
        Livre livre = trouver(id);
        if (!livre.isDisponible()) {
            throw new IllegalStateException("Ce livre est déjà emprunté");
        }
        livre.setDisponible(false);
        return repository.save(livre);
    }

    public Livre retourner(Long id) {
        Livre livre = trouver(id);
        if (livre.isDisponible()) {
            throw new IllegalStateException("Ce livre n'est pas emprunté");
        }
        livre.setDisponible(true);
        return repository.save(livre);
    }

    public double calculerFraisRetardDuLivre(Long id, int joursRetard) {
        Livre livre = trouver(id);
        return calculerFraisRetard(livre.getCategorie(), joursRetard);
    }

    public double calculerFraisRetard(String categorie, int joursRetard) {
        if (joursRetard <= 0) {
            return 0;
        }
        return TarifRetard.pour(categorie).calculer(joursRetard);
    }

    private void valider(String titre, String auteur, int anneePublication) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        if (auteur == null || auteur.isBlank()) {
            throw new IllegalArgumentException("L'auteur est obligatoire");
        }
        if (anneePublication < ANNEE_MIN || anneePublication > ANNEE_MAX) {
            throw new IllegalArgumentException("L'année de publication est invalide");
        }
    }

    private enum TarifRetard {
        JEUNESSE(0.10, 5),
        SCIENCE(0.50, 20),
        ROMAN(0.25, 10);

        private final double tarifParJour;
        private final double plafond;

        TarifRetard(double tarifParJour, double plafond) {
            this.tarifParJour = tarifParJour;
            this.plafond = plafond;
        }

        double calculer(int joursRetard) {
            return Math.min(joursRetard * tarifParJour, plafond);
        }

        static TarifRetard pour(String categorie) {
            for (TarifRetard tarif : values()) {
                if (categorie.equals(tarif.name())) {
                    return tarif;
                }
            }
            return ROMAN;
        }
    }
}