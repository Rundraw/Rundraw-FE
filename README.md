# Rundraw-FE

---

## 🛠 Tech Stack

### Android

![Java](https://img.shields.io/badge/Java-007396?style=flat-square&logo=openjdk&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=flat-square&logo=androidstudio&logoColor=white)

### Network & API

![Retrofit](https://img.shields.io/badge/Retrofit-48B983?style=flat-square&logoColor=white)
![OkHttp](https://img.shields.io/badge/OkHttp-000000?style=flat-square&logoColor=white)

### Map & Location

![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=flat-square&logo=googlemaps&logoColor=white)

### UI

![XML](https://img.shields.io/badge/XML-FF6600?style=flat-square&logoColor=white)
![RecyclerView](https://img.shields.io/badge/RecyclerView-3DDC84?style=flat-square&logo=android&logoColor=white)

### Collaboration

![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=flat-square&logo=notion&logoColor=white)

---

## 📂 Project Structure

```text
Rundraw-FE/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── example/
│   │       │           └── rundraw_fe/
│   │       │               ├── Activity/
│   │       │               ├── Adapter/
│   │       │               ├── Api/
│   │       │               ├── Model/
│   │       │               └── ...
│   │       │
│   │       ├── res/
│   │       │   ├── drawable/       # UI 이미지 및 Drawable
│   │       │   ├── layout/         # Activity 및 화면 레이아웃
│   │       │   ├── mipmap/         # 앱 아이콘
│   │       │   ├── values/         # 색상, 문자열, 테마
│   │       │   └── xml/            # XML 설정
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle
│
├── gradle/
│   └── wrapper/
│
├── .gitignore
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
└── settings.gradle
```

---

## 💾 Git & Commit Convention

### 브랜치 전략

| 타입 | 설명 | 예시 |
| :--- | :--- | :--- |
|**feat**|새로운 기능 개발|`feat/member`|

### 💬 Commit Message

| 태그 | 설명 |
| :--- | :--- |
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `docs` | 문서 수정 |
| `style` | 코드 포맷팅 |
| `refactor` | 코드 리팩토링 |
| `chore` | 빌드/패키지 설정 |

---

## 📱 프로젝트 소개

**Rundraw**는 사용자가 직접 러닝 코스를 설계하고,  
음성 내비게이션을 통해 경로를 따라 달리며 **GPS Art를 완성할 수 있는 러닝 플랫폼**입니다.

러닝 중 이동 경로와 페이스를 기록하고, 완성한 코스를 다른 사용자들과 공유할 수 있습니다.

---

## ✨ 핵심 기능

- 🎨 **GPS Art 코스 생성**
  - 지도 기반 러닝 코스 설계
  - 원하는 경로를 직접 생성

- 🎧 **음성 내비게이션**
  - 러닝 중 경로를 음성으로 안내
  - 화면을 계속 확인하지 않고도 코스 진행 가능

- 🏃 **러닝 기록**
  - 이동 경로 기록
  - 러닝 페이스 및 운동 데이터 기록

- 📍 **코스 관리**
  - 생성한 코스 저장 및 조회
  - 코스별 상세 정보 제공

- ❤️ **코스 좋아요 / 북마크**
  - 관심 있는 코스 저장
  - 다른 사용자의 코스 탐색

- 💬 **댓글**
  - 코스에 대한 댓글 작성 및 삭제
  - 사용자 간 코스 경험 공유

