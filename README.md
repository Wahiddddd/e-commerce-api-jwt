# E-Commerce JWT API

Project backend e-commerce sederhana menggunakan Spring Boot dan JWT untuk autentikasi.

## Fitur Utama
- **Autentikasi**: Registrasi dan Login menggunakan JWT.
- **Produk**: Tambah, Edit, Hapus, dan Lihat produk (Berbasis Role & Ownership).
- **Keranjang & Order**: Manajemen keranjang belanja dan proses checkout.

## Info Deployment Khusus (Wahid - Student 7)
- **Folder VPS**: `/home/student/spring-app/student7`
- **Port Aplikasi**: `9007`
- **Database**: `db_spring_student7`

---

## Cara Menjalankan di Lokal

### Prasyarat
- Java 17
- MySQL
- Maven

### Langkah-langkah
1. Clone repository:
   ```bash
   git clone https://github.com/USERNAME/REPOSITORY_NAME.git
   cd e-commerce-JWT
   ```
2. Buat database di MySQL:
   ```sql
   CREATE DATABASE db_spring_student7;
   ```
3. Jalankan program:
   ```bash
   mvn spring-boot:run
   ```
4. Akses API di: `http://localhost:9007`

---

## Step-by-Step Deployment ke VPS ( Wahid )

### 1. Masuk ke VPS
Gunakan SSH (pastikan Anda punya IP dan Password dari mentor):
```bash
ssh username@IP_ADDRESS_VPS
```

### 2. Masuk ke Folder Anda
Sesuai pemetaan, masuk ke folder `student7`:
```bash
cd /home/student/spring-app/student7
```

### 3. Deploy Menggunakan Docker (Rekomendasi)
Gunakan `docker-compose.yml` berikut (sesuaikan password database sesuai info mentor):
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "9007:9007"
    environment:
      - SERVER_PORT=9007
      - SPRING_DATASOURCE_URL=jdbc:mysql://db_student7:3306/db_spring_student7
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=rootpassword
    depends_on:
      - db_student7
  db_student7:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=db_spring_student7
    ports:
      - "3307:3306"
```

Jalankan:
```bash
docker-compose up -d
```

### 4. Link API Publik
API dapat diakses di:
`http://[IP_ADDRESS_VPS]:9007`

---

## Pengujian API
Endpoint Utama:
- `POST /api/users/register`: Pendaftaran user baru.
- `POST /api/users/login`: Login untuk mendapatkan token.
- `GET /api/products/my-products`: (Khusus Seller) Melihat produk sendiri.
