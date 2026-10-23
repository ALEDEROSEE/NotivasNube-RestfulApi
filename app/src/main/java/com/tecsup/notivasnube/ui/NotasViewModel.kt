package com.tecsup.notivasnube.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.notivasnube.data.Nota
import com.tecsup.notivasnube.data.NotaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotasUiState(
    val cargando: Boolean = true,
    val notas: List<Nota> = emptyList(),
    val error: String? = null,
    /** Hay una escritura (POST, PUT o DELETE) en vuelo: se bloquean los botones para no duplicar peticiones. */
    val ocupado: Boolean = false,
    /** Mensaje de una sola vez para el Snackbar. */
    val mensaje: String? = null
)

class NotasViewModel(private val repo: NotaRepository) : ViewModel() {

    private val _estado = MutableStateFlow(NotasUiState())
    val estado: StateFlow<NotasUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            try {
                val lista = repo.listar()
                _estado.update { it.copy(cargando = false, notas = lista) }
            } catch (e: Exception) {
                _estado.update { it.copy(cargando = false, error = describir(e)) }
            }
        }
    }

    /** GET de un solo objeto para precargar el formulario de edición. */
    suspend fun obtener(id: String): Result<Nota> = runCatching { repo.obtener(id) }

    /** Crea (id == null) o modifica. Llama a [alTerminar] solo si el servidor aceptó el cambio. */
    fun guardar(id: String?, cursoId: Int, nombre: String, peso: Int, valor: Double, alTerminar: () -> Unit) {
        if (_estado.value.ocupado) return
        _estado.update { it.copy(ocupado = true) }
        viewModelScope.launch {
            try {
                val nota = if (id == null) repo.crear(cursoId, nombre, peso, valor)
                else repo.actualizar(Nota(id, cursoId, nombre, peso, valor))
                _estado.update { s ->
                    val nuevas = if (id == null) s.notas + nota else s.notas.map { if (it.id == nota.id) nota else it }
                    s.copy(ocupado = false, notas = nuevas, mensaje = if (id == null) "Nota creada en el servidor" else "Nota actualizada")
                }
                alTerminar()
            } catch (e: Exception) {
                _estado.update { it.copy(ocupado = false, mensaje = "No se pudo guardar: " + describir(e)) }
            }
        }
    }

    fun borrar(id: String) {
        if (_estado.value.ocupado) return
        _estado.update { it.copy(ocupado = true) }
        viewModelScope.launch {
            try {
                repo.borrar(id)
                _estado.update { s -> s.copy(ocupado = false, notas = s.notas.filterNot { it.id == id }, mensaje = "Nota eliminada") }
            } catch (e: Exception) {
                _estado.update { it.copy(ocupado = false, mensaje = "No se pudo eliminar: " + describir(e)) }
            }
        }
    }

    fun mensajeMostrado() {
        _estado.update { it.copy(mensaje = null) }
    }

    private fun describir(e: Exception): String = when (e) {
        is java.net.UnknownHostException -> "Sin conexión con el servidor"
        is java.net.SocketTimeoutException -> "El servidor tardó demasiado en responder"
        else -> e.message ?: e.javaClass.simpleName
    }
}
