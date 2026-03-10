---
name: create-entity
description: Contract-First geliştirmede Spring Boot mikroservislerine kurumsal standartlara uygun JPA entity ve repository sınıfları oluşturmak için kullanılır. Kullanıcıdan gerekli girdileri toplar ve ilgili servis altına standart Java dosyaları üretir.
---

# Prompt Girdileri

Kullanıcıdan aşağıdaki bilgileri sırasıyla iste. Hepsi alınmadan implementasyona geçme.

| # | Girdi | Açıklama | Örnek |
|---|-------|----------|-------|
| 1 | `serviceName` | Hangi servise ait (kebab-case) | `product-service` |
| 2 | `entityName` | Entity sınıf adı (PascalCase, tekil) | `Product` |
| 3 | `fields` | Alan adı, tipi ve kısıtları | `name: String zorunlu max:200, sku: String zorunlu unique max:64, price: BigDecimal zorunlu >0` |
| 4 | `relations` | Başka entity'lerle ilişkiler (varsa) | `ManyToOne -> Category, OneToMany -> OrderItem` |
| 5 | `uniqueConstraints` | Birden fazla alan üzerinde bileşik unique kısıt (varsa) | `(sku, category) bileşik unique` |
| 6 | `generateRepository` | Repository arayüzü oluşturulsun mu? | `evet / hayır` |
| 7 | `customQueries` | Repository'e eklenecek özel sorgular (varsa) | `findBySku(String sku), findAllByCategory(Category category)` |

Eksik veya belirsiz bir girdi varsa uydurmadan kullanıcıya sor.

---

# Amaç

`com.turkcell.<servicePackage>.entity.<EntityName>.java` ve isteğe bağlı olarak  
`com.turkcell.<servicePackage>.repository.<EntityName>Repository.java` dosyalarını  
kurumsal JPA standartlarına tam uyumlu biçimde oluşturmak.

---

# Görev

1. Yukarıdaki girdileri topla.
2. Aşağıdaki **Standartlar** ve **Doğrulama Kontrol Listesi** bölümlerini uygulayarak dosyaları üret.
3. Dosyaları ilgili servis dizinlerine yaz:
   - `<service>/src/main/java/com/turkcell/<servicePackage>/entity/<EntityName>.java`
   - `<service>/src/main/java/com/turkcell/<servicePackage>/repository/<EntityName>Repository.java` (generateRepository: evet ise)
4. Üretim tamamlandıktan sonra yalnızca dosya adlarını ve uygulanan kısıtları özetle. Detay istenmediği sürece kodun tamamını tekrar yazdırma.

---

# Kurallar

- `AGENTS.MD` ve proje instruction dosyaları her zaman önceliklidir.
- Hiçbir alan, tip, kısıt veya ilişki **uydurulmamalıdır**. Belirsizlik durumunda implementasyonu durdur, kullanıcıya sor.
- Lombok **kesinlikle kullanılmaz**; getter/setter'lar manuel yazılır.
- Tüm `import` ifadeleri `jakarta.persistence.*` paketinden gelir (Spring Boot 3+).
- Primary key her zaman `UUID` tipinde olur; `Long` veya otomatik artan sayısal ID kullanılmaz.
- Audit alanları (`createdAt`, `updatedAt`) her entity'de zorunludur.

---

# Standartlar

## 1. Sınıf Anotasyonları

```java
@Entity
@Table(name = "products")   // çoğul, snake_case
public class Product { ... }
```

## 2. Primary Key

```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;
```

## 3. Sütun Kısıtları

| Durum | Anotasyon |
|-------|----------|
| Zorunlu, metin | `@Column(nullable = false, length = N)` |
| Zorunlu, benzersiz, metin | `@Column(nullable = false, unique = true, length = N)` |
| Opsiyonel, metin | `@Column(length = N)` |
| Para birimi | `@Column(nullable = false, precision = 10, scale = 2)` |
| Sayısal (integer/double) | `@Column(nullable = false)` |

## 4. Audit Alanları (Her Entity'de Zorunlu)

```java
@Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(nullable = false)
private LocalDateTime updatedAt;

@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
}
```

## 5. Constructor Kuralları

```java
protected Product() {}  // JPA için zorunlu — no-arg

public Product(String name, BigDecimal price, String sku) {  // iş mantığı constructor'u
    this.name = name;
    this.price = price;
    this.sku = sku;
}
```

## 6. Getter / Setter

- `id` için yalnızca getter yazılır, setter yazılmaz.
- Diğer tüm alanlar için getter ve setter manuel yazılır.
- Audit alanları (`createdAt`, `updatedAt`) için yalnızca getter yazılır.

## 7. İlişkiler

```java
// ManyToOne
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id", nullable = false)
private Category category;

// OneToMany
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items = new ArrayList<>();
```

## 8. Repository Arayüzü

```java
package com.turkcell.<servicePackage>.repository;

import com.turkcell.<servicePackage>.entity.<EntityName>;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface <EntityName>Repository extends JpaRepository<<EntityName>, UUID> {
    // customQueries buraya eklenir
}
```

---

# Doğrulama Kontrol Listesi

Üretim tamamlandıktan sonra aşağıdaki listeyi geç:

- [ ] Primary key `UUID` tipinde ve `@GeneratedValue(strategy = GenerationType.UUID)` kullanıyor mu?
- [ ] `@Entity` ve `@Table(name = "...")` anotasyonları mevcut mu?
- [ ] Tablo adı **çoğul** ve **snake_case** formatında mı? (`products`, `order_items`)
- [ ] Zorunlu metin alanlarında `nullable = false` ve `length` kısıtı var mı?
- [ ] `BigDecimal` para birimi alanlarında `precision` ve `scale` tanımlı mı?
- [ ] Audit alanları (`createdAt`, `updatedAt`) eklenmiş mi?
- [ ] `@PrePersist` ve `@PreUpdate` callback'leri mevcut mu?
- [ ] Lombok **kullanılmamış** mı?
- [ ] `id` için yalnızca getter yazılmış, setter yazılmamış mı?
- [ ] JPA için zorunlu olan **no-arg constructor** `protected` olarak mevcut mu?
- [ ] Tüm `import` ifadeleri `jakarta.persistence.*` paketinden mi?
- [ ] İlişkilerde `FetchType.LAZY` kullanılmış mı?
- [ ] Repository `JpaRepository<<EntityName>, UUID>` extend ediyor mu?

---

# Örnek Kullanım

```
/create-entity

serviceName         : product-service
entityName          : Product
fields              : name (String, zorunlu, max:200), sku (String, zorunlu, unique, max:64),
                      price (BigDecimal, zorunlu, >0), description (String, opsiyonel, max:500)
relations           : ManyToOne -> Category
uniqueConstraints   : yok
generateRepository  : evet
customQueries       : findBySku(String sku), findAllByCategory(Category category)
```

Beklenen çıktı: `Product.java` ve `ProductRepository.java` dosyaları oluşturulur.
