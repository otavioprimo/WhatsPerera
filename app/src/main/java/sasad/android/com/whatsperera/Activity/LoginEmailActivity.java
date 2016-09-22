package sasad.android.com.whatsperera.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.helper.Base64Custom;
import sasad.android.com.whatsperera.helper.Preferencias;
import sasad.android.com.whatsperera.model.Usuario;

public class LoginEmailActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button btLogar;
    private Usuario usuario;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        firebaseAuth = FirebaseAuth.getInstance();

        verificarUsuarioLogado();

        email = (EditText) findViewById(R.id.editLoginEmail);
        senha = (EditText) findViewById(R.id.editLoginSenha);
        btLogar = (Button) findViewById(R.id.btnLogar);


        btLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || senha.getText().toString().isEmpty()) {
                    SuperActivityToast.create(LoginEmailActivity.this, new Style(), Style.TYPE_STANDARD)
                            .setProgressBarColor(Color.WHITE)
                            .setText("Preencha os campos Email e Senha")
                            .setDuration(Style.DURATION_MEDIUM)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                            .setAnimations(Style.ANIMATIONS_SCALE).show();
                } else {
                    usuario = new Usuario();
                    usuario.setEmail(email.getText().toString());
                    usuario.setSenha(senha.getText().toString());
                    validarLogin();
                }
            }
        });
    }

    public void abrirCadastroUsuario(View view) {
        Intent intent = new Intent(LoginEmailActivity.this, CadastroUsuarioActivity.class);
        startActivity(intent);
        email.setText("");
        senha.setText("");
    }

    private void validarLogin() {
        firebaseAuth.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(LoginEmailActivity.this, "Logado com sucesso", Toast.LENGTH_SHORT).show();

                    String identificadorUsuarioLogado = Base64Custom.converterBase64(usuario.getEmail());

                    Preferencias preferencias = new Preferencias(LoginEmailActivity.this);
                    preferencias.salvarDados(identificadorUsuarioLogado);

                    abrirTelaPrincipal();
                } else {
                    //Toast.makeText(LoginEmailActivity.this, "Email ou senha inválidos!", Toast.LENGTH_SHORT).show();
                    SuperActivityToast.create(LoginEmailActivity.this, new Style(), Style.TYPE_STANDARD)
                            .setProgressBarColor(Color.WHITE)
                            .setText("Email ou senha inválidos!")
                            .setDuration(Style.DURATION_VERY_SHORT)
                            .setFrame(Style.FRAME_LOLLIPOP)
                            .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                            .setAnimations(Style.ANIMATIONS_SCALE).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal() {
        Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void verificarUsuarioLogado() {
        if (firebaseAuth.getCurrentUser() != null) {
            abrirTelaPrincipal();
            Log.i("logado", "Usuario logado " + firebaseAuth.getCurrentUser().toString());
        } else {
            Log.i("logado", "usuario deslogado");
        }
    }
}
