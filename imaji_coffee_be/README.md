# ☕ IMAJI COFFEE BACKEND

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 🚀 Overview
IMAJI COFFEE Backend is a **production-ready e-commerce API** built with Spring Boot.  
It supports **real-time chat, secure authentication, order processing**, and is fully **Dockerized for deployment**.

Designed with scalability and real-world practices in mind.

---

## ✨ Key Features
- 🔐 JWT Authentication & Authorization
- 👥 Role-based access (Admin / Customer)
- 🛒 Product & Order Management
- 💳 Payment Integration
- 📧 Email Notifications
- 💬 Real-time Chat (WebSocket + STOMP)
- ⚡ Caching with Caffeine
- 📊 Logging & Monitoring
- 📄 Swagger API Docs
- 🐳 Dockerized (1-command setup)
- 🔁 CI/CD Ready

---

## 🏗️ Architecture

```text
Client (Web / Mobile)
        │
        ▼
   REST API (Spring Boot)
        │
 ┌───────────────┐
 │  Service Layer│
 └───────────────┘
        │
 ┌───────────────┐
 │ Repository    │
 │ (JPA/Hibernate)
 └───────────────┘
        │
        ▼
      MySQL

WebSocket (Real-time Chat)
        ▲
        │
   STOMP Broker

```

## ⚙️ How to Run

### 🐳 Run with Docker (Recommended)

1. Clone the repository:
```bash
git clone https://github.com/sDuyToans/imaji_coffee_be
cd imaji_coffee_be
```
2.	Create a .env file in the root folder:
```
imaji_coffee_be/
├── docker-compose.yml
├── Dockerfile
├── .env   ← HERE
├── src/
├── target/
# Server
SERVER_PORT=8080

# Database
DATABASE_HOST=mysql
DATABASE_PORT=3306
DATABASE_NAME=imajicoffee
DATABASE_USERNAME=root
DATABASE_PASSWORD=root

# JWT
JWT_SECRET=your_super_secret_key_here

# Stripe
STRIPE_SECRET_KEY=your_stripe_secret
STRIPE_WEBHOOK_SECRET=your_webhook_secret

# Email (optional)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_password

# Google OAuth (optional)
GOOGLE_CLIENT_ID=your_client_id
GOOGLE_CLIENT_SECRET=your_client_secret

# Frontend
FRONT_END_CALL_BACK_URL=http://localhost:5173/auth/callback

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:5173
```
3.	Start the application:
```bash
docker compose up --build
```
4.	Access the app:
```
•	API: http://localhost:8080
•	Swagger: http://localhost:8080/swagger-ui.html
```

