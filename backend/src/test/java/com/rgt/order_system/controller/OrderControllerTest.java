package com.rgt.order_system.controller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        // MockMvc 및 Mockito 초기화
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    // 테스트용 주문 만들기
    @Test
    void testCreateOrder() throws Exception {
        // 주문 요청 JSON 데이터
        String orderJson = """
        {
            "foodName": "Pizza",
            "quantity": 2,
            "status": "ORDERED"
        }
        """;

        // 주문 생성 API 테스트
        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("주문이 접수되었습니다."));
    }

    // 테스트용 주문 변경
    @Test
    void testUpdateOrderStatus_Success() throws Exception {
        // 주문추가
        String orderJson = """
    {
        "foodName": "Pizza",
        "quantity": 2
    }
    """;

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(orderJson))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/order/0")
                        .param("status", "조리중")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .characterEncoding("UTF-8")) // 요청 인코딩 설정
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8")) // 응답 인코딩 확인
                .andExpect(content().string("주문 상태가 업데이트되었습니다."));
    }

    // 테스트용 없는 주문 확인
    @Test
    void testUpdateOrderStatus_InvalidIndex() throws Exception {
        mockMvc.perform(put("/order/100") // 존재하지 않는 인덱스
                        .param("status", "조리중")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .characterEncoding("UTF-8")) // 요청 인코딩
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8")) // 응답 인코딩 확인
                .andExpect(content().string("잘못된 주문 인덱스입니다."));
    }

}
