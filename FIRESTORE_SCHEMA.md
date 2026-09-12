# مخطط Firestore المشترك

يستخدم تطبيق **avendOc** وتطبيق **avendOc Admin** نفس مشروع Firebase ونفس قاعدة Firestore.

| المجموعة | الغرض | حقول أساسية |
|---|---|---|
| `listings` | إعلانات المستخدمين | `title`, `sellerId`, `category`, `createdAt`, `status` |
| `users` | حسابات المستخدمين وحالة الحظر | `name`, `phone`, `wilayaCode`, `isBlocked`, `updatedAt` |
| `offers` | عروض المساومة | `listingId`, `buyerId`, `proposedPriceDzd`, `status` |
| `chats` | رسائل المحادثات | `listingId`, `senderId`, `text`, `timestamp` |

## حالات الإعلان

يُنشئ تطبيق المستخدم الإعلان بحالة `PAYMENT_PENDING`. يراجعه تطبيق الأدمين، ثم يكتب `PUBLISHED` عند الموافقة أو `REJECTED` عند الرفض. لا يعرض تطبيق المستخدم في السوق العام إلا الإعلانات ذات الحالة `PUBLISHED`.

## حظر المستخدم

يكتب تطبيق الأدمين `users/{userId}.isBlocked = true`. يستمع تطبيق المستخدم إلى وثيقة المستخدم نفسها، ويحوّل الحساب إلى تسجيل خروج ويعرض رسالة الحظر فورًا عند تغيّر القيمة.

## الأمان قبل الإنتاج

يجب تفعيل Firebase Authentication وقواعد Firestore للتحقق من هوية الأدمين عبر Custom Claims. لا ينبغي السماح للعميل العادي بتعديل `isBlocked` أو حالة الإعلان، ويجب أن تكون صلاحية الكتابة على هذه الحقول محصورة بالأدمين.
