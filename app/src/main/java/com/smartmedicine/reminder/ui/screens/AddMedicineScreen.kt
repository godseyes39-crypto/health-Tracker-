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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.smartmedicine.reminder.data.model.Medicine
import com.smartmedicine.reminder.data.model.MedicineFrequency
import com.smartmedicine.reminder.ui.components.LargeButton
import com.smartmedicine.reminder.viewmodel.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    viewModel: MedicineViewModel,
    medicineId: Long = -1L,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(MedicineFrequency.ONCE_DAILY.displayName) }
    var timeHour by remember { mutableIntStateOf(8) }
    var timeMinute by remember { mutableIntStateOf(0) }
    var secondTimeHour by remember { mutableIntStateOf(20) }
    var secondTimeMinute by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }
    var frequencyExpanded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var existingMedicine by remember { mutableStateOf<Medicine?>(null) }

    // Load existing medicine for editing
    LaunchedEffect(medicineId) {
        if (medicineId != -1L) {
            val medicine = viewModel.getMedicineById(medicineId)
            if (medicine != null) {
                isEditing = true
                existingMedicine = medicine
                name = medicine.name
                dosage = medicine.dosage
                frequency = medicine.frequency
                timeHour = medicine.timeHour
                timeMinute = medicine.timeMinute
                secondTimeHour = medicine.secondTimeHour ?: 20
                secondTimeMinute = medicine.secondTimeMinute ?: 0
                notes = medicine.notes
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isEditing) "Edit Medicine" else "Add Medicine",
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
            // Medicine Name
            Text(
                "Medicine Name",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Aspirin") },
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dosage
            Text(
                "Dosage",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., 500mg, 1 tablet") },
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Frequency
            Text(
                "Frequency",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = frequencyExpanded,
                onExpandedChange = { frequencyExpanded = !frequencyExpanded }
            ) {
                OutlinedTextField(
                    value = frequency,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                ExposedDropdownMenu(
                    expanded = frequencyExpanded,
                    onDismissRequest = { frequencyExpanded = false }
                ) {
                    MedicineFrequency.entries.forEach { freq ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    freq.displayName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            onClick = {
                                frequency = freq.displayName
                                frequencyExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time Picker
            Text(
                "Reminder Time",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = timeHour.toString(),
                    onValueChange = {
                        val h = it.toIntOrNull()
                        if (h != null && h in 0..23) timeHour = h
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Hour (0-23)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
                OutlinedTextField(
                    value = timeMinute.toString(),
                    onValueChange = {
                        val m = it.toIntOrNull()
                        if (m != null && m in 0..59) timeMinute = m
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("Minute (0-59)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }

            // Second time for twice daily
            if (frequency == MedicineFrequency.TWICE_DAILY.displayName) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Second Reminder Time",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = secondTimeHour.toString(),
                        onValueChange = {
                            val h = it.toIntOrNull()
                            if (h != null && h in 0..23) secondTimeHour = h
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Hour (0-23)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    OutlinedTextField(
                        value = secondTimeMinute.toString(),
                        onValueChange = {
                            val m = it.toIntOrNull()
                            if (m != null && m in 0..59) secondTimeMinute = m
                        },
                        modifier = Modifier.weight(1f),
                        label = { Text("Minute (0-59)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
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
                placeholder = { Text("e.g., Take with food") },
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            LargeButton(
                text = if (isEditing) "Update Medicine" else "Save Medicine",
                onClick = {
                    if (name.isNotBlank() && dosage.isNotBlank()) {
                        val isTwiceDaily = frequency == MedicineFrequency.TWICE_DAILY.displayName
                        val medicine = Medicine(
                            id = existingMedicine?.id ?: 0,
                            name = name,
                            dosage = dosage,
                            frequency = frequency,
                            timeHour = timeHour,
                            timeMinute = timeMinute,
                            secondTimeHour = if (isTwiceDaily) secondTimeHour else null,
                            secondTimeMinute = if (isTwiceDaily) secondTimeMinute else null,
                            notes = notes,
                            isActive = existingMedicine?.isActive ?: true,
                            createdAt = existingMedicine?.createdAt ?: System.currentTimeMillis()
                        )
                        if (isEditing) {
                            viewModel.updateMedicine(medicine)
                        } else {
                            viewModel.addMedicine(medicine)
                        }
                        onNavigateBack()
                    }
                },
                icon = Icons.Default.Save
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
