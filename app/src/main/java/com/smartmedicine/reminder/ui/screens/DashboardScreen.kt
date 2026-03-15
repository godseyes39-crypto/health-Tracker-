package com.smartmedicine.reminder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartmedicine.reminder.data.model.HealthRecord
import com.smartmedicine.reminder.data.model.MedicineLog
import com.smartmedicine.reminder.ui.components.InfoCard
import com.smartmedicine.reminder.ui.components.SectionTitle
import com.smartmedicine.reminder.ui.theme.HealthBlue
import com.smartmedicine.reminder.ui.theme.HealthPink
import com.smartmedicine.reminder.ui.theme.HealthPurple
import com.smartmedicine.reminder.ui.theme.HealthTeal
import com.smartmedicine.reminder.ui.theme.MissedRed
import com.smartmedicine.reminder.ui.theme.TakenGreen
import com.smartmedicine.reminder.ui.theme.UpcomingOrange
import com.smartmedicine.reminder.viewmodel.HealthViewModel
import com.smartmedicine.reminder.viewmodel.MedicineViewModel

@Composable
fun DashboardScreen(
    medicineViewModel: MedicineViewModel,
    healthViewModel: HealthViewModel,
    onNavigateToMedicines: () -> Unit,
    onNavigateToHealth: () -> Unit
) {
    val todayLogs by medicineViewModel.todayLogs.collectAsState()
    val adherenceRate by medicineViewModel.adherenceRate.collectAsState()
    val takenCount by medicineViewModel.takenCount.collectAsState()
    val missedCount by medicineViewModel.missedCount.collectAsState()
    val totalCount by medicineViewModel.totalCount.collectAsState()

    val latestBP by healthViewModel.latestBP.collectAsState()
    val latestHeartRate by healthViewModel.latestHeartRate.collectAsState()
    val latestBloodSugar by healthViewModel.latestBloodSugar.collectAsState()
    val latestWeight by healthViewModel.latestWeight.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header
        Text(
            text = "Good Day!",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your health overview",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Medication Adherence Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Weekly Adherence",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${adherenceRate.toInt()}%",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { adherenceRate / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = TakenGreen,
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AdherenceStat(
                        icon = Icons.Default.CheckCircle,
                        label = "Taken",
                        count = takenCount,
                        color = TakenGreen
                    )
                    AdherenceStat(
                        icon = Icons.Default.Cancel,
                        label = "Missed",
                        count = missedCount,
                        color = MissedRed
                    )
                    AdherenceStat(
                        icon = Icons.Default.Schedule,
                        label = "Total",
                        count = totalCount,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Medicines
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(title = "Today's Medicines")
            TextButton(onClick = onNavigateToMedicines) {
                Text(
                    "View All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (todayLogs.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Medication,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No medicines scheduled for today",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            todayLogs.forEach { log ->
                TodayMedicineCard(log = log, onMarkTaken = {
                    medicineViewModel.markAsTaken(log.id)
                })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Health Summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(title = "Health Summary")
            TextButton(onClick = onNavigateToHealth) {
                Text(
                    "View All",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                title = "Blood Pressure",
                value = latestBP?.let {
                    "${it.value1.toInt()}/${it.value2?.toInt()}"
                } ?: "--/--",
                subtitle = "mmHg",
                valueColor = HealthBlue,
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                title = "Heart Rate",
                value = latestHeartRate?.let {
                    "${it.value1.toInt()}"
                } ?: "--",
                subtitle = "BPM",
                valueColor = HealthPink,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                title = "Blood Sugar",
                value = latestBloodSugar?.let {
                    "${it.value1.toInt()}"
                } ?: "--",
                subtitle = "mg/dL",
                valueColor = HealthPurple,
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                title = "Weight",
                value = latestWeight?.let {
                    String.format("%.1f", it.value1)
                } ?: "--",
                subtitle = "kg",
                valueColor = HealthTeal,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun AdherenceStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun TodayMedicineCard(
    log: MedicineLog,
    onMarkTaken: () -> Unit
) {
    val statusColor = when (log.status) {
        MedicineLog.STATUS_TAKEN -> TakenGreen
        MedicineLog.STATUS_MISSED -> MissedRed
        MedicineLog.STATUS_SNOOZED -> UpcomingOrange
        else -> UpcomingOrange
    }

    val statusIcon = when (log.status) {
        MedicineLog.STATUS_TAKEN -> Icons.Default.CheckCircle
        MedicineLog.STATUS_MISSED -> Icons.Default.Cancel
        else -> Icons.Default.Schedule
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = log.status,
                tint = statusColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.medicineName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = log.status.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = statusColor
                )
            }
            if (log.status == MedicineLog.STATUS_UPCOMING || log.status == MedicineLog.STATUS_SNOOZED) {
                TextButton(onClick = onMarkTaken) {
                    Text(
                        "Take",
                        style = MaterialTheme.typography.labelLarge,
                        color = TakenGreen
                    )
                }
            }
        }
    }
}
