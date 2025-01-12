package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlCheckRepository extends BaseRepository {

    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks (status_code, title, h1, description, url_id, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, urlCheck.getStatusCode());
            preparedStatement.setString(2, urlCheck.getTitle());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getDescription());
            preparedStatement.setLong(5, urlCheck.getUrlId());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
            }
        }
    }

    public static Map<Long, UrlCheck> getLastChecks() throws SQLException {
        var sql = "SELECT DISTINCT ON (url_id) * FROM url_checks ORDER BY url_id, created_at DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            Map<Long, UrlCheck> result = new HashMap<>();
            while (resultSet.next()) {
                var urlId = resultSet.getLong("url_id");
                var urlCheck = buildUrlCheck(resultSet, urlId);
                result.put(urlId, urlCheck);
            }
            return result;
        }
    }

    public static List<UrlCheck> getByUrlId(Long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id=? ORDER BY created_at DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                var urlCheck = buildUrlCheck(resultSet, urlId);
                result.add(urlCheck);
            }
            return result;
        }
    }

    private static UrlCheck buildUrlCheck(ResultSet resultSet, Long urlId) throws SQLException {

        return UrlCheck.builder()
                .id(resultSet.getLong("id"))
                .statusCode(resultSet.getInt("status_code"))
                .title(resultSet.getString("title"))
                .h1(resultSet.getString("h1"))
                .description(resultSet.getString("description"))
                .urlId(urlId)
                .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
