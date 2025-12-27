package com.myproject.mini_board.global.config;

import com.myproject.mini_board.domain.comment.JdbcCommentRepository;
import com.myproject.mini_board.domain.post.JdbcPostRepository;
import com.myproject.mini_board.domain.user.JdbcUserRepository;
import com.myproject.mini_board.domain.vote.post.PostVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private final DataSource dataSource;

    public DatabaseConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource);
    }


    @Bean
    public JdbcPostRepository jdbcPostRepository(NamedParameterJdbcTemplate template) {
        return new JdbcPostRepository(template,dataSource);
    }

    @Bean
    public JdbcCommentRepository jdbcCommentRepository(NamedParameterJdbcTemplate template) {
        return new JdbcCommentRepository(template,dataSource);
    }

    @Bean
    public JdbcUserRepository jdbcUserRepository(NamedParameterJdbcTemplate template) {
        return new JdbcUserRepository(template, dataSource);
    }

    @Bean
    public PostVoteRepository postVoteRepository(NamedParameterJdbcTemplate template) {
        return new PostVoteRepository(template, dataSource);
    }
}
