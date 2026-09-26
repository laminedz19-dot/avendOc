# avendOc Expo

تم نقل التدفقات الأساسية من تطبيق Android الأصلي إلى Expo SDK 54 / React Native:

- تصفح الإعلانات المنشورة والبحث والتصفية حسب التصنيف.
- تسجيل الدخول والتسجيل عبر Firebase Authentication.
- إنشاء إعلان جديد بحالة `PAYMENT_PENDING` حتى يراجعه الأدمين.
- عرض تفاصيل الإعلان وإرسال عروض تفاوض.
- عرض إعلانات المستخدم وعروضه.
- لوحة الأدمين عند وجود Custom Claim باسم `admin: true`.
- نشر/رفض الإعلانات وحظر المستخدمين عبر قواعد Firestore الحالية.
- شاشة خطأ واضحة عند تعذر الوصول إلى Firestore بدل مؤشر تحميل لا نهائي.

## التشغيل

```bash
pnpm install
pnpm check
pnpm dev:metro
```

## Firebase

الإعداد الموجود في `lib/firebase.ts` يستخدم مشروع `achridz-2c628`. ملف العميل لا يمنح صلاحيات إدارية. يجب تفعيل Email/Password في Firebase Authentication، وتعيين Custom Claim للأدمين من Firebase Admin SDK:

```json
{ "admin": true }
```

قواعد Firestore الحالية هي مصدر الصلاحيات النهائي، لذلك لا تعتمد الواجهة وحدها على حالة الأدمين.

## ربط المشروع بحساب expo.dev

بعد تسجيل الدخول إلى حساب Expo من داخل مجلد `expo-app`:

```bash
npx eas login
npx eas init
npx eas build:configure
npx eas build --platform android --profile preview
```

أمر `eas init` هو الذي ينشئ `projectId` الخاص بحسابك ويضيفه إلى إعدادات Expo. لم يتم وضع `owner` أو `projectId` ثابتين هنا لأنهما خاصان بحساب Expo الذي ستستخدمه.

يمكن استخدام الأمر التالي بعد إنشاء مشروع EAS:

```bash
npx eas update --branch production --message "Initial avendOc Expo migration"
```

## ملاحظات الترحيل

نسخة Expo تغطي وظائف السوق والمصادقة والعروض ولوحة الإدارة. رفع صور إيصالات الدفع والإشعارات الخلفية يحتاجان إضافة `expo-image-picker`/Storage و`expo-notifications` مع إعداد EAS Development Build؛ تركت حقول الصور متوافقة مع مخطط Firestore بدون إضافة مفتاح خاص داخل التطبيق.
