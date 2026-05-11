# Restaurant Management System - Backend

## Overview

Restaurant Management System Backend is a fullstack-oriented backend service designed to manage restaurant operational workflows including authentication, order management, payment processing, reporting, attendance, booking, table management, and shift scheduling.

This project was built using Spring Boot with a modular domain-based architecture to support scalable and maintainable development.

---

# Features

## Authentication & Authorization

* JWT Authentication
* Role-based access control
* Custom user details implementation
* Protected API endpoints

## Restaurant Operations

* Order Management
* Order Session Management
* QR Code Session Support
* Table Management
* Booking Management
* Attendance Tracking
* Shift Management

## Payment System

* Midtrans Integration
* Payment Status Tracking
* Multiple Payment Methods

## Reporting & Analytics

* Daily Sales Report
* Monthly Sales Report
* Yearly Sales Report
* Revenue Aggregation
* Sales By Cashier
* Top Selling Menu
* Payment Method Analytics

## Master Data Management

* Menu Management
* Category Management
* Ingredient Management
* Menu Category Management
* Menu Ingredient Management
* User Management

---

# Tech Stack

## Backend

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* PostgreSQL
* Maven

## Payment Integration

* Midtrans API

## Development Tools

* Git
* GitHub
* Postman
* IntelliJ IDEA / VSCode

---

# Architecture

This project uses a modular feature-based architecture.

```text
src/main/java/com/example/restaurant_be
│
├── attendance
├── auth
├── booking
├── category
├── common
├── config
├── ingredient
├── menu
├── menucategory
├── menuingredient
├── order
├── ordersession
├── payment
├── report
├── security
├── shift
├── table
└── user
```

Each module contains:

```text
controller/
service/
repository/
dto/
entity/
```

This structure helps maintain scalability, separation of concerns, and cleaner business logic management.

---

# Security

The application uses:

* JWT Token Authentication
* Spring Security
* Custom Authentication Entry Point
* Access Denied Handler
* Role-based authorization

Sensitive credentials are stored using environment variables.

Example:

```yaml
midtrans:
  server-key: ${MIDTRANS_SERVER_KEY}
  client-key: ${MIDTRANS_CLIENT_KEY}
```

---

# API Modules

## Authentication

* Login
* JWT Token Generation
* User Authentication

## Order System

* Create Order
* Update Order Status
* Manage Order Session
* QR Session Support

## Payment

* Payment Creation
* Payment Status Update
* Midtrans Integration

## Reporting

* Revenue Reports
* Sales Aggregation
* Top Menu Statistics
* Cashier Performance

---

# Environment Variables

Create a `.env` or environment configuration for:

```env
MIDTRANS_SERVER_KEY=your_midtrans_server_key
MIDTRANS_CLIENT_KEY=your_midtrans_client_key
JWT_SECRET=your_jwt_secret
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
```

---

# Database

Database used:

* PostgreSQL

Recommended tools:

* pgAdmin
* DBeaver

---

# Installation & Setup

## Clone Repository

```bash
git clone https://github.com/andhykakurniawan/restaurant-management-system-backend.git
```

## Navigate to Project

```bash
cd restaurant-management-system-backend
```

## Configure Database

Update `application.yml` configuration.

---

## Run Application

Using Maven:

```bash
./mvnw spring-boot:run
```

Or:

```bash
mvn spring-boot:run
```

---

# Future Improvements

* WebSocket Realtime Kitchen Updates
* Inventory Management System
* Advanced Dashboard Analytics
* Docker Containerization
* CI/CD Pipeline
* Unit & Integration Testing

---

# Project Status

Current Progress:

* Backend: ~80%
* Frontend: In Progress

Implemented modules include:

* Authentication
* Reporting
* Payment Integration
* Attendance
* Shift Management
* Table Management
* Booking System
* Order Session Management

---

# Author

Andhy Kurniawan

GitHub:
[https://github.com/andhykakurniawan](https://github.com/andhykakurniawan)
