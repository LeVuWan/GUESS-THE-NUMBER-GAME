# GUESS-THE-NUMBER-GAME

**Môi trường**
- Java 21 (project <java.version> = 21 trong `pom.xml`).
- Maven (hoặc dùng wrapper `mvnw.cmd` trên Windows).
- MySQL (hoặc chỉnh `spring.datasource.*` nếu dùng DB khác).

File cấu hình mặc định: `src/main/resources/application.properties` (ví dụ giá trị mặc định):
- `server.port=8080`
- `spring.datasource.url=jdbc:mysql://localhost:3306/guss_the_number_game`
- `spring.datasource.username=root`
- `spring.datasource.password=123456`
- `jwt.secret` và `jwt.expiration` (dùng cho JWT)

Trước khi chạy: đảm bảo MySQL đang chạy và tạo database `guss_the_number_game` (hoặc thay URL trong `application.properties`).

**Build & Run (Windows)**
1. Mở terminal ở thư mục dự án.
2. Build:

```bash
.\mvnw.cmd clean package
```

3. Chạy trực tiếp (sử dụng jar):

```bash
java -jar target\GUESS-THE-NUMBER-GAME-0.0.1-SNAPSHOT.jar
```

Hoặc chạy với Spring Boot plugin (phù hợp khi phát triển):

```bash
.\mvnw.cmd spring-boot:run
```

**Chạy tests**
```bash
.\mvnw.cmd test
```

**API chính & cách test nhanh (curl / Postman)**
Base URL mặc định: `http://localhost:8080`

Endpoints chính:
- `POST /auth/register` — đăng ký user
	- Body JSON: `{ "username": "youruser", "password": "123456" }`

- `POST /auth/login` — đăng nhập
	- Body JSON: `{ "username": "youruser", "password": "123456" }`
	- Response: `ResponseData` với `data` là `LoginResponse` chứa trường `accesstToken` (lưu ý tên trường trong code là `accesstToken`), `refreshToken`, `authenticated`.

- `POST /auth/refresh-token` — lấy access token mới
	- Body JSON: `{ "refreshToken": "<refreshToken>" }`
	- Response `data` trả về access token (string).

- `POST /auth/logout` — logout (body `LogoutDto` có `accessToken`)

- `POST /user/turn` — chơi 1 lượt (Yêu cầu JWT)
	- Header: `Authorization: Bearer <accessToken>`
	- Body JSON: `{ "userGuess": 3 }` (giá trị từ 1 đến 5)

- `GET /user/leadbord` — lấy leaderboard (top 10)
	- (Không cần body; hiện nằm ở `/user/leadbord`)

- `GET /user/me` — lấy thông tin user hiện tại (Yêu cầu JWT)

- `GET /vnpay/create-payment?amount=<amount>&language=<lang>` — tạo URL thanh toán VnPay (Yêu cầu JWT)
	- Trả về `PaymentRes` chứa `url` để redirect.

Ví dụ curl nhanh:

1) Đăng ký
```bash
curl -X POST http://localhost:8080/auth/register \
	-H "Content-Type: application/json" \
	-d "{ \"username\": \"testuser\", \"password\": \"123456\" }"
```

2) Login (lấy token)
```bash
curl -X POST http://localhost:8080/auth/login \
	-H "Content-Type: application/json" \
	-d "{ \"username\": \"testuser\", \"password\": \"123456\" }"
```
Response ví dụ (đóng gói trong `ResponseData`):
```json
{
	"status": 202,
	"message": "Login successful",
	"data": {
		"accesstToken": "<JWT_ACCESS_TOKEN>",
		"refreshToken": "<REFRESH_TOKEN>",
		"authenticated": true
	}
}
```

3) Gọi endpoint cần auth (ví dụ `GET /user/me`)
```bash
curl -H "Authorization: Bearer <JWT_ACCESS_TOKEN>" http://localhost:8080/user/me
```

4) Gửi lượt chơi
```bash
curl -X POST http://localhost:8080/user/turn \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer <JWT_ACCESS_TOKEN>" \
	-d "{ \"userGuess\": 3 }"
```

5) Refresh token
```bash
curl -X POST http://localhost:8080/auth/refresh-token \
	-H "Content-Type: application/json" \
	-d "{ \"refreshToken\": \"<REFRESH_TOKEN>\" }"
```

**Postman**
- Có một file môi trường trong `postman/environments/New_Environment.postman_environment.json` — import vào Postman để dùng sẵn biến.
 - Có một file môi trường trong `postman/environments/New_Environment.postman_environment.json` — import vào Postman để dùng sẵn biến.

Postman Collection (export)
- File collection đã có sẵn: `postman/collections/GuessTheNumber.postman_collection.json`.
- Hướng dẫn nhanh:
	1. Mở Postman -> Import -> chọn file `postman/collections/GuessTheNumber.postman_collection.json`.
	2. Import tiếp `postman/environments/New_Environment.postman_environment.json` vào Environments.
	3. Chọn environment vừa import, chỉnh `baseUrl` nếu cần (mặc định `http://localhost:8080`).
	4. Chạy request `Login` để lưu `accessToken` và `refreshToken` vào environment (request có test script tự lưu).
	5. Thực hiện các request khác (sử dụng biến `{{accessToken}}` cho header `Authorization`).

Ghi chú: collection chứa các request: `Register`, `Login` (script tự lưu token), `Get Current User`, `User Turn`, `Leaderboard`, `Create VnPay Payment`, `Refresh Token`, `Logout`.

**Cấu hình/tuỳ chỉnh**
- Đổi `application.properties` trực tiếp hoặc set biến môi trường theo Spring Boot conventions để thay `spring.datasource.*` hoặc `jwt.secret`.
- Nếu muốn chạy trên cổng khác, thay `server.port`.

**Lưu ý**
- Nếu gặp lỗi kết nối DB: kiểm tra `spring.datasource.url`, username/password, và tạo database.
---

