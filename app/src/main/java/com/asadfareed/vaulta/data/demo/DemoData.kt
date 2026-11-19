package com.asadfareed.vaulta.data.demo

import com.asadfareed.vaulta.domain.model.CardNetwork
import com.asadfareed.vaulta.domain.model.Category
import com.asadfareed.vaulta.domain.model.Contact
import com.asadfareed.vaulta.domain.model.NewTransaction
import com.asadfareed.vaulta.domain.model.PaymentCard
import java.time.Clock
import java.time.Duration

/**
 * Deterministic demo content so the app looks alive on first launch.
 * In production this would be replaced by a remote data source.
 */
object DemoData {

    val contacts = listOf(
        Contact("c1", "Sara Khan", "@sarak"),
        Contact("c2", "Daniel Moore", "@dmoore"),
        Contact("c3", "Aisha Raza", "@aisha.r"),
        Contact("c4", "Leo Martins", "@leom"),
        Contact("c5", "Hina Malik", "@hinam"),
        Contact("c6", "Omar Siddiqui", "@omars"),
    )

    val cards = listOf(
        PaymentCard(
            id = "card_main",
            holder = "ASAD FAREED",
            last4 = "4821",
            expiry = "08/29",
            network = CardNetwork.VISA,
            isVirtual = false,
            monthlyLimit = 300_000,
            spentThisMonth = 128_450,
        ),
        PaymentCard(
            id = "card_virtual",
            holder = "ASAD FAREED",
            last4 = "9034",
            expiry = "11/27",
            network = CardNetwork.MASTERCARD,
            isVirtual = true,
            monthlyLimit = 50_000,
            spentThisMonth = 21_990,
        ),
    )

    private data class Seed(
        val title: String,
        val subtitle: String,
        val amount: Long,
        val category: Category,
        val hoursAgo: Long,
    )

    private val seeds = listOf(
        Seed("Apple Store", "Shopping · Card ••4821", -12_900, Category.SHOPPING, 3),
        Seed("Uber", "Ride to office", -1_450, Category.TRANSPORT, 7),
        Seed("Sara Khan", "Received · Dinner split", 4_200, Category.TRANSFER, 20),
        Seed("Starbucks", "Coffee", -650, Category.FOOD, 26),
        Seed("Netflix", "Monthly subscription", -1_599, Category.ENTERTAINMENT, 40),
        Seed("K-Electric", "Electricity bill", -8_730, Category.BILLS, 52),
        Seed("Careem Food", "Lunch order", -2_340, Category.FOOD, 70),
        Seed("Spotify", "Premium family", -1_699, Category.ENTERTAINMENT, 96),
        Seed("Daraz", "Headphones", -6_499, Category.SHOPPING, 110),
        Seed("Shell", "Fuel", -5_200, Category.TRANSPORT, 130),
        Seed("Acme Corp", "Monthly salary", 540_000, Category.SALARY, 150),
        Seed("Chughtai Lab", "Annual check-up", -4_500, Category.HEALTH, 190),
        Seed("Gym Pro", "Membership", -3_000, Category.HEALTH, 230),
        Seed("Zara", "Clothing", -9_850, Category.SHOPPING, 280),
        Seed("PTCL", "Internet bill", -4_299, Category.BILLS, 320),
        Seed("Bundu Khan", "Dinner with team", -7_600, Category.FOOD, 380),
        Seed("Daniel Moore", "Sent · Concert tickets", -12_000, Category.TRANSFER, 430),
        Seed("Cinepax", "Movie night", -2_400, Category.ENTERTAINMENT, 500),
        Seed("Freelance client", "Mobile app milestone", 180_000, Category.SALARY, 560),
        Seed("Foodpanda", "Groceries", -5_640, Category.FOOD, 620),
        Seed("Uber", "Airport drop", -3_150, Category.TRANSPORT, 700),
        Seed("Amazon", "Books", -4_380, Category.SHOPPING, 800),
        Seed("SNGPL", "Gas bill", -2_910, Category.BILLS, 900),
        Seed("Acme Corp", "Monthly salary", 540_000, Category.SALARY, 870),
        Seed("Opening deposit", "Account funded", 250_000, Category.TRANSFER, 1_400),
    )

    fun transactions(clock: Clock): List<NewTransaction> {
        val now = clock.instant()
        return seeds.map {
            NewTransaction(
                title = it.title,
                subtitle = it.subtitle,
                amount = it.amount,
                category = it.category,
                timestamp = now.minus(Duration.ofHours(it.hoursAgo)),
            )
        }
    }
}
