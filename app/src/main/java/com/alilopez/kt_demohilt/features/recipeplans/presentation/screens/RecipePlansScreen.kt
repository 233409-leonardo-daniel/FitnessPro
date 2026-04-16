package com.alilopez.kt_demohilt.features.recipeplans.presentation.screens

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
import com.alilopez.kt_demohilt.features.recipeplans.data.workers.DownloadRecipePlanWorker
import com.alilopez.kt_demohilt.features.recipeplans.domain.entities.RecipePlan
import com.alilopez.kt_demohilt.features.recipeplans.presentation.viewmodels.RecipePlansViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePlansScreen(
    onNavigateToDetail: (Int, String) -> Unit,
    onNavigateToPremium: () -> Unit,
    onOpenDrawer: () -> Unit,
    membership: String?,
    userId: Int?,
    viewModel: RecipePlansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val isPremium = membership != null && membership != "gratuito"

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val accentColor = Color(0xFF10B981)

    var showCreateDialog by remember { mutableStateOf(false) }

    fun startDownload(plan: RecipePlan) {
        if (!isPremium) {
            onNavigateToPremium()
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .build()

        val data = Data.Builder()
            .putInt(DownloadRecipePlanWorker.KEY_PLAN_ID, plan.id)
            .putString(DownloadRecipePlanWorker.KEY_PLAN_NAME, plan.name)
            .putString(DownloadRecipePlanWorker.KEY_PLAN_DESC, plan.description)
            .putInt(DownloadRecipePlanWorker.KEY_USER_ID, userId ?: -1)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadRecipePlanWorker>()
            .setConstraints(constraints)
            .setInputData(data)
            .addTag("download_recipe_plan_${plan.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "download_recipe_${plan.id}",
            ExistingWorkPolicy.KEEP,
            downloadRequest
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Mis Listas de Recetas", fontWeight = FontWeight.Bold) },
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
            onRefresh = viewModel::loadRecipePlans,
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
                !uiState.isLoading && uiState.recipePlans.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No tienes listas de recetas", color = textColor, fontSize = 18.sp)
                        Text("¡Crea tu primera lista para empezar!", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.recipePlans) { plan ->
                            RecipePlanItem(
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
            CreateRecipePlanDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, desc, isPrivate ->
                    viewModel.createPlan(name, desc, isPrivate)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun RecipePlanItem(
    plan: RecipePlan,
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
                                "Privada", 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
                Text(text = plan.description, fontSize = 14.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${plan.recipes.size} recetas", fontSize = 12.sp, color = Color.Gray)
            }
            
            // Solo mostrar el estado de descargado si el usuario es Premium
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

@Composable
fun CreateRecipePlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Lista de Recetas") },
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
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Lista Privada")
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
                onClick = { if (name.isNotBlank()) onConfirm(name, desc, isPrivate) },
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
