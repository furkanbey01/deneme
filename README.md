# Saat 58 Alarm

Saat 58 Alarm, her saat `58. dakikada` tek seferlik exact alarm kuran minimal bir Android uygulamasıdır. Alarm tetiklenince foreground service başlar, alarm sesi döngü halinde çalar, telefon döngü halinde titreşir ve standart Android alarm bildirimi gösterilir. Bildirimdeki **Kapat** aksiyonu telefondan veya Samsung Galaxy Watch 6 Classic'e bridge edilen bildirimden alarmı durdurur.

## MP3 dosyası nereye koyulur?

Alarm sesi için gerçek MP3 dosyasını şu yola koyun:

```text
app/src/main/res/raw/alarm_sound.mp3
```

Codex PR aracı ikili dosyaları kabul etmediği için repoya MP3 eklenmedi. Dosya eklenirse uygulama `alarm_sound` raw resource'unu `MediaPlayer` ile döngü halinde oynatır. Dosya yoksa uygulama derlenir ve test amaçlı yedek alarm tonu üretir.

## GitHub Actions'tan APK nasıl indirilir?

1. GitHub'da repo sayfasını açın.
2. **Actions** sekmesine gidin.
3. `Android APK Build` workflow çalışmasını seçin.
4. Başarılı build altında **Artifacts** bölümünden `Saat58Alarm-debug-apk` artifact'ını indirin.
5. ZIP içindeki `app-debug.apk` dosyasını telefona kurun.

## Telefonda gerekli izinler nasıl açılır?

Uygulamadaki **İzinleri Aç** butonu bildirim iznini ister ve Android 12+ cihazlarda exact alarm ayar ekranını açar. Gerekirse sistem ayarlarından şunları kontrol edin:

- Bildirimler açık olmalı.
- Alarmlar ve hatırlatıcılar / exact alarm izni açık olmalı.
- Pil kısıtlamaları alarm güvenilirliği için kapatılabilir.

## Samsung Galaxy Watch 6 Classic bildirimi

Ayrı Wear OS uygulaması yoktur. Bildirim standart Android bildirimi olarak hazırlanır ve Wear OS'e otomatik bridge edilir. Saatte bildirimin görünmesi ve **Kapat** aksiyonunun çalışması için telefondaki Galaxy Wearable bildirim ayarlarında bu uygulamanın bildirimleri açık olmalıdır.
