package hk.ust.cse.comp4111.database;

import com.mysql.jdbc.exceptions.MySQLTimeoutException;
import hk.ust.cse.comp4111.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

public class DatabaseTransaction {

    public static int TIMEOUT_VALUE = 5;

    public static boolean commit(@NotNull Transaction transaction) throws SQLException {
        Connection connection = transaction.getConnection();
        connection.commit();
        return true;
    }

    public static boolean cancel(@NotNull Transaction transaction) throws SQLException {
        Connection connection = transaction.getConnection();
        connection.rollback();
        return true;
    }

    public static boolean update(@NotNull Transaction transaction, Transaction.TransactionAction action) throws SQLException {
        Connection connection = transaction.getConnection();
        try (PreparedStatement preparedUpdate = connection.prepareStatement("UPDATE books SET available = ? WHERE id = ?")) {
            // no need to check book status according to Canvas Q&A
            preparedUpdate.setInt(1, action.isAvailable() ? 1 : 0);
            preparedUpdate.setInt(2, action.getBookId());
            preparedUpdate.setQueryTimeout(TIMEOUT_VALUE);
            preparedUpdate.executeUpdate();
        } catch (SQLTimeoutException | MySQLTimeoutException e) {
            return false;
        }
        return true;
    }
}
