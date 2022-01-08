# foods_application

*`2017년 졸업 논문 발표 (Foods Application)`*

[TOC]

## 1. 개요

### 1.1 프로젝트 개요



> **Android 기반의 어플리케이션 및 연동 서버를 제작한 졸업 논문 프로젝트**
>
> *방문한 음식점에 대한 기록 및 평점, 후기등의 공유가 활발한 현대 사회에서 그 기록을 보다 효율적이고 일목 요연하게 관리하기 위한 SNS 서비스 

![image-20220108163029643](C:\Users\ksw19\AppData\Roaming\Typora\typora-user-images\image-20220108163029643.png)

![image-20220108163011718](C:\Users\ksw19\AppData\Roaming\Typora\typora-user-images\image-20220108163011718.png)

* 주요 기능

  * 사용자가 방문한 음식점의 위치를 기반으로 음식사진과 해당 업체의 정보를 올려 나만의 지도기반 음식점 관리

  * 지도기반의 음식 게시물을 Facebook 친구들과 함께 공유하고, 그룹화하여 효율적인 소통 SNS를 구축



### 1.2  추진 배경 및 필요성

> 기존 시스템을 크게 3가지로 나누어 조사 및 서베이를 수행

 

* ##### 비교 1: 음식사진을 갤러리 내부에 저장하는 경우.

  * 음식사진을 찾아보는데 어려움이 있음

  * 음식사진에 음식점의 세부 내용 (위치, 음식점이름 등) 이 기술되어 있지 않음

  * 친구들과 음식사진을 공유하는데 별도의 매체가 필요

\-

* ##### 비교 2:  IOS기반의 장소별 사진보기

  - 사진에 대한 장소별 보기 기능이 제공됨

  - 하지만 음식 사진뿐만 아니라 저장된 모든 사진이 지도에 나타남

  - 음식사진에 음식점의 세부 내용(위치, 음식점이름 등)이 기술되어 있지 않음

 

* ##### 비교3. 블로그 웹 포스팅

  * 음식사진에 대한 상세한 설명 제공

  * 하지만 한눈에 많은 음식에 대한 정보를 파악하기 어려움

  * 광고 허위 정보가 많아 신뢰도가 낮음



## 2. 개발 내용 및 결과물

### 2.1 목표

* 안드로이드 기반 음식 기행록 어플리케이션 개발
*  이는 그 동안 공부했던 Java, 데이터베이스, 네트워크, 쓰레드, php, 소프트웨어 공학, 서버–클라이언트 구조 등 학사과정 전반을 통틀어 습득한 지식을 활용하여 졸업 자격을 평가 받기 위함



### 2.2 개발 내용 및 결과물

#### 2.2.1 구현 내용 및 사용 기술

* **서버 구축** 
  Apache Tomcat을 사용
* DB연동 및 접근을 위해 중간 Layer인 php 사용

 

* **페이스북** **연동**

  * 페이스북 로그인 API인 [Graph API](https://github.com/facebook/facebook-android-sdk) 활용

    * FaceBook을 이용하여 Login 및 회원가입의 번거로움을 줄임

    * FaceBook의 친구 목록을 기반으로 프로필 사진 및 친구 연동을 활용



* **이미지 업로드, 평점, 코멘트, 장소검색**

  * 장소 검색은 GoogleMap Search API  활용

    * 음식점의 위치 및 정보를 얻어옴

    * 또한 검색이 되지 않는 음식점의 경우에도 사용자가 직접 위치를 지정 할 수 있음

  * [square/picasso](https://github.com/square/picasso) 기반 서버-클라이언트간 이미지 전송, 캐싱, 동기화
    * 카메라로 직접 촬영 또는 갤러리에서 이미지를 불러 올 수 있음
    * 불러온 이미지 및 평점, 코멘트를 서버에 업로드

 

* **사용자 저장/ 친구 공유 지도 **

  * 공유 지도는 Google Map API 활용 

    * 지도를 기반으로 하여 개개인의 저장된 음식 기행록들을 일목요연하게 볼 수 있음.

    *  DB의 저장된 내용으로 사용자가 등록한 음식점의 좌표와 기행문을 불러오며 그 좌표를 지도에 Marker를 표시하여 보여줌.

    * 각 Marker 클릭 시 해당 기행문과 평점, 사진등의 자세한 정보를 볼 수 있음.

 

* **공유**

  * 친구의 지도를 Access할 수 있도록 권한을 요청하는 기능 

    * Access 요청을 받은 친구는 요청을 수락 할 수 있음

    * 공유를 허락한 친구의 지도를 공유받을 수 있음

    * 친구의 지도를 Access할 수 있는 친구는 목록에서 표시

    * 또한 내가 공유해준 친구들의 목록을 볼 수 있음



* **그룹화**

  * 친구들에게 그룹신청을 할 수 있는 기능

    * 그룹신청을 받은 친구는 그룹요청을 수락/취소 할 수 있음

    * 그룹화를 한 그룹원들은 그룹 기행록을 함께 공유하여 작성 가능

    * 그룹원들의 목록을 조회 할 수 있음



#### 2.2.2 시스템 기능 및 구조 설계도
