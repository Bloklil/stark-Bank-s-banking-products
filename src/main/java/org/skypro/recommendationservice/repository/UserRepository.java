package org.skypro.recommendationservice.repository;

import org.skypro.recommendationservice.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(@Qualifier("recommendationsJdbcTemplate")JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT ID, USERNAME, FIRST_NAME, LAST_NAME FROM USERS WHERE USERNAME = ?";
        List<User> users = jdbcTemplate.query(sql, new UserRowMapper(), username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public List<User> findUsersByPartialUsername(String usernamePart) {
        String sql = "SELECT ID, USERNAME, FIRST_NAME, LAST_NAME FROM USERS WHERE USERNAME LIKE ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), "%" + usernamePart + "%");
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(
                    UUID.fromString(rs.getString("ID")),
                    rs.getString("USERNAME"),
                    rs.getString("FIRST_NAME"),
                    rs.getString("LAST_NAME")
            );
        }
    }
}
