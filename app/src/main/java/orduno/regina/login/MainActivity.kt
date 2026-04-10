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
import com.google.firebase.database.database
import orduno.regina.login.ui.theme.LoginTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var nombre = intent.getStringExtra("nombre") ?: ""
        var contra = intent.getStringExtra("contra") ?: ""
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaInicio(
                        auth,
                        name = nombre,
                        pass = contra,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        //auth = Firebase.auth
        //val database = Firebase.database
        //val myRef = database.getReference("message")

        //myRef.setValue("Hello, World!")

        if(auth.currentUser != null){
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
        }
    }
}

@Composable
fun PantallaInicio(auth: FirebaseAuth, name: String, pass:String, modifier: Modifier = Modifier) {

    var correo by remember { mutableStateOf("") }
    var contra by remember { mutableStateOf("") }

    var correoError by rememberSaveable { mutableStateOf(false) }
    var contraError by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text =  "Inicio de Sesión", fontSize =  24.sp)

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

        Row(){
            Button(onClick = {
                val intent = Intent(context, RegistroActivity::class.java)
                context.startActivity(intent)
            }) { Text(text = "Registrarse")}

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = {
                correoError = correo.isEmpty()
                contraError = contra.isEmpty()

                if (correoError || contraError ){
                    Toast.makeText(context, "No dejar campos vacíos", Toast.LENGTH_SHORT).show()
                }
                if (contra.isNotEmpty() && correo.isNotEmpty()){
                    auth.signInWithEmailAndPassword(correo, contra)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                val intent = Intent(context, PrincipalActivity::class.java)
                                intent.putExtra("correo", correo)
                                context.startActivity(intent)
                            } else{
                                Toast.makeText(context, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                            }
                        }

                }

            }) { Text(text = "Ingresar")}
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick ={
            val intent = Intent(context, ContrasenaActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Olvidé mi contraseña")
        }

    }

}

