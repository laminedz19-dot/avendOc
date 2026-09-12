package com.example.data

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseFirestoreService {

    private var firestore: FirebaseFirestore? = null
    private var listingsListener: ListenerRegistration? = null
    private var isInitialized = false

    fun initialize(onListingsUpdated: (List<ListingItem>) -> Unit) {
        try {
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
                isInitialized = true
                listenToListings(onListingsUpdated)
                Log.d("FirebaseFirestoreService", "Firebase Firestore initialized successfully")
            }
        } catch (e: Exception) {
            Log.w("FirebaseFirestoreService", "Firestore not available or not configured yet: ${e.message}")
        }
    }

    private fun listenToListings(onListingsUpdated: (List<ListingItem>) -> Unit) {
        val db = firestore ?: return
        try {
            listingsListener = db.collection("listings")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("FirebaseFirestoreService", "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val remoteListings = snapshot.documents.mapNotNull { doc ->
                            try {
                                val categoryStr = doc.getString("category") ?: "OTHER"
                                val category = try {
                                    CategoryType.valueOf(categoryStr)
                                } catch (_: Exception) {
                                    CategoryType.OTHER
                                }

                                val conditionStr = doc.getString("condition") ?: "GOOD"
                                val condition = try {
                                    ItemCondition.valueOf(conditionStr)
                                } catch (_: Exception) {
                                    ItemCondition.GOOD
                                }

                                val statusStr = doc.getString("status") ?: "PAYMENT_PENDING"
                                val status = try {
                                    AdStatus.valueOf(statusStr)
                                } catch (_: Exception) {
                                    AdStatus.PUBLISHED
                                }

                                ListingItem(
                                    id = doc.id,
                                    title = doc.getString("title") ?: "",
                                    description = doc.getString("description") ?: "",
                                    priceDzd = doc.getLong("priceDzd") ?: 0L,
                                    isNegotiable = doc.getBoolean("isNegotiable") ?: true,
                                    category = category,
                                    wilayaCode = doc.getString("wilayaCode") ?: "16",
                                    wilayaNameAr = doc.getString("wilayaNameAr") ?: "الجزائر",
                                    wilayaNameFr = doc.getString("wilayaNameFr") ?: "Alger",
                                    commune = doc.getString("commune") ?: "",
                                    condition = condition,
                                    sellerId = doc.getString("sellerId") ?: "seller-amine",
                                    sellerName = doc.getString("sellerName") ?: "بائع جزائري",
                                    sellerPhone = doc.getString("sellerPhone") ?: "0661234567",
                                    isSellerVerified = doc.getBoolean("isSellerVerified") ?: true,
                                    status = status,
                                    paymentReference = doc.getString("paymentReference") ?: "",
                                    paymentDate = doc.getString("paymentDate") ?: "",
                                    paymentProofReceiptUrl = doc.getString("paymentProofReceiptUrl") ?: "",
                                    createdAt = doc.getString("createdAt") ?: "الآن",
                                    viewsCount = (doc.getLong("viewsCount") ?: 0L).toInt(),
                                    offersCount = (doc.getLong("offersCount") ?: 0L).toInt(),
                                    featuredTag = doc.getString("featuredTag")
                                )
                            } catch (e: Exception) {
                                Log.e("FirebaseFirestoreService", "Error parsing listing document", e)
                                null
                            }
                        }

                        if (remoteListings.isNotEmpty()) {
                            onListingsUpdated(remoteListings)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w("FirebaseFirestoreService", "Error setting up listener: ${e.message}")
        }
    }

    fun syncListing(item: ListingItem) {
        val db = firestore ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = hashMapOf(
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
                    "createdAt" to item.createdAt,
                    "viewsCount" to item.viewsCount,
                    "offersCount" to item.offersCount
                )
                db.collection("listings").document(item.id).set(data)
                Log.d("FirebaseFirestoreService", "Synced listing ${item.id} to Firestore")
            } catch (e: Exception) {
                Log.w("FirebaseFirestoreService", "Failed to sync listing to Firestore: ${e.message}")
            }
        }
    }

    fun syncUser(account: UserAccount) {
        val db = firestore ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users").document(account.id).set(
                    mapOf(
                        "name" to account.name,
                        "phone" to account.phone,
                        "wilayaCode" to account.wilayaCode,
                        "isBlocked" to account.isBlocked,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                Log.w("FirebaseFirestoreService", "Failed to sync user: ${e.message}")
            }
        }
    }

    fun listenToUserBlock(userId: String, onBlockedChanged: (Boolean) -> Unit): ListenerRegistration? {
        val db = firestore ?: return null
        return db.collection("users").document(userId).addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                onBlockedChanged(snapshot.getBoolean("isBlocked") ?: false)
            }
        }
    }

    fun syncOffer(offer: NegotiationOffer) {
        val db = firestore ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = hashMapOf(
                    "listingId" to offer.listingId,
                    "listingTitle" to offer.listingTitle,
                    "buyerId" to offer.buyerId,
                    "buyerName" to offer.buyerName,
                    "buyerPhone" to offer.buyerPhone,
                    "originalPriceDzd" to offer.originalPriceDzd,
                    "proposedPriceDzd" to offer.proposedPriceDzd,
                    "counterPriceDzd" to (offer.counterPriceDzd ?: 0L),
                    "status" to offer.status.name,
                    "message" to offer.message,
                    "timestamp" to offer.timestamp
                )
                db.collection("offers").document(offer.id).set(data)
            } catch (e: Exception) {
                Log.w("FirebaseFirestoreService", "Failed to sync offer: ${e.message}")
            }
        }
    }

    fun syncChatMessage(message: ChatMessage) {
        val db = firestore ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = hashMapOf(
                    "listingId" to message.listingId,
                    "senderId" to message.senderId,
                    "senderName" to message.senderName,
                    "text" to message.text,
                    "timestamp" to message.timestamp,
                    "isFromMe" to message.isFromMe
                )
                db.collection("chats").document(message.id).set(data)
            } catch (e: Exception) {
                Log.w("FirebaseFirestoreService", "Failed to sync chat message: ${e.message}")
            }
        }
    }

    fun isCloudConnected(): Boolean = isInitialized && firestore != null
}
