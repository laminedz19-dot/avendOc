# AvendOc Expo Migration

تمت إضافة مشروعَي Expo منفصلين بدل تطبيقَي Kotlin القديمين:

- `expo-user`: تطبيق المستخدم، المصادقة بالبريد، السوق، وإنشاء إعلان بحالة `PAYMENT_PENDING`.
- `expo-admin`: تطبيق الأدمن، دخول Firebase مع فحص Custom Claim باسم `admin`، وعرض الإعلانات المعلقة والموافقة أو الرفض.

## التشغيل المحلي

```bash
cd expo-user
npm install
npx expo start
```

```bash
cd expo-admin
npm install
npx expo start
```

## البناء عبر Expo.dev / EAS

بعد تسجيل الدخول إلى حساب Expo وربط كل مجلد بمشروع Expo مستقل:

```bash
cd expo-user
npx eas login
npx eas init
npx eas build:configure
npx eas build --platform android --profile preview
```

ثم كرر الأوامر داخل `expo-admin`. يجب استخدام مشروعَي Expo مختلفين حتى يبقى لكل APK اسم ومعرّف مستقل.

## Firebase

المشروعان يستخدمان مشروع Firebase نفسه `achridz-2c628`. يجب أن تكون مصادقة Email/Password مفعّلة. يجب منح حساب الأدمن Custom Claim باسم `admin: true`؛ التطبيق لا يسمح بالدخول إلى لوحة الأدمن بدون هذه الصلاحية.

حالة الإعلان الجديدة هي `PAYMENT_PENDING` ولا تظهر في سوق المستخدم حتى يغيّر الأدمن حالتها إلى `PUBLISHED`.

> نسخة Kotlin القديمة ما زالت موجودة مؤقتًا كنسخة احتياطية أثناء التحقق من البديل Expo. بعد اختبار EAS يمكن حذفها في commit مستقل.
