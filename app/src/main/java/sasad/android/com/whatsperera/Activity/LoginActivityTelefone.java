package sasad.android.com.whatsperera.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.Random;

import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.helper.Permissao;
import sasad.android.com.whatsperera.helper.Preferencias;

public class LoginActivityTelefone extends AppCompatActivity {

    private EditText telefone;
    private EditText codigo_area;
    private EditText codigo_pais;
    private EditText nome;
    private Button cadastrar;

    private String[] permissoesNecessarias = new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.INTERNET
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_telefone);

        Permissao.validaPermissoes(1,this,permissoesNecessarias);

        cadastrar = (Button) findViewById(R.id.btCadastrar);
        telefone = (EditText) findViewById(R.id.edit_Telefone);
        codigo_area = (EditText) findViewById(R.id.edit_cod_area);
        codigo_pais = (EditText) findViewById(R.id.edit_cod_pais);
        nome = (EditText) findViewById(R.id.edit_nome);

        //Máscaras
        SimpleMaskFormatter simpleMaskCodPais = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter simpleMaskCodArea = new SimpleMaskFormatter("NN");
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");

        MaskTextWatcher maskTextTelefone = new MaskTextWatcher(telefone, simpleMaskTelefone);
        MaskTextWatcher maskTextCodPais = new MaskTextWatcher(codigo_pais, simpleMaskCodPais);
        MaskTextWatcher maskTextCodArea = new MaskTextWatcher(codigo_area, simpleMaskCodArea);

        telefone.addTextChangedListener(maskTextTelefone);
        codigo_pais.addTextChangedListener(maskTextCodPais);
        codigo_area.addTextChangedListener(maskTextCodArea);


        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto =
                        codigo_pais.getText().toString() +
                                codigo_area.getText().toString() +
                                telefone.getText().toString();
                String telefoneSemFormatacao = telefoneCompleto.replace("+", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("-", "");

                //Gerar Token
                Random randomico = new Random();
                int numeroRandomico = randomico.nextInt(9999 - 1000) + 1000;
                String token = String.valueOf(numeroRandomico);
                String mensagemEnvio = "WhatsPerera Código de validação: " + token;

                //Salvar dados para validação
                Preferencias preferencias = new Preferencias(LoginActivityTelefone.this);
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormatacao, token);

                //envio de SMS
                boolean enviadoSms = envioSMS("+" + telefoneSemFormatacao, mensagemEnvio);

                if (enviadoSms){

                    Intent intent = new Intent(LoginActivityTelefone.this,ValidadorActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(LoginActivityTelefone.this,"Problema ao enviar SMS, tente novamente!",Toast.LENGTH_LONG).show();
                }


                //HashMap<String,String> usuario = preferencias.getDadosUsuario();
                //Log.i("TOKEN","T: " + usuario.get("token") + " U: " + usuario.get("telefone"));
            }
        });

        //Muda para o proximo editText depois que preencher o primeiro
        codigo_pais.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (codigo_pais.getText().toString().length() == 3) {
                    codigo_area.requestFocus();
                }
            }
        });


        codigo_area.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (codigo_area.getText().toString().length() == 2) {
                    telefone.requestFocus();
                }
            }
        });


        telefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (telefone.getText().toString().length() == 9) {
                    cadastrar.requestFocus();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //Envio do SMS
    private boolean envioSMS(String telefone, String mensagem) {
        try {

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grandResults){
        super.onRequestPermissionsResult(requestCode,permissions,grandResults);
        for (int resultado : grandResults){

            if (resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setTitle("Permissões Negadas");
        build.setMessage("Para utilizar o Perera é necessário aceitar as permissões");
        build.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = build.create();
        dialog.show();
    }


}
