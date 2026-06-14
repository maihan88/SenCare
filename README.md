# SenCare App

## 1. Giới thiệu dự án

**SenCare** là ứng dụng Android hỗ trợ chủ thú cưng quản lý hồ sơ thú cưng, lưu giữ nhật ký khoảnh khắc, tìm kiếm spa thú cưng theo vị trí, đặt lịch spa và tìm trạm thú y gần khu vực người dùng.

Ứng dụng có 2 vai trò chính:

* **Người dùng thông thường**: Chủ pet.
* **Chủ spa**: Người quản lý hồ sơ spa thú cưng.

---

## 2. Công nghệ sử dụng

* Android Studio
* Java
* XML Layout
* LinearLayout
* Firebase Authentication
* Cloud Firestore
* Cloudinary — lưu trữ ảnh
* Google Maps SDK
* Camera / Gallery
* Glide — hiển thị ảnh từ URL
* Git / GitHub — quản lý source code nhóm

---

## 3. Thành viên và phân công

| Thành viên | Branch                            | Chức năng phụ trách                    |
| ---------- | --------------------------------- | -------------------------------------- |
| Trí        | `feature/auth-profile`            | Authentication + User Profile          |
| Trân       | `feature/pet-diary`               | Pet Portfolio + Pet Diary              |
| Hân        | `feature/spa-booking`             | Spa Booking + Spa Search Map           |
| Nghi       | `feature/spa-owner-vet-dashboard` | Spa Owner + Veterinary Map + Dashboard |

---

## 4. Cấu trúc branch

Dự án sử dụng các branch chính sau:

```bash
main
dev
feature/auth-profile
feature/pet-diary
feature/spa-booking
feature/spa-owner-vet-dashboard
```

### Ý nghĩa từng branch

| Branch                            | Ý nghĩa                                               |
| --------------------------------- | ----------------------------------------------------- |
| `main`                            | Bản ổn định nhất, dùng để nộp hoặc demo               |
| `dev`                             | Bản phát triển chung, nơi gộp code của các thành viên |
| `feature/auth-profile`            | Branch làm việc của Trí                               |
| `feature/pet-diary`               | Branch làm việc của Trân                              |
| `feature/spa-booking`             | Branch làm việc của Hân                               |
| `feature/spa-owner-vet-dashboard` | Branch làm việc của Nghi                              |

---

## 5. Quy tắc Git bắt buộc

### 5.1. Không code trực tiếp trên `main`

Không ai được code trực tiếp trên branch `main`.

Branch `main` chỉ dùng cho bản ổn định cuối cùng.

---

### 5.2. Không code trực tiếp trên `dev`

Branch `dev` là nơi gom code chung.

Mỗi người phải code trên branch riêng của mình, sau đó mới tạo Pull Request vào `dev`.

---

### 5.3. Mỗi người chỉ làm trên branch của mình

| Thành viên | Chỉ làm trên branch               |
| ---------- | --------------------------------- |
| Trí        | `feature/auth-profile`            |
| Trân       | `feature/pet-diary`               |
| Hân        | `feature/spa-booking`             |
| Nghi       | `feature/spa-owner-vet-dashboard` |

---

### 5.4. Trước khi code phải cập nhật code mới nhất từ `dev`

Mỗi lần chuẩn bị code, cần làm theo thứ tự:

```bash
git checkout dev
git pull origin dev
git checkout ten-branch-cua-minh
git merge dev
```

Ví dụ với Trí:

```bash
git checkout dev
git pull origin dev
git checkout feature/auth-profile
git merge dev
```

Mục đích: lấy code mới nhất của nhóm trước khi tiếp tục làm.

## Thiết lập sau khi clone project

Sau khi clone project về, mỗi thành viên cần:

1. Mở project bằng Android Studio.
2. Chờ Gradle Sync tải thư viện.
3. Tạo hoặc mở file `local.properties`.
4. Thêm các key được Leader cung cấp:

