---
description: Bu dosya Clean Code prensipleri içerisinde fonksiyon prensiplerinin doğru uygulanması üzerine kural setini içeren bir dosyadır.
applyTo: "**/*.java"
---

# Clean Code - Fonksiyon Kuralları

## Amaç

Fonksiyonlar, kodun en temel yapı taşlarıdır. İyi yazılmış bir fonksiyon; tek bir işi yapar, kolay okunur ve yan etki üretmez. Bu dosya, Robert C. Martin'in *Clean Code* kitabındaki fonksiyon prensiplerini Spring Boot / Java projelerine uyarlar ve bağlayıcı kural olarak tanımlar.

---

# 1. Fonksiyonlar Küçük Olmalıdır (Functions Should Be Small)

Her fonksiyon **20 satırı geçmemelidir**. İdeal uzunluk 5-10 satırdır. Bloklar (if, else, while içleri) tek satır veya tek metot çağrısı içermelidir.

```java
// YANLIS — çok uzun, birden fazla iş yapıyor
public ProductResponse createProduct(ProductCreateRequest request) {
    if (request.getName() == null || request.getName().isBlank()) {
        throw new IllegalArgumentException("Name cannot be blank");
    }
    if (request.getPrice() <= 0) {
        throw new IllegalArgumentException("Price must be positive");
    }
    Product product = new Product();
    product.setName(request.getName());
    product.setPrice(request.getPrice());
    product.setStock(request.getStock());
    product.setActive(true);
    product.setCreatedAt(LocalDateTime.now());
    Product savedProduct = productRepository.save(product);
    return new ProductResponse(savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice());
}

// DOGRU — her adım kendi metoduna ayrılmış
public ProductResponse createProduct(ProductCreateRequest request) {
    validateCreateRequest(request);
    Product product = buildProduct(request);
    Product savedProduct = productRepository.save(product);
    return toResponse(savedProduct);
}
```

---

# 2. Tek İş Yap (Do One Thing)

Bir fonksiyon yalnızca **bir iş yapmalı, onu iyi yapmalı ve yalnızca onu yapmalıdır**. Fonksiyonun birden fazla soyutlama seviyesinde işlem yapması yasaktır.

"Bir iş yapıp yapmadığını" anlamanın yolu: Fonksiyondan anlamlı bir başka fonksiyon çıkarılabiliyorsa, o fonksiyon birden fazla iş yapıyordur.

```java
// YANLIS — hem doğrulama, hem dönüşüm, hem kayıt yapıyor
public ProductResponse saveAndNotify(ProductCreateRequest request) {
    if (request.getPrice() < 0) throw new IllegalArgumentException("...");
    Product product = new Product();
    product.setName(request.getName());
    productRepository.save(product);
    emailService.sendNewProductEmail(product);
    return toResponse(product);
}

// DOGRU — her metot tek iş yapıyor
public ProductResponse createProduct(ProductCreateRequest request) {
    validateCreateRequest(request);
    Product savedProduct = saveProduct(request);
    notifyProductCreated(savedProduct);
    return toResponse(savedProduct);
}
```

---

# 3. Soyutlama Seviyesi Tutarlı Olmalıdır (One Level of Abstraction per Function)

Bir fonksiyon içindeki tüm ifadeler **aynı soyutlama seviyesinde** olmalıdır. Yüksek seviyeli iş mantığı ile düşük seviyeli detaylar (string manipülasyonu, null kontrolü vb.) aynı fonksiyonda bulunmamalıdır.

