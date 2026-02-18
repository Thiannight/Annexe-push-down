import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    private final DBConnection dbConnection = new DBConnection();

    // Q1
    public List<InvoiceTotal> findInvoiceTotals() {
        String sql = """
                SELECT i.id,
                       i.customer_name,
                       i.status,
                       SUM(il.quantity * il.unit_price) AS total
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                GROUP BY i.id, i.customer_name, i.status
                ORDER BY i.id
                """;

        List<InvoiceTotal> results = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(new InvoiceTotal(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        InvoiceStatus.valueOf(rs.getString("status")),
                        rs.getDouble("total")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    // Q2
    public List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() {
        String sql = """
                SELECT i.id,
                       i.customer_name,
                       i.status,
                       SUM(il.quantity * il.unit_price) AS total
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                WHERE i.status IN ('CONFIRMED', 'PAID')
                GROUP BY i.id, i.customer_name, i.status
                ORDER BY i.id
                """;

        List<InvoiceTotal> results = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(new InvoiceTotal(
                        rs.getInt("id"),
                        rs.getString("customer_name"),
                        InvoiceStatus.valueOf(rs.getString("status")),
                        rs.getDouble("total")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }

    // Q3
    public InvoiceStatusTotals computeStatusTotals() {
        String sql = """
                SELECT
                    SUM(CASE WHEN i.status = 'PAID'
                             THEN il.quantity * il.unit_price ELSE 0 END) AS total_paid,
                    SUM(CASE WHEN i.status = 'CONFIRMED'
                             THEN il.quantity * il.unit_price ELSE 0 END) AS total_confirmed,
                    SUM(CASE WHEN i.status = 'DRAFT'
                             THEN il.quantity * il.unit_price ELSE 0 END) AS total_draft
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new InvoiceStatusTotals(
                        rs.getDouble("total_paid"),
                        rs.getDouble("total_confirmed"),
                        rs.getDouble("total_draft")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // Q4
    public Double computeWeightedTurnover() {
        String sql = """
                SELECT
                    SUM(
                        CASE
                            WHEN i.status = 'PAID'
                                THEN il.quantity * il.unit_price
                            WHEN i.status = 'CONFIRMED'
                                THEN (il.quantity * il.unit_price) * 0.5
                            ELSE 0
                        END
                    ) AS weighted_turnover
                FROM invoice i
                JOIN invoice_line il ON il.invoice_id = i.id
                """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("weighted_turnover");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0.0;
    }

    //Q5-A
    public List<InvoiceTaxSummary> findInvoiceTaxSummaries() {
        String sql = """
            SELECT
                i.id,
                SUM(il.quantity * il.unit_price) AS total_ht,
                SUM(il.quantity * il.unit_price) *
                    ((SELECT rate FROM tax_config LIMIT 1) / 100) AS total_tva,
                SUM(il.quantity * il.unit_price) *
                    (1 + (SELECT rate FROM tax_config LIMIT 1) / 100) AS total_ttc
            FROM invoice i
            JOIN invoice_line il ON il.invoice_id = i.id
            GROUP BY i.id
            ORDER BY i.id
            """;

        List<InvoiceTaxSummary> results = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(new InvoiceTaxSummary(
                        rs.getInt("id"),
                        rs.getDouble("total_ht"),
                        rs.getDouble("total_tva"),
                        rs.getDouble("total_ttc")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return results;
    }


    // Q5-B
    public Double computeWeightedTurnoverTtc() {
        String sql = """
            SELECT
                SUM(
                    CASE
                        WHEN i.status = 'PAID'
                            THEN (il.quantity * il.unit_price) *
                                 (1 + (SELECT rate FROM tax_config LIMIT 1) / 100)
                        WHEN i.status = 'CONFIRMED'
                            THEN (il.quantity * il.unit_price) *
                                 (1 + (SELECT rate FROM tax_config LIMIT 1) / 100) * 0.5
                        ELSE 0
                    END
                ) AS weighted_turnover_ttc
            FROM invoice i
            JOIN invoice_line il ON il.invoice_id = i.id
            """;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("weighted_turnover_ttc");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0.0;
    }
}
