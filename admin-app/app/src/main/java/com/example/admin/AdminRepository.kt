package com.example.admin

import android.util.Log
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
    private val _users = MutableStateFlow<List<AdminUser>>(emptyList())
    val users: StateFlow<List<AdminUser>> = _users.asStateFlow()
    private val _connectionError = MutableStateFlow<String?>(null)
    val connectionError: StateFlow<String?> = _connectionError.asStateFlow()
    private var listener: ListenerRegistration? = null
    private var db: FirebaseFirestore? = null
    private var isInitialSnapshot = true

    init {
        try {
            if (FirebaseApp.getApps(AdminApplication.context).isEmpty()) {
                _connectionError.value = "Firebase غير مهيأ. أضف google-services.json الخاص بالمشروع."
            } else {
                db = FirebaseFirestore.getInstance()
                listener = db!!.collection("listings").addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        reportFirestoreError("الإعلانات", error)
                        return@addSnapshotListener
                    }
                    _connectionError.value = null
                    if (snapshot != null && !isInitialSnapshot) {
                        snapshot.documentChanges
                            .filter { it.type == com.google.firebase.firestore.DocumentChange.Type.ADDED }
                            .mapNotNull { it.document.takeIf { doc -> doc.getString("status") == "PAYMENT_PENDING" }?.getString("title") }
                            .forEach { title -> AdminNotificationHelper.notifyNewListing(AdminApplication.context, title) }
                    }
                    isInitialSnapshot = false
                    _listings.value = snapshot?.documents?.map { doc ->
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
                db!!.collection("users").addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        reportFirestoreError("المستخدمين", error)
                        return@addSnapshotListener
                    }
                    _connectionError.value = null
                    _users.value = snapshot?.documents?.map { doc ->
                        AdminUser(
                            id = doc.id,
                            name = doc.getString("name") ?: "مستخدم بدون اسم",
                            phone = doc.getString("phone") ?: "",
                            isBlocked = doc.getBoolean("isBlocked") ?: false
                        )
                    } ?: emptyList()
                }
            }
        } catch (error: Exception) {
            reportFirestoreError("Firebase", error)
        }
    }

    private fun reportFirestoreError(source: String, error: Exception) {
        Log.e(TAG, "Firestore listener failed for $source", error)
        _connectionError.value = "تعذر الاتصال بقاعدة البيانات ($source): ${error.localizedMessage ?: "تحقق من Firebase والصلاحيات"}"
    }

    fun approve(id: String) = updateStatus(id, "PUBLISHED")
    fun reject(id: String) = updateStatus(id, "REJECTED")
    fun setUserBlocked(id: String, blocked: Boolean) {
        db?.collection("users")?.document(id)?.update("isBlocked", blocked)
            ?.addOnFailureListener { reportFirestoreError("تحديث حظر المستخدم", it) }
    }
    private fun updateStatus(id: String, status: String) {
        db?.collection("listings")?.document(id)?.update("status", status)
            ?.addOnFailureListener { reportFirestoreError("تحديث حالة الإعلان", it) }
    }
    companion object { private const val TAG = "AdminRepository" }
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

data class AdminUser(
    val id: String,
    val name: String,
    val phone: String,
    val isBlocked: Boolean
)
