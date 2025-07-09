package com.exasol.adapter.dialects.oracle.helper;

import java.sql.SQLException;

/**
 * Functional interface, which extends {@link java.util.function.Consumer} and provides acceptThrowable method, which delegates to accept and throws SQLException
 */
@FunctionalInterface
public interface ThrowsSqlConsumer<T> {

    void accept(T t) throws SQLException;
}
