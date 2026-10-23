package com.tecsup.notivasnube.ui

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.notivasnube.data.Cursos
import com.tecsup.notivasnube.data.Nota
import com.tecsup.notivasnube.data.promedioDe
import java.util.Locale

private const val NUEVA = "nueva"

@Composable
fun NotasApp(vm: NotasViewModel) {
    val navController = rememberNavController()
    val estado by vm.estado.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val ruta = navController.currentBackStackEntryAsState().value?.destination?.route
    val enLista = ruta == null || ruta == "lista"

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        if (enLista) "NOTIVas · Notas en la nube" else "Registrar nota",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    if (!enLista) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            if (enLista) {
                FloatingActionButton(onClick = { navController.navigate("form/$NUEVA") }) {
                    Icon(Icons.Filled.Add, contentDescription = "Nueva nota")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pv ->
        NavHost(navController, startDestination = "lista", modifier = Modifier.padding(pv)) {
            composable("lista") {
                ListaNotas(
                    estado = estado,
                    alReintentar = vm::cargar,
                    alEditar = { navController.navigate("form/$it") },
                    alBorrar = vm::borrar
                )
            }
            composable("form/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                val id = it.arguments!!.getString("id")!!.takeIf { s -> s != NUEVA }
                FormularioNota(vm, id) { navController.popBackStack() }
            }
        }
    }
}

@Composable
private fun ListaNotas(
    estado: NotasUiState,
    alReintentar: () -> Unit,
    alEditar: (String) -> Unit,
    alBorrar: (String) -> Unit
) {
    var porBorrar by remember { mutableStateOf<Nota?>(null) }

    when {
        estado.cargando -> Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Consultando el servidor…")
        }

        estado.error != null -> Column(
            Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally
        ) {
            Text("No pude traer las notas", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(estado.error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
            Button(onClick = alReintentar) { Text("Reintentar") }
        }

        else -> LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Resumen(estado.notas) }
            if (estado.notas.isEmpty()) {
                item {
                    Text(
                        "Todavía no hay notas guardadas en el servidor. Toca + para registrar la primera.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            items(estado.notas, key = { it.id }) { nota ->
                TarjetaNota(nota, !estado.ocupado, { alEditar(nota.id) }, { porBorrar = nota })
            }
        }
    }

    porBorrar?.let { nota ->
        AlertDialog(
            onDismissRequest = { porBorrar = null },
            title = { Text("Eliminar nota") },
            text = { Text("¿Eliminar «${nota.nombre}» de ${Cursos.porId(nota.cursoId).nombre}? Se borra también del servidor.") },
            confirmButton = {
                TextButton(onClick = { alBorrar(nota.id); porBorrar = null }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { porBorrar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Resumen(notas: List<Nota>) {
    val prom = promedioDe(notas)
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Promedio ponderado", style = MaterialTheme.typography.labelLarge)
            Text(
                if (prom == null) "—" else String.format(Locale.US, "%.2f", prom),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text("${notas.size} evaluaciones guardadas en api.restful-api.dev", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun TarjetaNota(nota: Nota, habilitado: Boolean, alEditar: () -> Unit, alBorrar: () -> Unit) {
    val curso = Cursos.porId(nota.cursoId)
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(nota.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(curso.nombre, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Peso ${nota.peso}%  ·  Nota " + String.format(Locale.US, "%.1f", nota.valor),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = alEditar, enabled = habilitado) { Icon(Icons.Outlined.Edit, contentDescription = "Editar") }
            IconButton(onClick = alBorrar, enabled = habilitado) { Icon(Icons.Outlined.Delete, contentDescription = "Eliminar") }
        }
    }
}

@Composable
private fun FormularioNota(vm: NotasViewModel, id: String?, alTerminar: () -> Unit) {
    val estado by vm.estado.collectAsState()
    var cursoId by remember { mutableStateOf(Cursos.lista.first().id) }
    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(id != null) }
    var falloCarga by remember { mutableStateOf<String?>(null) }
    var intento by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }

    // Editar = GET /objects/{id} para precargar el formulario, y PUT al grabar.
    LaunchedEffect(id) {
        if (id != null) {
            vm.obtener(id).onSuccess {
                cursoId = it.cursoId
                nombre = it.nombre
                peso = it.peso.toString()
                valor = it.valor.toString()
            }.onFailure { falloCarga = it.message ?: "No se pudo leer la nota" }
            cargando = false
        }
    }

    val pesoNum = peso.toIntOrNull()
    val valorNum = valor.replace(',', '.').toDoubleOrNull()
    val errNombre = if (nombre.isBlank()) "Escribe el nombre de la evaluación" else null
    val errPeso = if (pesoNum == null || pesoNum !in 1..100) "El peso va de 1 a 100" else null
    val errValor = if (valorNum == null || valorNum < 0.0 || valorNum > 20.0) "La nota va de 0 a 20" else null

    if (cargando) {
        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("GET objects/$id")
        }
        return
    }
    if (falloCarga != null) {
        Column(Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally) {
            Text(falloCarga!!, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = alTerminar) { Text("Volver") }
        }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ExposedDropdownMenuBox(expanded = menu, onExpandedChange = { menu = it }) {
            OutlinedTextField(
                value = Cursos.porId(cursoId).nombre,
                onValueChange = {},
                readOnly = true,
                label = { Text("Curso") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menu) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            androidx.compose.material3.ExposedDropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                Cursos.lista.forEach { c ->
                    DropdownMenuItem(text = { Text(c.nombre) }, onClick = { cursoId = c.id; menu = false })
                }
            }
        }
        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it }, label = { Text("Evaluación") },
            isError = intento && errNombre != null, supportingText = { if (intento) errNombre?.let { Text(it) } },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = peso, onValueChange = { peso = it }, label = { Text("Peso (%)") },
            isError = intento && errPeso != null, supportingText = { if (intento) errPeso?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = valor, onValueChange = { valor = it }, label = { Text("Nota (0 a 20)") },
            isError = intento && errValor != null, supportingText = { if (intento) errValor?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                enabled = !estado.ocupado,
                onClick = {
                    intento = true
                    if (errNombre == null && errPeso == null && errValor == null) {
                        vm.guardar(id, cursoId, nombre.trim(), pesoNum!!, valorNum!!, alTerminar)
                    }
                }
            ) { Text(if (estado.ocupado) "Guardando…" else "Grabar") }
            OutlinedButton(onClick = alTerminar) { Text("Cancelar") }
        }
    }
}
