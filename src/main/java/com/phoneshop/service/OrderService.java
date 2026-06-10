package com.phoneshop.service;

import com.phoneshop.dto.*;
import com.phoneshop.entity.*;
import com.phoneshop.exception.*;
import com.phoneshop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PhoneService phoneService;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Order order = Order.builder()
                .user(user)
                .totalPrice(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Phone phone = phoneService.getPhoneOrThrow(itemReq.getPhoneId());

            // Stock validation — core business rule
            if (phone.getStock() < itemReq.getQuantity()) {
                throw new InsufficientStockException(phone.getModel(), itemReq.getQuantity(), phone.getStock());
            }

            // Deduct stock
            phone.setStock(phone.getStock() - itemReq.getQuantity());

            BigDecimal unitPrice = phone.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .phone(phone)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(unitPrice)
                    .build();

            order.addItem(item);
        }

        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);
        log.info("Order {} placed for user {}", saved.getId(), userEmail);

        return toResponse(saved);
    }

    public OrderResponse findById(Long id, String userEmail) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        // Users can only see their own orders; admins can see all
        User caller = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        if (caller.getRole() != User.Role.ADMIN
                && !order.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You are not allowed to view this order");
        }

        return toResponse(order);
    }

    public PageResponse<OrderResponse> findMyOrders(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findByUserId(user.getId(), pageable);

        return PageResponse.<OrderResponse>builder()
                .content(orders.getContent().stream().map(this::toResponse).toList())
                .page(orders.getNumber())
                .size(orders.getSize())
                .totalElements(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .last(orders.isLast())
                .build();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .phoneId(item.getPhone().getId())
                        .phoneModel(item.getPhone().getModel())
                        .brandName(item.getPhone().getBrand().getName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .subtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .items(itemResponses)
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
