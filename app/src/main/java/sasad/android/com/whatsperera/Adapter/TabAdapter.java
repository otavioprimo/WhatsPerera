package sasad.android.com.whatsperera.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import sasad.android.com.whatsperera.Fragment.ContatosFragment;
import sasad.android.com.whatsperera.Fragment.ConversasFragment;

/**
 * Created by Usuário on 18/09/2016.
 */
public class TabAdapter extends FragmentStatePagerAdapter{

    private String[] tituloAbas = {"CONVERSAS","CONTATOS"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new ConversasFragment();
                break;
            case 1:
                fragment = new ContatosFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tituloAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tituloAbas[position];
    }
}
