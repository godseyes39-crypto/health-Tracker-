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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartmedicine.reminder.data.model.HealthRecord
import com.smartmedicine.reminder.ui.components.EmptyStateMessage
import com.smartmedicine.reminder.ui.theme.HealthBlue
import com.smartmedicine.reminder.ui.theme.HealthPink
import com.smartmedicine.reminder.ui.theme.HealthPurple
import com.smartmedicine.reminder.ui.theme.HealthTeal
import com.smartmedicine.reminder.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HealthTrackingScreen(
    viewModel: HealthViewModel,
    onAddRecord: (String) -> Unit
) {
    val records by viewModel.allRecords.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Health Tracking",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Monitor your vital signs",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Add Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HealthTypeButton(
                title = "Blood\nPressure",
                icon = Icons.Default.Favorite,
                color = HealthBlue,
                onClick = { onAddRecord(HealthRecord.TYPE_BLOOD_PRESSURE) },
                modifier = Modifier.weight(1f)
            )
            HealthTypeButton(
                title = "Heart\nRate",
                icon = Icons.Default.MonitorHeart,
                color = HealthPink,
                onClick = { onAddRecord(HealthRecord.TYPE_HEART_RATE) },
                modifier = Modifier.weight(1f)
            )
            HealthTypeButton(
                title = "Blood\nSugar",
                icon = Icons.Default.Opacity,
                color = HealthPurple,
                onClick = { onAddRecord(HealthRecord.TYPE_BLOOD_SUGAR) },
                modifier = Modifier.weight(1f)
            )
            HealthTypeButton(
                title = "Weight",
                icon = Icons.Default.MonitorWeight,
                color = HealthTeal,
                onClick = { onAddRecord(HealthRecord.TYPE_WEIGHT) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Recent Records",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (records.isEmpty()) {
            EmptyStateMessage(
                message = "No health records yet.\nTap a category above to add one.",
                icon = Icons.Default.FavoriteBorder
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(records, key = { it.id }) { record ->
                    HealthRecordCard(
                        record = record,
                        onDelete = { viewModel.deleteRecord(record) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun HealthTypeButton(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Icon(
                Icons.Default.Add,
                contentDescription = "Add",
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun HealthRecordCard(
    record: HealthRecord,
    onDelete: () -> Unit
) {
    val (icon, color, typeLabel) = when (record.type) {
        HealthRecord.TYPE_BLOOD_PRESSURE -> Triple(Icons.Default.Favorite, HealthBlue, "Blood Pressure")
        HealthRecord.TYPE_HEART_RATE -> Triple(Icons.Default.MonitorHeart, HealthPink, "Heart Rate")
        HealthRecord.TYPE_BLOOD_SUGAR -> Triple(Icons.Default.Opacity, HealthPurple, "Blood Sugar")
        HealthRecord.TYPE_WEIGHT -> Triple(Icons.Default.MonitorWeight, HealthTeal, "Weight")
        else -> Triple(Icons.Default.Favorite, HealthBlue, "Unknown")
    }

    val valueText = if (record.type == HealthRecord.TYPE_BLOOD_PRESSURE) {
        "${record.value1.toInt()}/${record.value2?.toInt()} ${record.unit}"
    } else if (record.type == HealthRecord.TYPE_WEIGHT) {
        String.format("%.1f %s", record.value1, record.unit)
    } else {
        "${record.value1.toInt()} ${record.unit}"
    }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault())
    val dateText = dateFormat.format(Date(record.recordedAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (record.notes.isNotEmpty()) {
                    Text(
                        text = record.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
