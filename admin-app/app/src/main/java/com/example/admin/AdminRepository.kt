package com.example.admin

import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Uses the same Firestore `listings` collection as avendOc.
class AdminRepository {
    private val _listings = MutableStateFlow<List<AdminListing>>(emptyList())
    val listings: StateFlow<List<AdminListing>> = _listings.asStateFlow()
    private var listener: ListenerRegistration? = null
    private var db: FirebaseFirestore? = null

    init {
        try {
            if (FirebaseApp.getApps(com.example.admin.AdminApplication.context).isNotEmpty()) {
                db = FirebaseFirestore.getInstance()
                listener = db!!.collection("listings").addSnapshotListener { snapshot, _ ->
                    _listings.value = snapshot?.documents?.mapNotNull { doc ->
                        AdminListing(
                            id = doc.id,
                            title = doc.getString("title") ?: "بدون عنوان",
                            description = doc.getString("description") ?: "",
                            sellerName = doc.getString("sellerName") ?: "",
                            sellerPhone = doc.getString("sellerPhone") ?: "",
                            wilaya = doc.getString("wilayaNameAr") ?: doc.getString("wilayaCode") ?: "",
                            commune = doc.getString("commune") ?: "",
                            priceDzd = doc.getLong("priceDzd") ?: 0L,
                            status = doc.getString("status") ?: "PAYMENT_PENDING",
                            category = doc.getString("category") ?: "OTHER",
                            createdAt = doc.getString("createdAt") ?: "غير محدد"
                        )
                    } ?: emptyList()
                }
            }
        } catch (_: Exception) { }
    }

    fun approve(id: String) = updateStatus(id, "PUBLISHED")
    fun reject(id: String) = updateStatus(id, "REJECTED")
    private fun updateStatus(id: String, status: String) {
        db?.collection("listings")?.document(id)?.update("status", status)
    }
}

data class AdminListing(
    val id: String,
    val title: String,
    val description: String,
    val sellerName: String,
    val sellerPhone: String,
    val wilaya: String,
    val commune: String,
    val priceDzd: Long,
    val status: String,
    val category: String,
    val createdAt: String
)
