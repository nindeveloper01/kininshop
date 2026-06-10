package com.phoneshop.service;

import com.phoneshop.dto.*;
import com.phoneshop.entity.*;
import com.phoneshop.entity.Order;
import com.phoneshop.exception.*;
import com.phoneshop.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private PhoneService phoneService;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Phone testPhone;

    @BeforeEach
    void setUp() {
        Brand brand = Brand.builder().id(1L).name("Apple").country("USA").build();

        testUser = User.builder()
                .id(1L)
                .email("customer@test.com")
                .passwordHash("hashed")
                .role(User.Role.CUSTOMER)
                .build();

        testPhone = Phone.builder()
                .id(1L)
                .model("iPhone 15")
                .price(new BigDecimal("999.00"))
                .stock(10)
                .brand(brand)
                .build();
    }

    @Test
    @DisplayName("placeOrder: should deduct stock and return order response")
    void placeOrder_successfulOrder_deductsStockAndReturnsResponse() {
        // Arrange
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setPhoneId(1L);
        itemReq.setQuantity(2);

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setItems(List.of(itemReq));

        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(testUser));
        when(phoneService.getPhoneOrThrow(1L)).thenReturn(testPhone);

        Order savedOrder = Order.builder()
                .id(100L)
                .user(testUser)
                .totalPrice(new BigDecimal("1998.00"))
                .status(Order.Status.PENDING)
                .build();
        OrderItem savedItem = OrderItem.builder()
                .id(1L)
                .order(savedOrder)
                .phone(testPhone)
                .quantity(2)
                .unitPrice(new BigDecimal("999.00"))
                .build();
        savedOrder.setItems(List.of(savedItem));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        OrderResponse response = orderService.placeOrder(request, "customer@test.com");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getTotalPrice()).isEqualByComparingTo("1998.00");
        assertThat(response.getStatus()).isEqualTo(Order.Status.PENDING);
        assertThat(testPhone.getStock()).isEqualTo(8); // 10 - 2 = 8

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("placeOrder: should throw InsufficientStockException when stock is 0")
    void placeOrder_stockIsZero_throwsInsufficientStockException() {
        // Arrange
        testPhone.setStock(0);

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setPhoneId(1L);
        itemReq.setQuantity(1);

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setItems(List.of(itemReq));

        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(testUser));
        when(phoneService.getPhoneOrThrow(1L)).thenReturn(testPhone);

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(request, "customer@test.com"))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("iPhone 15");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder: should throw InsufficientStockException when requested > available")
    void placeOrder_requestedExceedsStock_throwsException() {
        // Arrange
        testPhone.setStock(3);

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setPhoneId(1L);
        itemReq.setQuantity(5);

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setItems(List.of(itemReq));

        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(testUser));
        when(phoneService.getPhoneOrThrow(1L)).thenReturn(testPhone);

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(request, "customer@test.com"))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("requested 5")
                .hasMessageContaining("available 3");
    }

    @Test
    @DisplayName("placeOrder: should correctly sum total for multiple items")
    void placeOrder_multipleItems_calculatesCorrectTotal() {
        // Arrange
        Brand brand = testPhone.getBrand();
        Phone phone2 = Phone.builder()
                .id(2L).model("iPhone 14")
                .price(new BigDecimal("799.00"))
                .stock(5)
                .brand(brand)
                .build();

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setPhoneId(1L);
        item1.setQuantity(1); // 999

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setPhoneId(2L);
        item2.setQuantity(2); // 799 * 2 = 1598

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setItems(List.of(item1, item2));

        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(testUser));
        when(phoneService.getPhoneOrThrow(1L)).thenReturn(testPhone);
        when(phoneService.getPhoneOrThrow(2L)).thenReturn(phone2);

        // Capture the saved order to verify total
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        Order fakeReturn = Order.builder().id(1L).user(testUser)
                .totalPrice(new BigDecimal("2597.00"))
                .status(Order.Status.PENDING).items(new ArrayList<>()).build();
        when(orderRepository.save(orderCaptor.capture())).thenReturn(fakeReturn);

        // Act
        orderService.placeOrder(request, "customer@test.com");

        // Assert total = 999 + 1598 = 2597
        Order capturedOrder = orderCaptor.getValue();
        assertThat(capturedOrder.getTotalPrice()).isEqualByComparingTo("2597.00");
    }

    @Test
    @DisplayName("placeOrder: should throw ResourceNotFoundException when user not found")
    void placeOrder_userNotFound_throwsException() {
        // Arrange
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        PlaceOrderRequest request = new PlaceOrderRequest();
        request.setItems(List.of());

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(request, "ghost@test.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findById: user can view their own order")
    void findById_ownOrder_returnsOrder() {
        // Arrange
        Order order = Order.builder()
                .id(1L)
                .user(testUser)
                .totalPrice(BigDecimal.TEN)
                .status(Order.Status.PENDING)
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(testUser));

        // Act
        OrderResponse response = orderService.findById(1L, "customer@test.com");

        // Assert
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findById: should throw AccessDeniedException for other user's order")
    void findById_otherUsersOrder_throwsAccessDeniedException() {
        // Arrange
        User otherUser = User.builder().id(2L).email("other@test.com")
                .role(User.Role.CUSTOMER).build();
        Order order = Order.builder()
                .id(1L).user(otherUser)
                .totalPrice(BigDecimal.TEN)
                .status(Order.Status.PENDING)
                .items(new ArrayList<>()).build();

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThatThrownBy(() -> orderService.findById(1L, "customer@test.com"))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}
