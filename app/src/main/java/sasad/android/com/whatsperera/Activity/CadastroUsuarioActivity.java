package sasad.android.com.whatsperera.Activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
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
                    chamarToastCampos("Preencha o campo Nome");
                } else if (email.getText().toString().isEmpty()) {
                    chamarToastCampos("Preencha o campo Email");
                } else if (senha.getText().toString().isEmpty()) {
                    chamarToastCampos("Preencha o campo Senha");
                } else {
                    usuario = new Usuario();
                    usuario.setNome(nome.getText().toString().trim());
                    usuario.setEmail(email.getText().toString().trim());
                    usuario.setSenha(senha.getText().toString().trim());
                    chamarToastCadastro();
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
                    SuperToast.create(CadastroUsuarioActivity.this, "CADASTRADO COM SUCESSO", Style.DURATION_SHORT,Style.green()).show();
                    finish();
                } else {
                    //Toast.makeText(CadastroUsuarioActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    SuperToast.create(CadastroUsuarioActivity.this, task.getException().getMessage(), Style.DURATION_SHORT,Style.green()).show();
                }
            }
        });
    }

    private void chamarToastCadastro(){
        SuperActivityToast.create(this, new Style(), Style.TYPE_PROGRESS_BAR)
                .setProgressBarColor(Color.WHITE)
                .setText("VERIFICANDO CADASTRO")
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                .setAnimations(Style.ANIMATIONS_POP).show();
    }

    private void chamarToastCampos(String texto){
        SuperActivityToast.create(this, new Style(), Style.TYPE_STANDARD)
                .setText(texto)
                .setDuration(Style.DURATION_VERY_SHORT)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                .setAnimations(Style.ANIMATIONS_FADE).show();
    }


}
