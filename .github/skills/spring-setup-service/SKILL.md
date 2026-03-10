---
name: spring-setup-service
description: Bu projede yeni bir Spring Boot mikroservis projesi oluşturma görevini yürüten yetenek. product-service referans alınarak standart dosya yapısını, bağımlılıkları ve konfigürasyonu üretir.
---

## Ne Zaman Kullanılacak?

Kullanıcı aşağıdaki işlemlerden herhangi birini içeren bir istekte bulunursa bu yetenek devreye girer:

- yeni mikroservis oluştur
- yeni servis kur / yeni servis ekle
- gateway / registry server / discovery server oluştur
- Spring Boot projesi başlat

**Örnek tetikleyici promptlar:**

- `inventory-service adında yeni bir servis oluştur.`
- `notification-service ekle.`
- `API Gateway kur.`
- `Eureka discovery server oluştur.`
- `Projeye yeni bir server implementasyonu yap.`

---

## Adım 1 — Bağlam Toplama

Üretim başlamadan önce aşağıdaki bilgilerin tamamı netleştirilmiş olmalıdır. Kullanıcı sağlamamışsa sor:

1. **Servis adı** nedir? (kebab-case, örn: `inventory-service`)
2. **Servis türü** nedir?
   - **Business Service**: REST API, JPA, veritabanı içeren standart servis (ör: product-service, order-service)
   - **Infrastructure Component**: gateway, registry-server, config-server gibi altyapı bileşeni
3. **Port numarası** nedir? (mevcut servislerin portlarıyla çakışmamalı)
4. Business service ise: **hangi kaynakları (resource) yönetecek?** (ör: inventory, notification)
5. Business service ise: **OpenAPI spec dosyası var mı?** `/docs/openapi/` altında

---

## Adım 2 — Dosya Dökümü Hazırla

Kullanıcıya aşağıdaki dosya dökümünü sun ve yazılı onay al. Onay gelmeden implementasyona başlanmaz.

### Business Service için Dosya Dökümü

| Dosya | İşlem | Neden |
|---|---|---|
| `pom.xml` | Oluşturulacak | Maven proje tanımı |
| `application.yml` | Oluşturulacak | Uygulama konfigürasyonu |
| `<ServiceName>Application.java` | Oluşturulacak | Spring Boot giriş noktası |
| `<Resource>Controller.java` | Oluşturulacak | REST controller (v1 stub) |
| `dto/ErrorResponse.java` | Oluşturulacak | Standart hata modeli |
| `exception/GlobalExceptionHandler.java` | Oluşturulacak | Merkezi hata yönetimi |
| `docs/openapi/<service-name>-v1.yml` | Oluşturulacak (yoksa) | OpenAPI sözleşmesi |

### Infrastructure Component için Dosya Dökümü

| Dosya | İşlem | Neden |
|---|---|---|
| `pom.xml` | Oluşturulacak | Maven proje tanımı |
| `application.yml` | Oluşturulacak | Bileşene özgü konfigürasyon |
| `<ComponentName>Application.java` | Oluşturulacak | Spring Boot giriş noktası |

---

## Adım 3 — Üretim

Aşağıdaki standartları uygulayarak dosyaları üret.

### 3.1 pom.xml

