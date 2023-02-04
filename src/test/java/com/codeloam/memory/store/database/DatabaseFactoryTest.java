package com.codeloam.memory.store.database;

import com.codeloam.memory.store.database.simple.SimpleDatabase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test DatabaseFactory.
 *
 * @author jinyu.li
 * @since 1.0
 */
public class DatabaseFactoryTest {
    @Test
    public void testCreate() {
        Database database = DatabaseFactory.create(DatabaseType.Simple);
        assertNotNull(database);
        assertInstanceOf(SimpleDatabase.class, database);
    }
}
