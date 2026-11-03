package ca.cegep.biblio.service;

import ca.cegep.biblio.model.Livre;
import ca.cegep.biblio.repository.LivreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TP partie C : à vous d'implémenter les 3 tests ci-dessous.
 * Retirez @Disabled de chaque test une fois son implémentation écrite.
 * Respectez le modèle Arrange / Act / Assert.
 */
@ExtendWith(MockitoExtension.class)
class LivreServiceTest {

    @Mock
    private LivreRepository repository;

    @InjectMocks
    private LivreService service;

    // ---------- 1 test simple (sans mock) ----------

    @Test
    void calculerFraisRetard_longRetard_plafonneLeMontant() {
        // Arrange
        String categorie = "SCIENCE";
        int joursRetard = 1000;

        // Act
        double frais = service.calculerFraisRetard(categorie, joursRetard);

        // Assert
        assertEquals(20.0, frais, 0.001);
    }

    // ---------- 1 test sur une exception (assertThrows) ----------

    @Test
    void emprunter_livreDejaEmprunte_lanceIllegalStateException() {
        // Arrange
        Livre livre = new Livre(1L, "Livre test", "Auteur test",
                "0000000000000", 2000, "ROMAN");
        livre.setDisponible(false);
        when(repository.findById(1L)).thenReturn(Optional.of(livre));

        // Act + Assert
        assertThrows(IllegalStateException.class, () -> service.emprunter(1L));
        verify(repository, never()).save(livre);
    }

    // ---------- 1 test avec mock (when / thenReturn / verify) ----------

    @Test
    void emprunter_livreDisponible_marqueIndisponibleEtSauvegarde() {
        // Arrange
        Livre livre = new Livre(1L, "Livre test", "Auteur test",
                "0000000000000", 2000, "ROMAN");
        when(repository.findById(1L)).thenReturn(Optional.of(livre));
        when(repository.save(livre)).thenReturn(livre);

        // Act
        Livre resultat = service.emprunter(1L);

        // Assert
        assertFalse(resultat.isDisponible());
        verify(repository, times(1)).save(livre);
    }
}


