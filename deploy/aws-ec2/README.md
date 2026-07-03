# AWS EC2 — Deploy Member Management (Sydney)

Ghi chú triển khai VM Ubuntu trên AWS **ap-southeast-2** (Sydney).

## Thông tin instance (hiện tại)

| Mục | Giá trị |
|-----|---------|
| **Tên** | `member-management-vm` |
| **Instance ID** | `i-05a145f2b91ac8340` |
| **Region** | Asia Pacific (Sydney) — `ap-southeast-2` |
| **Type** | `t3.micro` |
| **State** | Running |
| **Public IPv4** | `3.26.185.21` |
| **Public DNS** | `ec2-3-26-185-21.ap-southeast-2.compute.amazonaws.com` |
| **Private IP** | `172.31.36.179` |
| **OS user** | `ubuntu` |
| **SSH key** | `/Users/nguyen.van.thanhg/Downloads/NeosThanh.pem` |
| **Console** | [EC2 ap-southeast-2](https://ap-southeast-2.console.aws.amazon.com/ec2/home?region=ap-southeast-2#Instances:) |

## SSH vào VM

> **Lưu ý:** Không dùng dấu `<>` quanh IP — zsh sẽ báo `parse error`.

```bash
chmod 400 /Users/nguyen.van.thanhg/Downloads/NeosThanh.pem
ssh -i /Users/nguyen.van.thanhg/Downloads/NeosThanh.pem ubuntu@3.26.185.21
```

Nếu timeout / Connection refused → kiểm tra **Security group** → Inbound rules:

| Type | Port | Source | Mục đích |
|------|------|--------|----------|
| SSH | 22 | My IP (hoặc 0.0.0.0/0) | SSH |
| Custom TCP | 8080 | 0.0.0.0/0 | Spring Boot app |

## Cài đặt trên VM (sau khi SSH)

```bash
# 1. Cập nhật hệ thống
sudo apt update && sudo apt upgrade -y

# 2. Cài Docker
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker

docker --version
docker compose version
```

## Deploy app bằng Docker Compose

Repo cần có `Dockerfile`, `docker-compose.yml`, `.env.example` (trên branch `develop` sau khi merge deploy).

```bash
# 3. Clone project
git clone https://github.com/thanhnv-3702/foods_and_drinks.git
cd foods_and_drinks
git checkout develop

# 4. Cấu hình env
cp .env.example .env
nano .env
# Đổi MYSQL_ROOT_PASSWORD và DB_PASSWORD thành mật khẩu mạnh

# 5. Build & chạy
docker compose up -d --build
docker compose ps
docker compose logs -f app
```

Đợi log: `Started MemberManagementApplication`.

**URL app:** http://3.26.185.21:8080

### Tài khoản demo (seed lần đầu)

| Email | Password | Role |
|-------|----------|------|
| `admin@slearn.local` | `Admin@12345` | ADMIN |
| `user@slearn.local` | `User@12345` | USER |

## Cập nhật sau này

```bash
cd ~/foods_and_drinks
git pull
docker compose up -d --build
```

## Lệnh hữu ích

```bash
docker compose logs -f app      # log ứng dụng
docker compose logs -f mysql    # log database
docker compose down             # dừng
docker compose up -d            # khởi động lại
```

## Troubleshooting

| Vấn đề | Cách xử lý |
|--------|------------|
| `zsh: parse error` khi SSH | Bỏ `<>` quanh IP: `ubuntu@3.26.185.21` |
| SSH timeout | Mở port 22 trên Security group |
| Không vào `:8080` | Mở port 8080 trên Security group |
| App restart loop | `docker compose logs app` — thường sai DB password |
| Chưa có `docker-compose.yml` trên GitHub | Push/merge branch deploy trước, hoặc `scp` file từ máy local |

## Chi phí

- `t3.micro` có thể nằm trong **AWS Free Tier** (12 tháng đầu, tài khoản mới).
- Theo dõi: AWS Console → **Billing → Free tier**.

## Tham khảo

- Oracle Cloud (alternative): `deploy/oracle-cloud/README.md`
- Local dev: `README.md` — `DB_PASSWORD=Admin@12345 mvn spring-boot:run`
