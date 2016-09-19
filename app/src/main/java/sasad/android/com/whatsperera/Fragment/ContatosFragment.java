package sasad.android.com.whatsperera.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sasad.android.com.whatsperera.R;
import sasad.android.com.whatsperera.helper.Preferencias;
import sasad.android.com.whatsperera.model.Contato;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> contatos;


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Instanciar objetos
        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //Montar listview e adapter
        listView = (ListView) view.findViewById(R.id.lv_contatos);
        adapter = new ArrayAdapter(
                getActivity(),
                R.layout.lista_contatos,
                contatos
        );

        listView.setAdapter(adapter);

        //Recuperar contatos do firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String idenficadorUsuarioLogado = preferencias.getIdenficiador();


        DatabaseReference databaseReferencia = FirebaseDatabase.getInstance().getReference();
        DatabaseReference contatosReferencia = databaseReferencia.child("contatos")
                                                 .child(idenficadorUsuarioLogado);

        //listener para recuperar contatos
        contatosReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Listar contatos para usuario

                contatos.clear();
                for (DataSnapshot dados:dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato.getNome());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
