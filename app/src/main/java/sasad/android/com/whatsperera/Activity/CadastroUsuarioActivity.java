package sasad.android.com.whatsperera.Activity;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.*;

import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.helper.Base64Custom;
import sasad.android.com.whatsperera.model.Usuario;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button botaoCadastrar;
    private Usuario usuario;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        nome = (EditText) findViewById(R.id.editCadastroNome);
        email = (EditText) findViewById(R.id.editCadastroEmail);
        senha = (EditText) findViewById(R.id.editCadastroSenha);

        botaoCadastrar = (Button) findViewById(R.id.btCadastrarUsuario);


        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nome.getText().toString().isEmpty()) {
                    Toast.makeText(CadastroUsuarioActivity.this, "Preencha o campo Nome", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(CadastroUsuarioActivity.this, "Preencha o campo Email", Toast.LENGTH_SHORT).show();
                } else if (senha.getText().toString().isEmpty()) {
                    Toast.makeText(CadastroUsuarioActivity.this, "Preencha o campo Senha", Toast.LENGTH_SHORT).show();
                } else {
                    usuario = new Usuario();
                    usuario.setNome(nome.getText().toString().trim());
                    usuario.setEmail(email.getText().toString().trim());
                    usuario.setSenha(senha.getText().toString().trim());

                    cadastrarUsuario();
                }
            }
        });
    }

    private void cadastrarUsuario() {

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String identificador = Base64Custom.converterBase64(usuario.getEmail());

                    usuario.setId(identificador);
                    usuario.salvar();
                    Toast.makeText(CadastroUsuarioActivity.this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CadastroUsuarioActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
