package sasad.android.com.whatsperera.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;

import sasad.android.com.whatsperera.Adapter.TabAdapter;
import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.helper.Base64Custom;
import sasad.android.com.whatsperera.helper.Preferencias;
import sasad.android.com.whatsperera.helper.SlidingTabLayout;
import sasad.android.com.whatsperera.model.Contato;
import sasad.android.com.whatsperera.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private static long back_pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsPerera");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        //configurar sliding tab
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent2));

        //configurar o adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);

        Log.i("logado", "Email: " + firebaseAuth.getCurrentUser().getEmail().toString() + "\nUID: " + firebaseAuth.getCurrentUser().getUid().toString());

        SuperActivityToast.create(this, new Style(), Style.TYPE_PROGRESS_CIRCLE)
                .setProgressBarColor(Color.WHITE)
                .setText("CARREGANDO")
                .setDuration(Style.DURATION_LONG)
                .setFrame(Style.FRAME_LOLLIPOP)
                .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                .setAnimations(Style.ANIMATIONS_POP).show();


    }

    //cria o menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    //ação pra item dos menus
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_sair:
                dialogDeslogar("Deslogar da sessão atual", "Deseja deslogar desta sessão?");
                return true;
            case R.id.item_adicionar:
                abrirCadastroContato();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deslogarUsuario() {
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginEmailActivity.class);
        startActivity(intent);
        if (firebaseAuth.getCurrentUser() != null) {
            Log.i("logadoMain", "usuario logado");
        } else {
            Log.i("logadoMain", "usuario deslogado");
        }
    }

    private void dialogDeslogar(String titulo, String mensagem) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

        dialog.setTitle(titulo);
        dialog.setMessage(mensagem);
        dialog.setCancelable(false);
        //dialog.setIcon(R.drawable.ic_exit_to_app);

        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SuperActivityToast.create(MainActivity.this, new Style(), Style.TYPE_STANDARD)
                        .setProgressBarColor(Color.WHITE)
                        .setText("DESLOGADO")
                        .setDuration(Style.DURATION_VERY_SHORT)
                        .setFrame(Style.FRAME_LOLLIPOP)
                        .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_GREEN))
                        .setAnimations(Style.ANIMATIONS_SCALE).show();
                deslogarUsuario();
            }
        });
        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create();
        dialog.show();
    }

    private void abrirCadastroContato() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

        dialog.setTitle("Novo Contato");
        dialog.setMessage("Email do usuário:");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.ic_person_add);

        final EditText editText = new EditText(this);
        editText.setSingleLine(true);
        editText.setInputType(10);
        dialog.setView(editText);

        dialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String emailContato = editText.getText().toString();
                if (emailContato.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Preencha o campo e-mail", Toast.LENGTH_SHORT).show();
                } else {
                    //verifica se o usuario ja esta cadastrado
                    final String identificadorContato = Base64Custom.converterBase64(emailContato);

                    //recuperar a instancia do firebase
                    DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference usuarioReferencia = databaseReferencia.child("usuarios").child(identificadorContato);

                    //fazer uma consulta unica
                    usuarioReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                //Recupera dados do contato a ser adicionado
                                Usuario usuarioContato = new Usuario();
                                usuarioContato = dataSnapshot.getValue(Usuario.class);


                                //Recuperar dados do usuario logado
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado = preferencias.getIdenficiador();

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identificadorContato);
                                contato.setNome(usuarioContato.getNome());
                                contato.setEmail(usuarioContato.getEmail());

                                //Salvar dados no firebase
                                DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference contatoReferencia = databaseReferencia.child("contatos")
                                                                        .child(identificadorUsuarioLogado)
                                                                        .child(identificadorContato);
                                contatoReferencia.setValue(contato);


                            } else {
                                Toast.makeText(MainActivity.this, "Usuário não possui cadastro", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(),"Toque novamente para sair",Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
