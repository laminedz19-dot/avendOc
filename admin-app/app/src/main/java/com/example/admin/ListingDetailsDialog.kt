package com.example.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListingDetailsDialog(
    listing: AdminListing,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    var rejectionReason by remember { mutableStateOf("") }
    var showRejectForm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تفاصيل الإعلان", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(listing.title, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text(listing.description.ifBlank { "لا يوجد وصف" }, modifier = Modifier.padding(top = 6.dp))
                Spacer(Modifier.height(10.dp))
                Text("السعر: ${formatDzdForDetails(listing.priceDzd)}", color = Color(0xFF087F5B), fontWeight = FontWeight.Bold)
                Text("البائع: ${listing.sellerName.ifBlank { "غير محدد" }}")
                Text("الهاتف: ${listing.sellerPhone.ifBlank { "غير محدد" }}")
                Text("الموقع: ${listing.wilaya} - ${listing.commune}")
                Text("الفئة: ${listing.category}")
                Text("تاريخ الإنشاء: ${listing.createdAt}")
                if (listing.rejectionReason.isNotBlank()) {
                    Text("سبب الرفض السابق: ${listing.rejectionReason}", color = Color(0xFFB91C1C), modifier = Modifier.padding(top = 8.dp))
                }
                if (showRejectForm) {
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rejectionReason,
                        onValueChange = { rejectionReason = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("سبب الرفض (إلزامي)") },
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!showRejectForm) {
                    Button(onClick = onApprove, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF087F5B))) {
                        Text("موافقة ونشر")
                    }
                    Button(onClick = { showRejectForm = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))) {
                        Text("رفض")
                    }
                } else {
                    Button(onClick = { if (rejectionReason.isNotBlank()) onReject(rejectionReason) }, enabled = rejectionReason.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))) {
                        Text("تأكيد الرفض")
                    }
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}

private fun formatDzdForDetails(value: Long): String = "$value دج"
