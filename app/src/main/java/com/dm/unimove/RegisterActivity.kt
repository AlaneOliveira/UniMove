package com.dm.unimove

import android.app.Activity
import android.content.Context
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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
    var rememberMe by rememberSaveable { mutableStateOf(false) }
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
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 38.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
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
                HorizontalDivider(
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
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "IconeCadeado"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.offset(x = (-8).dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it }
                    )
                    Text("Lembre-se de mim", style = MaterialTheme.typography.labelMedium)
                }

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
                                    val sharedPref = activity.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                    if (rememberMe) {
                                        with(sharedPref.edit()) {
                                            putBoolean("remember_me", true)
                                            apply()
                                        }
                                    } else {
                                        with(sharedPref.edit()) {
                                            putBoolean("remember_me", false)
                                            apply()
                                        }
                                    }

                                    Toast.makeText(activity, "Cadastro feito com sucesso!", Toast.LENGTH_LONG).show()
                                    val intent =
                                        Intent(activity, MainActivity::class.java).apply {
                                            // Limpa a pilha de navegação para que o usuário não volte para a tela de registro
                                            flags =
                                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        }
                                    activity.startActivity(intent)
                                } else {
                                    Toast.makeText(activity, "Cadastro falhou! ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    },
                    enabled = email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && confirmpassword.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("CADASTRE-SE", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }

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
