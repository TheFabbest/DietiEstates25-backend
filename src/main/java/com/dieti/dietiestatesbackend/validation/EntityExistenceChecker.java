package com.dieti.dietiestatesbackend.validation;

/**
 * Interfaccia per verificare l'esistenza di un'entità nel datastore.
 * Implementazioni concrete (es. JPA) forniscono la logica di accesso ai dati.
 * Mantiene Single Responsibility separando la query dal validatore.
 */
public interface EntityExistenceChecker {

    /**
     * Controlla se esiste almeno un'istanza dell'entità {@code entityClass}
     * per cui il valore del campo {@code fieldName} è uguale a {@code value}.
     *
     * @param entityClass la classe JPA dell'entità da verificare
     * @param fieldName   il nome del campo dell'entità usato per il confronto
     * @param value       il valore da cercare
     * @return true se esiste almeno un'entità che soddisfa la condizione, false altrimenti
     */
    boolean exists(Class<?> entityClass, String fieldName, Object value);
}