```properties
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
CLOUDINARY_CLOUD_NAME=YOUR_CLOUDINARY_CLOUD_NAME
CLOUDINARY_UPLOAD_PRESET=YOUR_CLOUDINARY_UPLOAD_PRESET

---

### 5.5. Sau khi code xong phải commit và push lên branch cá nhân

Ví dụ với Trí:

```bash
git status
git add .
git commit -m "feat: add login screen"
git push origin feature/auth-profile
```

Ví dụ với Trân:

```bash
git status
git add .
git commit -m "feat: add pet list screen"
git push origin feature/pet-diary
```

Ví dụ với Hân:

```bash
git status
git add .
git commit -m "feat: add spa search map"
git push origin feature/spa-booking
```

Ví dụ với Nghi:

```bash
git status
git add .
git commit -m "feat: add spa owner profile form"
git push origin feature/spa-owner-vet-dashboard
```

---

### 5.6. Khi làm xong một phần chức năng thì tạo Pull Request vào `dev`

Sau khi push branch cá nhân lên GitHub, vào GitHub tạo Pull Request:

```text
base: dev
compare: feature/branch-cua-minh
```

Ví dụ:

```text
base: dev
compare: feature/auth-profile
```

Leader kiểm tra rồi mới merge vào `dev`.

---

### 5.7. Sau khi Pull Request được merge vào `dev`

Mỗi thành viên nên cập nhật lại code mới nhất:

```bash
git checkout dev
git pull origin dev
git checkout ten-branch-cua-minh
git merge dev
```

Ví dụ:

```bash
git checkout dev
git pull origin dev
git checkout feature/pet-diary
git merge dev
```

---

## 6. Lệnh Git thường dùng

### 6.1. Clone project lần đầu

```bash
git clone <link-repo>
cd SenCare-App
```

---

### 6.2. Xem đang ở branch nào

```bash
git branch
```

Branch hiện tại sẽ có dấu `*`.

---

### 6.3. Chuyển sang branch khác

```bash
git checkout ten-branch
```

Ví dụ:

```bash
git checkout feature/auth-profile
```

---

### 6.4. Lấy code mới nhất từ GitHub

```bash
git pull origin ten-branch
```

Ví dụ:

```bash
git pull origin dev
```

---

### 6.5. Kiểm tra file đã thay đổi

```bash
git status
```

---

### 6.6. Thêm toàn bộ file đã sửa vào commit

```bash
git add .
```

---

### 6.7. Commit code

```bash
git commit -m "noi dung commit"
```

Ví dụ:

```bash
git commit -m "feat: add register screen"
```

---

### 6.8. Push code lên branch cá nhân

```bash
git push origin ten-branch
```

Ví dụ:

```bash
git push origin feature/auth-profile
```

---

## 7. Quy tắc đặt tên commit

Nên dùng format:

```text
loai: mo ta ngan gon
```

Các loại commit nên dùng:

| Loại       | Ý nghĩa                               | Ví dụ                                      |
| ---------- | ------------------------------------- | ------------------------------------------ |
| `feat`     | Thêm chức năng mới                    | `feat: add login screen`                   |
| `fix`      | Sửa lỗi                               | `fix: validate empty email`                |
| `ui`       | Chỉnh giao diện                       | `ui: update pet card layout`               |
| `refactor` | Sửa code cho gọn, không đổi chức năng | `refactor: clean auth code`                |
| `firebase` | Liên quan Firebase                    | `firebase: save user profile to firestore` |
| `map`      | Liên quan bản đồ                      | `map: show spa markers`                    |

Không nên commit kiểu:

```text
update
fix
abc
lan cuoi
test
```

---

## 8. Các file không tự ý sửa nếu chưa báo Leader

Các file sau dễ gây lỗi toàn project, nên không tự ý sửa nếu chưa báo Leader:

```text
build.gradle
settings.gradle
AndroidManifest.xml
google-services.json
local.properties
Constants.java
FirebaseUtil.java
colors.xml
themes.xml
```

Nếu cần thêm thư viện, thêm Activity vào Manifest, thêm quyền Android, hoặc đổi theme, cần báo Leader trước.

---

## 9. Cấu trúc thư mục dự án

```text
com.example.sencare
│
├── MainActivity.java
│
├── activities
│   ├── auth
│   │   ├── LoginActivity.java
│   │   ├── RegisterActivity.java
│   │   └── ForgotPasswordActivity.java
│   │
│   ├── profile
│   │   └── ProfileActivity.java
│   │
│   ├── dashboard
│   │   ├── UserHomeActivity.java
│   │   └── SpaOwnerHomeActivity.java
│   │
│   ├── pet
│   │   ├── PetListActivity.java
│   │   ├── PetFormActivity.java
│   │   └── PetDetailActivity.java
│   │
│   ├── diary
│   │   ├── PetDiaryListActivity.java
│   │   ├── DiaryTimelineActivity.java
│   │   ├── AddDiaryActivity.java
│   │   └── DiaryDetailActivity.java
│   │
│   ├── spaowner
│   │   ├── SpaFormActivity.java
│   │   └── SpaProfileActivity.java
│   │
│   ├── booking
│   │   ├── SpaSearchActivity.java
│   │   ├── SpaMapActivity.java
│   │   ├── SpaDetailActivity.java
│   │   ├── BookingFormActivity.java
│   │   └── BookingListActivity.java
│   │
│   └── vet
│       └── VetMapActivity.java
│
├── adapters
│   ├── PetAdapter.java
│   ├── DiaryAdapter.java
│   ├── SpaAdapter.java
│   ├── BookingAdapter.java
│   └── VetClinicAdapter.java
│
├── models
│   ├── User.java
│   ├── Pet.java
│   ├── Diary.java
│   ├── Spa.java
│   ├── Booking.java
│   └── VetClinic.java
│
└── utils
    ├── Constants.java
    ├── FirebaseUtil.java
    ├── ValidationUtil.java
    ├── ImageUtil.java
    ├── CloudinaryUtil.java
    ├── LocationUtil.java
    └── DateTimeUtil.java
