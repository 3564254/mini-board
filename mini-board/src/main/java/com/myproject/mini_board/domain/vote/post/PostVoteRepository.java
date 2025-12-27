package com.myproject.mini_board.domain.vote.post;

import com.myproject.mini_board.domain.vote.Vote;
import com.myproject.mini_board.domain.vote.VoteType;
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
public class PostVoteRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;
    private final RowMapper<PostVote> postVoteRowMapper = new BeanPropertyRowMapper<>(PostVote.class);

    public PostVoteRepository(NamedParameterJdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("post_votes")
                .usingGeneratedKeyColumns("id");
    }
    public PostVote getPostVote(Long postId, Long userId) {
        String sql = """
                SELECT * FROM post_votes WHERE post_id = :postId AND user_id = :userId
                """;
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("userId", userId);
        try {
            return jdbcTemplate.queryForObject(sql, parameterSource, postVoteRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<PostVote> getAllByUserId(Long userId) {
        String sql = """
                SELECT * FROM post_votes WHERE user_id = :userId
                """;
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("userId", userId);
        return jdbcTemplate.query(sql, parameterSource, postVoteRowMapper);
    }

    public void saveVote(Long postId, Long userId, VoteType voteType) {
        String sql = """
                INSERT INTO post_votes (post_id, user_id, vote_type) VALUES (:postId, :userId, :voteType)
                """;
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("userId", userId)
                .addValue("voteType", voteType.name());
        jdbcTemplate.update(sql, parameterSource);
    }

    public void updateVote(Long postId, Long userId, VoteType voteType) {
        String sql = "UPDATE post_votes SET vote_type = :voteType WHERE post_id = :postId AND user_id = :userId";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("userId", userId)
                .addValue("voteType", voteType.name());
        jdbcTemplate.update(sql, parameterSource);
    }

    public void deleteVote(Long postId, Long userId) {
        String sql = "DELETE FROM post_votes WHERE post_id = :postId AND user_id = :userId";
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("userId", userId);
        jdbcTemplate.update(sql, parameterSource);
    }
}
