# Product Management System - Setup Guide

## Project Structure Created

I've built a complete product management system with the following components:

### Backend Components
1. **Entity** (`Product.java`)
   - Fields: id, name, price, categoryName, image
   - JPA entity mapped to `products` table

2. **Repository** (`ProductRepository.java`)
   - Spring Data JPA repository for database operations

3. **Service** (`ProductService.java`)
   - Business logic for product management
   - CRUD operations

4. **Controllers**
   - `ProductController.java` - Handles product management routes
   - `HomeController.java` - Redirects root path to products

### Frontend Templates (Thymeleaf)
1. **list.html** - Display all products with Edit/Delete buttons
2. **form.html** - Create/Edit product form

### Database Configuration
- **Database**: MySQL `bai5_qlsp`
- **Hibernate**: Auto-create/update tables

---

## Getting Started

### 1. Create MySQL Database
```sql
CREATE DATABASE bai5_qlsp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Update Database Credentials (if needed)
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bai5_qlsp
spring.datasource.username=root
spring.datasource.password=your_password_here
```

Default: **username=root, password=empty**

### 3. Run the Application
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
cd "C:\Users\Nhan\OneDrive\Download\J2EE_NguyenThanhNhanj_Buoi6"
mvn spring-boot:run
```

Or run the JAR directly:
```powershell
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 4. Access the Application
- URL: `http://localhost:8080`
- You'll be redirected to `http://localhost:8080/products`

---

## Features Implemented

✅ **View all products** - Display products in a table format
✅ **Create product** - Form to add new products
✅ **Edit product** - Update existing products
✅ **Delete product** - Remove products with confirmation
✅ **Database persistence** - MySQL integration
✅ **Responsive UI** - Professional styling with Bootstrap-like design

---

## Product Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Product name |
| price | Double | Yes | Product price |
| categoryName | String | Yes | Category/Type |
| image | String | No | Image URL |

---

## Sample Data (Optional)
To add sample products, you can use SQL:
```sql
INSERT INTO products (name, price, category_name, image) VALUES 
('Lenovo ThinkPad T13 15.6\" Laptop Intel Core i7 08100 11USB SSD 16GB RAM FHD', 27000, 'Laptop', 'https://example.com/laptop.jpg'),
('iPhone 16 Pro Max 1TB', 41990, 'Điện thoại', 'https://example.com/iphone.jpg');
```

---

## Troubleshooting

### Database Connection Error
- Ensure MySQL is running
- Check username/password in application.properties
- Verify database `bai5_qlsp` exists

### Port 8080 Already In Use
- Change port in `application.properties`: `server.port=8081`

### Dependencies Not Downloaded
- Delete `target` folder
- Run: `mvn clean install -DskipTests`

---

## Project Created By
GitHub Copilot - Spring Boot Full Stack Development
