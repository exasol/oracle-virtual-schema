package com.exasol.adapter.dialects.oracle.helper;

import java.sql.SQLException;

/**
 * Functional interface, which is similar to {@link java.util.function.Consumer} and throws SQLException
 */
@FunctionalInterface
public interface ThrowsSqlConsumer<T> {

    void accept(T t) throws SQLException;
}
