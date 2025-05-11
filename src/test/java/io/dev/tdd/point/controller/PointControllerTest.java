package io.dev.tdd.point.controller;

import io.dev.tdd.point.PointHistory;
import io.dev.tdd.point.UserPoint;
import io.dev.tdd.point.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;

@AutoConfigureMockMvc
@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    private UserPoint userPoint;

    @BeforeEach
    void setUp() {
        userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());
    }

    @Test
    void testGetPoint() throws Exception {
        // given
        long id = 1L;
        given(pointService.getPoint(id)).willReturn(userPoint);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}", id));

        //then
        resultActions .andExpect(MockMvcResultMatchers.status().isOk()) // 200 OK
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id)) // id 값 확인
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    void testGetHistory() throws Exception {
        // given
        long id = 1L;
        List<PointHistory> pointHistoryList = Collections.emptyList();
        given(pointService.getHistories(id)).willReturn(pointHistoryList);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/point/{id}/history", id));

        //then
        resultActions .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    void testCharge() throws Exception {
        // given
        long id = 1L;
        long amount = 500L;
        given(pointService.charge(id, amount)).willReturn(userPoint);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/point/{id}/charge", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(amount)));

        //then
        resultActions .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    void testUse() throws Exception {
        // given
        long id = 1L;
        long amount = 500L;
        given(pointService.use(id, amount)).willReturn(userPoint);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/point/{id}/use", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(amount)));

        //then
        resultActions .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

}