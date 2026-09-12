package com.example.admin

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminApp() {
    val repository = remember { AdminRepository() }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    val listings by repository.listings.collectAsState()
    val users by repository.users.collectAsState()
    val pending = listings.filter { it.status == "PAYMENT_PENDING" }
    val active = listings.filter { it.status == "PUBLISHED" }
    var rejectId by remember { mutableStateOf<String?>(null) }
    var chartMode by remember { mutableStateOf(ChartMode.BY_DATE) }
    var showUsers by remember { mutableStateOf(false) }

    if (showUsers) {
        UserManagementScreen(
            users = users,
            onBack = { showUsers = false },
            onSetBlocked = repository::setUserBlocked
        )
        return
    }

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF087F5B))
            Column(Modifier.padding(start = 10.dp)) {
                Text("لوحة إدارة avendOc", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF087F5B))
                Text("${pending.size} إعلان ينتظر الموافقة", fontSize = 13.sp, color = Color.Gray)
            }
            TextButton(onClick = { showUsers = true }) { Text("إدارة المستخدمين") }
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
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = chartMode == ChartMode.BY_DATE,
                onClick = { chartMode = ChartMode.BY_DATE },
                label = { Text("حسب التاريخ") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD1FAE5))
            )
            FilterChip(
                selected = chartMode == ChartMode.BY_CATEGORY,
                onClick = { chartMode = ChartMode.BY_CATEGORY },
                label = { Text("حسب النوع") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFEF3C7))
            )
        }
        Spacer(Modifier.height(8.dp))
        ListingChart(listings = listings, mode = chartMode)
        Spacer(Modifier.height(14.dp))
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

private enum class ChartMode { BY_DATE, BY_CATEGORY }

@Composable
private fun ListingChart(listings: List<AdminListing>, mode: ChartMode) {
    val groups = if (mode == ChartMode.BY_DATE) {
        listings.groupingBy { it.createdAt }.eachCount().toList().takeLast(7)
    } else {
        listings.groupingBy { it.category }.eachCount().toList().sortedByDescending { it.second }.take(7)
    }
    Card(Modifier.fillMaxWidth().height(220.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))) {
        Column(Modifier.padding(12.dp)) {
            Text(if (mode == ChartMode.BY_DATE) "توزيع الإعلانات حسب تاريخ الإنشاء" else "توزيع الإعلانات حسب النوع", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            if (groups.isEmpty()) {
                Text("لا توجد بيانات كافية للرسم البياني.", color = Color.Gray, modifier = Modifier.padding(top = 40.dp))
            } else {
                val maxValue = groups.maxOf { it.second }.toFloat().coerceAtLeast(1f)
                Canvas(Modifier.fillMaxWidth().weight(1f)) {
                    val slot = size.width / groups.size
                    val barWidth = (slot * 0.55f).coerceAtLeast(18f)
                    groups.forEachIndexed { index, entry ->
                        val barHeight = size.height * (entry.second / maxValue)
                        val left = index * slot + (slot - barWidth) / 2f
                        drawRoundRect(Color(0xFF087F5B), topLeft = Offset(left, size.height - barHeight), size = Size(barWidth, barHeight), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
                        drawContext.canvas.nativeCanvas.drawText(entry.second.toString(), left + barWidth / 2f, size.height - barHeight - 6f, android.graphics.Paint().apply { color = android.graphics.Color.DKGRAY; textSize = 28f; textAlign = android.graphics.Paint.Align.CENTER })
                        drawContext.canvas.nativeCanvas.drawText(entry.first.take(9), left + barWidth / 2f, size.height - 2f, android.graphics.Paint().apply { color = android.graphics.Color.GRAY; textSize = 22f; textAlign = android.graphics.Paint.Align.CENTER })
                    }
                }
            }
        }
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
