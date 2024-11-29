package com.example.checkwork.NavigationRegister.dataentryes

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

fun generatePdf(
    context: Context,
    checkEntries: List<CheckEntry>,
    employeeId: String,
    departamento: String,
    nombreEmpresa: String
) {
    // Crear un documento PDF
    val pdfDocument = PdfDocument()
    val pageWidth = 300
    val pageHeight = 600
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    var page = pdfDocument.startPage(pageInfo)

    // Crear un objeto Paint para dibujar el texto
    val paint = Paint().apply {
        textSize = 12f // Tamaño del texto
        color = android.graphics.Color.BLACK // Color del texto
    }

    // Variables para manejar la posición
    var yPosition = 50
    val lineHeight = 20
    val margin = 10

    // Canvas de la página
    var canvas = page.canvas

    try {
        // Título del PDF
        paint.textSize = 16f // Cambiar tamaño para el título
        paint.isFakeBoldText = true // Negrita para el título
        canvas.drawText("Registros de Check-In", (pageWidth / 4).toFloat(), yPosition.toFloat(), paint)
        yPosition += 30

        // Información del empleado
        paint.textSize = 12f // Tamaño normal para el texto
        paint.isFakeBoldText = false // Texto normal para el resto del contenido
        canvas.drawText("Empresa: $nombreEmpresa", margin.toFloat(), yPosition.toFloat(), paint)
        yPosition += lineHeight

        canvas.drawText("Empleado: $employeeId", margin.toFloat(), yPosition.toFloat(), paint)
        yPosition += lineHeight

        canvas.drawText("Departamento: $departamento", margin.toFloat(), yPosition.toFloat(), paint)
        yPosition += lineHeight

        // Encabezados de la tabla
        paint.textSize = 12f // Tamaño normal para el texto
        paint.isFakeBoldText = true // Negrita para encabezados
        canvas.drawText("Fecha", margin.toFloat(), yPosition.toFloat(), paint)
        canvas.drawText("Hora", 120f, yPosition.toFloat(), paint)
        canvas.drawText("Tipo", 200f, yPosition.toFloat(), paint)
        yPosition += lineHeight

        paint.isFakeBoldText = false // Texto normal para los registros

        // Iterar por los registros
        checkEntries.forEach { entry ->
            val fecha = entry.fecha
            val hora = entry.hora
            val tipo = entry.tipo

            canvas.drawText(fecha, margin.toFloat(), yPosition.toFloat(), paint)
            canvas.drawText(hora, 120f, yPosition.toFloat(), paint)
            canvas.drawText(tipo, 200f, yPosition.toFloat(), paint)
            yPosition += lineHeight

            if (yPosition > pageHeight - margin * 4) {
                // Finalizar la página actual y crear una nueva si es necesario
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdfDocument.pages.size + 1).create())
                canvas = page.canvas
                yPosition = 50

                // Dibujar encabezados nuevamente en la nueva página
                canvas.drawText("Fecha", margin.toFloat(), yPosition.toFloat(), paint)
                canvas.drawText("Hora", 120f, yPosition.toFloat(), paint)
                canvas.drawText("Tipo", 200f, yPosition.toFloat(), paint)
                yPosition += lineHeight
            }
        }

        pdfDocument.finishPage(page)

        // Guardar el archivo en el almacenamiento
        val filePath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "RegistrosCheckIn.pdf"
        )

        pdfDocument.writeTo(FileOutputStream(filePath))
        Toast.makeText(context, "PDF guardado en: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar el PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}