**Sabit değerler (DECISIONS.MD'den):**

- Parent: `spring-boot-starter-parent` `3.5.11`
- groupId: `com.turkcell`
- Java version: `21`
- Build plugin: `spring-boot-maven-plugin`

**Business Service bağımlılık seti:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.8</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

> Infrastructure component bağımlılıkları bileşen türüne göre değişir (örn: spring-cloud-starter-gateway, spring-cloud-netflix-eureka-server). Kullanıcıya sor, uydurma.

### 3.2 application.yml

**Business Service şablonu** (`<service-name>` ve `<port>` yerine gerçek değerler yazılır):

```yaml
spring:
  application:
    name: <service-name>

  datasource:
    url: jdbc:h2:mem:<servicename>db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: <port>
```

> `<servicename>db` kısmında servis adından tire (`-`) karakterleri kaldırılır. Örn: `order-service` → `orderdb`.

### 3.3 Application Sınıfı

Ana sınıf her zaman paketin kökünde yer alır:

```java
package com.turkcell.<packagename>;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class <ServiceName>Application {

    public static void main(String[] args) {
        SpringApplication.run(<ServiceName>Application.class, args);
    }
}
```

> `<packagename>`: kebab-case servis adından tireler kaldırılır. Örn: `order-service` → `orderservice`.

### 3.4 Paket Yapısı (Business Service)

```
src/main/java/com/turkcell/<packagename>/
  <ServiceName>Application.java
  <Resource>Controller.java
  dto/
    ErrorResponse.java
  exception/
    GlobalExceptionHandler.java
```

JPA gereksinimi varsa (entity, repository): `spring-generate-entity` yeteneğini devreye al.

### 3.5 Controller Stub (Business Service)

Controller sınıfı path-versioned (`/api/v1/`) ve `@Validated` anotasyonlu olacak şekilde oluşturulur. OpenAPI spec mevcutsa endpoint imzaları spec'e uygun olmalıdır:

```java
package com.turkcell.<packagename>;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/<resource-name>")
public class <Resource>Controller {
    // TODO: inject service and implement endpoints per OpenAPI spec
}
```

### 3.6 ErrorResponse DTO

Her business service'de bu record zorunludur:

```java
package com.turkcell.<packagename>.dto;

import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        List<String> details
) {}
```

### 3.7 GlobalExceptionHandler

Her business service'de bu sınıf zorunludur:

```java
package com.turkcell.<packagename>.exception;

import com.turkcell.<packagename>.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", "Request validation failed", details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", "Request validation failed", details));
    }
}
```

### 3.8 OpenAPI Spec

Eğer `/docs/openapi/<service-name>-v1.yml` yoksa, servise ait boş bir OpenAPI 3.0 iskelet dosyası oluşturulur:

```yaml
openapi: 3.0.3
info:
  title: <Service Name> API
  version: v1
paths: {}
```

---

## Adım 4 — Doğrulama Kontrol Listesi

Üretim tamamlandıktan sonra aşağıdaki listeyi geç:

- [ ] `pom.xml`'de `spring-boot-starter-parent` versiyonu `3.5.11` mi?
- [ ] `groupId` `com.turkcell` mi?
- [ ] Java version `21` mi?
- [ ] Paket adı kebab-case değil, tireler olmadan yazılmış mı? (`orderservice`, `productservice`)
- [ ] `application.yml`'de `spring.application.name` servis adıyla eşleşiyor mu?
- [ ] H2 datasource URL'si `<servicename>db` formatında mı?
- [ ] Port numarası mevcut servislerle çakışmıyor mu?
- [ ] Business service: `ErrorResponse` record mu? (class değil)
- [ ] Business service: `GlobalExceptionHandler` `@RestControllerAdvice` ile işaretlenmiş mi?
- [ ] Business service: Controller `@RequestMapping` path'i `/api/v1/` ile başlıyor mu?
- [ ] Business service: `springdoc-openapi-starter-webmvc-ui` versiyonu `2.8.8` mi?
- [ ] `/docs/openapi/<service-name>-v1.yml` mevcut mu?
- [ ] Lombok kullanılmamış mı?

---

## Standartlar ve Kurallar

### Genel Kurallar (Tüm Servis Türleri)

- Bağımlılık versiyonları **uydurulmaz**; bu belgede belirtilenlerin dışında bir bağımlılık eklenecekse kullanıcıdan onay alınır.
- Kod üretimi teker teker değil, dosya grupları halinde onaylanarak ilerler (AGENTS.MD §1.3).
- Herhangi bir breaking change senaryosunda implementasyon durdurulur, kullanıcı uyarılır.

### Adlandırma Kuralları

| Kavram | Format | Örnek |
|---|---|---|
| Servis dizini | kebab-case | `inventory-service` |
| Maven artifactId | kebab-case | `inventory-service` |
| Java paketi | tireler olmadan, lowercase | `com.turkcell.inventoryservice` |
| Ana sınıf | PascalCase + Application | `InventoryServiceApplication` |
| Controller sınıfı | PascalCase + Controller | `InventoryController` |

### Dependency Injection

Constructor injection kullanılır. `@Autowired` kullanılmaz:

```java
// DOGRU
public InventoryController(InventoryService service) {
    this.service = service;
}

// YANLIS
@Autowired
private InventoryService service;
```

### DTO Kuralları

DTO'lar her zaman Java `record` olarak tanımlanır, `class` kullanılmaz.

### API Versiyonlama

Path versiyonlama kullanılır: `/api/v1/<resource>`. Breaking change gerektiren güncellemelerde v2 controller ayrı olarak eklenir; mevcut v1 değiştirilmez.
