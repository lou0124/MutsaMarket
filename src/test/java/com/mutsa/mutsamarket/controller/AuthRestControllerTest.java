package com.mutsa.mutsamarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsa.mutsamarket.controller.request.Login;
import com.mutsa.mutsamarket.controller.request.SignUp;
import com.mutsa.mutsamarket.entity.Users;
import com.mutsa.mutsamarket.repository.user.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mutsa.mutsamarket.controller.response.ResponseMessageConst.LOGIN_SUCCESS;
import static com.mutsa.mutsamarket.controller.response.ResponseMessageConst.SIGNUP_SUCCESS;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 성공")
    void test1() throws Exception {

        String username = "lou0124";

        SignUp signUp = SignUp.builder()
                .username(username)
                .password("1234")
                .email("lou0124@naver.com")
                .build();

        mockMvc.perform(post("/api/auth/sign-up")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(SIGNUP_SUCCESS))
                .andDo(MockMvcResultHandlers.print());

        List<Users> all = userRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

        Users user = all.get(0);
        assertThat(user.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("로그인 성공")
    void test2() throws Exception {

        String username = "lou0124";
        String password = "1234";

        Users user = Users.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(user);

        Login login = new Login(username, password);

        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(LOGIN_SUCCESS))
                .andExpect(jsonPath("$.token", Matchers.notNullValue()))
                .andDo(MockMvcResultHandlers.print());
    }
}