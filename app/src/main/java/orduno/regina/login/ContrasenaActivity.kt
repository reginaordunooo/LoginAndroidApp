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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import orduno.regina.login.ui.theme.LoginTheme

class ContrasenaActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RecuperarContra(
                        auth,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        auth = Firebase.auth
    }
}

@Composable
fun RecuperarContra(auth:FirebaseAuth, modifier: Modifier = Modifier) {

    var correo by remember { mutableStateOf("") }

    var correoError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text =  "Recuperar contraseña", fontSize =  24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text =  "Se te enviara un enlace de recuperación a tu correo", fontSize =  16.sp)

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
                if (correoError) Text("Campo requerido")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(){
            Button(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }) { Text(text = "Cancelar")}

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                correoError = correo.isEmpty()
                if ( correoError ){
                    Toast.makeText(context, "No dejar campos vacíos", Toast.LENGTH_SHORT).show()
                }
                if (correo.isNotEmpty()) {
                    auth.sendPasswordResetEmail(correo)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(context, "Correo enviado", Toast.LENGTH_SHORT).show()
                            } else{
                                Toast.makeText(context, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }) { Text(text = "Recuperar Contraseña")}
        }

    }

}