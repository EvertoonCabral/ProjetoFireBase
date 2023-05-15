package com.everton.projetofirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {


    private EditText edit_nome, edit_email, edit_senha;
    private Button bt_cadastrar;

    String [] mensagens = {"Preencha todos os campos","Cadatro realizado com sucesso"};
    String usuarioID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();

        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = edit_nome.getText().toString();
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if(nome.isEmpty() || email.isEmpty() || senha.isEmpty()){

                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    CadastrarUsuario(view);
                }

            }
        });

    }


    public void CadastrarUsuario(View view){
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        //cadastrando email e senha no fireBase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    //se o cadastro acontecer de maneira correta salvar dados no banco de dados
                    SalvarDadosUsuario();

                    Snackbar snackbar = Snackbar.make(view, mensagens[1], Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }else{
                    String erro;
                    try {
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e) {

                        erro = "A senha deve ter no minimo 6 caracteres";

                    }catch(FirebaseAuthUserCollisionException e){

                        erro = "Email já cadastrado";

                    }catch(FirebaseAuthInvalidCredentialsException e){

                        erro = "Email invalido";

                    }catch (Exception e){

                    erro = "Erro ao cadastrar usuario";
                    }

                    Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }

            }
        });

    }

private void SalvarDadosUsuario(){

     String nome = edit_nome.getText().toString();

    //criando o banco de dados
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Map<String,Object> usuario = new HashMap<>();
    usuario.put("nome",nome);

    //é preciso iniciar o FireBaseAuth para acessar o usuario atual, o metod getCurrentUser().getUid pega o usuario e o ID autal para colocar no Banco de dados
    usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //criando uma coleção de usuario, onde cada usuario criado no banco de dados tera um documento baseado no usuarioID
    DocumentReference documentReference = db.collection("Usuario").document(usuarioID);
    documentReference.set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void unused) {

            //se os dados forem salvos com sucesso:

            Log.d("db","Sucesso ao salvar os dados");

        }
    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //se os dados nao forem salvos imprimir a msg e o erro em forma de String

                    Log.d("db_error","Erro ao salvar os dados"+ e.toString());
                }
            });
}

    public void IniciarComponentes(){
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
    }

}