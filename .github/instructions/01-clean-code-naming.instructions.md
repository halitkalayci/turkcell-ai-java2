---
description: Bu dosya Clean Code prensipleri içerisinde isimlendirme prensiplerinin doğru uygulanması üzerine kural setini içeren bir dosyadır.
applyTo: "**/*.java"
---

# Clean Code - İsimlendirme Kuralları

## Amaç

İsimler, kodun en temel iletişim aracıdır. İyi seçilmiş isimler; yorum satırına, ek belgeye ve "ne yapar bu?" sorusuna gerek bırakmaz. Bu dosya, Robert C. Martin'in *Clean Code* kitabındaki isimlendirme prensiplerini Spring Boot / Java projelerine uyarlar ve bağlayıcı kural olarak tanımlar.

---

# Genel Kurallar

## 1. Niyet Açıklayan İsimler Kullan (Use Intention-Revealing Names)

İsim; neyi tuttuğunu, ne yaptığını ve neden kullanıldığını tek başına açıklamalıdır. İsim ek bir yoruma ihtiyaç duyuyorsa o isim yanlıştır.

```java
// YANLIS
int d; // elapsed time in days

// DOGRU
int elapsedTimeInDays;
```

```java
// YANLIS — ne döndürdüğü belirsiz
public List<int[]> getThem() {
    List<int[]> list1 = new ArrayList<>();
    for (int[] x : theList)
        if (x[0] == 4) list1.add(x);
    return list1;
}

// DOGRU
public List<Cell> getFlaggedCells() {
    List<Cell> flaggedCells = new ArrayList<>();
    for (Cell cell : gameBoard)
        if (cell.isFlagged()) flaggedCells.add(cell);
    return flaggedCells;
}
```

---

## 2. Yanlış Bilgi Vermekten Kaçın (Avoid Disinformation)

İsim, okuyucuyu yanıltmamalıdır. Özellikle tip bilgisi içeren sahte isimlerden kaçın.

```java
// YANLIS — List değil ama adında List var, kafa karıştırır
Map<String, Product> productList;

// DOGRU
Map<String, Product> productsByName;
```

---

## 3. Anlamlı Ayrımlar Yap (Make Meaningful Distinctions)

Sadece derleyiciyi geçmek için isimlere anlamsız ek koymak yasaktır.

```java
// YANLIS — Product, ProductInfo, ProductData hepsi aynı şeyi ifade ediyor
class Product {}
class ProductInfo {}
class ProductData {}

// DOGRU — her sınıf ayrı sorumluluğu olan bağımsız bir konsept
class Product {}           // domain entity
class ProductResponse {}   // API çıktı modeli
class ProductCreateRequest {} // API girdi modeli
```

---

## 4. Telaffuz Edilebilir İsimler Kullan (Use Pronounceable Names)

Sesli okunabilir olmayan isimler, ekip içi tartışmayı zorlaştırır.

```java
// YANLIS — telaffuz edilemiyor
private Date genymdhms;
private Date modymdhms;
private final String pszqint = "102";

// DOGRU
private Date generationTimestamp;
private Date modificationTimestamp;
private final String recordId = "102";
```

---

## 5. Aranabilir İsimler Kullan (Use Searchable Names)

Tek harfli ve sayısal sabit isimler, kod tabanında arama yapmayı imkânsızlaştırır.

```java
// YANLIS — 5'i aramak bütün sayıları getirir
for (int j = 0; j < 5; j++) { sum += t[j] * 4 / 5; }

// DOGRU
private static final int WORK_DAYS_PER_WEEK = 5;
private static final int REAL_DAYS_PER_IDEAL_DAY = 5;

for (int taskIndex = 0; taskIndex < NUMBER_OF_TASKS; taskIndex++) {
    int realWeeks = taskEstimate[taskIndex] * REAL_DAYS_PER_IDEAL_DAY / WORK_DAYS_PER_WEEK;
    sum += realWeeks;
}
```

---

## 6. Kodlamalardan Kaçın (Avoid Encodings)

Hungarian Notation, üye öneki (`m_`) ve interface öneki (`I`) yasaktır.

```java
// YANLIS
String strProductName;
int iStock;
boolean bIsActive;
private String m_description;
interface IProductService {}

// DOGRU
String productName;
int stock;
boolean active;
private String description;
interface ProductService {}
```

---

## 7. Zihinsel Haritalamadan Kaçın (Avoid Mental Mapping)

Okuyucunun ismi gerçek kavrama çevirmek için ekstra düşünmesini gerektirme.

```java
// YANLIS — r'nin URL olduğunu ancak bağlamdan tahmin edebilirsin
for (String r : urlList) {
    // ...
}

// DOGRU
for (String url : urlList) {
    // ...
}
```

---

## 8. Tek Konsept İçin Tek Kelime (One Word Per Concept)

Aynı kavram için fetch, retrieve, get gibi farklı kelimeler kullanmak tutarsızlık yaratır.

```java
// YANLIS — aynı işlem için farklı kelimeler
customerController.fetchCustomer(id);
productController.retrieveProduct(id);

// DOGRU — tüm servisler get kullanır
customerController.getCustomer(id);
productController.getProduct(id);
```

---

## 9. Çözüm ve Problem Alanı İsimlerini Kullan

Bilgisayar bilimi terimlerini (AccountVisitor, JobQueue) ve problem alanı terimlerini (Order, Invoice, Shipment) uygun bağlamda kullan.

```java
// Cozum alani terimi
public class ProductCommandHandler {}
public class PaymentGatewayAdapter {}
// Problem alani terimi
public class Invoice {}
```

---

# Sınıf İsimleri

## Kurallar

