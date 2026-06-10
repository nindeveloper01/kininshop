package com.phoneshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoneshop.dto.*;
import com.phoneshop.exception.*;
import com.phoneshop.security.*;
import com.phoneshop.service.PhoneService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhoneController.class)
@Import({SecurityConfig.class, JwtUtil.class, JwtFilter.class, UserDetailsServiceImpl.class, GlobalExceptionHandler.class})
class PhoneControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PhoneService phoneService;
    @MockBean private com.phoneshop.repository.UserRepository userRepository;

    private PhoneResponse samplePhone;

    @BeforeEach
    void setUp() {
        samplePhone = PhoneResponse.builder()
                .id(1L)
                .model("iPhone 15 Pro")
                .price(new BigDecimal("1199.00"))
                .stock(50)
                .brandId(1L)
                .brandName("Apple")
                .brandCountry("USA")
                .build();
    }

    // ─── GET /phones ────────────────────────────────────────────

    @Test
    @DisplayName("GET /phones - public, returns 200 with page")
    void getAll_noAuth_returns200() throws Exception {
        PageResponse<PhoneResponse> page = PageResponse.<PhoneResponse>builder()
                .content(List.of(samplePhone))
                .page(0).size(10).totalElements(1).totalPages(1).last(true)
                .build();

        when(phoneService.findAll(0, 10, null)).thenReturn(page);

        mockMvc.perform(get("/api/v1/phones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].model").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /phones?keyword=apple - uses keyword search")
    void getAll_withKeyword_returns200() throws Exception {
        PageResponse<PhoneResponse> page = PageResponse.<PhoneResponse>builder()
                .content(List.of(samplePhone)).page(0).size(10)
                .totalElements(1).totalPages(1).last(true).build();

        when(phoneService.findAll(0, 10, "apple")).thenReturn(page);

        mockMvc.perform(get("/api/v1/phones").param("keyword", "apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // ─── GET /phones/{id} ───────────────────────────────────────

    @Test
    @DisplayName("GET /phones/{id} - public, returns 200")
    void getById_existingId_returns200() throws Exception {
        when(phoneService.findById(1L)).thenReturn(samplePhone);

        mockMvc.perform(get("/api/v1/phones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.brandName").value("Apple"));
    }

    @Test
    @DisplayName("GET /phones/{id} - returns 404 for unknown id")
    void getById_unknownId_returns404() throws Exception {
        when(phoneService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Phone", 99L));

        mockMvc.perform(get("/api/v1/phones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    // ─── POST /phones ────────────────────────────────────────────

    @Test
    @DisplayName("POST /phones - requires ADMIN role, returns 201")
    @WithMockUser(roles = "ADMIN")
    void create_asAdmin_returns201() throws Exception {
        PhoneRequest request = new PhoneRequest();
        request.setModel("iPhone 15 Pro");
        request.setPrice(new BigDecimal("1199.00"));
        request.setStock(50);
        request.setBrandId(1L);

        when(phoneService.create(any())).thenReturn(samplePhone);

        mockMvc.perform(post("/api/v1/phones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.model").value("iPhone 15 Pro"));
    }

    @Test
    @DisplayName("POST /phones - returns 403 for CUSTOMER role")
    @WithMockUser(roles = "CUSTOMER")
    void create_asCustomer_returns403() throws Exception {
        PhoneRequest request = new PhoneRequest();
        request.setModel("Test");
        request.setPrice(BigDecimal.TEN);
        request.setStock(1);
        request.setBrandId(1L);

        mockMvc.perform(post("/api/v1/phones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /phones - returns 401 for unauthenticated request")
    void create_noAuth_returns401() throws Exception {
        PhoneRequest request = new PhoneRequest();
        request.setModel("Test");
        request.setPrice(BigDecimal.TEN);
        request.setStock(1);
        request.setBrandId(1L);

        mockMvc.perform(post("/api/v1/phones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /phones - returns 400 for invalid request body")
    @WithMockUser(roles = "ADMIN")
    void create_invalidBody_returns400() throws Exception {
        // Missing required fields
        mockMvc.perform(post("/api/v1/phones")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").exists());
    }

    // ─── DELETE /phones/{id} ─────────────────────────────────────

    @Test
    @DisplayName("DELETE /phones/{id} - returns 204 for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void delete_asAdmin_returns204() throws Exception {
        doNothing().when(phoneService).delete(1L);

        mockMvc.perform(delete("/api/v1/phones/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /phones/{id} - returns 404 when phone not found")
    @WithMockUser(roles = "ADMIN")
    void delete_unknownId_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Phone", 99L))
                .when(phoneService).delete(99L);

        mockMvc.perform(delete("/api/v1/phones/99").with(csrf()))
                .andExpect(status().isNotFound());
    }
}
