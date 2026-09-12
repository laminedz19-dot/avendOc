package com.example.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    var isCheckingSession by remember { mutableStateOf(true) }
                    var isAdmin by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        val user = if (FirebaseApp.getApps(AdminApplication.context).isEmpty()) null else FirebaseAuth.getInstance().currentUser
                        if (user == null) {
                            isCheckingSession = false
                        } else {
                            // Force refresh so removed admin claims are not accepted from a stale token.
                            user.getIdToken(true)
                                .addOnSuccessListener { token ->
                                    isAdmin = token.claims["admin"] == true
                                    isCheckingSession = false
                                    if (!isAdmin) FirebaseAuth.getInstance().signOut()
                                    else registerCurrentAdminMessagingToken()
                                }
                                .addOnFailureListener {
                                    FirebaseAuth.getInstance().signOut()
                                    isCheckingSession = false
                                }
                        }
                    }

                    when {
                        isCheckingSession -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        isAdmin -> AdminApp(onLogout = {
                            signOutAdmin()
                            isAdmin = false
                        })
                        else -> AdminLoginScreen(onSignedIn = { isAdmin = true })
                    }
                }
            }
        }
    }
}
