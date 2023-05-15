package com.everton.projetofirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class TelaPrincipal extends AppCompatActivity {

    private TextView nomeUsuario, emailUsuario;
    private Button btDeslogar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        getSupportActionBar().hide();
        IniciarComponentes();

        btDeslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //aqui eu deslogo
                FirebaseAuth.getInstance().signOut();
                //aqui eu mando o usuario para a tela de login
                Intent intent = new Intent(TelaPrincipal.this,FormLogin.class);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //a string ta recebendo o FirebaseAuth, a instancia do servidor e pegando o Usuario atual logado na sessão e seu email
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //a string ta recebendo o FirebaseAuth, a instancia do servidor e pegando o Usuario atual logado na sessão e seu ID
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuario").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot!= null){
                nomeUsuario.setText(documentSnapshot.getString("nome"));
                emailUsuario.setText(email);

                }

            }
        });
    }

    private void IniciarComponentes(){

        nomeUsuario = findViewById(R.id.textNomeUsuario);
        emailUsuario = findViewById(R.id.textEmailUsuario);
        btDeslogar = findViewById(R.id.bt_deslogar);

    }

}