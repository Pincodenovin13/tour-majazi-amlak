package com.example

import com.example.data.AppRepository
import com.example.data.IranProvincesData
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testSubscriptionPlansPricing() {
    val repo = AppRepository.instance
    val plans = repo.availablePlans
    assertEquals(3, plans.size)

    val plan1 = plans.find { it.durationMonths == 1 }
    assertNotNull(plan1)
    assertEquals(1_500_000L, plan1?.price)
    assertNull(plan1?.badge)

    val plan2 = plans.find { it.durationMonths == 2 }
    assertNotNull(plan2)
    assertEquals(2_500_000L, plan2?.price)
    assertEquals("محبوب", plan2?.badge)

    val plan3 = plans.find { it.durationMonths == 3 }
    assertNotNull(plan3)
    assertEquals(4_000_000L, plan3?.price)
    assertEquals("به‌صرفه‌ترین", plan3?.badge)
  }

  @Test
  fun testTieredCityAdsPricing() {
    val repo = AppRepository.instance
    // 15 days in Sari = 15 * 130,000 = 1,950,000 Toman (exact user prompt example)
    val priceSari15 = repo.calculateAdPrice("ساری", 15)
    assertEquals(1_950_000L, priceSari15)

    // 15 days in Tehran = 15 * 130,000 * 1.5 = 2,925,000 Toman
    val priceTehran15 = repo.calculateAdPrice("تهران", 15)
    assertEquals(2_925_000L, priceTehran15)

    // 5 days in Sari = 5 * 150,000 = 750,000 Toman
    val priceSari5 = repo.calculateAdPrice("ساری", 5)
    assertEquals(750_000L, priceSari5)

    // 25 days in Sari = 25 * 110,000 = 2,750,000 Toman
    val priceSari25 = repo.calculateAdPrice("ساری", 25)
    assertEquals(2_750_000L, priceSari25)
  }

  @Test
  fun testWithdrawalRules() {
    val repo = AppRepository.instance
    // Under 500,000 should fail
    assertFalse(repo.submitWithdrawalRequest(400_000L, "6037", "IR1234567890123456", "test"))
    // Invalid Sheba should fail
    assertFalse(repo.submitWithdrawalRequest(500_000L, "6037", "1234567890123456", "test"))
  }

  @Test
  fun testAgentActiveDeactiveAndExpiryWarning() {
    val repo = AppRepository()
    // By default remainingDays is 50 -> active
    assertTrue(repo.isAgentActive())
    assertFalse(repo.isSubscriptionNearExpiry())

    // Set to 2 days -> active and near expiry warning
    repo.setSubscriptionRemainingDays(2)
    assertTrue(repo.isAgentActive())
    assertTrue(repo.isSubscriptionNearExpiry())

    // Set to 0 days -> deactive
    repo.setSubscriptionRemainingDays(0)
    assertFalse(repo.isAgentActive())
    assertFalse(repo.isSubscriptionNearExpiry())
  }

  @Test
  fun testColleagueCommissionBurningWhenDeactive() {
    val repo = AppRepository()
    val plan2 = repo.availablePlans[1] // 2-month plan (2.5M, commission 15% = 375,000)

    // When active: receives commission
    repo.setSubscriptionRemainingDays(30)
    val earningsBefore = repo.commissionStat.value.totalEarnings
    repo.processColleagueAgentPlanPurchase("همکار ۱", plan2)
    val earningsAfter = repo.commissionStat.value.totalEarnings
    assertEquals(earningsBefore + 375_000L, earningsAfter)

    // When deactive: commission burns!
    repo.setSubscriptionRemainingDays(0)
    val burnedBefore = repo.commissionStat.value.burnedBalance
    repo.processColleagueAgentPlanPurchase("همکار ۲", plan2)
    val burnedAfter = repo.commissionStat.value.burnedBalance
    assertEquals(burnedBefore + 375_000L, burnedAfter)
  }

  @Test
  fun testMarkPropertySoldDeletesProperty() {
    val repo = AppRepository()
    val initialSize = repo.properties.value.size
    val firstProp = repo.properties.value.first()
    repo.markPropertySold(firstProp.id)
    assertEquals(initialSize - 1, repo.properties.value.size)
    assertNull(repo.getPropertyById(firstProp.id))
  }
}
