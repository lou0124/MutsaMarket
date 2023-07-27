package com.mutsa.mutsamarket.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutsa.mutsamarket.api.request.ItemCreate;
import com.mutsa.mutsamarket.api.request.Login;
import com.mutsa.mutsamarket.api.response.JwtResponse;
import com.mutsa.mutsamarket.entity.Item;
import com.mutsa.mutsamarket.entity.Users;
import com.mutsa.mutsamarket.entity.enumtype.ItemStatus;
import com.mutsa.mutsamarket.repository.ItemRepository;
import com.mutsa.mutsamarket.repository.UserRepository;
import com.mutsa.mutsamarket.util.EntityGetter;
import com.mutsa.mutsamarket.util.LoginUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.mutsa.mutsamarket.api.response.ResponseMessageConst.*;
import static com.mutsa.mutsamarket.util.EntityGetter.*;
import static com.mutsa.mutsamarket.util.LoginUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("아이템 등록 성공")
    void test1() throws Exception {

        String username = "lou0124";
        String password = "1234";

        Users user = getUser(username, password);

        userRepository.save(user);

        String token = loginAndGetJwtToken(mockMvc, username, password);
        String itemTitle = "중고 맥북 팝니다";

        ItemCreate itemCreate = ItemCreate.builder().title(itemTitle)
                .description("2019년 맥북 프로 13인치 모델입니다")
                .minPriceWanted(1000000)
                .build();

        mockMvc.perform(post("/items")
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(objectMapper.writeValueAsString(itemCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ITEM_CREATE))
                .andDo(print());

        List<Item> all = itemRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

        Item item = all.get(0);
        assertThat(item.getTitle()).isEqualTo(itemTitle);
    }

    @Test
    @DisplayName("아이템 페이지 조회 성공")
    void test2() throws Exception {

        String username = "lou0124";
        String password = "1234";

        Users user = getUser(username, password);

        userRepository.save(user);

        for (int i = 0; i < 30; i++) {
            Item item = getItem(user, "title" + i, "description" + i, i * 1000);
            itemRepository.save(item);
        }

        int limit = 25;

        mockMvc.perform(get("/items")
                        .param("page", "1")
                        .param("limit", String.valueOf(limit))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(limit))
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 단건 조회 성공")
    void test3() throws Exception {

        String username = "lou0124";
        String password = "1234";

        Users user = getUser(username, password);

        userRepository.save(user);

        Item item = getItem(user, "title", "description", 10000);

        itemRepository.save(item);

        mockMvc.perform(get("/items/" + item.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 수정 성공")
    void test4() throws Exception {

        String username = "lou0124";
        String password = "1234";

        Users user = getUser(username, password);
        userRepository.save(user);

        Item item = getItem(user, "title", "description", 10000);
        itemRepository.save(item);

        String token = loginAndGetJwtToken(mockMvc, username, password);

        String newItemTitle = "new title";
        ItemCreate itemCreate = ItemCreate.builder().title(newItemTitle)
                .description("2019년 맥북 프로 13인치 모델입니다")
                .minPriceWanted(1000000)
                .build();

        mockMvc.perform(put("/items/" + item.getId())
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .content(objectMapper.writeValueAsString(itemCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ITEM_UPDATE))
                .andDo(print());

        Item findItem = itemRepository.findById(item.getId()).get();

        assertThat(findItem.getTitle()).isEqualTo(newItemTitle);
    }

    @Test
    @DisplayName("아이템 삭제 성공")
    void test5() throws Exception {

        String username = "lou0124";
        String password = "1234";

        Users user = getUser(username, password);
        userRepository.save(user);

        Item item = getItem(user, "title", "description", 10000);
        itemRepository.save(item);

        String token = loginAndGetJwtToken(mockMvc, username, password);

        mockMvc.perform(delete("/items/" + item.getId())
                        .contentType(APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ITEM_DELETE))
                .andDo(print());

        assertThatThrownBy(() -> itemRepository.findById(item.getId()).get())
                .isInstanceOf(NoSuchElementException.class);
    }
}