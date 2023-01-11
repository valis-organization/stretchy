package com.example.stretchy.features.traininglist.ui.composable

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.stretchy.features.traininglist.ui.TrainingListViewModel
import com.example.stretchy.R
import com.example.stretchy.Screen
import com.example.stretchy.common.convertSecondsToMinutes
import com.example.stretchy.features.traininglist.ui.data.Training
import com.example.stretchy.features.traininglist.ui.data.TrainingListUiState
import com.example.stretchy.theme.White80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TrainingListComposable(
    viewModel: TrainingListViewModel,
    navController: NavController,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("exerciseCreatorScreen") }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    Menu(viewModel, onExportClick, onImportClick)
                }
            )
        }
    ) { contentPadding ->

        Box(
            modifier = Modifier
                .padding(contentPadding)
                .background(White80)
                .fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Row(modifier = Modifier.padding(start = 24.dp)) {
                    Text(
                        text = stringResource(R.string.stretches),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                when (val state = viewModel.uiState.collectAsState().value) {
                    is TrainingListUiState.Empty ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.exercises_not_added),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = stringResource(R.string.do_it_by_add_button),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    is TrainingListUiState.Loading ->
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    is TrainingListUiState.Loaded -> TrainingListComposable(
                        state.trainings,
                        navController
                    )
                }
            }
        }
    }
}

@Composable
fun Menu(viewModel: TrainingListViewModel, onExportClick: () -> Unit, onImportClick: () -> Unit) {
    val context = LocalContext.current
    var expanded by remember {
        mutableStateOf(false)
    }
    IconButton(
        onClick = {
            expanded = true
        }
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "menu",
            tint = Color.White
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    if (isPermissionsGranted(context, READ_EXTERNAL_STORAGE)) {
                        CoroutineScope(Dispatchers.Default).launch {
                            viewModel.import()
                        }
                    } else {
                        onImportClick()
                    }
                    Toast.makeText(context, R.string.import_trainings, Toast.LENGTH_LONG).show()
                }
            ) {
                Text(text = stringResource(id = R.string.import_trainings))
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    if (isPermissionsGranted(context, WRITE_EXTERNAL_STORAGE)) {
                        viewModel.export()
                    } else {
                        onExportClick()
                    }
                    Toast.makeText(context, R.string.export_trainings, Toast.LENGTH_LONG).show()
                }
            ) {
                Text(text = stringResource(id = R.string.export_trainings))
            }
        }
    }
}

@Composable
private fun TrainingListComposable(trainingList: List<Training>, navController: NavController) {
    LazyColumn {
        items(trainingList) { training ->
            Box(
                modifier = Modifier.clickable {
                    navController.navigate("executeTraining?id=${training.id}")
                },
            ) {
                TrainingComposable(item = training,navController)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TrainingComposable(item: Training,navController: NavController) {
    Column(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .height(152.dp)
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.fillMaxWidth()){
            Text(text = item.itemName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().clickable { navController.navigate("exerciseCreatorScreen?id=${item.id}") },Arrangement.End) {
    /*            IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "menu",
                    )
                    }*/
               Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Add")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = stringResource(R.string.exercises),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${item.numberOfExercises}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(100.dp))
            Column {
                Text(
                    text = stringResource(R.string.total_time),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = convertSecondsToMinutes(item.timeInSeconds.toLong()),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


private fun isPermissionsGranted(context: Context, permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val result =
            ContextCompat.checkSelfPermission(context, permission)
        result == PackageManager.PERMISSION_GRANTED
    }
}