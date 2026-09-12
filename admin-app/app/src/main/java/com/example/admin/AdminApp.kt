package com.example.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminApp() {
    val repository = remember { AdminRepository() }
    val listings by repository.listings.collectAsState()
    val pending = listings.filter { it.status == "PAYMENT_PENDING" }
    val active = listings.filter { it.status == "PUBLISHED" }
    var rejectId by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF087F5B))
            Column(Modifier.padding(start = 10.dp)) {
                Text("لوحة إدارة avendOc", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF087F5B))
                Text("${pending.size} إعلان ينتظر الموافقة", fontSize = 13.sp, color = Color.Gray)
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "الإعلانات النشطة",
                value = active.size,
                icon = { Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFF087F5B)) }
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "الإعلانات المعلقة",
                value = pending.size,
                icon = { Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFB45309)) }
            )
        }
        Spacer(Modifier.height(18.dp))
        Text("الإعلانات التي تحتاج مراجعة", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        if (pending.isEmpty()) {
            Text("لا توجد إعلانات معلقة حاليًا.", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(pending, key = { it.id }) { item ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))) {
                        Column(Modifier.padding(14.dp)) {
                            Text(item.title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(formatDzd(item.priceDzd), color = Color(0xFF087F5B), fontWeight = FontWeight.Black)
                            Text("${item.sellerName} • ${item.sellerPhone}", fontSize = 12.sp, color = Color.Gray)
                            Text("${item.wilaya} - ${item.commune}", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(8.dp))
                            Text(item.description, fontSize = 13.sp, maxLines = 3)
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { repository.approve(item.id) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF087F5B))) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                    Text("موافقة ونشر")
                                }
                                OutlinedButton(onClick = { rejectId = item.id }, modifier = Modifier.weight(1f)) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                    Text("رفض")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    rejectId?.let { id ->
        AlertDialog(
            onDismissRequest = { rejectId = null },
            title = { Text("رفض الإعلان؟") },
            text = { Text("سيبقى الإعلان مخفيًا من السوق العام.") },
            confirmButton = { TextButton(onClick = { repository.reject(id); rejectId = null }) { Text("تأكيد الرفض") } },
            dismissButton = { TextButton(onClick = { rejectId = null }) { Text("إلغاء") } }
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    title: String,
    value: Int,
    icon: @Composable () -> Unit
) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))) {
        Column(Modifier.padding(14.dp)) {
            icon()
            Spacer(Modifier.height(8.dp))
            Text(value.toString(), fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

private fun formatDzd(value: Long): String = NumberFormat.getNumberInstance(Locale("fr", "DZ")).format(value) + " دج"