```java
// YANLIS — yüksek seviye iş mantığı ile düşük seviye detaylar karışıyor
public void processOrder(Order order) {
    // Yüksek seviye
    validateOrder(order);
    // Düşük seviye detay — soyutlama ihlali
    String formattedDate = order.getCreatedAt()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    order.setLabel("Order-" + order.getId() + "-" + formattedDate);
    // Yüksek seviye
    saveOrder(order);
    sendConfirmation(order);
}

// DOGRU
public void processOrder(Order order) {
    validateOrder(order);
    assignOrderLabel(order);
    saveOrder(order);
    sendConfirmation(order);
}

private void assignOrderLabel(Order order) {
    String formattedDate = order.getCreatedAt()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    order.setLabel("Order-" + order.getId() + "-" + formattedDate);
}
```

---

# 4. Fonksiyon Argümanları (Function Arguments)

Argüman sayısı mümkün olan en az düzeyde tutulmalıdır. Öncelik sırası: **niladic (0) > monadic (1) > dyadic (2)**. Üç ve üzeri argüman (triadic+) ancak zorunluluk halinde kabul edilir; bu durumda argümanlar bir nesne ile sarmalanmalıdır.

```java
// YANLIS — çok fazla argüman
public Product createProduct(String name, double price, int stock,
                             String category, boolean active, UUID supplierId) {}

// DOGRU — Request nesnesi ile sarmalanmış
public ProductResponse createProduct(ProductCreateRequest request) {}
```

## Boolean Argüman Yasağı

Boolean argüman, fonksiyonun iki iş yaptığının açık bir işaretidir. Yasaktır.

```java
// YANLIS
public void updateProduct(UUID id, ProductUpdateRequest request, boolean notifySupplier) {}

// DOGRU — iki ayrı metot
public void updateProduct(UUID id, ProductUpdateRequest request) {}
public void updateProductAndNotify(UUID id, ProductUpdateRequest request) {}
```

---

# 5. Yan Etkisiz Olmalıdır (Have No Side Effects)

Fonksiyonun adı ne söylüyorsa yalnızca onu yapmalıdır. Beklenmedik durum değişikliği, gizli kayıt işlemi veya dış sistem çağrısı **yan etkidir** ve yasaktır.

```java
// YANLIS — checkPassword aynı zamanda session başlatıyor (gizli yan etki)
public boolean checkPassword(String username, String password) {
    User user = userRepository.findByUsername(username);
    if (passwordEncoder.matches(password, user.getPassword())) {
        sessionManager.initialize(user); // YAN ETKİ
        return true;
    }
    return false;
}

// DOGRU — her metot kendi işini yapar
public boolean isPasswordValid(String username, String password) {
    User user = userRepository.findByUsername(username);
    return passwordEncoder.matches(password, user.getPassword());
}

public void initializeSession(User user) {
    sessionManager.initialize(user);
}
```

---

# 6. Komut-Sorgu Ayrımı (Command Query Separation)

Bir fonksiyon ya bir **işlem yapar (command)** ya da bir **değer döndürür (query)**; ikisini aynı anda yapamaz.

```java
// YANLIS — hem durum değiştiriyor hem boolean dönüyor
public boolean set(String attribute, String value) {
    if (attributeExists(attribute)) {
        setAttribute(attribute, value);
        return true;
    }
    return false;
}

// DOGRU — sorgu ve komut ayrılmış
public boolean hasAttribute(String attribute) {
    return attributeExists(attribute);
}

public void setAttribute(String attribute, String value) {
    // sadece günceller
}
```

---

# 7. Hata Kodları Yerine Exception Kullan (Prefer Exceptions to Returning Error Codes)

Hata durumunu `int`, `boolean` veya `null` ile döndürmek yasaktır. Her zaman **anlamlı bir exception** fırlatılmalıdır.

```java
// YANLIS — hata kodu dönüyor
public int deleteProduct(UUID id) {
    if (!productRepository.existsById(id)) {
        return -1; // hata kodu
    }
    productRepository.deleteById(id);
    return 0;
}

// YANLIS — null dönüyor
public Product findProduct(UUID id) {
    return productRepository.findById(id).orElse(null);
}

// DOGRU — exception fırlatılıyor
public void deleteProduct(UUID id) {
    if (!productRepository.existsById(id)) {
        throw new ProductNotFoundException(id);
    }
    productRepository.deleteById(id);
}

public Product findProduct(UUID id) {
    return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
}
```

