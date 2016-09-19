package sasad.android.com.whatsperera.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.helper.Preferencias;

public class ValidadorActivity extends AppCompatActivity {

    private EditText codigo;
    private Button botaotValidar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigo = (EditText) findViewById(R.id.edit_cod_validacao);
        botaotValidar = (Button) findViewById(R.id.btValidar);

        SimpleMaskFormatter simpleMaskCodigo = new SimpleMaskFormatter("NNNN");
        MaskTextWatcher maskTextCodigo = new MaskTextWatcher(codigo, simpleMaskCodigo);
        codigo.addTextChangedListener(maskTextCodigo);

        botaotValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Recuperar preferencias do usuario
                Preferencias preferencias = new Preferencias(ValidadorActivity.this);
                HashMap<String, String> usuario = preferencias.getDadosUsuario();

                String tokenGerado = usuario.get("token");
                String tokenDigitado = codigo.getText().toString();

                if (tokenDigitado.equals(tokenGerado)) {
                    Toast.makeText(ValidadorActivity.this,"Código Validado",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ValidadorActivity.this,"Código Inválido",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
