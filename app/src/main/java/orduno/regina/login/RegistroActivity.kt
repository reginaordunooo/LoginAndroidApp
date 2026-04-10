package orduno.regina.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import orduno.regina.login.ui.theme.LoginTheme
import java.util.Calendar

class RegistroActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth;

    private lateinit var database: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Registrarse(
                        auth,
                        database,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        auth = Firebase.auth
        database = Firebase.database.reference
    }
}

@Composable
fun Registrarse(auth:FirebaseAuth, database: DatabaseReference, modifier: Modifier = Modifier) {

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contra by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    var nombreError by rememberSaveable { mutableStateOf(false) }
    var correoError by rememberSaveable { mutableStateOf(false) }
    var contraError by rememberSaveable { mutableStateOf(false) }
    var confirmarError by rememberSaveable { mutableStateOf(false) }
    var fechaError by rememberSaveable { mutableStateOf(false) }


    val context = LocalContext.current

    fun edadValida(fecha: String):Boolean {
        val partes = fecha.split("/")

        if (partes.size != 3) return false

        val añoNacimiento = partes[2].toIntOrNull() ?: return false

        val añoActual = Calendar.getInstance().get(Calendar.YEAR)

        val edad = añoActual - añoNacimiento

        return edad >= 18
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text =  "Registro", fontSize =  24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                nombreError = it.isEmpty() },
            label = { Text(text = "Nombre completo")},
            isError = nombreError,
            supportingText = {
                if (nombreError) Text("Campo requerido")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                correoError = it.isEmpty() },
            label = { Text(text = "Correo electrónico")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = correoError,
            supportingText = {
                if (correoError) {
                    Text("Campo requerido")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contra,
            onValueChange = {
                contra = it
                contraError = it.isEmpty() },
            label = { Text(text = "Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = contraError,
            supportingText = {
                if (contraError) Text("Campo requerido")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmar,
            onValueChange = {
                confirmar = it
                confirmarError = it.isEmpty() },
            label = { Text(text = "Confirmar contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = confirmarError,
            supportingText = {
                if (confirmarError) {
                    if (confirmar.isEmpty()) {
                        Text("Campo requerido")
                    } else {
                        Text("Las contraseñas no coinciden")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fecha,
            onValueChange = {
                fecha = it
                fechaError = it.isEmpty() },
            label = { Text(text = "Fecha de nacimiento (dd/mm/aaaa)")},
            isError = fechaError,
            supportingText = {
                if (fechaError) Text("Campo requerido")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            nombreError = nombre.isEmpty()
            correoError = correo.isEmpty()
            contraError = contra.isEmpty()
            confirmarError = confirmar.isEmpty() || contra != confirmar
            fechaError = fecha.isEmpty()

            if (nombreError || correoError || contraError || confirmarError || fechaError ){
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
            }
            if (!fechaError && !edadValida(fecha)) {
                Toast.makeText(context, "No se permiten menores de edad", Toast.LENGTH_SHORT).show()
            }

            if (!nombreError && !correoError && !contraError && !confirmarError && !fechaError) {
                auth.createUserWithEmailAndPassword(correo, contra)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(context, "Se creo con exito el usuario", Toast.LENGTH_SHORT).show()
                            var userID = auth.currentUser?.uid ?:"anonimo"
                            var usuario = Usuario(nombre, correo, fecha)

                            database.child("usuarios").child(userID).setValue(usuario)

                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra("nombre", nombre)
                            intent.putExtra("contra", contra)
                            context.startActivity(intent)
                        } else{
                            Toast.makeText(context, "Usuario no creado", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else{
                Toast.makeText(context, "No dejar campos vacíos", Toast.LENGTH_SHORT).show()
            }

        }) { Text(text = "Registrarse")}

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick ={
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Volver al inicio")
        }
    }

}