## try-catch Bloğu Ayrı Metoda Taşınmalıdır

```java
// YANLIS — try-catch iş mantığıyla karışıyor
public void deleteProduct(UUID id) {
    try {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
        auditService.logDeletion(product);
    } catch (ProductNotFoundException e) {
        log.error("Product not found: {}", id);
        throw e;
    }
}

// DOGRU — try-catch kendi metodunda izole
public void deleteProduct(UUID id) {
    try {
        performDeletion(id);
    } catch (ProductNotFoundException e) {
        log.error("Product not found: {}", id);
        throw e;
    }
}

private void performDeletion(UUID id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    productRepository.delete(product);
    auditService.logDeletion(product);
}
```

---

# 8. Kendini Tekrar Etme (Don't Repeat Yourself - DRY)

Aynı mantık birden fazla yerde kopyalanmamalıdır. Tekrar eden kod bloğu tespit edildiğinde, ortak bir metoda çıkarılmalıdır.

```java
// YANLIS — doğrulama mantığı iki yerde kopyalanmış
public ProductResponse createProduct(ProductCreateRequest request) {
    if (request.getName() == null || request.getName().isBlank())
        throw new IllegalArgumentException("Name cannot be blank");
    if (request.getPrice() <= 0)
        throw new IllegalArgumentException("Price must be positive");
    // ...
}

public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {
    if (request.getName() == null || request.getName().isBlank())
        throw new IllegalArgumentException("Name cannot be blank");
    if (request.getPrice() <= 0)
        throw new IllegalArgumentException("Price must be positive");
    // ...
}

// DOGRU — ortak doğrulama metoda çıkarıldı
private void validateProductName(String name) {
    if (name == null || name.isBlank())
        throw new IllegalArgumentException("Name cannot be blank");
}

private void validateProductPrice(double price) {
    if (price <= 0)
        throw new IllegalArgumentException("Price must be positive");
}
```

---

# 9. Switch İfadeleri (Switch Statements)

`switch` ifadeleri doğaları gereği birden fazla iş yapar. Mümkün olduğunda **polimorfizm** ile değiştirilmelidir. Kaçınılamıyorsa `abstract factory` içine gömülmeli ve tekrar edilmemelidir.

```java
// YANLIS — switch her yere yayılıyor
public double calculateDiscount(String productType) {
    switch (productType) {
        case "ELECTRONICS": return 0.10;
        case "CLOTHING":    return 0.20;
        case "FOOD":        return 0.05;
        default:            return 0.0;
    }
}

// DOGRU — polimorfizm ile çözüldü
public interface DiscountPolicy {
    double calculate();
}

public class ElectronicsDiscountPolicy implements DiscountPolicy {
    public double calculate() { return 0.10; }
}

public class ClothingDiscountPolicy implements DiscountPolicy {
    public double calculate() { return 0.20; }
}
```

---

# Özet Kural Tablosu

| Kural | Zorunluluk |
|---|---|
| Fonksiyon 20 satırı geçmemeli | ZORUNLU |
| Tek iş yapmalı | ZORUNLU |
| Aynı soyutlama seviyesi | ZORUNLU |
| Argüman sayısı maksimum 2 (nesne hariç) | ZORUNLU |
| Boolean argüman yasak | ZORUNLU |
| Yan etki yasak | ZORUNLU |
| Komut veya sorgu; ikisi birden yasak | ZORUNLU |
| Null / hata kodu dönme yasak; exception fırlat | ZORUNLU |
| try-catch ayrı metotta izole edilmeli | ZORUNLU |
| Tekrar eden kod bloğu metoda çıkarılmalı | ZORUNLU |
| switch yerine polimorfizm tercih edilmeli | ZORUNLU |