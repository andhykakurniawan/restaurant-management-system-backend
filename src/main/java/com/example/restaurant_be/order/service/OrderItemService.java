package com.example.restaurant_be.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.common.exception.ConflictException;
import com.example.restaurant_be.common.exception.NotFoundException;
import com.example.restaurant_be.menu.entity.Menu;
import com.example.restaurant_be.menu.repository.MenuRepository;
import com.example.restaurant_be.order.dto.OrderItemRequest;
import com.example.restaurant_be.order.dto.OrderItemResponse;
import com.example.restaurant_be.order.entity.Order;
import com.example.restaurant_be.order.entity.OrderItem;
import com.example.restaurant_be.order.entity.Status;
import com.example.restaurant_be.order.repository.OrderItemRepository;
import com.example.restaurant_be.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderItemService {
        private final OrderItemRepository orderItemRepository;
        private final OrderRepository orderRepository;
        private final MenuRepository menuRepository;

        public List<OrderItemResponse> findAll(UUID orderId) {

                orderRepository.findById(orderId)
                                .orElseThrow(() -> new NotFoundException("Order not found"));

                return orderItemRepository.findByOrder_Id(orderId)
                                .stream()
                                .map(this::toResponse)
                                .toList();
        }

        private void validateOrderEditable(Order order) {

                if (order.getStatus() != Status.OPEN) {
                        throw new ConflictException("Order already locked");
                }
        }

        public OrderItemResponse create(UUID orderId, OrderItemRequest request) {

                Order order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new NotFoundException("Order not found"));

                validateOrderEditable(order);

                Menu menu = menuRepository.findById(request.menuId())
                                .orElseThrow(() -> new NotFoundException("Menu not found"));

                BigDecimal price = menu.getPrice();

                Optional<OrderItem> existingItem = orderItemRepository.findByOrder_IdAndMenu_Id(orderId,
                                request.menuId());

                OrderItem item;

                if (existingItem.isPresent()) {

                        item = existingItem.get();

                        int newQty = item.getQuantity() + request.quantity();

                        BigDecimal newSubtotal = price.multiply(BigDecimal.valueOf(newQty));

                        item.setQuantity(newQty);
                        item.setSubTotal(newSubtotal);

                } else {

                        item = new OrderItem();

                        item.setOrder(order);
                        item.setMenu(menu);
                        item.setQuantity(request.quantity());
                        item.setPriceSnapshot(price);

                        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(request.quantity()));

                        item.setSubTotal(subtotal);
                }

                OrderItem saved = orderItemRepository.save(item);

                recalcOrderTotal(order);

                return toResponse(saved);
        }

        private void recalcOrderTotal(Order order) {

                List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());

                BigDecimal total = items.stream()
                                .map(OrderItem::getSubTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                order.setTotalAmount(total);

                orderRepository.save(order);
        }

        public OrderItemResponse update(UUID orderId, UUID itemId, OrderItemRequest request) {

                OrderItem item = orderItemRepository.findById(itemId)
                                .orElseThrow(() -> new NotFoundException("Item not found"));

                validateItemBelongsToOrder(item, orderId);

                BigDecimal price = item.getPriceSnapshot();

                BigDecimal newSubtotal = price.multiply(BigDecimal.valueOf(request.quantity()));

                Order order = item.getOrder();

                validateOrderEditable(order);

                order.setTotalAmount(
                                order.getTotalAmount()
                                                .subtract(item.getSubTotal())
                                                .add(newSubtotal));

                item.setQuantity(request.quantity());
                item.setSubTotal(newSubtotal);

                orderRepository.save(order);
                OrderItem saved = orderItemRepository.save(item);

                return toResponse(saved);
        }

        public void delete(UUID orderId, UUID itemId) {

                OrderItem item = orderItemRepository.findById(itemId)
                                .orElseThrow(() -> new NotFoundException("Item not found"));

                validateItemBelongsToOrder(item, orderId);

                Order order = item.getOrder();

                order.setTotalAmount(
                                order.getTotalAmount().subtract(item.getSubTotal()));

                validateOrderEditable(order);
                
                orderRepository.save(order);

                orderItemRepository.delete(item);
        }

        private OrderItemResponse toResponse(OrderItem orderItem) {
                return new OrderItemResponse(
                                orderItem.getId(),
                                orderItem.getMenu().getId(),
                                orderItem.getMenu().getName(),
                                orderItem.getQuantity(),
                                orderItem.getPriceSnapshot(),
                                orderItem.getSubTotal());
        }

        private void validateItemBelongsToOrder(OrderItem item, UUID orderId) {
                if (!item.getOrder().getId().equals(orderId)) {
                        throw new ConflictException("Item does not belong to order");
                }
        }

}
