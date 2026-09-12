package com.example.data

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FirebaseFirestoreService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var firestore: FirebaseFirestore? = null
    private var listingsListener: ListenerRegistration? = null
    private var isInitialized = false

    fun initialize(onListingsUpdated: (List<ListingItem>) -> Unit) {
        try {
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
                isInitialized = true
                listenToListings(onListingsUpdated)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firestore initialization failed: ${e.message}")
        }
    }

    private fun listenToListings(onListingsUpdated: (List<ListingItem>) -> Unit) {
        val db = firestore ?: return
        listingsListener = db.collection("listings")
            .whereEqualTo("status", AdStatus.PUBLISHED.name)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listings listener failed", error)
                    return@addSnapshotListener
                }
                val listings = snapshot?.documents?.mapNotNull(::parseListing).orEmpty()
                onListingsUpdated(listings)
            }
    }

    private fun parseListing(doc: com.google.firebase.firestore.DocumentSnapshot): ListingItem? {
        return try {
            val category = doc.getString("category")?.let { value ->
                runCatching { CategoryType.valueOf(value) }.getOrDefault(CategoryType.OTHER)
            } ?: CategoryType.OTHER
            val condition = doc.getString("condition")?.let { value ->
                runCatching { ItemCondition.valueOf(value) }.getOrDefault(ItemCondition.GOOD)
            } ?: ItemCondition.GOOD
            val status = doc.getString("status")?.let { value ->
                runCatching { AdStatus.valueOf(value) }.getOrDefault(AdStatus.PUBLISHED)
            } ?: return null
            val sellerId = doc.getString("sellerId") ?: return null
            ListingItem(
                id = doc.id,
                title = doc.getString("title") ?: return null,
                description = doc.getString("description") ?: "",
                priceDzd = doc.getLong("priceDzd") ?: return null,
                isNegotiable = doc.getBoolean("isNegotiable") ?: true,
                category = category,
                wilayaCode = doc.getString("wilayaCode") ?: "",
                wilayaNameAr = doc.getString("wilayaNameAr") ?: "",
                wilayaNameFr = doc.getString("wilayaNameFr") ?: "",
                commune = doc.getString("commune") ?: "",
                condition = condition,
                sellerId = sellerId,
                sellerName = doc.getString("sellerName") ?: "",
                sellerPhone = doc.getString("sellerPhone") ?: "",
                isSellerVerified = doc.getBoolean("isSellerVerified") ?: false,
                status = status,
                paymentReference = doc.getString("paymentReference") ?: "",
                paymentDate = doc.getString("paymentDate") ?: "",
                paymentProofReceiptUrl = doc.getString("paymentProofReceiptUrl") ?: "",
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time?.toString()
                    ?: doc.getString("createdAt") ?: "",
                viewsCount = (doc.getLong("viewsCount") ?: 0L).toInt(),
                offersCount = (doc.getLong("offersCount") ?: 0L).toInt(),
                images = (doc.get("images") as? List<*>)?.filterIsInstance<String>().orEmpty(),
                featuredTag = doc.getString("featuredTag")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing listing ${doc.id}", e)
            null
        }
    }

    fun syncListing(item: ListingItem) {
        val db = firestore ?: return
        scope.launch {
            try {
                val data = hashMapOf<String, Any?>(
                    "title" to item.title,
                    "description" to item.description,
                    "priceDzd" to item.priceDzd,
                    "isNegotiable" to item.isNegotiable,
                    "category" to item.category.name,
                    "wilayaCode" to item.wilayaCode,
                    "wilayaNameAr" to item.wilayaNameAr,
                    "wilayaNameFr" to item.wilayaNameFr,
                    "commune" to item.commune,
                    "condition" to item.condition.name,
                    "sellerId" to item.sellerId,
                    "sellerName" to item.sellerName,
                    "sellerPhone" to item.sellerPhone,
                    "isSellerVerified" to item.isSellerVerified,
                    "status" to item.status.name,
                    "paymentReference" to item.paymentReference,
                    "paymentDate" to item.paymentDate,
                    "paymentProofReceiptUrl" to item.paymentProofReceiptUrl,
                    "images" to item.images,
                    "viewsCount" to item.viewsCount,
                    "offersCount" to item.offersCount,
                    "createdAt" to if (item.createdAt.isBlank()) FieldValue.serverTimestamp() else item.createdAt
                )
                db.collection("listings").document(item.id).set(data)
            } catch (e: Exception) {
                Log.w(TAG, "Failed to sync listing", e)
            }
        }
    }

    fun syncUser(account: UserAccount) {
        val db = firestore ?: return
        if (account.id.isBlank()) return
        scope.launch {
            runCatching {
                db.collection("users").document(account.id).set(
                    mapOf(
                        "name" to account.name,
                        "phone" to account.phone,
                        "wilayaCode" to account.wilayaCode,
                        "isBlocked" to account.isBlocked,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }.onFailure { Log.w(TAG, "Failed to sync user", it) }
        }
    }

    fun listenToUserBlock(userId: String, onBlockedChanged: (Boolean) -> Unit): ListenerRegistration? {
        if (userId.isBlank()) return null
        return firestore?.collection("users")?.document(userId)?.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) onBlockedChanged(snapshot.getBoolean("isBlocked") ?: false)
        }
    }

    fun syncOffer(offer: NegotiationOffer) {
        val db = firestore ?: return
        if (offer.buyerId.isBlank()) return
        scope.launch {
            runCatching {
                db.collection("offers").document(offer.id).set(
                    mapOf(
                        "listingId" to offer.listingId,
                        "listingTitle" to offer.listingTitle,
                        "sellerId" to offer.sellerId,
                        "buyerId" to offer.buyerId,
                        "buyerName" to offer.buyerName,
                        "buyerPhone" to offer.buyerPhone,
                        "originalPriceDzd" to offer.originalPriceDzd,
                        "proposedPriceDzd" to offer.proposedPriceDzd,
                        "counterPriceDzd" to offer.counterPriceDzd,
                        "status" to offer.status.name,
                        "message" to offer.message,
                        "timestamp" to if (offer.timestamp.isBlank()) FieldValue.serverTimestamp() else offer.timestamp
                    )
                )
            }.onFailure { Log.w(TAG, "Failed to sync offer", it) }
        }
    }

    fun syncChatMessage(message: ChatMessage) {
        val db = firestore ?: return
        if (message.senderId.isBlank()) return
        scope.launch {
            runCatching {
                db.collection("chats").document(message.id).set(
                    mapOf(
                        "listingId" to message.listingId,
                        "senderId" to message.senderId,
                        "senderName" to message.senderName,
                        "text" to message.text,
                        "timestamp" to if (message.timestamp.isBlank()) FieldValue.serverTimestamp() else message.timestamp
                    )
                )
            }.onFailure { Log.w(TAG, "Failed to sync chat message", it) }
        }
    }

    fun isCloudConnected(): Boolean = isInitialized && firestore != null

    fun close() {
        listingsListener?.remove()
        listingsListener = null
        scope.cancel()
    }

    private companion object { const val TAG = "FirebaseFirestoreService" }
}
