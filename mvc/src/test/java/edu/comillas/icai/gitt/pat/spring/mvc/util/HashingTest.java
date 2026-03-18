package edu.comillas.icai.gitt.pat.spring.mvc.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashingTest {

    // Instanciamos la clase que vamos a probar
    private final Hashing hashing = new Hashing();

    @Test
    void testHashGeneraFormatoCorrecto() {
        // Arrange (Preparar)
        String password = "miPasswordSecreto";

        // Act (Actuar)
        String hashedPassword = hashing.hash(password);

        // Assert (Comprobar)
        assertNotNull(hashedPassword, "El hash no debería ser nulo");
        assertTrue(hashedPassword.contains(":"), "El hash debe contener un ':' separando el salt del hash");
    }

    @Test
    void testHashGeneraDiferentesSaltsParaLaMismaPassword() {
        // Arrange
        String password = "miPasswordSecreto";

        // Act
        String hash1 = hashing.hash(password);
        String hash2 = hashing.hash(password);

        // Assert
        assertNotEquals(hash1, hash2, "Dos hashes de la misma contraseña deben ser diferentes por el salt aleatorio");
    }

    @Test
    void testCompareDevuelveTrueSiCoinciden() {
        // Arrange
        String password = "miPasswordSecreto";
        String hashedPassword = hashing.hash(password);

        // Act
        boolean isMatch = hashing.compare(password, hashedPassword);

        // Assert
        assertTrue(isMatch, "La contraseña plana debería coincidir con su propia versión hasheada");
    }

    @Test
    void testCompareDevuelveFalseSiSonDiferentes() {
        // Arrange
        String correctPassword = "miPasswordSecreto";
        String wrongPassword = "passwordIncorrecto";
        String hashedPassword = hashing.hash(correctPassword);

        // Act
        boolean isMatch = hashing.compare(wrongPassword, hashedPassword);

        // Assert
        assertFalse(isMatch, "Una contraseña distinta no debería coincidir con el hash");
    }

    @Test
    void testCompareManejaExcepcionesDevolviendoFalse() {
        // Arrange
        String password = "miPasswordSecreto";
        String hashMalformado = "estoNoEsUnHashValidoNiTieneLosDosPuntos";

        // Act
        boolean isMatch = hashing.compare(password, hashMalformado);

        // Assert
        assertFalse(isMatch, "Si el hash está malformado, el catch debería atrapar la excepción y devolver false");
    }
}