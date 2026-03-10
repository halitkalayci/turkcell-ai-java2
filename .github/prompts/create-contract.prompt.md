---
name: create-contract
description: Contract-First developmentta yeni bir OpenAPI sözleşmesi oluşturmak için kullanılır. Kullanıcıdan gerekli girdileri toplar ve docs/openapi altına standart bir YAML dosyası üretir.
---

# Prompt Girdileri

Kullanıcıdan aşağıdaki bilgileri sırаsıyla iste. Hepsi alınmadan implementasyona geçme.

| # | Girdi | Açıklama | Örnek |
|---|-------|----------|-------|
| 1 | `serviceName` | Servis adı (kebab-case) | `order-service` |
| 2 | `version` | API versiyonu | `v1` |
| 3 | `port` | Servis portu | `8082` |
| 4 | `resources` | Kaynak adı (lowerCamelCase, tekil) | `order` |
| 5 | `fields` | Her kaynak için alan adı, tipi ve kısıtları | `id: uuid, totalPrice: double >0, status: enum[PENDING,CONFIRMED,CANCELLED]` |
| 6 | `endpoints` | Hangi CRUD operasyonları dahil edilsin | `GET list, GET by id, POST, PUT, DELETE` |
| 7 | `securedEndpoints` | Hangi endpoint'ler bearer auth gerektirir | `POST, PUT, DELETE` |
| 8 | `businessRules` | Ek validasyon ve iş kuralları | `status: sadece PENDING→CONFIRMED geçişine izin ver` |

Eksik veya belirsiz bir girdi varsa uydurmadan kullanıcıya sor.

---

# Amaç

`docs/openapi/{serviceName}-{version}.yml` dosyasını OpenAPI 3.0.3 standardında, proje sözleşme kurallarına tam uyumlu biçimde oluşturmak.

---

# Görev

1. Yukarıdaki girdileri topla.
2. Aşağıdaki **Tasarım Kuralları** ve **Beklenen Çıktı Formatı** bölümlerini uygulayarak YAML dosyasını oluştur.
3. Dosyayı `docs/openapi/{serviceName}-{version}.yml` yoluna yaz.
4. Dosya oluşturulduktan sonra yalnızca dosya adını ve hangi endpoint'lerin eklendiğini özetle. Detay istenmediği sürece YAML içeriğini tekrar yazdırma.

---

# Kurallar

- `AGENTS.MD` ve proje instruction dosyaları her zaman önceliklidir.
- Hiçbir alan, tip, kısıt veya iş kuralı **uydurulmamalıdır**. Belirsizlik durumunda implementasyonu durdur, kullanıcıya sor.
- Sözleşme implementasyona göre değiştirilemez; implementasyon sözleşmeye uymak zorundadır.
- BREAKING CHANGE içeren bir güncelleme talep edilirse işlemi durdur, kullanıcıyı uyar ve versiyon yükseltme stratejisi öner.
- `additionalProperties: false` — tüm request body şemalarına eklenir.
- Tüm `4xx` hata yanıtları `ErrorResponse` şemasını kullanır.
- SKU veya benzeri unique constraint ihlalleri `409 Conflict` döner.

---

# Beklenen Çıktı Formatı

Aşağıdaki şablon yapısına uy. Referans dosya olarak `#file:product-service-v1.yml` kullanılır.

```yaml
openapi: 3.0.3
info:
  title: {ServiceName} API
  version: {version}.0.0
  description: {serviceName} microservice contract (contract-first).

servers:
  - url: http://localhost:{port}

tags:
  - name: {Resource}s
    description: {Resource} management endpoints

paths:
  /api/{version}/{resource}s:
    get:          # sadece endpoints listesinde varsa ekle
    post:         # sadece endpoints listesinde varsa ekle

  /api/{version}/{resource}s/{id}:
    get:          # sadece endpoints listesinde varsa ekle
    put:          # sadece endpoints listesinde varsa ekle
    delete:       # sadece endpoints listesinde varsa ekle

components:
  securitySchemes:
    bearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT

  schemas:
    {Resource}:           # Tam kaynak şeması (response)
    {Resource}CreateRequest:
    {Resource}UpdateRequest:
    {Resource}Page:       # Sadece list endpoint varsa
    ErrorResponse:        # Her sözleşmede zorunlu
```

---

# Tasarım Kuralları

1. **Path versiyonlama** — tüm path'ler `/api/{version}/` ile başlar (örn. `/api/v1/orders`).
2. **Operasyon ID'leri** — `listOrders`, `createOrder`, `getOrderById`, `replaceOrder`, `deleteOrder` formatında, camelCase.
3. **Pagination** — `GET` liste endpoint'i her zaman `page` (min:0, default:0), `size` (min:1, max:200, default:20) ve opsiyonel `q` query parametrelerini içerir; yanıt `{Resource}Page` şemasını döner.
4. **Zorunlu alanlar** — request body şemalarında `required` listesi mutlaka tanımlanır.
5. **Enum alanlar** — `type: string` + `enum: [...]` olarak tanımlanır.
6. **UUID id alanları** — `type: string, format: uuid` kullanılır.
7. **Auth** — `securedEndpoints` listesindeki operasyonlara `security: [{bearerAuth: []}]` eklenir; diğerlerine `security: []`.
8. **Location header** — `POST` başarı yanıtı (201) `Location` header'ını içerir.
9. **ErrorResponse şeması** her dosyada aynı yapıda tanımlanır:
   ```yaml
   ErrorResponse:
     type: object
     required: [code, message]
     properties:
       code:
         type: string
       message:
         type: string
       details:
         type: array
         items:
           type: string
   ```
10. **Dosya adı** — `{serviceName}-{version}.yml` formatı, örn. `order-service-v1.yml`.

---

# Örnek Kullanım

```
/create-contract

serviceName   : order-service
version       : v1
port          : 8082
resources     : order
fields        : id (uuid), customerId (uuid, zorunlu), totalPrice (double, >0), status (enum: PENDING|CONFIRMED|CANCELLED)
endpoints     : GET list, GET by id, POST, PUT, DELETE
securedEndpoints : POST, PUT, DELETE
businessRules : customerId boş olamaz; totalPrice 0'dan büyük olmalı
```

Beklenen çıktı: `docs/openapi/order-service-v1.yml` dosyası oluşturulur.
