# AvendOc Admin — Expo

تطبيق الأدمن المنفصل. تسجيل الدخول متاح فقط لحساب Firebase يحمل Custom Claim باسم `admin: true`. بعد الدخول يعرض الإعلانات ذات الحالة `PAYMENT_PENDING`، مع أزرار الموافقة والنشر أو الرفض.

```bash
npm install
npx expo start
```

للبناء عبر Expo.dev استخدم `eas.json` وملف `EXPO_MIGRATION.md` في جذر المستودع.
