package io.github.pronto.markov.web.rest;

import java.util.Collections;
import java.util.List;

import io.github.pronto.markov.MarkovTextGeneratorApp;
import io.github.pronto.markov.security.annotation.AdminRole;
import io.github.pronto.markov.security.jwt.TokenProvider;
import io.github.pronto.markov.service.ResultService;
import io.github.pronto.markov.service.dto.ResultDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.github.pronto.markov.security.jwt.JWTConfigurer.AUTHORIZATION_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MarkovTextGeneratorApp.class)
@AutoConfigureMockMvc
public class ResultResourceIntTest {

    private final static String user = "admin@localhost.com";
    @Autowired
    private ResultService resultService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private MockMvc mvc;

    @Test
    public void createResult() throws Exception {
        int databaseSizeBeforeCreate = resultService.getAllUserResults(Pageable.unpaged(), user).size();

        mvc.perform(post("/api/results")
            .header(AUTHORIZATION_HEADER, getToken())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertToJson(getResult())))
            .andExpect(status().isCreated());

        // Validate the User in the database
        List<ResultDTO> resList = resultService.getAllUserResults(Pageable.unpaged(), user);
        assertThat(resList).hasSize(databaseSizeBeforeCreate + 1);
        ResultDTO testRes = resList.get(resList.size() - 1);
        assertThat(getResult().getData()).isEqualTo(testRes.getData());
    }

    @Test
    public void createResultAnon() throws Exception {
        mvc.perform(post("/api/results")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertToJson(ResultDTO.builder().build())))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getAllUserData() throws Exception {
        createResult();

        mvc.perform(get("/api/results?sort=id,desc")
            .header(AUTHORIZATION_HEADER, getToken()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].result").value(hasItem(getResult().getData()))); //res will be the same
    }

    @Test
    public void deleteUser() throws Exception {
        createResult();

        int databaseSizeBeforeDelete = resultService.getAllUserResults(Pageable.unpaged(), user).size();

        mvc.perform(delete("/api/results/{id}", 1)
            .header(AUTHORIZATION_HEADER, getToken()))
            .andExpect(status().is2xxSuccessful());

        List<ResultDTO> userList = resultService.getAllUserResults(Pageable.unpaged(), user);
        assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
    }

    private String getToken() {
        return "Bearer " + tokenProvider
            .createToken(new UsernamePasswordAuthenticationToken("admin@localhost.com", "test",
                Collections.singletonList(new SimpleGrantedAuthority(AdminRole.name))));
    }

    private ResultDTO getResult() {
        return ResultDTO.builder()
            .resultSize(10)
            .suffixSize(1)
            .data("1 2 3 4 5 6 7 8 9 10")
            .build();
    }
}