```

---

## 10. Ý nghĩa các package

| Package                | Ý nghĩa                                        |
| ---------------------- | ---------------------------------------------- |
| `activities.auth`      | Các màn hình đăng nhập, đăng ký, quên mật khẩu |
| `activities.profile`   | Màn hình hồ sơ người dùng                      |
| `activities.dashboard` | Màn hình chính của user và spa owner           |
| `activities.pet`       | Quản lý thú cưng                               |
| `activities.diary`     | Nhật ký khoảnh khắc của thú cưng               |
| `activities.spaowner`  | Chủ spa thêm/sửa hồ sơ spa                     |
| `activities.booking`   | Người dùng tìm spa và đặt lịch                 |
| `activities.vet`       | Bản đồ trạm thú y                              |
| `adapters`             | Adapter cho RecyclerView                       |
| `models`               | Class dữ liệu tương ứng với Firestore          |
| `utils`                | Hàm dùng chung cho toàn app                    |

---

# 11. Luồng tổng quát của ứng dụng

## 11.1. Luồng mở app

```text
Mở app
→ MainActivity
→ Kiểm tra người dùng đã đăng nhập chưa
```

Nếu chưa đăng nhập:

```text
MainActivity
→ LoginActivity
```

Nếu đã đăng nhập:

```text
MainActivity
→ đọc users/{uid}
→ kiểm tra role
```

Nếu role là user:

```text
MainActivity
→ UserHomeActivity
```

Nếu role là spa_owner và chưa có hồ sơ spa:

```text
MainActivity
→ SpaFormActivity
```

Nếu role là spa_owner và đã có hồ sơ spa:

```text
MainActivity
→ SpaOwnerHomeActivity
```

---

## 11.2. Luồng đăng ký

```text
RegisterActivity
→ Người dùng nhập tên, email, mật khẩu, xác nhận mật khẩu, chọn role
→ Kiểm tra dữ liệu hợp lệ
→ Tạo tài khoản bằng Firebase Auth
→ Tạo document users/{uid} trong Firestore
→ Điều hướng theo role
```

Nếu đăng ký role user:

```text
RegisterActivity
→ UserHomeActivity
```

Nếu đăng ký role spa_owner:

```text
RegisterActivity
→ SpaFormActivity
```

---

## 11.3. Luồng đăng nhập

```text
LoginActivity
→ Người dùng nhập email và mật khẩu
→ Firebase Auth đăng nhập
→ Đọc users/{uid}
→ Kiểm tra role
```

Nếu role user:

```text
LoginActivity
→ UserHomeActivity
```

Nếu role spa_owner nhưng chưa có hồ sơ spa:

```text
LoginActivity
→ SpaFormActivity
```

Nếu role spa_owner đã có hồ sơ spa:

```text
LoginActivity
→ SpaOwnerHomeActivity
```

---

## 11.4. Luồng quên mật khẩu

```text
LoginActivity
→ Người dùng bấm "Quên mật khẩu?"
→ ForgotPasswordActivity
→ Nhập email
→ Firebase gửi email khôi phục mật khẩu
→ Thông báo thành công
→ Quay lại LoginActivity
```

---

## 11.5. Luồng hồ sơ người dùng

```text
UserHomeActivity
→ ProfileActivity
→ Hiển thị tên, email, ảnh đại diện
→ Người dùng chỉnh sửa tên hoặc ảnh
→ Ảnh upload lên Cloudinary
→ Lưu avatarUrl và avatarPublicId vào users/{uid}
```

---

## 11.6. Luồng quản lý thú cưng

```text
UserHomeActivity
→ PetListActivity
→ Hiển thị danh sách pet của user hiện tại
```

Thêm pet:

```text
PetListActivity
→ PetFormActivity
→ Nhập tên, loài, tuổi, tính cách
→ Chụp ảnh hoặc chọn ảnh
→ Upload ảnh lên Cloudinary
→ Lưu dữ liệu vào pets/{petId}
→ Quay lại PetListActivity
```

Sửa pet:

```text
PetListActivity
→ PetDetailActivity hoặc PetFormActivity
→ Sửa thông tin
→ Nếu đổi ảnh thì upload ảnh mới lên Cloudinary
→ Cập nhật pets/{petId}
```

Xóa pet:

```text
PetListActivity
→ Chọn xóa
→ Hiện hộp thoại xác nhận
→ Xóa pets/{petId}
→ Có thể xóa hoặc bỏ liên kết ảnh Cloudinary nếu làm kịp
```

---

## 11.7. Luồng Pet Diary

```text
UserHomeActivity
→ PetDiaryListActivity
→ Hiển thị danh sách pet
→ Chọn một pet
→ DiaryTimelineActivity
```

Thêm khoảnh khắc:

```text
DiaryTimelineActivity
→ Bấm nút thêm ảnh
→ AddDiaryActivity
→ Chụp ảnh hoặc chọn ảnh
→ Nhập caption
→ Upload ảnh lên Cloudinary
→ Lưu dữ liệu vào diaries/{diaryId}
→ Quay lại DiaryTimelineActivity
```

Xem chi tiết:

```text
DiaryTimelineActivity
→ Chọn một ảnh
→ DiaryDetailActivity
→ Hiển thị ảnh lớn, caption, thời gian
```

---

## 11.8. Luồng chủ spa thêm hồ sơ spa

```text
SpaOwnerHomeActivity hoặc MainActivity
→ Nếu chưa có hồ sơ spa
→ SpaFormActivity
```

Trong `SpaFormActivity`:

```text
Nhập tên spa
Nhập địa chỉ
Nhập số điện thoại
Nhập mô tả
Nhập danh sách dịch vụ
Nhập khoảng giá
Chọn hoặc nhập vị trí spa
Chọn ảnh spa
Upload ảnh lên Cloudinary
Lưu dữ liệu vào spas/{spaId}
Cập nhật users/{uid}: hasSpaProfile = true, spaId = spaId
Chuyển đến SpaOwnerHomeActivity
```

---

## 11.9. Luồng chủ spa xem/sửa hồ sơ spa

```text
SpaOwnerHomeActivity
→ SpaProfileActivity
→ Hiển thị thông tin spa của chủ spa hiện tại
→ Bấm chỉnh sửa
→ SpaFormActivity
→ Cập nhật thông tin
→ Lưu lại vào spas/{spaId}
```

---

## 11.10. Luồng tìm spa theo khoảng cách

```text
UserHomeActivity
→ SpaSearchActivity
→ Nhập khoảng cách mong muốn theo km
→ Bấm tìm kiếm
→ SpaMapActivity
```

Trong `SpaMapActivity`:

```text
Xin quyền vị trí
Lấy vị trí hiện tại của user
Đọc danh sách spa từ collection spas
Tính khoảng cách từ user đến từng spa
Chỉ hiển thị marker spa nằm trong khoảng cách đã nhập
Người dùng bấm marker
→ Hiển thị tên spa, địa chỉ, khoảng cách
→ Có nút xem chi tiết hoặc đặt lịch
```

---

## 11.11. Luồng xem chi tiết spa và đặt lịch

```text
SpaMapActivity
→ Chọn marker spa
→ SpaDetailActivity
→ Hiển thị tên spa, địa chỉ, số điện thoại, dịch vụ, khoảng giá, khoảng cách
→ Bấm đặt lịch
→ BookingFormActivity
```

Trong `BookingFormActivity`:

```text
Chọn pet
Chọn dịch vụ
Chọn ngày bằng DatePicker
Chọn giờ bằng TimePicker
Bấm xác nhận
Lưu booking vào bookings/{bookingId}
Thông báo đặt lịch thành công
Quay lại BookingListActivity hoặc SpaSearchActivity
```

---

## 11.12. Luồng quản lý lịch hẹn

```text
UserHomeActivity
→ BookingListActivity
```

Trong `BookingListActivity`:

```text
Đọc bookings theo userId hiện tại
Chỉ lấy status = active
Chia thành 2 nhóm:
- Lịch sắp tới
- Lịch đã qua
```

Hủy lịch:

```text
BookingListActivity
→ Bấm hủy lịch
→ Xác nhận
→ Cập nhật status = cancelled
→ Ẩn khỏi danh sách active hoặc chuyển sang trạng thái đã hủy
```

---

## 11.13. Luồng bản đồ trạm thú y

```text
UserHomeActivity
→ VetMapActivity
```

Trong `VetMapActivity`:

```text
Xin quyền vị trí
Lấy vị trí hiện tại
Đọc danh sách vetClinics từ Firestore
Chỉ hiển thị clinic có active = true
Cắm marker lên Google Map
Người dùng bấm marker
→ Hiển thị tên, địa chỉ, số điện thoại
→ Bấm "Gọi ngay"
→ Mở ứng dụng gọi điện
→ Bấm "Chỉ đường"
→ Mở Google Maps gốc để dẫn đường
```

---

# 12. Danh sách Activity và mục đích

## 12.1. Main

| Activity       | Mục đích                                            |
| -------------- | --------------------------------------------------- |
| `MainActivity` | Màn hình khởi động, kiểm tra đăng nhập và phân role |

---

## 12.2. Auth

| Activity                 | Mục đích                                             |
| ------------------------ | ---------------------------------------------------- |
| `LoginActivity`          | Đăng nhập bằng email và mật khẩu                     |
| `RegisterActivity`       | Đăng ký tài khoản mới, chọn role user hoặc spa_owner |
| `ForgotPasswordActivity` | Gửi email khôi phục mật khẩu                         |

---

## 12.3. Profile

| Activity          | Mục đích                                    |
| ----------------- | ------------------------------------------- |
| `ProfileActivity` | Xem và chỉnh sửa thông tin cá nhân của user |

---

## 12.4. Dashboard

| Activity               | Mục đích                                                     |
| ---------------------- | ------------------------------------------------------------ |
| `UserHomeActivity`     | Trang chính của chủ pet, hiển thị shortcut và tiện ích nhanh |
| `SpaOwnerHomeActivity` | Trang chính của chủ spa, hiển thị hồ sơ spa của họ           |

---

## 12.5. Pet Portfolio

| Activity            | Mục đích                                      |
| ------------------- | --------------------------------------------- |
| `PetListActivity`   | Hiển thị danh sách thú cưng của user hiện tại |
| `PetFormActivity`   | Thêm hoặc chỉnh sửa hồ sơ thú cưng            |
| `PetDetailActivity` | Xem chi tiết thông tin thú cưng               |

---

## 12.6. Pet Diary

| Activity                | Mục đích                                         |
| ----------------------- | ------------------------------------------------ |
| `PetDiaryListActivity`  | Hiển thị danh sách pet để chọn pet cần xem diary |
| `DiaryTimelineActivity` | Hiển thị album/timeline ảnh của một pet          |
| `AddDiaryActivity`      | Thêm ảnh và caption mới cho pet                  |
| `DiaryDetailActivity`   | Xem chi tiết một ảnh diary                       |

---

## 12.7. Spa Owner

| Activity             | Mục đích                                 |
| -------------------- | ---------------------------------------- |
| `SpaFormActivity`    | Chủ spa thêm hoặc chỉnh sửa hồ sơ spa    |
| `SpaProfileActivity` | Chủ spa xem thông tin hồ sơ spa của mình |

---

## 12.8. Spa Booking

| Activity              | Mục đích                                  |
| --------------------- | ----------------------------------------- |
| `SpaSearchActivity`   | Người dùng nhập khoảng cách muốn tìm spa  |
| `SpaMapActivity`      | Hiển thị các spa phù hợp trên Google Map  |
| `SpaDetailActivity`   | Hiển thị chi tiết spa được chọn           |
| `BookingFormActivity` | Đặt lịch spa cho pet                      |
| `BookingListActivity` | Xem lịch sắp tới, lịch đã qua và hủy lịch |

---

## 12.9. Veterinary Map

| Activity         | Mục đích                                                      |
| ---------------- | ------------------------------------------------------------- |
| `VetMapActivity` | Hiển thị trạm thú y trên bản đồ, hỗ trợ gọi ngay và chỉ đường |

---

# 13. Cấu trúc Firestore

## 13.1. Collection `users`

```text
users/{uid}
```

Fields:

```text
uid
email
fullName
role
avatarUrl
avatarPublicId
hasSpaProfile
spaId
createdAt
updatedAt
```

Role chỉ dùng 2 giá trị:

```text
user
spa_owner
```

---

## 13.2. Collection `pets`

```text
pets/{petId}
```

Fields:

```text
petId
ownerId
name
species
age
personality
imageUrl
imagePublicId
createdAt
updatedAt
```

---

## 13.3. Collection `diaries`

```text
diaries/{diaryId}
```

Fields:

```text
diaryId
ownerId
petId
imageUrl
imagePublicId
caption
createdAt
updatedAt
```

---

## 13.4. Collection `spas`

```text
spas/{spaId}
```

Fields:

```text
spaId
ownerId
spaName
address
phone
description
services
priceRange
imageUrl
imagePublicId
latitude
longitude
open
createdAt
updatedAt
```

---

## 13.5. Collection `bookings`

```text
bookings/{bookingId}
```

Fields:

```text
bookingId
userId
petId
spaId
petName
spaName
serviceName
bookingDate
bookingTime
bookingTimestamp
status
createdAt
updatedAt
```

Status chỉ dùng:

```text
active
cancelled
```

---

## 13.6. Collection `vetClinics`

```text
vetClinics/{clinicId}
```

Fields:

```text
clinicId
name
address
phone
latitude
longitude
note
active
createdAt
updatedAt
```

---

# 14. Cloudinary

Dự án dùng Cloudinary để lưu ảnh thay cho Firebase Storage.

Các loại ảnh cần upload:

```text
Ảnh đại diện user
Ảnh thú cưng
Ảnh diary
Ảnh spa
```

Sau khi upload ảnh lên Cloudinary, app lưu vào Firestore:

```text
imageUrl
imagePublicId
```

Riêng user avatar:

```text
avatarUrl
avatarPublicId
```

---

# 15. Quy tắc đặt tên field

Không tự ý đổi tên field trong Firestore.

Ví dụ phải dùng:

```text
ownerId
petId
spaId
userId
bookingId
role
status
createdAt
updatedAt
```

Không dùng các tên khác như:

```text
user_id
owner_id
idUser
pet_id
shopId
clinic_id
```

---

# 16. Quy tắc làm việc theo module

## Trí

Được sửa chủ yếu:

```text
activities/auth/LoginActivity.java
activities/auth/RegisterActivity.java
activities/auth/ForgotPasswordActivity.java
activities/profile/ProfileActivity.java
models/User.java
utils/ValidationUtil.java
```

Layout tương ứng:

```text
activity_login.xml
activity_register.xml
activity_forgot_password.xml
activity_profile.xml
```

---

## Trân

Được sửa chủ yếu:

```text
activities/pet
activities/diary
adapters/PetAdapter.java
adapters/DiaryAdapter.java
models/Pet.java
models/Diary.java
utils/ImageUtil.java
utils/CloudinaryUtil.java nếu cần
```

Layout tương ứng:

```text
activity_pet_list.xml
activity_pet_form.xml
activity_pet_detail.xml
activity_pet_diary_list.xml
activity_diary_timeline.xml
activity_add_diary.xml
activity_diary_detail.xml
item_pet.xml
item_diary.xml
```

---

## Hân

Được sửa chủ yếu:

```text
activities/booking
adapters/SpaAdapter.java
adapters/BookingAdapter.java
models/Booking.java
models/Spa.java nếu cần đọc dữ liệu spa
utils/LocationUtil.java
utils/DateTimeUtil.java
```

Layout tương ứng:

```text
activity_spa_search.xml
activity_spa_map.xml
activity_spa_detail.xml
activity_booking_form.xml
activity_booking_list.xml
item_spa.xml
item_booking.xml
```

---

## Nghi

Được sửa chủ yếu:

```text
activities/spaowner
activities/vet
activities/dashboard
adapters/VetClinicAdapter.java
models/Spa.java
models/VetClinic.java
```

Layout tương ứng:

```text
activity_spa_form.xml
activity_spa_profile.xml
activity_user_home.xml
activity_spa_owner_home.xml
activity_vet_map.xml
item_vet_clinic.xml
```

---

# 17. Checklist trước khi push code

Trước khi push code lên GitHub, mỗi người cần kiểm tra:

```text
[ ] App build không lỗi
[ ] Không sửa nhầm file của người khác
[ ] Không tự ý sửa build.gradle
[ ] Không tự ý sửa AndroidManifest.xml nếu chưa báo Leader
[ ] Không đổi tên collection/field trong Firestore
[ ] Không commit file rác
[ ] Đã chạy thử màn hình mình làm
[ ] Commit message rõ ràng
```

---

# 18. Checklist trước khi tạo Pull Request

Trước khi tạo Pull Request vào `dev`:

```text
[ ] Đã pull code mới nhất từ dev
[ ] Đã merge dev vào branch cá nhân
[ ] Đã xử lý conflict nếu có
[ ] App chạy được sau khi merge dev
[ ] Đã push branch cá nhân lên GitHub
[ ] Pull Request có mô tả rõ đã làm gì
```

---

# 19. Mẫu mô tả Pull Request

Khi tạo Pull Request, ghi mô tả như sau:

```text
## Đã làm
- Thêm màn hình ...
- Kết nối Firebase ...
- Validate ...

