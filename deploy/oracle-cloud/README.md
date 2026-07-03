# Deploy lên Oracle Cloud (Always Free VM)

Hướng dẫn chạy **Member Management** trên VM miễn phí Oracle Cloud với Docker + MySQL.

## Tổng quan

```
Internet → Oracle VM (port 8080) → Docker: app (Spring Boot) → Docker: mysql
```

## Bước 1 — Tạo VM Always Free

1. Đăng ký [Oracle Cloud Free Tier](https://www.oracle.com/cloud/free/).
2. **Compute → Instances → Create instance**
3. Gợi ý cấu hình:
   - **Image:** Ubuntu 22.04
   - **Shape:** `VM.Standard.A1.Flex` (Ampere, Always Free) — 1 OCPU, 6 GB RAM
   - **Networking:** public subnet, gán public IP
   - **SSH key:** thêm public key của bạn
4. **Security List / Ingress rules** (VCN → Security Lists):
   | Port | Source | Mục đích |
   |------|--------|----------|
   | 22 | Your IP/0.0.0.0/0 | SSH |
   | 8080 | 0.0.0.0/0 | Spring Boot app |
5. (Tuỳ chọn) Mở port **80/443** nếu sau này gắn Nginx + HTTPS.

## Bước 2 — SSH vào VM

```bash
ssh ubuntu@<PUBLIC_IP>
```

## Bước 3 — Cài Docker

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y git ca-certificates curl

# Docker official install (Ubuntu)
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker

docker --version
docker compose version
```

## Bước 4 — Clone project

```bash
git clone https://github.com/thanhnv-3702/foods_and_drinks.git
cd foods_and_drinks
git checkout develop   # hoặc branch bạn muốn deploy
```

## Bước 5 — Cấu hình môi trường

```bash
cp .env.example .env
nano .env
```

Đổi mật khẩu mạnh cho `MYSQL_ROOT_PASSWORD` và `DB_PASSWORD`.

## Bước 6 — Build & chạy

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f app
```

Đợi log có dòng `Started MemberManagementApplication`.

Truy cập: `http://<PUBLIC_IP>:8080`

### Tài khoản mặc định (seed lần đầu)

| Email | Password | Role |
|-------|----------|------|
| admin@slearn.local | Admin@12345 | ADMIN |
| user@slearn.local | User@12345 | USER |

## Bước 7 — Cập nhật sau này

```bash
cd foods_and_drinks
git pull
docker compose up -d --build
```

## Lệnh hữu ích

```bash
# Xem log
docker compose logs -f app
docker compose logs -f mysql

# Dừng / khởi động lại
docker compose down
docker compose up -d

# Backup MySQL volume
docker compose exec mysql mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" member_management > backup.sql
```

## (Tuỳ chọn) Nginx reverse proxy + HTTPS

Khi cần domain + SSL (Let's Encrypt):

1. Trỏ DNS A record về `<PUBLIC_IP>`.
2. Cài Nginx + Certbot trên VM.
3. Proxy `80/443` → `localhost:8080`.
4. Đóng public port 8080 trên Security List (chỉ mở 80/443).

## Troubleshooting

| Vấn đề | Cách xử lý |
|--------|------------|
| Không vào được `:8080` | Kiểm tra Oracle Security List ingress + `sudo ufw status` |
| App restart liên tục | `docker compose logs app` — thường do DB chưa sẵn sàng hoặc sai password |
| Hết RAM (OOM) | Giảm shape hoặc tắt service không cần; A1 6GB thường đủ |
| Seed không chạy | Xóa volume và chạy lại: `docker compose down -v` (mất data) |

## Chi phí

VM **Always Free** (Ampere A1 trong quota) = **$0/tháng** nếu không vượt free tier.
