package com.alilopez.kt_demohilt.features.workoutplans.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.*
import com.alilopez.kt_demohilt.features.workoutplans.data.workers.DownloadWorkoutPlanWorker
import com.alilopez.kt_demohilt.features.workoutplans.domain.entities.WorkoutPlan
import com.alilopez.kt_demohilt.features.workoutplans.presentation.viewmodels.WorkoutPlansViewModel

val exerciseTypes = listOf(
    "FUERZA",
    "CARDIO",
    "PLIOMETRÍA",
    "ESTIRAMIENTO",
    "LEVANTAMIENTO DE PESAS",
    "YOGA",
    "AERÓBICOS"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlansScreen(
    onNavigateToDetail: (Int, String) -> Unit,
    onNavigateToPremium: () -> Unit,
    onOpenDrawer: () -> Unit,
    membership: String?,
    userId: Int?,
    viewModel: WorkoutPlansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val isPremium = membership != null && membership != "gratuito"

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val accentColor = Color(0xFF10B981)

    var showCreateDialog by remember { mutableStateOf(false) }

    fun startDownload(plan: WorkoutPlan) {
        if (!isPremium) {
            onNavigateToPremium()
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val data = Data.Builder()
            .putInt(DownloadWorkoutPlanWorker.KEY_PLAN_ID, plan.id)
            .putString(DownloadWorkoutPlanWorker.KEY_PLAN_NAME, plan.name)
            .putString(DownloadWorkoutPlanWorker.KEY_PLAN_DESC, plan.description)
            .putString(DownloadWorkoutPlanWorker.KEY_PLAN_TYPE, plan.planType)
            .putInt(DownloadWorkoutPlanWorker.KEY_USER_ID, userId ?: -1)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorkoutPlanWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .addTag("download_plan_${plan.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "download_workout_${plan.id}",
            ExistingWorkPolicy.KEEP,
            downloadRequest
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Mis Rutinas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = textColor)
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Crear lista", tint = accentColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::loadWorkoutPlans,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "Error desconocido",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                !uiState.isLoading && uiState.workoutPlans.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No tienes rutinas creadas", color = textColor, fontSize = 18.sp)
                        Text("¡Crea tu primera lista para empezar!", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.workoutPlans) { plan ->
                            WorkoutPlanItem(
                                plan = plan,
                                isPremium = isPremium,
                                onClick = { onNavigateToDetail(plan.id, plan.name) },
                                onDelete = { viewModel.deletePlan(plan) },
                                onDownload = { startDownload(plan) },
                                isDarkTheme = isDarkTheme
                            )
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreatePlanDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, desc, type, isPrivate ->
                    viewModel.createPlan(name, desc, type, isPrivate)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun WorkoutPlanItem(
    plan: WorkoutPlan,
    isPremium: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    isDarkTheme: Boolean
) {
    val cardBg = if (isDarkTheme) Color(0xFF1E293B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = plan.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                    if (plan.isPrivate) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = Color.Gray.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "Privado", 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                Text(text = plan.description, fontSize = 14.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = plan.planType, fontSize = 12.sp, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    Text(text = "• ${plan.exercises.size} ejercicios", fontSize = 12.sp, color = Color.Gray)
                }
            }
            
            if (plan.isDownloaded && isPremium) {
                Icon(
                    Icons.Default.CheckCircle, 
                    contentDescription = "Descargado", 
                    tint = Color(0xFF10B981),
                    modifier = Modifier.padding(12.dp)
                )
            } else {
                IconButton(onClick = onDownload) {
                    Icon(Icons.Default.Download, contentDescription = "Descargar", tint = Color(0xFF3B82F6))
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFEF4444))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(exerciseTypes[0]) }
    var isPrivate by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Rutina") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Plan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        exerciseTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Plan Privado")
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, desc, selectedType, isPrivate) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
