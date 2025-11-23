package com.dm.unimove

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dm.unimove.ui.theme.UnimoveTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnimoveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmpassword by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val activity: Activity = context as Activity

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. Plano de fundo
        Image(
            painter = painterResource(id = R.drawable.fundo_mapa_lilas),
            contentDescription = "Background Map",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // 2. Cartão Branco
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 38.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Título
                Text(
                    text = "Crie sua conta",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Divider(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(vertical = 4.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )

                // Campo Nome
                OutlinedTextField(
                    value = name,
                    label = { Text("Nome") },
                    onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "IconePessoa") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo Email
                OutlinedTextField(
                    value = email,
                    label = { Text("Email") },
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "IconeEmail") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo Senha
                OutlinedTextField(
                    value = password,
                    label = { Text("Senha") },
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "IconeCadeado") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo Confirmar Senha
                OutlinedTextField(
                    value = confirmpassword,
                    label = { Text("Confirmar Senha") },
                    onValueChange = { confirmpassword = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "IconeCadeado"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(3.dp))
                Button(
                    onClick = {
                        if (password != confirmpassword) {
                            Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                            password = ""
                            confirmpassword = ""
                            return@Button
                        }
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(activity,
                                        "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(activity,
                                        "Cadastro falhou! ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    },
                    enabled = email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("CADASTRE-SE", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }

                // Link para Login
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Já tem uma conta?", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = {
                        activity.startActivity(
                            Intent(activity, LoginActivity::class.java)
                        )
                    }) {
                        Text(
                            text = "Faça Login aqui",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                        )
                    }
                }
            }
        }
    }
}
/*


        Row(modifier = Modifier) {
            Button( onClick = {
                Firebase.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity,
                                "Registro OK!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(activity,
                                "Registro FALHOU!", Toast.LENGTH_LONG).show()
                        }
                    }
            },
                enabled = email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && confirmpassword.isNotEmpty() && password == confirmpassword
            ) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(
                onClick = {
                    name = ""
                    email = ""
                    password = ""
                    confirmpassword = ""
                }
            ) {
                Text("Limpar")
            }
        }
    }
}
*/