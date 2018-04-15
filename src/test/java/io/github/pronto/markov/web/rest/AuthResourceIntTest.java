package io.github.pronto.markov.web.rest;

import io.github.pronto.markov.MarkovTextGeneratorApp;
import io.github.pronto.markov.security.annotation.UserRole;
import io.github.pronto.markov.service.UserService;
import io.github.pronto.markov.service.dto.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the AuthResource REST controller.
 *
 * @see AuthResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MarkovTextGeneratorApp.class)
@AutoConfigureMockMvc
public class AuthResourceIntTest {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAuthorize() throws Exception {
        UserDTO user = UserDTO.builder()
            .email("user-jwt-controller@example.com")
            .password(passwordEncoder.encode("test"))
            .authorities(singleton(UserRole.name))
            .build();

        userService.createUser(user);

        AuthResource.Login login = new AuthResource.Login(user.getEmail(), user.getPassword());
        mockMvc.perform(post("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertToJson(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isString())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andExpect(header().string("Authorization", not(nullValue())))
            .andExpect(header().string("Authorization", not(isEmptyString())));
    }

    @Test
    public void testAuthorizeFails() throws Exception {
        AuthResource.Login login = new AuthResource.Login("user-jwt-controller@example.com", "wrong password");
        mockMvc.perform(post("/api/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertToJson(login)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.id_token").doesNotExist())
            .andExpect(header().doesNotExist("Authorization"));
    }
}
