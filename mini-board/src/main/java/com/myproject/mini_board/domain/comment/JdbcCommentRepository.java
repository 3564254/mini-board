// [리팩토링 제안]
// 1. RowMapper 캐싱: commentRowMapper() 메서드는 호출될 때마다 새로운 BeanPropertyRowMapper 인스턴스를 생성합니다.
//    RowMapper는 상태를 가지지 않으므로(stateless), 클래스의 상수로 만들어 재사용하는 것이 성능에 유리합니다.
//    예: `private static final RowMapper<Comment> COMMENT_ROW_MAPPER = new BeanPropertyRowMapper<>(Comment.class);`
//
// 2. Optional<T> 반환: findById 메서드에서 EmptyResultDataAccessException을 catch하여 null을 반환하고 있습니다.
//    CommentRepository 인터페이스의 반환 타입을 Optional<Comment>로 변경하고, 이 클래스에서는 try-catch 블록 대신
//    query() 메서드를 사용하여 결과 리스트의 크기를 확인하는 방식으로 변경하면 더 안전하고 명확한 코드가 됩니다.
//
// 3. 일관성 없는 save 로직: 다른 Repository의 save 메서드는 SimpleJdbcInsert를 사용하여 ID를 생성하고, 생성된 객체를 반환합니다.
//    하지만 현재 save 메서드는 void를 반환하고, 직접 작성한 SQL을 사용하며, 생성된 ID를 객체에 설정해주지 않습니다.
//    - 다른 Repository와 같이 SimpleJdbcInsert를 사용하도록 수정하고, 반환 타입을 Comment로 변경하여 생성된 Comment 객체를 반환하도록 통일하는 것이 좋습니다.
//    - 예: `Number key = jdbcInsert.executeAndReturnKey(parameterSource); comment.setId(key.longValue()); return comment;`
package com.myproject.mini_board.domain.comment;

import com.myproject.mini_board.web.dto.comment.CommentResponseDTO;
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
import java.util.List;
@Repository
public class JdbcCommentRepository implements CommentRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<Comment> COMMENT_ROW_MAPPER = new BeanPropertyRowMapper<>(Comment.class);
    private final RowMapper<CommentResponseDTO> COMMENT_ROW_MAPPER_WITH_USERNAME = new BeanPropertyRowMapper<>(CommentResponseDTO.class);

    public JdbcCommentRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("comments")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public void save(Comment comment) {
        String sql = "INSERT INTO comments(post_id, content, user_id) VALUES (:postId, :content, :userId)";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(comment);
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public Comment findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        try {
            return jdbcTemplate.queryForObject(sql, parameterSource, COMMENT_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = :postId ORDER BY id ASC";
        SqlParameterSource parameterSource = new MapSqlParameterSource("postId", postId);
        return jdbcTemplate.query(sql, parameterSource, COMMENT_ROW_MAPPER);
    }

    @Override
    public List<CommentResponseDTO> findByPostIdWithname(Long postId) {
        String sql = """
                SELECT c.*, u.username
                FROM comments c
                LEFT JOIN users u ON c.user_id = u.id
                WHERE post_id = :postId
                """;
        SqlParameterSource parameterSource = new MapSqlParameterSource("postId", postId);
        return jdbcTemplate.query(sql, parameterSource, COMMENT_ROW_MAPPER_WITH_USERNAME);
    }

    @Override
    public List<Comment> findAll() {
        String sql = "SELECT * FROM comments ORDER BY id ASC";
        return jdbcTemplate.query(sql, COMMENT_ROW_MAPPER);
    }

    @Override
    public void update(Comment comment) {
        String sql = "UPDATE comments SET content = :content WHERE id = :id";
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(comment);
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM comments WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameterSource);
    }

}
