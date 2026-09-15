# نظام الحسابات — تطبيق Android

تم إنشاء هيكل مشروع Android قابل للفتح في Android Studio.

## ما تم تجهيزه
- واجهة عربية RTL.
- 12 شهرًا.
- إدخال نهار/ليل.
- شبكة + كاش + إجمالي تلقائي.
- تقرير شهري وسنوي.
- حفظ محلي للبيانات.
- إنشاء نسخة احتياطية JSON للمشاركة/الحفظ.

## ملف المصدر
DOC-20260913-WA0006.xlsx

## بنية ملف Excel المكتشفة
[
  {
    "name": "شهر1",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر2",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر3",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر4",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر5",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر6",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر7",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر 8",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر 9",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر10",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر11",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "شهر12",
    "max_row": 128,
    "max_col": 13,
    "merged": 130
  },
  {
    "name": "اقفال سنوي",
    "max_row": 19,
    "max_col": 3,
    "merged": 0
  }
]

## تشغيل المشروع
1. افتح المجلد `accounts_app_android` في Android Studio.
2. انتظر مزامنة Gradle.
3. Build > Build APK(s).
4. ستجد APK في `app/build/outputs/apk/debug/`.

## ملاحظة مهمة عن النسخ التلقائي
ربط Google Drive تلقائيًا يحتاج إعداد OAuth/Google Cloud ومفتاح/معرّف تطبيق Android. لذلك المشروع الحالي يجهز طبقة البيانات والنسخة الاحتياطية محليًا، أما المزامنة السحابية التلقائية فتحتاج بيانات حساب Google/مشروع Google Cloud قبل إصدار النسخة النهائية.