- Sınıf isimleri **isim (noun)** ya da **isim tamlaması** olmalıdır.
- `Manager`, `Processor`, `Data`, `Info` gibi belirsiz son eklerden kaçın.
- İsim tek başına nesnenin sorumluluğunu açıklamalıdır.
- Her zaman **PascalCase** kullanılır.

## Doğru Örnekler

```java
// Entity
public class Product {}
public class Order {}

// DTO
public record ProductCreateRequest(...) {}
public record ProductResponse(...) {}

// Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {}

// Service
public class ProductService {}

// Exception
public class ProductNotFoundException extends RuntimeException {}

// Controller
public class ProductController {}
```

## Yanlış Örnekler

```java
// YANLIS — belirsiz son ekler
public class ProductManager {}
public class ProductData {}

// YANLIS — fiil ile basliyor
public class ManageProduct {}

// YANLIS — kisaltma
public class PrdCtrl {}
```

---

# Metot İsimleri

## Kurallar

- Metot isimleri **fiil (verb)** ya da **fiil tamlaması** olmalıdır.
- Her zaman **camelCase** kullanılır.
- Erişici metotlar `get` ile, değiştirici metotlar `set` ile başlar.
- Boolean döndüren metotlar `is`, `has`, `can`, `should` ile başlar.
- Fabrika metotları nesnenin oluşturulacağı bağlamı açıklar.

## Erişici / Değiştirici Metotlar

```java
// DOGRU
product.getPrice();
product.setPrice(99.99);

// YANLIS
product.price();       // get eksik
product.changePrice(); // set yerine change kullanildi
```

## Boolean Metotlar

```java
// DOGRU
boolean isActive();
boolean hasStock();
boolean canBeOrdered();

// YANLIS
boolean active();      // is eksik
boolean checkStock();  // soru ifade etmiyor
```

## İş Mantığı Metotları

```java
// DOGRU — ne yaptığı açık, fiil + nesne yapısı
public ProductResponse createProduct(ProductCreateRequest request) {}
public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) {}
public void deleteProduct(UUID id) {}
public ProductPage listProducts(int page, int size, String query) {}
public StockUpdateResponse updateStock(UUID id, StockUpdateRequest request) {}
public ProductResponse findById(UUID id) {}

// YANLIS
public ProductResponse process(ProductCreateRequest request) {}
public void doUpdate(UUID id, ProductUpdateRequest request) {}
```

## Aşırı Yüklenmiş (Overloaded) Kurucular Yerine Fabrika Metodu

```java
// YANLIS — hangi Complex hangisi?
Complex fulcrumPoint = new Complex(23.0);

// DOGRU — ne oluşturulduğu açık
Complex fulcrumPoint = Complex.fromRealNumber(23.0);
```

---

# Değişken İsimleri

## Kurallar

- Değişken isimleri her zaman **camelCase** kullanır.
- Tek harfli değişkenler **yalnızca kısa döngü sayaçlarında** kabul edilir (`i`, `j`, `k`). Bunun dışında kesinlikle yasaktır.
- Sabitler (constants) **SCREAMING_SNAKE_CASE** ile yazılır.
- Tip bilgisi isme eklenmez (No Hungarian Notation).
- Kısaltma kullanılmaz; okunabilirlik kısalıktan önce gelir.

## Alan Değişkenleri (Fields)

```java
// DOGRU
private String productName;
private double unitPrice;
private LocalDateTime createdAt;

// YANLIS
private String pName;     // kisaltma
private double dPrice;    // tip on eki
private int s;            // tek harf
```

## Yerel Değişkenler (Local Variables)

```java
// DOGRU
public ProductPage listProducts(int page, int size, String query) {
    PageRequest pageRequest = PageRequest.of(page, size);
    Page<Product> productPage = productRepository.findAll(pageRequest);
    List<ProductResponse> productResponses = productPage.getContent()
            .stream().map(this::toResponse).toList();
    return new ProductPage(productResponses, page, size,
            productPage.getTotalElements(), productPage.getTotalPages());
}

// YANLIS — p, s, q, pr, pg, lst hiçbir şey anlatmıyor
public ProductPage listProducts(int p, int s, String q) {
    PageRequest pr = PageRequest.of(p, s);
    Page<Product> pg = productRepository.findAll(pr);
    return new ProductPage(pg.getContent().stream().map(this::toResponse).toList(), p, s, ...);
}
```

## Sabitler (Constants)

```java
// DOGRU
public static final int MAX_PRODUCT_NAME_LENGTH = 200;
public static final double TAX_RATE = 0.20;

// YANLIS
public static final int n = 200;
public static final int minStock = 0; // camelCase degil
```

## Döngü Değişkenleri

```java
// DOGRU — kisa kapsamli sayac icin i/j/k kabul edilebilir
for (int i = 0; i < products.size(); i++) { ... }

// Kapsam buyudugunde anlamli isim zorunludur
for (Product product : products) {
    processProduct(product);
}

// YANLIS — ic ice dongu, i/j yetersiz kaliyor
for (int i = 0; i < orders.size(); i++)
    for (int j = 0; j < orders.get(i).getItems().size(); j++) { ... }

// DOGRU
for (int orderIndex = 0; orderIndex < orders.size(); orderIndex++) {
    Order order = orders.get(orderIndex);
    for (int itemIndex = 0; itemIndex < order.getItems().size(); itemIndex++) { ... }
}
```

## Boolean Değişkenler

```java
// DOGRU
boolean isActive;
boolean hasDiscount;
boolean shouldSendNotification;

// YANLIS
boolean flag;   // ne anlama geliyor?
boolean check;  // ne kontrol ediliyor?
```

## Anlamsız ve Bağlam Dışı İsimler Yasak

```java
// YANLIS
Object obj;
String temp;
int data;

// DOGRU
Product foundProduct;
String encodedToken;
int retryCount;
```