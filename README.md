# Banking System API

A comprehensive REST API for a banking system built with Spring Boot, featuring secure authentication, account management, and financial transactions.

## 🚀 Features

- **User Authentication & Authorization**
  - JWT-based authentication
  - Role-based access control (USER/ADMIN)
  - Secure password management

- **Account Management**
  - Create, read, update, and delete accounts
  - Deposit and withdrawal operations
  - Account ownership validation

- **Financial Operations**
  - Money transfers between accounts
  - Transaction history tracking
  - Balance management

- **Security Features**
  - Password encryption with BCrypt
  - JWT token validation
  - Role-based endpoint protection
  - Input validation and sanitization

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.13
- **Language**: Java 21
- **Database**: MySQL 9.6+ with Flyway migrations
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Validation**: Jakarta Validation API

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 9.6+ Server
- Git

## ⚙️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd banking-system
```

### 2. Database Setup
Create a MySQL database named `Banking`:
```sql
CREATE DATABASE Banking;
```

### 3. Environment Configuration
Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/Banking
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### 4. Build and Run
```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔐 Authentication

The API uses JWT (JSON Web Tokens) for authentication. Include the JWT token in the Authorization header for protected endpoints:

```
Authorization: Bearer <your-jwt-token>
```

## 📚 API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securepassword",
  "role": "USER"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securepassword"
}
```

**Response**: JWT token string

#### Change Password
```http
POST /auth/change-password
Authorization: Bearer <token>
Content-Type: application/json

{
  "oldPassword": "currentpassword",
  "newPassword": "newsecurepassword"
}
```

#### Get User Profile
Retrieves the profile information of the currently authenticated user from the JWT bearer token.

```http
GET /auth/profile
Authorization: Bearer <token>
```

**Response:**
```json
{
  "id": 1,
  "username": "admin_user",
  "role": "ROLE_ADMIN"
}
```

**Example:**
```bash
curl -H "Authorization: Bearer <token>" \
  http://localhost:8080/auth/profile
```

### Account Endpoints

#### Create Account (ADMIN only)
```http
POST /accounts
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "address": "123 Main St",
  "mobileNumber": "1234567890",
  "emailId": "john.doe@example.com",
  "initialBalance": 1000.00
}
```

#### Get Account Details
```http
GET /accounts/{accountId}
Authorization: Bearer <token>
```

#### Get All Accounts
```http
GET /accounts/all
Authorization: Bearer <token>
```

#### Get My Accounts
Retrieves accounts based on the authenticated user from the JWT bearer token:
- **Regular users** - Returns their own accounts only
- **ADMIN users** - Returns all accounts in the system

The endpoint automatically identifies the user from the JWT token, no username parameter needed.

```http
GET /accounts/my-accounts
Authorization: Bearer <token>
```

**Response:** Array of AccountResponse objects

**Examples:**
```bash
# User viewing their own accounts
curl -H "Authorization: Bearer <user-token>" \
  http://localhost:8080/accounts/my-accounts

# Admin viewing all accounts in the system
curl -H "Authorization: Bearer <admin-token>" \
  http://localhost:8080/accounts/my-accounts
```

#### Update Account (ADMIN only)
```http
PUT /accounts/{accountId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Doe",
  "address": "456 Oak St",
  "mobileNumber": "0987654321",
  "emailId": "jane.doe@example.com"
}
```

#### Delete Account (ADMIN only)
```http
DELETE /accounts/{accountId}
Authorization: Bearer <token>
```

#### Deposit Money
```http
POST /accounts/{accountId}/deposit?amount=500.00
Authorization: Bearer <token>
```

#### Withdraw Money
```http
POST /accounts/{accountId}/withdraw?amount=200.00
Authorization: Bearer <token>
```

### Transfer Endpoints

#### Transfer Money
```http
POST /transfers
Authorization: Bearer <token>
Content-Type: application/json

