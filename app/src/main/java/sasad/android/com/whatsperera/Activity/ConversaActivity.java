package sasad.android.com.whatsperera.Activity;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.helper.Base64Custom;
import sasad.android.com.whatsperera.helper.Preferencias;
import sasad.android.com.whatsperera.model.Mensagem;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btMensagem;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> mensagens;
    private ValueEventListener valueEventListenerMensagem;

    private String nomeDestinatario;
    private String idUsuarioDestinatario;
    private String idUsuarioLogado;


    private FirebaseDatabase firebaseDatabase;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_conversa, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btMensagem = (ImageButton) findViewById(R.id.bt_enviar);
        listView = (ListView) findViewById(R.id.lv_mensagens);

        //Recuperar dados do usuario logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioLogado = preferencias.getIdenficiador();

        //Recuperar dados enviados da intent
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            //Recuperar dados do usuario
            nomeDestinatario = extra.getString("nome");
            idUsuarioDestinatario = Base64Custom.converterBase64(extra.getString("email"));

        }


        //configurar a Toolbar
        toolbar.setTitle(nomeDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        /**  Montagem listView e adapter **/
        mensagens = new ArrayList<>();
        adapter = new ArrayAdapter<String>(
                ConversaActivity.this,
                android.R.layout.simple_list_item_1,
                mensagens
        );
        listView.setAdapter(adapter);

        /** Recuperar mensagens do firebase **/
        DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mensagemReferencia = databaseReferencia.child("mensagem");
        mensagemReferencia.child(idUsuarioLogado).child(idUsuarioDestinatario);

        //Criar listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mensagens.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    //recuperar mensagem individual
                    Mensagem mensagem = dados.getValue(Mensagem.class);

                    //Adicinar a lista mensagem
                    mensagens.add(mensagem.getMensagem());

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mensagemReferencia.addValueEventListener(valueEventListenerMensagem);

        //Enviar mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                //Testar se o texto esta preenchido
                if (textoMensagem.isEmpty()) {
                    Toast.makeText(ConversaActivity.this,"Digite uma mensagem",Toast.LENGTH_SHORT).show();
                } else {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioLogado);
                    mensagem.setMensagem(textoMensagem);

                    salvarMensagemFirebase(idUsuarioLogado, idUsuarioDestinatario, mensagem);
                    editMensagem.setText("");
                }
            }
        });
    }

    private boolean salvarMensagemFirebase(String idRemetente, String idDestinario, Mensagem mensagem) {
        try {
            DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mensagemReferencia = databaseReferencia.child("mensagem");
            mensagemReferencia.child(idRemetente)
                    .child(idDestinario)
                    .push()
                    .setValue(mensagem);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