## Đã test
- Chạy app không lỗi
- Test trường hợp ...
- Test trường hợp ...

## Ghi chú
- Cần Leader kiểm tra ...
```

Ví dụ:

```text
## Đã làm
- Thêm màn hình LoginActivity
- Kết nối Firebase Auth
- Validate email và password rỗng
- Điều hướng sang UserHomeActivity sau khi đăng nhập thành công

## Đã test
- Đăng nhập đúng tài khoản
- Đăng nhập sai mật khẩu
- Bỏ trống email/password

## Ghi chú
- Cần kiểm tra lại role routing với spa_owner
```

---

# 20. Luồng làm việc hằng ngày đề xuất

Mỗi lần bắt đầu code:

```bash
git checkout dev
git pull origin dev
git checkout ten-branch-cua-minh
git merge dev
```

Code chức năng.

Sau khi code xong:

```bash
git status
git add .
git commit -m "feat: mo ta chuc nang"
git push origin ten-branch-cua-minh
```

Khi hoàn thành một phần đủ ổn:

```text
Tạo Pull Request từ branch cá nhân vào dev
Báo Leader review
Leader merge vào dev nếu không lỗi
```

---

# 21. Ghi chú quan trọng

Dự án này ưu tiên hoàn thành V1.0 ổn định, không tự ý thêm scope mới.

Không tự ý thêm các chức năng ngoài phạm vi nếu chưa thống nhất với nhóm, ví dụ:

```text
Chat
Thanh toán online
Đánh giá spa
Thông báo tự động
Quản lý lịch cho chủ spa
Google Places API
```

Các chức năng này có thể để sau nếu còn thời gian.
