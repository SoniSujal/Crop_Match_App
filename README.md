# 🌾 AgriMatch - Smart Crop Trading Platform

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Liquibase](https://img.shields.io/badge/Liquibase-Core-yellow.svg)](https://www.liquibase.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

> **AgriMatch** is a comprehensive full-stack crop trading platform that connects farmers and buyers through intelligent matching algorithms, secure authentication, and a modern responsive interface.

## 📋 Table of Contents

- [Overview](#-overview)
- [🛠 Tech Stack](#-tech-stack)
- [✨ Key Features](#-key-features)
- [🚀 Installation & Setup](#-installation--setup)
- [🗄️ Database Setup](#️-database-setup)
- [🏃‍♂️ Running the Application](#️-running-the-application)
- [📁 Project Structure](#-project-structure)
- [📸 Screenshots](#-screenshots)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [👨‍💻 Author](#-author)

## 🎯 Overview

AgriMatch is a sophisticated crop trading platform designed to bridge the gap between farmers and buyers. The application features role-based access control, intelligent crop matching algorithms, secure payment processing, and a modern responsive UI.

### Core Functionality:
- **Multi-role Authentication**: Secure JWT-based authentication for Farmers, Buyers, and Admin
- **Smart Matching**: Fuzzy matching algorithms for crop recommendations
- **Request Management**: Complete buyer request and farmer response workflow
- **Image Management**: Crop image upload and management system
- **Admin Dashboard**: Comprehensive admin panel for user and category management

## 🛠 Tech Stack

### Backend
- **Java 17** - Core programming language
- **Spring Boot 3.5.0** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **MySQL 8.0** - Primary database
- **Liquibase** - Database migration management
- **JWT** - Token-based authentication
- **Gradle** - Build tool and dependency management

### Frontend
- **React 18.2.0** - UI framework
- **React Router DOM** - Client-side routing
- **Axios** - HTTP client
- **React Icons** - Icon library
- **React Slick** - Carousel component
- **CSS3** - Styling

### Development Tools
- **Git** - Version control
- **Gradle** - Build automation
- **npm** - Package management
- **MySQL Workbench** - Database management

## ✨ Key Features

### 🔐 Authentication & Authorization
- JWT-based secure authentication
- Role-based access control (Farmer, Buyer, Admin)
- Protected routes and API endpoints
- Session management

### 👨‍🌾 Farmer Features
- Crop listing and management
- Image upload for crops
- Response to buyer requests
- Dashboard with analytics
- Crop availability management

### 🛒 Buyer Features
- Browse available crops
- Create crop requests
- Receive recommendations
- Order management
- Payment processing
- Request tracking

### 👨‍💼 Admin Features
- User management (view, edit, delete)
- Category management
- System monitoring
- Analytics dashboard

### 🔍 Smart Matching
- Fuzzy matching algorithms
- Category-based recommendations
- Quality and availability filtering
- Real-time matching updates

### 📱 Responsive Design
- Mobile-first approach
- Cross-browser compatibility
- Modern UI/UX design
- Dark mode support

## 🚀 Installation & Setup

### Prerequisites
- Java 17 or higher
- Node.js 16+ and npm
- MySQL 8.0+
- Git

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd CropMatchApp/cropMatch-App
   ```

2. **Configure database**
   ```bash
   # Create MySQL database
   mysql -u root -p
   CREATE DATABASE crop_match_app;
   ```

3. **Update application properties**
   ```bash
   # Edit src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/crop_match_app
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Run database migrations**
   ```bash
   ./gradlew liquibaseUpdate
   ```

5. **Build and run the backend**
   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd crop-match-frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm start
   ```

## 🗄️ Database Setup

### Liquibase Migrations
The project uses Liquibase for database schema management. Migration files are located in:
```
src/main/resources/db/changelog/
```

### Key Tables
- `user_detail` - User information
- `user_type` - Role definitions
- `category` - Crop categories
- `crop` - Crop information
- `available_crops` - Available crop listings
- `buyer_request` - Buyer requests
- `buyer_request_farmer` - Request responses
- `order` - Order management

### Running Migrations
```bash
# Apply all pending migrations
./gradlew liquibaseUpdate

# Rollback to specific version
./gradlew liquibaseRollback -PliquibaseCommandValue=<version>

# Generate SQL without executing
./gradlew liquibaseSql
```

## 🏃‍♂️ Running the Application

### Development Mode

1. **Start Backend**
   ```bash
   cd cropMatch-App
   ./gradlew bootRun
   ```
   Backend will be available at: `http://localhost:8080`

2. **Start Frontend**
   ```bash
   cd crop-match-frontend
   npm start
   ```
   Frontend will be available at: `http://localhost:3000`

### Production Build

1. **Build Backend**
   ```bash
   ./gradlew build
   java -jar build/libs/cropMatch-App-0.0.1-SNAPSHOT.jar
   ```

2. **Build Frontend**
   ```bash
   npm run build
   ```

## 📁 Project Structure

```
cropMatch-App/
├── src/main/java/com/cropMatch/
│   ├── config/                 # Configuration classes
│   ├── controller/             # REST API controllers
│   │   ├── admin/             # Admin endpoints
│   │   ├── auth/              # Authentication endpoints
│   │   ├── buyer/             # Buyer endpoints
│   │   ├── farmer/            # Farmer endpoints
│   │   └── user/              # User management
│   ├── dto/                   # Data Transfer Objects
│   ├── enums/                 # Enumeration classes
│   ├── exception/             # Custom exceptions
│   ├── model/                 # Entity models
│   ├── repository/            # Data access layer
│   ├── service/               # Business logic
│   ├── security/              # Security configuration
│   └── utils/                 # Utility classes
├── src/main/resources/
│   ├── db/changelog/          # Liquibase migrations
│   └── application.properties # Configuration
└── crop-match-frontend/
    ├── src/
    │   ├── components/        # React components
    │   │   ├── admin/         # Admin components
    │   │   ├── auth/          # Authentication
    │   │   ├── buyer/         # Buyer components
    │   │   ├── farmer/        # Farmer components
    │   │   └── common/        # Shared components
    │   ├── context/           # React context
    │   ├── services/          # API services
    │   └── styles/            # CSS files
    └── public/                # Static assets
```

## 📸 Screenshots

### Dashboard Views
![Admin Dashboard](screenshots/admin-dashboard.png)
*Admin Dashboard with user management and analytics*

![Farmer Dashboard](screenshots/farmer-dashboard.png)
*Farmer Dashboard with crop management*

![Buyer Dashboard](screenshots/buyer-dashboard.png)
*Buyer Dashboard with recommendations*

### Key Features
![Crop Management](screenshots/crop-management.png)
*Crop listing and management interface*

![Request System](screenshots/request-system.png)
*Buyer request and farmer response workflow*

![Matching Algorithm](screenshots/matching-algorithm.png)
*Smart crop matching and recommendations*

> *Note: Screenshots are placeholders. Replace with actual application screenshots.*

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Development Guidelines
- Follow Java coding conventions
- Use meaningful commit messages
- Write unit tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**AgriMatch Development Team**

- **Backend Development**: Spring Boot, Java 17
- **Frontend Development**: React 18
- **Database Design**: MySQL with Liquibase
- **DevOps**: Gradle, Git

---

<div align="center">

**Made with ❤️ for the agricultural community**

[![GitHub stars](https://img.shields.io/github/stars/your-repo/agrimatch.svg?style=social&label=Star)](https://github.com/your-repo/agrimatch)
[![GitHub forks](https://img.shields.io/github/forks/your-repo/agrimatch.svg?style=social&label=Fork)](https://github.com/your-repo/agrimatch)
[![GitHub issues](https://img.shields.io/github/issues/your-repo/agrimatch.svg)](https://github.com/your-repo/agrimatch/issues)

</div>
