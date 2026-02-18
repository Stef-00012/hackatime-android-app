package com.stefdp.hackatime.screens.loading

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.stefdp.hackatime.LocalUpdateUserStats
import com.stefdp.hackatime.R
import com.stefdp.hackatime.screens.BiometricAuthScreen
import com.stefdp.hackatime.screens.HomeScreen
import com.stefdp.hackatime.screens.LoginScreen
import com.stefdp.hackatime.ui.theme.HackatimeStatsTheme
import com.stefdp.hackatime.utils.SecureStorage
import com.stefdp.hackatime.utils.getBiometricStatus

@Composable
fun LoadingScreen(
    navController: NavHostController,
    context: Context,
    activity: FragmentActivity
) {
    val updateUserStats = LocalUpdateUserStats.current

    LaunchedEffect(Unit) {
        val secureStore = SecureStorage.getInstance(context)

        val unlockWithBiometrics = secureStore.get("unlockWithBiometrics")?.toBoolean() ?: false
        val biometricAuthenticationStatus = getBiometricStatus(context)
        val newUserStats = updateUserStats()

        if (newUserStats == null) {
            navController.navigate(LoginScreen) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        } else {
            if (unlockWithBiometrics && biometricAuthenticationStatus == BiometricManager.BIOMETRIC_SUCCESS) {
                navController.navigate(BiometricAuthScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            } else {
                navController.navigate(HomeScreen) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.loading_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        CircularProgressIndicator()
    }
}