package orduno.regina.login

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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

    myRef.get().addOnSuccessListener { snapshot ->
        nombre = snapshot.child("name").value.toString()
        correo = snapshot.child("correo").value.toString()
        fecha = snapshot.child("fecha").value.toString()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Welcome!", fontSize = 40.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$nombre", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$correo", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$fecha", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$edad", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Button(onClick = {
            Firebase.auth.signOut()

            (context as? Activity)?.finish()
        }) {
            Text(text = "Cerrar Sesión")
        }
    }

}