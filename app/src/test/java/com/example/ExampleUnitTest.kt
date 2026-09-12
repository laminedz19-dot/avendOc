package com.example

import com.example.data.AdStatus
import com.example.data.ListingItem
import com.example.data.PlatformSettings
import com.example.data.CategoryType
import com.example.data.ItemCondition
import com.example.data.DeliveryOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun platformSettingsUseOfficialFee() {
    assertEquals(300, PlatformSettings().feeAmountDzd)
  }

  @Test
  fun productionListingDefaultsAreSafe() {
    val listing = ListingItem(
      title = "اختبار",
      description = "وصف اختبار صالح",
      priceDzd = 10_000,
      category = CategoryType.OTHER,
      wilayaCode = "16",
      wilayaNameAr = "الجزائر",
      wilayaNameFr = "Alger",
      commune = "الجزائر",
      condition = ItemCondition.GOOD,
      sellerId = "firebase-uid",
      sellerName = "حساب حقيقي",
      sellerPhone = "0550000000",
      deliveryOption = DeliveryOption.HAND_TO_HAND
    )

    assertEquals(0, listing.viewsCount)
    assertFalse(listing.isSellerVerified)
    assertEquals(AdStatus.PAYMENT_REQUIRED, listing.status)
    assertTrue(listing.createdAt.isBlank())
  }
}
