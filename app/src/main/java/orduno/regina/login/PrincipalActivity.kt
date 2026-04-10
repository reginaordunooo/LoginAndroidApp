package orduno.regina.login

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import orduno.regina.login.ui.theme.LoginTheme
import java.util.Calendar

class PrincipalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //var nombre = intent.getStringExtra("nombre")
        //var correo = intent.getStringExtra("correo")

        //val user = nombre ?: correo ?: "user"

        var uid = Firebase.auth.currentUser?.uid ?: ""

        var myRef = Firebase.database.getReference("usuarios").child(uid)
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        myRef,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(myRef: DatabaseReference, modifier: Modifier = Modifier) {

    var nombre by remember { mutableStateOf("Loading...") }
    var correo by remember { mutableStateOf("Loading...") }
    var fecha by remember { mutableStateOf("Loading...") }
    var edad by remember { mutableStateOf("Loading...") }
    val context = LocalContext.current

    var showNombreDialog by remember { mutableStateOf(false) }
    var showFechaDialog by remember { mutableStateOf(false) }

    var nuevoValor by remember { mutableStateOf("") }

    myRef.get().addOnSuccessListener { snapshot ->
        nombre = snapshot.child("name").value.toString()
        correo = snapshot.child("correo").value.toString()
        fecha = snapshot.child("fecha").value.toString()
        edad = snapshot.child("edad").value.toString()
    }

    fun calcularEdad(fecha: String): Int {
        val partes = fecha.split("/")

        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val año = partes[2].toInt()

        val hoy = Calendar.getInstance()

        val añoActual = hoy.get(Calendar.YEAR)
        val mesActual = hoy.get(Calendar.MONTH) + 1
        val diaActual = hoy.get(Calendar.DAY_OF_MONTH)

        var edad = añoActual - año

        if (mesActual < mes || (mesActual == mes && diaActual < dia)) {
            edad--
        }

        return edad
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Welcome!", fontSize = 40.sp)
        Spacer(modifier = modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "$nombre", fontSize = 32.sp)
            Spacer(modifier = modifier.width(16.dp))

            Button(onClick = {
                nuevoValor = nombre
                showNombreDialog = true
            }) {
                Text(text = "Editar")
            }
        }
        Spacer(modifier = modifier.height(16.dp))

        Text(text = "$correo", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "$fecha", fontSize = 32.sp)
            Spacer(modifier = modifier.width(16.dp))

            Button(onClick = {
                nuevoValor = fecha
                showFechaDialog = true
            }) {
                Text(text = "Editar")
            }
        }
        Spacer(modifier = modifier.height(16.dp))

        Text(text = "$edad", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Button(onClick = {
            Firebase.auth.signOut()

            (context as? Activity)?.finish()
        }) {
            Text(text = "Cerrar Sesión", fontSize = 24.sp)
        }
    }

    if (showNombreDialog) {
        AlertDialog(
            onDismissRequest = { showNombreDialog = false },
            title = { Text("Editar nombre") },
            text = {
                OutlinedTextField(
                    value = nuevoValor,
                    onValueChange = { nuevoValor = it },
                    label = { Text("Nombre") }
                )
            },
            confirmButton = {
                Button(onClick = {

                    myRef.child("name").setValue(nuevoValor)

                    nombre = nuevoValor
                    showNombreDialog = false

                }) {
                    Text("Confirmar")
                }
            }
        )
    }

    if (showFechaDialog) {
        AlertDialog(
            onDismissRequest = { showFechaDialog = false },
            title = { Text("Editar fecha") },
            text = {
                OutlinedTextField(
                    value = nuevoValor,
                    onValueChange = { nuevoValor = it },
                    label = { Text("Fecha (dd/mm/aaaa)") }
                )
            },
            confirmButton = {
                Button(onClick = {

                    val nuevaEdad = calcularEdad(nuevoValor)

                    myRef.child("fecha").setValue(nuevoValor)
                    myRef.child("edad").setValue(nuevaEdad)

                    fecha = nuevoValor
                    edad = nuevaEdad.toString()

                    showFechaDialog = false

                }) {
                    Text("Confirmar")
                }
            }
        )
    }

}