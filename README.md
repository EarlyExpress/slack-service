# 🔔 Notification Slack Service

EarlyExpress MSA 플랫폼의 **Slack 알림 서비스**입니다.  
주문·배송·AI 메시지 등 다양한 이벤트를 실시간 수신하고, Slack API로 메시지를 전송합니다.  
예약 메시지는 스케줄링 + Redis 캐시를 통해 안정적으로 관리합니다.

---

## 📋 목차

1.  [서비스 개요](#서비스-개요)
2.  [아키텍처](#아키텍처)
3.  [핵심 기능](#핵심-기능)
4.  [도메인 모델 (DDD)](#도메인-모델-ddd)
5.  [Kafka 이벤트 흐름](#kafka-이벤트-흐름)
6.  [Slack 메시지 전송 구조](#slack-메시지-전송-구조)
7.  [스케줄링 메시지 처리](#스케줄링-메시지-처리)
8.  [API 명세](#api-명세)
9.  [기술 스택](#기술-스택)
10. [향후 고도화 계획](#향후-고도화-계획)
11. [트러블슈팅](#트러블슈팅)

---

## 📝 서비스 개요

Slack Notification Service는 EarlyExpress 플랫폼의 실시간·예약 알림 시스템입니다.

- Slack 메시지 실시간 전송
- 예약 메시지 스케줄링 발송
- Kafka 기반 비동기 알림 처리
- AI 서비스 메시지 자동 전달
- 중복 전송 방지 Redis 캐시
- DDD 기반 도메인 분리 및 이벤트 기반 처리

---
## 기술 스택

| 구분 | 기술 |
|------|------|
| **Framework** | Spring Boot 3.5.7 |
| **Language** | Java 21 |
| **Database** | PostgreSQL |
| **Messaging** | Apache Kafka |
| **Service Discovery** | Netflix Eureka |
| **Security** | OAuth 2.0 Resource Server (Keycloak) |
| **Service Communication** | OpenFeign |
| **Build Tool** | Gradle |

---

## 🏗 아키텍처

```` mermaid 
flowchart TD
    OrderService["Order Service"]
    AIService["AI Service"]
    NotificationService["Notification Slack Service"]
    Slack["Slack"]
    Redis["Redis Cache"]

    OrderService -->|Kafka: notification-events| NotificationService
    AIService -->|Kafka: ai-notification-events| NotificationService
    NotificationService -->|Slack API| Slack
    NotificationService --> Redis

    subgraph Notification Slack Service
        KafkaConsumer["Kafka Consumer"]
        SlackMessageSender["Slack Message Sender"]
        ScheduledProcessor["Scheduled Message Processor"]
    end

    NotificationService --> KafkaConsumer
    KafkaConsumer --> SlackMessageSender
    ScheduledProcessor --> SlackMessageSender
````
설명
---

Order Service, AI Service에서 이벤트 발생 → Kafka 전송  
Notification Slack Service에서 이벤트 수신 → Slack API 호출  
Redis 캐시를 통해 중복 전송 방지 및 예약 메시지 관리  

---

## 🚀 핵심 기능

- Slack 메시지 실시간 전송
- Slack Web API 사용
- Kafka 기반 알림 처리
  - Order Service 및 AI Service 이벤트 수신
- 예약 메시지 발송 (Scheduling)
  - Redis + Spring Scheduler 활용
  - 정각 알림 / 배송 시작 예정 등 다양한 패턴 지원
- Redis 메시지 캐싱
  - Slack 전송 이력 캐싱
  - 예약 메시지 저장 캐시
- 슬랙 메시지 조회
  - 최근 전송 로그 조회
  - 특정 이벤트 기반 메시지 기록 검색

---

## 📦 도메인 모델 (DDD)

### Aggregate Root: Notification

```java
public class Notification {
    private NotificationId id;
    private NotificationType type;      // ORDER_CONFIRMED, AI_MESSAGE, DELIVERY_STARTED ...
    private String targetChannel;       // Slack 채널 ID
    private String messageBody;         // 실제 전송될 텍스트
    private LocalDateTime scheduledAt;  // 예약 발송일시
    private NotificationStatus status;  // PENDING, SENT, FAILED
    private LocalDateTime createdAt;
}
```
## 📦 Value Objects

| VO                  | 설명                                |
|-------------------- |------------------------------------|
| NotificationId      | UUID 기반 식별자                      |
| SlackMessage        | Slack API로 전송될 최종 메시지 포맷      |
| ScheduledAt         | 예약 시간 정보                        |
| NotificationStatus  | PENDING / SENT / FAILED            |

---

## 🔄 Kafka 이벤트 흐름

### Order Service → Notification Service

**Topic:** `notification-events`

```json
{
  "eventType": "ORDER_CONFIRMED",
  "orderId": "order-123",
  "receiverName": "홍길동",
  "receiverEmail": "user@example.com",
  "estimatedDeliveryTime": "2025-01-21T14:00:00",
  "message": "주문이 확정되었습니다!"
}
```
## 💬 Slack 메시지 전송 구조

```text
Kafka Event
      │
      ▼
NotificationFactory (전략 패턴)
      │
      ▼
SlackMessageBuilder (Markdown / Block Kit)
      │
      ▼
SlackApiClient (WebClient)
      │
      ▼
Slack API
```
## ⚠ 전송 실패 처리

- 전송 실패 시:
  - 재시도 (backoff 적용)
  - Redis 실패 기록 저장
  - DLQ 옵션 지원

---

## ⏰ 스케줄링 메시지 처리

```text
POST /notifications/schedule
```
        ▼
DB 저장 + Redis 캐싱
        ▼ (매 1분 실행)
ScheduledMessageProcessor
        ▼
예약 시간 도달 시 Slack 전송
## 📝 예약 타입 예시

- 특정 시각 발송 (2025-02-11T18:00:00)
- 발송 시한 임박 자동 알림
- 매시간 상태 요약 (운영/모니터링 팀용)

---

## 📄 API 명세

### Slack 메시지 즉시 전송

```bash
POST /api/notifications/send
```

