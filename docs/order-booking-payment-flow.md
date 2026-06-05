# Order, Booking, Session, and Payment Flow

This document locks the MVP business flow for booking, table usage, orders, and payments.

## Walk-In Flow

1. Waiter selects an `AVAILABLE` table.
2. System creates an `OrderSession`.
3. Table status becomes `OCCUPIED`.
4. System creates one main `Order` for the session.
5. Waiter adds order items while the order is `OPEN`.
6. Order moves through `CONFIRMED -> PREPARING -> READY -> SERVED`.
7. Cashier creates an order payment when the order is `SERVED`.
8. Order becomes `COMPLETED`.
9. Waiter closes the session.
10. Table status becomes `AVAILABLE`.

## Booking Flow

1. Customer creates a booking for an online table.
2. Booking status is `WAITING_PAYMENT`.
3. Table is not marked `OCCUPIED` during payment waiting.
4. Midtrans payment success changes booking to `CONFIRMED`.
5. Table status becomes `RESERVED`.
6. Customer arrives and waiter checks in the booking.
7. System creates an `OrderSession` from the booking.
8. Booking status becomes `CHECKED_IN`.
9. Table status becomes `OCCUPIED`.
10. Order and payment follow the same flow as walk-in.

## Payment Rules

- Booking payment is reservation DP handled by Midtrans callback.
- Order payment is food and drink payment handled by cashier.
- For MVP, booking DP is not automatically deducted from the order total.

## MVP Constraints

- One active `OrderSession` has one main `Order`.
- Booking does not include preorder menu items yet.
- Order items can only be changed while the order is `OPEN`.
- Order payment is only allowed when the order is `SERVED`.
- Session close is blocked while it has an unfinished order.
