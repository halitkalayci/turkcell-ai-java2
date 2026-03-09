### AGENTS.MD
Bu dosya bu projede çalışacak AI ya da Developerın uyması gereken koşulsuz, şartsız ve istisnasız sağlaması gereken kurallar dizesini içerir.

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

### 1.3) KOD OLUŞTURMA STRATEJİSİ

Dosya oluşturma işlemleri bütünsel olarak ele alınamaz. Maksimum 10'ar dosyalık birbirleri ile ilişki gruplar halinde bölünmelidir. Her grup arası ek onay beklemek zorunludur.