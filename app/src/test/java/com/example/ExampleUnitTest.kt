package com.example

import com.example.data.PlatformSettings
import com.example.data.UserAccount
import com.example.data.UserRole
import com.example.ui.MarketplaceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {
  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPlatformSettingsCcpDetails() {
    val settings = PlatformSettings()
    assertEquals(300, settings.feeAmountDzd)
    assertEquals("0008761821", settings.ccpAccount)
    assertEquals("94", settings.ccpKey)
    assertEquals("007999990008761821", settings.baridiMobRip)
  }

  @Test
  fun testUserAccountRegistrationAndLogin() {
    val vm = MarketplaceViewModel()
    
    // Test initial showAuthDialog is true as requested ("عند فتح التطبيق تظهر لائحة بها التسجيل وتحتها الدخول")
    assertTrue(vm.showAuthDialog.value)
    
    // Register
    vm.register("سمير بلقاسم", "0661234567", "16", UserRole.SELLER)
    assertEquals("سمير بلقاسم", vm.userAccount.value.name)
    assertEquals("0661234567", vm.userAccount.value.phone)
    assertTrue(vm.userAccount.value.isLoggedIn)
    assertFalse(vm.showAuthDialog.value)

    // Logout
    vm.logout()
    assertFalse(vm.userAccount.value.isLoggedIn)

    // Login
    vm.login("0559876543", "secret", UserRole.BUYER)
    assertTrue(vm.userAccount.value.isLoggedIn)
    assertEquals(UserRole.BUYER, vm.currentUserRole.value)
  }

  @Test
  fun testReceiptVerification() = runTest(testDispatcher) {
    val vm = MarketplaceViewModel()
    assertFalse(vm.createAdForm.value.isReceiptVerified)

    // Trigger verification
    vm.verifyReceipt("content://media/receipt_1.jpg")
    advanceTimeBy(1500)
    advanceUntilIdle()

    // Verification must succeed with correct details
    assertTrue(vm.createAdForm.value.isReceiptVerified)
    val result = vm.createAdForm.value.receiptVerification
    assertTrue(result.isValid)
    assertFalse(result.isScanning)
    assertEquals("007999990008761821", result.extractedAccount)
    assertEquals(300, result.extractedAmountDzd)
    assertNotNull(result.extractedTransactionRef)
  }

  @Test
  fun testVisitorCountRangeAndUpdates() {
    val vm = MarketplaceViewModel()
    val initialCount = vm.visitorCount.value

    // Must be between 10,000 and 30,000
    assertTrue("Initial count $initialCount must be >= 10000", initialCount >= 10000)
    assertTrue("Initial count $initialCount must be <= 30000", initialCount <= 30000)

    // Run multiple iterations of updateVisitorCount to verify strict bounds
    repeat(100) {
      vm.updateVisitorCount()
      val current = vm.visitorCount.value
      assertTrue("Fluctuating count $current must be >= 10000", current >= 10000)
      assertTrue("Fluctuating count $current must be <= 30000", current <= 30000)
    }
  }
}

