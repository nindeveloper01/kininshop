package com.phoneshop.service;

import com.phoneshop.dto.*;
import com.phoneshop.entity.*;
import com.phoneshop.exception.*;
import com.phoneshop.repository.PhoneRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhoneServiceTest {

    @Mock private PhoneRepository phoneRepository;
    @Mock private BrandService brandService;

    @InjectMocks
    private PhoneService phoneService;

    private Brand apple;
    private Phone iphone;

    @BeforeEach
    void setUp() {
        apple = Brand.builder().id(1L).name("Apple").country("USA").build();
        iphone = Phone.builder()
                .id(1L).model("iPhone 15 Pro")
                .price(new BigDecimal("1199.00"))
                .stock(50)
                .brand(apple)
                .build();
    }

    @Test
    @DisplayName("findById: returns phone response for valid id")
    void findById_validId_returnsPhoneResponse() {
        when(phoneRepository.findById(1L)).thenReturn(Optional.of(iphone));

        PhoneResponse response = phoneService.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getModel()).isEqualTo("iPhone 15 Pro");
        assertThat(response.getBrandName()).isEqualTo("Apple");
        assertThat(response.getPrice()).isEqualByComparingTo("1199.00");
    }

    @Test
    @DisplayName("findById: throws ResourceNotFoundException for unknown id")
    void findById_unknownId_throwsNotFoundException() {
        when(phoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> phoneService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("create: saves phone and returns response")
    void create_validRequest_savesAndReturnsResponse() {
        PhoneRequest request = new PhoneRequest();
        request.setModel("Galaxy S24");
        request.setPrice(new BigDecimal("899.00"));
        request.setStock(30);
        request.setBrandId(1L);

        Brand samsung = Brand.builder().id(1L).name("Samsung").country("South Korea").build();
        Phone savedPhone = Phone.builder()
                .id(5L).model("Galaxy S24")
                .price(new BigDecimal("899.00"))
                .stock(30).brand(samsung).build();

        when(brandService.getBrandOrThrow(1L)).thenReturn(samsung);
        when(phoneRepository.save(any(Phone.class))).thenReturn(savedPhone);

        PhoneResponse response = phoneService.create(request);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getModel()).isEqualTo("Galaxy S24");
        verify(phoneRepository).save(any(Phone.class));
    }

    @Test
    @DisplayName("create: throws ResourceNotFoundException when brand not found")
    void create_brandNotFound_throwsException() {
        PhoneRequest request = new PhoneRequest();
        request.setModel("Test");
        request.setPrice(BigDecimal.TEN);
        request.setStock(1);
        request.setBrandId(999L);

        when(brandService.getBrandOrThrow(999L))
                .thenThrow(new ResourceNotFoundException("Brand", 999L));

        assertThatThrownBy(() -> phoneService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("delete: deletes existing phone")
    void delete_existingPhone_deletesSuccessfully() {
        when(phoneRepository.existsById(1L)).thenReturn(true);

        phoneService.delete(1L);

        verify(phoneRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete: throws ResourceNotFoundException for unknown id")
    void delete_unknownId_throwsNotFoundException() {
        when(phoneRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> phoneService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(phoneRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("findAll: returns paginated results")
    void findAll_noKeyword_returnsPaginatedResults() {
        Page<Phone> page = new PageImpl<>(List.of(iphone), PageRequest.of(0, 10), 1);
        when(phoneRepository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<PhoneResponse> result = phoneService.findAll(0, 10, null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("findAll: uses keyword search when keyword provided")
    void findAll_withKeyword_callsSearchMethod() {
        Page<Phone> page = new PageImpl<>(List.of(iphone), PageRequest.of(0, 10), 1);
        when(phoneRepository.searchByKeyword(eq("apple"), any(Pageable.class))).thenReturn(page);

        PageResponse<PhoneResponse> result = phoneService.findAll(0, 10, "apple");

        assertThat(result.getContent()).hasSize(1);
        verify(phoneRepository).searchByKeyword(eq("apple"), any(Pageable.class));
        verify(phoneRepository, never()).findAll(any(Pageable.class));
    }
}
