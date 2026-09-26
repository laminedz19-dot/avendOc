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

## البناء التلقائي عبر GitHub Actions

يوجد Workflow مستقل لكل تطبيق:

- `.github/workflows/expo-user-eas.yml`
- `.github/workflows/expo-admin-eas.yml`

أضف Secret واحدًا إلى إعدادات المستودع في GitHub باسم `EXPO_TOKEN`. يجب أن يكون هذا Token من حساب Expo الذي يملك مشروعي `avendoc-user` و`avendoc-admin`.

قبل أول تشغيل للـ Workflow، نفّذ مرة واحدة داخل كل مشروع:

```bash
npx eas login
npx eas init
```

ثم ادفع التغييرات التي يضيفها `eas init` إلى `app.json`، وخصوصًا `expo.extra.eas.projectId`. الـ Workflow يوقف التشغيل برسالة واضحة إذا لم يكن المشروع مربوطًا بـ EAS.

بعد ذلك، كل push إلى `main` أو `master` يغيّر ملفات المشروع المناسب يشغّل بناء Android تلقائيًا بملف `preview` وينتظر رابط البناء في لوحة EAS. يمكن أيضًا تشغيل Workflow يدويًا من تبويب **Actions** واختيار `preview` أو `production`.

## اختبار البناء محليًا قبل الرفع إلى GitHub

استخدم السكريبت التالي لفحص TypeScript، وتجميع Metro، ثم تنفيذ بناء Android محلي عبر EAS CLI لكل مشروع:

```bash
chmod +x scripts/test-eas-build-local.sh
./scripts/test-eas-build-local.sh
```

لاختبار تطبيق واحد أو Profile مختلف:

```bash
./scripts/test-eas-build-local.sh --project user --profile preview
./scripts/test-eas-build-local.sh --project admin --profile production
```

ينتج السكريبت ملفات APK داخل `artifacts/eas-local/`. يجب تشغيل `npx eas init` مرة واحدة داخل كل مشروع أولًا، كما يجب توفر متطلبات EAS local build وتسجيل الدخول إلى Expo أو ضبط `EXPO_TOKEN`.

## Firebase

المشروعان يستخدمان مشروع Firebase نفسه `achridz-2c628`. يجب أن تكون مصادقة Email/Password مفعّلة. يجب منح حساب الأدمن Custom Claim باسم `admin: true`؛ التطبيق لا يسمح بالدخول إلى لوحة الأدمن بدون هذه الصلاحية.

حالة الإعلان الجديدة هي `PAYMENT_PENDING` ولا تظهر في سوق المستخدم حتى يغيّر الأدمن حالتها إلى `PUBLISHED`.

> نسخة Kotlin القديمة ما زالت موجودة مؤقتًا كنسخة احتياطية أثناء التحقق من البديل Expo. بعد اختبار EAS يمكن حذفها في commit مستقل.
