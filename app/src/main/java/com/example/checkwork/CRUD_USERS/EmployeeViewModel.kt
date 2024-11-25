package com.example.checkwork.CRUD_USERS

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EmployeeViewModel(
    private val repository: EmployeeRepository
) : ViewModel() {

    // Estado para los registros del empleado
    private val _employeeRecords = MutableStateFlow<List<EmployeeRecord>>(emptyList())
    val employeeRecords: StateFlow<List<EmployeeRecord>> get() = _employeeRecords

    // Estado para el nombre del empleado
    private val _employeeName = MutableStateFlow("Cargando...")
    val employeeName: StateFlow<String> get() = _employeeName

    // Método para obtener registros del empleado
    fun fetchEmployeeRecords(employeeId: String) {
        viewModelScope.launch {
            try {
                println("Consultando registros en ViewModel para employeeId: $employeeId")
                val records = repository.getEmployeeRecords(employeeId)
                if (records.isNotEmpty()) {
                    println("Registros obtenidos en ViewModel: $records")
                } else {
                    println("No se encontraron registros para employeeId: $employeeId")
                }
                _employeeRecords.value = records // Actualiza el estado con los datos obtenidos
            } catch (e: Exception) {
                println("Error en fetchEmployeeRecords: ${e.message}")
                _employeeRecords.value = emptyList() // Limpia el estado en caso de error
            }
        }
    }

    // Método para obtener el nombre del empleado
    fun fetchEmployeeName(employeeId: String) {
        viewModelScope.launch {
            try {
                println("Consultando nombre del empleado en ViewModel para employeeId: $employeeId")
                val name = repository.getEmployeeName(employeeId)
                if (name.isNotEmpty()) {
                    println("Nombre recibido en ViewModel: $name")
                } else {
                    println("No se encontró un nombre para employeeId: $employeeId")
                }
                _employeeName.value = name // Actualiza el estado con el nombre obtenido
            } catch (e: Exception) {
                println("Error en fetchEmployeeName: ${e.message}")
                _employeeName.value = "Error al cargar nombre" // Establece un estado de error
            }
        }
    }
}
