# avendOc Admin

تطبيق Android مستقل لإدارة إعلانات **avendOc**. يقرأ من مجموعة Firestore المسماة `listings`، ويعرض الإعلانات ذات الحالة `PAYMENT_PENDING`، ثم يتيح للأدمين تحويلها إلى `PUBLISHED` أو `REJECTED`.

## الربط مع التطبيق الأساسي

ضع ملف `google-services.json` الخاص **بنفس مشروع Firebase المستخدم في avendOc** داخل مجلد `app/` قبل البناء. لا يوجد هذا الملف في المستودع الحالي حفاظًا على إعدادات المشروع السرية.

يجب أن تسمح قواعد Firestore للأدمين بقراءة وتحديث الحقول التالية:

```text
listings/{listingId}.status
```

يفضل في بيئة الإنتاج استخدام Firebase Authentication وCustom Claims باسم `admin`، وعدم الاعتماد على أي دخول تجريبي.

## التشغيل

```bash
./gradlew assembleDebug
```

التطبيق الأساسي موجود في مجلد `avendOc`، بينما هذا التطبيق المستقل موجود في `avendOc-admin`.
