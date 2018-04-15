package io.github.pronto.markov.web.rest;

import java.util.List;

import io.github.pronto.markov.MarkovTextGeneratorApp;
import io.github.pronto.markov.domain.User;
import io.github.pronto.markov.repository.UserRepository;
import io.github.pronto.markov.security.annotation.AdminRole;
import io.github.pronto.markov.security.annotation.UserRole;
import io.github.pronto.markov.service.UserService;
import io.github.pronto.markov.service.dto.UserDTO;
import io.github.pronto.markov.web.rest.errors.ExceptionTranslator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MarkovTextGeneratorApp.class)
public class UserResourceIntTest {

    private UserDTO defaultUser;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;
    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
    @Autowired
    private ExceptionTranslator exceptionTranslator;
    private MockMvc restUserMockMvc;

    @Before
    public void setup() {
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(new UserResource(userRepository, userService))
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter)
            .build();
    }

    @Before
    public void initTest() {
        defaultUser = userService.createUser(UserDTO.builder()
            .email("johndoe@localhost")
            .password("passjohndoe")
            .authorities(singleton(UserRole.name))
            .build());
    }

    @After
    public void clean() {
        userRepository.deleteAll();
    }

    private static UserDTO createRandomUser() {
        return UserDTO.builder()
            .password(RandomStringUtils.random(60))
            .email(RandomStringUtils.randomAlphabetic(5) + "@test.com")
            .authorities(singleton(UserRole.name))
            .build();
    }

    @Test
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User
        UserDTO user = createRandomUser();

        restUserMockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertToJson(user)))
            .andExpect(status().isCreated());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate + 1);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        UserDTO user = createRandomUser().toBuilder().id(defaultUser.getId()).build();
        restUserMockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertToJson(user)))
            .andExpect(status().isBadRequest());
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void createUserWithExistingEmail() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        restUserMockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertToJson(createRandomUser().toBuilder().email(defaultUser.getEmail()).build())))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllUsers() throws Exception {

        restUserMockMvc.perform(get("/api/users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.[*].email").value(hasItem(defaultUser.getEmail())));
    }

    @Test
    public void getUser() throws Exception {

        restUserMockMvc.perform(get("/api/user/{email}", defaultUser.getEmail()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.email").value(defaultUser.getEmail()));
    }

    @Test
    public void getNonExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/user/unknown"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUser() throws Exception {
        int databaseSizeBeforeDelete = userRepository.findAll().size();

        // Delete the user
        restUserMockMvc.perform(delete("/api/users/{email}", defaultUser.getEmail()))
            .andExpect(status().is2xxSuccessful());

        // Validate the database is empty
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void getAllAuthorities() throws Exception {
        restUserMockMvc.perform(get("/api/users/authorities"))
            .andExpect(status().is2xxSuccessful())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").value(containsInAnyOrder(UserRole.name, AdminRole.name)));
    }

}