{
  "sourceAccountId": 1,
  "targetAccountId": 2,
  "amount": 100.00
}
```

#### Get Transfer Details
```http
GET /transfers/{transferId}
Authorization: Bearer <token>
```

### Transaction Endpoints

#### Get My Transactions
Retrieves transactions based on the authenticated user from the JWT bearer token:
- **Regular users** - Returns transactions from all their accounts only
- **ADMIN users** - Returns all transactions in the system

The endpoint automatically identifies the user from the JWT token, no parameters needed.

```http
GET /transactions/my-transactions
Authorization: Bearer <token>
```

**Response:** Array of TransactionResponse objects

**Examples:**
```bash
# User viewing their own transactions
curl -H "Authorization: Bearer <user-token>" \
  http://localhost:8080/transactions/my-transactions

# Admin viewing all transactions in the system
curl -H "Authorization: Bearer <admin-token>" \
  http://localhost:8080/transactions/my-transactions
```

#### Get Transactions by Account
Returns transactions for a specific account:
- **Regular users** can only view transactions from their own accounts
- **ADMIN users** can view transactions from any account

```http
GET /transactions/account/{accountId}
Authorization: Bearer <token>
```

**Parameters:**
- `accountId` (path parameter) - The account ID to fetch transactions for

**Response:** Array of TransactionResponse objects

**Examples:**
```bash
# User viewing their own account transactions
curl -H "Authorization: Bearer <user-token>" \
  http://localhost:8080/transactions/account/1

# Admin viewing any account's transactions
curl -H "Authorization: Bearer <admin-token>" \
  http://localhost:8080/transactions/account/2
```

#### Get Transactions by Type
```http
GET /transactions/account/{accountId}/type?type=CREDIT
Authorization: Bearer <token>
```

#### Get Transactions by Time Range
```http
GET /transactions/account/{accountId}/range?startTime=<timestamp>&endTime=<timestamp>
Authorization: Bearer <token>
```

#### Get Transaction Details
```http
GET /transactions/{id}
Authorization: Bearer <token>
```

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    created_timestamp BIGINT
);
```

### Accounts Table
```sql
CREATE TABLE accounts (
    account_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    mobile_number VARCHAR(255) NOT NULL,
    email_id VARCHAR(255) NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    total_transaction_value DECIMAL(15,2) DEFAULT 0.00,
    created_timestamp BIGINT,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY,
    account_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    timestamp BIGINT,
    type ENUM('CREDIT', 'DEBIT'),
    reference_id BIGINT,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
```

### Transfers Table
```sql
CREATE TABLE transfers (
    transfer_id BIGINT PRIMARY KEY,
    source_account_id BIGINT,
    target_account_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    timestamp BIGINT,
    accepted BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (source_account_id) REFERENCES accounts(account_id),
    FOREIGN KEY (target_account_id) REFERENCES accounts(account_id)
);
```

## 🔒 Security Configuration

### Role-Based Access Control
- **USER**: Can view their own accounts, perform transfers, deposits, and withdrawals
- **ADMIN**: Full access including account creation, updates, and deletions

### JWT Configuration
- Algorithm: HMAC-SHA (256-bit minimum key length)
- Token expiration: Configurable
- Secure key storage: Environment-based configuration

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

### Test Coverage
- Unit tests for all services
- Integration tests for controllers
- Security tests for authentication
- Database integration tests

## 🚀 Deployment

### Production Configuration
1. Set environment variables for database credentials
2. Configure JWT secret key securely
3. Enable SSL/TLS
4. Set up proper logging
5. Configure database connection pooling

### Docker Support
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/banking-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📊 Monitoring & Logging

- Spring Boot Actuator endpoints available at `/actuator`
- Structured logging with configurable levels
- Database query logging enabled in development

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the HELP.md file for additional documentation
- Review the Spring Boot documentation

## 🔄 Future Enhancements

- [ ] Two-factor authentication (2FA)
- [ ] Account statement generation
- [ ] Scheduled transfers
- [ ] API rate limiting
- [ ] Audit logging
- [ ] Email notifications
- [ ] Swagger API documentation
- [ ] Pagination for list endpoints
- [ ] Advanced search and filtering</content>
<parameter name="filePath">/Users/venkataraghavendratejaswidevaguptapu/Downloads/banking-system/README.md
