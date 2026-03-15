package com.smartmedicine.reminder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.smartmedicine.reminder.data.model.HealthRecord
import com.smartmedicine.reminder.ui.components.LargeButton
import com.smartmedicine.reminder.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHealthRecordScreen(
    viewModel: HealthViewModel,
    recordType: String,
    onNavigateBack: () -> Unit
) {
    var value1 by remember { mutableStateOf("") }
    var value2 by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val title = when (recordType) {
        HealthRecord.TYPE_BLOOD_PRESSURE -> "Blood Pressure"
        HealthRecord.TYPE_HEART_RATE -> "Heart Rate"
        HealthRecord.TYPE_BLOOD_SUGAR -> "Blood Sugar"
        HealthRecord.TYPE_WEIGHT -> "Weight"
        else -> "Health Record"
    }

    val unit = when (recordType) {
        HealthRecord.TYPE_BLOOD_PRESSURE -> "mmHg"
        HealthRecord.TYPE_HEART_RATE -> "BPM"
        HealthRecord.TYPE_BLOOD_SUGAR -> "mg/dL"
        HealthRecord.TYPE_WEIGHT -> "kg"
        else -> ""
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Add $title",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (recordType == HealthRecord.TYPE_BLOOD_PRESSURE) {
                // Systolic
                Text(
                    "Systolic (upper number)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = value1,
                    onValueChange = { value1 = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., 120") },
                    suffix = { Text("mmHg") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Diastolic
                Text(
                    "Diastolic (lower number)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = value2,
                    onValueChange = { value2 = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., 80") },
                    suffix = { Text("mmHg") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Single value input
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))

                val placeholder = when (recordType) {
                    HealthRecord.TYPE_HEART_RATE -> "e.g., 72"
                    HealthRecord.TYPE_BLOOD_SUGAR -> "e.g., 100"
                    HealthRecord.TYPE_WEIGHT -> "e.g., 70.5"
                    else -> ""
                }

                OutlinedTextField(
                    value = value1,
                    onValueChange = { value1 = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(placeholder) },
                    suffix = { Text(unit) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (recordType == HealthRecord.TYPE_WEIGHT)
                            KeyboardType.Decimal else KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes
            Text(
                "Notes (Optional)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("e.g., After breakfast") },
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            LargeButton(
                text = "Save Record",
                onClick = {
                    val v1 = value1.toFloatOrNull()
                    if (v1 != null) {
                        val v2 = value2.toFloatOrNull()
                        val record = HealthRecord(
                            type = recordType,
                            value1 = v1,
                            value2 = v2,
                            unit = unit,
                            notes = notes
                        )
                        viewModel.addRecord(record)
                        onNavigateBack()
                    }
                },
                icon = Icons.Default.Save
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
