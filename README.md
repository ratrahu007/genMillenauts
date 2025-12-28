🧠 GenMillenauts – Mental Awareness Platform

A full-stack mental wellness platform designed to provide accessible psychological support using secure therapy booking, AI-powered mental health analysis, and real-time notifications, built with Spring Boot, Google Cloud Vertex AI, and modern cloud-native architecture.

🚀 Project Overview

GenMillenauts is a modular mental awareness platform that combines human therapy services with AI-driven mental health insights.
The system helps users detect early stress patterns, receive AI-based guidance, and connect with professional therapists in a secure and scalable way.

✨ Key Features

🔐 Authentication & Security

JWT-based authentication

BCrypt password encryption

Role-based access control (User / Therapist / Admin)

Secure stateless APIs




👤 User Service

User registration & login

Profile management

Session history tracking



🧑‍⚕️ Therapist Service

Therapist onboarding & profile management

Availability scheduling

Expertise-based tagging



📅 Booking Service

Therapist appointment booking

Slot conflict validation

Booking lifecycle management



💳 Payment Service

Secure session payment handling

Payment status tracking



🧠 AI Mental Health Service (Core Innovation)

AI-powered stress & emotion analysis

Mental health chat analysis

Personalized AI-generated coping suggestions

Early detection of high-risk mental health signals



🔔 Notifications

SMS notifications using Twilio

Email alerts using Spring Mail

Booking confirmations & reminders



⚙️ Asynchronous Processing

AI processing & notification handling




☁️ Google Cloud & Vertex AI Integration

The AI capabilities are powered using Google Cloud Platform (GCP) and Vertex AI.

Why Vertex AI?

Enterprise-grade security

Scalable AI inference

Managed access to Gemini / LLM models

Suitable for sensitive mental health data



AI Workflow

User Input
   ↓
Spring Boot AI Service
   ↓
Vertex AI (Gemini / Model Endpoint)
   ↓
Stress Analysis + Insights
   ↓
User Dashboard / Alerts




🔐 GCP IAM & Authentication (Important)

Instead of hardcoding API keys, this project uses IAM-based authentication.

Steps Used:

Created a GCP Service Account

Assigned required roles:

Vertex AI User

Vertex AI Viewer


Downloaded the service account JSON key

Set system environment variable:

Windows
setx GOOGLE_APPLICATION_CREDENTIALS "C:\path\to\service-account.json"


Linux / macOS
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/service-account.json"


✔ This allows the application to securely access Vertex AI without exposing secrets.

📦 GCP & Vertex AI Maven Dependencies
<!-- Google Cloud Vertex AI -->
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-vertexai</artifactId>
  <version>1.38.0</version>
</dependency>

<!-- Google Auth Library -->
<dependency>
  <groupId>com.google.auth</groupId>
  <artifactId>google-auth-library-oauth2-http</artifactId>
  <version>1.23.0</version>
</dependency>

🏗️ Project Structure
src/main/java/com/rahul/genmillenauts
│
├── aiservice        # Vertex AI & AI logic
├── userservice     # User authentication & profiles
├── therapist       # Therapist management
├── booking         # Appointment booking
├── payment         # Payment handling
├── common          # Shared utilities
├── global          # Global configs & exception handling

⚙️ Tech Stack

Backend: Java 21, Spring Boot

Security: Spring Security, JWT, BCrypt

AI: Vertex AI (Gemini models)

Cloud: Google Cloud Platform

Database: MySQL

Messaging: RabbitMQ

Notifications: Twilio (SMS), Spring Mail (Email)

Build Tool: Maven



▶️ Running the Project
Prerequisites

Java 21

Maven

MySQL

RabbitMQ

Google Cloud account with Vertex AI enabled

Run Command
./mvnw spring-boot:run


Application will start at:

http://localhost:8080



💡 Why This Project Matters

Demonstrates real-world cloud IAM security

Shows AI + healthcare domain responsibility

Uses production-grade Spring Boot architecture

Integrates GCP Vertex AI in Java (rare & valuable skill)

📌 Future Enhancements
Advanced therapist recommendation engine

Multilingual AI support

Crisis escalation workflows

👨‍💻 Author

Rahul
Full-Stack Java Developer | Cloud & AI Enthusiast
