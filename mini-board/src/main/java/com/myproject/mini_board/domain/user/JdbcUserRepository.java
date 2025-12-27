// [리팩토링 제안]
// 1. RowMapper 캐싱: userRowMapper() 메서드는 호출될 때마다 새로운 BeanPropertyRowMapper 인스턴스를 생성합니다.
//    RowMapper는 상태를 가지지 않으므로(stateless), 클래스의 상수로 만들어 재사용하는 것이 성능에 유리합니다.
//    예: `private static final RowMapper<User> USER_ROW_MAPPER = new BeanPropertyRowMapper<>(User.class);`
//
// 2. Optional<T> 반환: findById, findByLoginId와 같은 조회 메서드에서 EmptyResultDataAccessException을 catch하여 null을 반환하고 있습니다.
//    UserRepository 인터페이스의 반환 타입을 Optional<User>로 변경하고, 이 클래스에서는 try-catch 블록 대신 `Optional.ofNullable(jdbcTemplate.queryForObject(...))`을 사용하거나,
//    query() 메서드를 사용하여 결과 리스트의 크기를 확인하는 방식으로 변경하면 더 안전하고 명확한 코드가 됩니다.
//    - `queryForObject`는 결과가 없으면 예외를 던지므로, `query`를 사용하여 리스트로 결과를 받고, 리스트가 비어있으면 `Optional.empty()`를, 아니면 `Optional.of(list.get(0))`을 반환하는 것이 일반적입니다.
//
// 3. 파라미터 소스 일관성: SqlParameterSource를 생성하는 방식이 `MapSqlParameterSource`, `BeanPropertySqlParameterSource`, `Map.of` 등 다양하게 사용되고 있습니다.
//    하나의 스타일(예: `MapSqlParameterSource` 또는 `Map.of`)로 통일하면 코드의 일관성과 가독성을 높일 수 있습니다.
package com.myproject.mini_board.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;


@Repository
public class JdbcUserRepository implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    public JdbcUserRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public User save(User user) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        Number key = jdbcInsert.executeAndReturnKey(parameterSource);

        user.setId(key.longValue());
        return user;
    }

    @Override
    public User findById(Long id) {;
        String sql = "SELECT * FROM users WHERE id = :id";
        // Map<String, Object> param = Map.of("id", id);도 가능
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("id", id);
        try {
            return jdbcTemplate.queryForObject(sql, parameterSource, userRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User findByLoginId(String loginId) {
        String sql = "SELECT * FROM users WHERE login_id = :loginId";
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("loginId", loginId);
        try {
            return jdbcTemplate.queryForObject(sql, parameterSource, userRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String findUsernameById(Long id) {
        String sql = "SELECT username FROM users WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("id", id);
        try {
            return jdbcTemplate.queryForObject(sql, parameterSource, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET password = :password, username = :username WHERE id = :id";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM users where id = :id";
        Map<String, Object> parameterMap = Map.of("id", id);
        jdbcTemplate.update(sql,parameterMap);
    }

    private RowMapper<User> userRowMapper() {
        return new BeanPropertyRowMapper<>(User.class);
    }
}
