### AGENTS.MD
Bu dosya bu projede çalışacak AI ya da Developerın uyması gereken koşulsuz, şartsız ve istisnasız sağlaması gereken kurallar dizesini içerir.

Bu projede çalışırken herhangi bir karar bulmak istersen [DECISIONS.MD](.ai/governance/DECISIONS.MD) dosyasına bakabilirsin.

---

## 1) NASIL ÇALIŞILIR? (ZORUNLU İŞ AKIŞI)

### 1.1) UYDURMA YOK

Eğer herhangi bir bilgi eksikliğine/yanlışlığına rastlarsan bu bilgiyi uyduramazsın. İlgili işlemi durdur, kullanıcıya sor ve netleştirdikten sonra ilerle.

Özellikle uydurulması kesinlikle yasak olanlar:

- Bağımlılıklar

- Kütüphane versiyonları

- endpoint, request/response modelleri, hata modelleri

- DB şema/kolonları

- İş kuralları


### 1.2) ÖNCE PLANLA, SONRA KODLA

Kod üretmeden önce şunları yapmak zorundasın;

- Bir dosya dökümü hazırla. (hangi dosyalar değişecek/eklenecek + neden)

- Eğer yeni varsa bir bağımlılık matrisi hazırla. (Ne, hangi versiyonla, neden eklendi?)

- Planı sun, onay almadan asla implementasyona başlama.

### 1.3) KOD OLUŞTURMA STRATEJİSİ

Dosya oluşturma işlemleri bütünsel olarak ele alınamaz. Maksimum 10'ar dosyalık birbirleri ile ilişki gruplar halinde bölünmelidir. Her grup arası ek onay beklemek zorunludur.

### 1.4) ÇIKTI FORMATI

- Resmi bir dil kullan, emojilere yer verme.

- Yapılan işi minimal cümleler ile özet olarak maksimum 2-3 cümlelik özetlere böl. Detay istenmediği sürece verme.

- İlgili promptda öneri özellikle istenmediği sürece önerilerde bulunma.

- Dosya dökümlerine yalnızca dosya ismi yaz, DOSYA YOLU EKLEME.

- Her işlem sonrası bu işlemi test edebileceğim en kısa happy-path'i ver.

örneğin:

Bir spring product-service endpoint happy-path verirken:
````

`cd product-service`
`mvn spring-boot:run`
`GET http://localhost:8081/products` -> OK Gözüküyorsa test 
başarılı.

````


## 2) CONTRACT-FIRST YAKLAŞIMI

### 2.1 OpenAPI doğruluğun tek kaynağıdır (Single Source of Truth)

- API sözleşmeleri şu dizinde bulunur: `/docs/openapi`

- Her servis **kendi OpenAPI dosyasına sahip olmalıdır**:
  - `docs/openapi/product-service-v1.yml`
  - `docs/openapi/order-service-v1.yml`

- Swagger UI, bu sözleşmelerden üretilir.

- Implementasyon **her zaman sözleşmeye uymalıdır**; sözleşme implementasyona göre değiştirilemez.

### 2.2 Kod Üretim Politikası (Code Generation Policy)

- OpenAPI araçları **yalnızca repoda zaten mevcutsa** kullanılabilir.

- Açık onay olmadan **yeni bir OpenAPI generator bağımlılığı eklenmemelidir**.

- Eğer generator yoksa, controller ve DTO’lar **spec’e uygun olacak şekilde manuel olarak implement edilmelidir**.

### 2.3 Swagger UI

- Tüm servisler Swagger implementasyonu yapmalıdır.

## 3) VERSİYONLAMA

- API'lerde path versiyonlama kullanılır. örneğin `/api/v1/products`

- API'lerde talep edilen herhangi bir değişiklik BREAKING ise değişikliği yapma. Kullanıcıyı uyar ve dur.

- BREAKING değişikliklerde versiyon yükseltme stratejisini öner.

### 3.1) BREAKING CHANGE KRİTERLERİ

Aşağıdakilerden herhangi biri varsa değişiklik BREAKING'dir:

- Request şemasına zorunlu alan eklenmesi
- Mevcut bir alanın kaldırılması veya yeniden adlandırılması
- Bir alanın tipinin değiştirilmesi
- Mevcut bir endpoint'in kaldırılması veya path'inin değiştirilmesi
- Response şemasından alan çıkarılması
- HTTP durum kodlarının değiştirilmesi

Planlama aşamasında bu listeyle karşılaştırma yapılmadan implementasyona geçilemez.