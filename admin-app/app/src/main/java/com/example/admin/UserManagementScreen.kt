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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun UserManagementScreen(
    users: List<AdminUser>,
    onBack: () -> Unit,
    onSetBlocked: (String, Boolean) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = users.filter { user ->
        query.isBlank() || user.name.contains(query, true) || user.phone.contains(query, true)
    }

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "رجوع") }
            Column(Modifier.padding(start = 6.dp)) {
                Text("إدارة المستخدمين", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF087F5B))
                Text("${users.count { it.isBlocked }} محظور من أصل ${users.size}", fontSize = 12.sp, color = Color.Gray)
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("البحث بالاسم أو الهاتف") }
        )
        Spacer(Modifier.height(12.dp))
        if (filtered.isEmpty()) {
            Text("لا توجد حسابات في مجموعة users أو لا توجد نتائج.", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtered, key = { it.id }) { user ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (user.isBlocked) Color(0xFFFFF1F2) else Color(0xFFF8FAFC))) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (user.isBlocked) Icons.Default.Block else Icons.Default.Person, contentDescription = null, tint = if (user.isBlocked) Color(0xFFB91C1C) else Color(0xFF087F5B))
                            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                                Text(user.name, fontWeight = FontWeight.Bold)
                                Text(user.phone.ifBlank { "لا يوجد رقم هاتف" }, fontSize = 12.sp, color = Color.Gray)
                                Text(if (user.isBlocked) "الحساب محظور" else "الحساب نشط", fontSize = 11.sp, color = if (user.isBlocked) Color(0xFFB91C1C) else Color(0xFF087F5B))
                            }
                            Button(onClick = { onSetBlocked(user.id, !user.isBlocked) }, colors = ButtonDefaults.buttonColors(containerColor = if (user.isBlocked) Color(0xFF087F5B) else Color(0xFFB91C1C))) {
                                Text(if (user.isBlocked) "رفع الحظر" else "حظر")
                            }
                        }
                    }
                }
            }
        }
    }
}
