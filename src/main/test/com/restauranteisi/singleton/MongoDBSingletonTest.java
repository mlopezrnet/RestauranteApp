package com.restauranteisi.singleton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MongoDBSingletonTest {

    @Test
    @DisplayName("Prueba de Verifición de instancia")
    void testGetInstance() {
        MongoDBSingleton instance1 = MongoDBSingleton.getInstance();
        MongoDBSingleton instance2 = MongoDBSingleton.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }
}
