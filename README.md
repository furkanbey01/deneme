# Saat 58 Alarm

Saat 58 Alarm, aktif edildiğinde her saatin 58. dakikasında tek seferlik exact alarm kuran sade bir Android uygulamasıdır. Alarm tetiklenince uygulamaya gömülü `app/src/main/res/raw/alarm_sound.mp3` eklendiğinde bu sesi döngü halinde çalar, telefon titreşir ve standart Android alarm bildirimi gösterilir. Bildirimdeki **Kapat** aksiyonu telefondan veya Samsung Galaxy Watch 6 Classic'e bridge edilen bildirimden alarmı durdurur.

## MP3 dosyası nereye koyulur?

Alarm sesi şu dosyada olmalıdır:

```text
app/src/main/res/raw/alarm_sound.mp3
```

Codex PR aracı ikili dosya kabul etmediği için repoya MP3 eklenmemiştir. Gerçek kullanım için bu konuma kısa/uygun bir MP3 alarm sesi koyun. Dosya yoksa uygulama derlenir ve test için basit yedek alarm tonu kullanır; dosya eklendiğinde kod bu kaynağı `alarm_sound` raw resource olarak döngü halinde oynatır.

## GitHub Actions'tan APK nasıl indirilir?

1. GitHub'da repo sayfasını açın.
2. **Actions** sekmesine gidin.
3. `Android APK Build` workflow çalışmasını seçin.
4. Başarılı build altında **Artifacts** bölümünden `Saat58Alarm-debug-apk` artifact'ını indirin.
5. ZIP içindeki `app-debug.apk` dosyasını Android telefona kurun.

## Telefonda gerekli izinler nasıl açılır?

Uygulama ekranındaki **İzinleri Aç** butonu bildirim iznini ister ve Android 12+ cihazlarda exact alarm ayar ekranına yönlendirir. Gerekirse sistem ayarlarından ayrıca şunları kontrol edin:

- Bildirimler: açık olmalı.
- Alarmlar ve hatırlatıcılar / Exact alarm: uygulama için izinli olmalı.
- Pil optimizasyonları: alarm güvenilirliği için kısıtlamasız kullanılması önerilir.

## Samsung Galaxy Watch 6 Classic bildirimi

Ayrı Wear OS uygulaması yoktur. Bildirim standart Android bildirimi olarak hazırlanmıştır ve Wear OS'e otomatik bridge edilir. Samsung Galaxy Watch 6 Classic'te bildirimin görünmesi ve **Kapat** aksiyonunun saatten çalışması için telefondaki Galaxy Wearable / saat bildirim ayarlarında bu uygulamanın bildirimleri açık olmalıdır.
