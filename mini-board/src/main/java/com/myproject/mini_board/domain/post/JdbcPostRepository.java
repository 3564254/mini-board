// 2. Optional<T> 반환: findById 메서드에서 EmptyResultDataAccessException을 catch하여 null을 반환하고 있습니다.
//    PostRepository 인터페이스의 반환 타입을 Optional<Post>로 변경하고, 이 클래스에서는 try-catch 블록 대신 `Optional.ofNullable(jdbcTemplate.queryForObject(...))`을 사용하거나,
//    query() 메서드를 사용하여 결과 리스트의 크기를 확인하는 방식으로 변경하면 더 안전하고 명확한 코드가 됩니다.

package com.myproject.mini_board.domain.post;

import com.myproject.mini_board.web.dto.page.PageConst;
import com.myproject.mini_board.web.dto.post.PostResponseDTO;
import com.myproject.mini_board.web.dto.post.PostSearchCond;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcPostRepository implements PostRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private static final RowMapper<Post> POST_ROW_MAPPER = new BeanPropertyRowMapper<>(Post.class);
    private static final RowMapper<PostResponseDTO> POST_DETAIL_ROW_MAPPER = new BeanPropertyRowMapper<>(PostResponseDTO.class);

    public JdbcPostRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("posts")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public Post save(Post post) {
        // 1. 자바에서 시간 주입 (가장 효율적)
        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }
        if (post.getUpdatedAt() == null) {
            post.setUpdatedAt(LocalDateTime.now());
        }
        post.setLikeCount(0L);
        post.setDislikeCount(0L);

        // 2. 저장 (시간값이 있으므로 DB에도 정상 저장됨)
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(post);
        Number key = jdbcInsert.executeAndReturnKey(parameterSource);

        // 3. ID 주입
        post.setId(key.longValue());

        return post;
    }

    @Override
    public Post findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        try {
            return jdbcTemplate.queryForObject(sql, parameterSource, POST_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    @Override
    public PostResponseDTO findByIdWithName(Long id) {
        String sql = """
                SELECT p.*, u.username
                FROM posts p
                LEFT JOIN users u ON p.user_id = u.id
                WHERE p.id = :id
                """;
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        try {
            return jdbcTemplate.queryForObject(sql, parameterSource, POST_DETAIL_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public List<PostResponseDTO> searchPosts(PostSearchCond cond, int page) {
        // 1. 기본 쿼리 (표준 JOIN 방식)
        String sql = """
                    SELECT p.*, u.username
                    FROM posts p
                    LEFT JOIN users u ON p.user_id = u.id
                    WHERE 1=1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder(sql);

        // 1. [제목 + 내용] 둘 다 값이 있는 경우
        if (StringUtils.hasText(cond.getTitle()) && StringUtils.hasText(cond.getContent())) {
            builder.append(" AND (p.title LIKE :keyword OR p.content LIKE :keyword)");
            params.addValue("keyword", "%" + cond.getTitle() + "%");
        }
        // 2. [제목]만 있는 경우
        else if (StringUtils.hasText(cond.getTitle())) {
            builder.append(" AND p.title LIKE :title");
            params.addValue("title", "%" + cond.getTitle() + "%");
        }
        // 3. [내용]만 있는 경우
        else if (StringUtils.hasText(cond.getContent())) {
            builder.append(" AND p.content LIKE :content");
            params.addValue("content", "%" + cond.getContent() + "%");
        }

        // 4. [작성자] 검색
        if (StringUtils.hasText(cond.getUsername())) {
            builder.append(" AND u.username LIKE :username");
            params.addValue("username", "%" + cond.getUsername() + "%");
        }

        builder.append(" ORDER BY p.id DESC LIMIT :limit OFFSET :offset");
        params.addValue("limit", PageConst.SIZE);
        params.addValue("offset", PageConst.getOffset(page));

        return jdbcTemplate.query(builder.toString(), params, POST_DETAIL_ROW_MAPPER);
    }


    @Override
    public List<PostResponseDTO> findAll(int page) {
        String sql = """
                    SELECT p.*, u.username
                    FROM ( SELECT id
                           FROM posts
                           ORDER BY id DESC
                           LIMIT :limit OFFSET :offset
                           ) AS temp
                    JOIN posts p ON temp.id = p.id
                    LEFT JOIN users u ON p.user_id = u.id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", PageConst.SIZE);
        params.addValue("offset", PageConst.getOffset(page));

        return jdbcTemplate.query(sql, params, POST_DETAIL_ROW_MAPPER);
    }


    @Override
    public void update(Post post) {
        post.setUpdatedAt(LocalDateTime.now());

        String sql = "UPDATE posts SET title = :title, content = :content, updated_at = :updatedAt WHERE id = :id";
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(post);
        jdbcTemplate.update(sql, sqlParameterSource);

    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public int count() {
        String sql = "SELECT count(*) FROM posts";
        return jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Integer.class);
    }

    @Override
    public int searchCount(PostSearchCond cond) {
        String sql = """
            SELECT COUNT(*)
            FROM posts p
            LEFT JOIN users u ON p.user_id = u.id
            WHERE 1=1
            """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder(sql);

        // 1. [제목 + 내용] 둘 다 값이 있는 경우
        if (StringUtils.hasText(cond.getTitle()) && StringUtils.hasText(cond.getContent())) {
            builder.append(" AND (p.title LIKE :keyword OR p.content LIKE :keyword)");
            params.addValue("keyword", "%" + cond.getTitle() + "%");
        }
        // 2. [제목]만 있는 경우
        else if (StringUtils.hasText(cond.getTitle())) {
            builder.append(" AND p.title LIKE :title");
            params.addValue("title", "%" + cond.getTitle() + "%");
        }
        // 3. [내용]만 있는 경우
        else if (StringUtils.hasText(cond.getContent())) {
            builder.append(" AND p.content LIKE :content");
            params.addValue("content", "%" + cond.getContent() + "%");
        }

        // 4. [작성자] 검색
        if (StringUtils.hasText(cond.getUsername())) {
            builder.append(" AND u.username LIKE :username");
            params.addValue("username", "%" + cond.getUsername() + "%");
        }

        return jdbcTemplate.queryForObject(builder.toString(), params, Integer.class);
    }

    @Override
    public void increaseLikeCount(Long id) {
        String sql = "UPDATE posts SET like_count = like_count + 1 WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public void decreaseLikeCount(Long id) {
        String sql = "UPDATE posts SET like_count = like_count - 1 WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public void increaseDislikeCount(Long id) {
        String sql = "UPDATE posts SET dislike_count = dislike_count + 1 WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameterSource);
    }

    @Override
    public void decreaseDislikeCount(Long id) {
        String sql = "UPDATE posts SET dislike_count = dislike_count - 1 WHERE id = :id";
        SqlParameterSource parameterSource = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, parameterSource);
    }
}