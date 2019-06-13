package jrodr41.cs.uic.edu.gopherhuntingv2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GopherHuntingAdapter extends BaseAdapter {

    private Context mContext;
    private GopherHole[] gopherHoles;

    // constructor
    public GopherHuntingAdapter(Context context) {
        this.mContext = context;
        this.gopherHoles = new GopherHole[100];
        createGopherHoles();
    }

    private void createGopherHoles() {
        for(int i = 0;  i < 100; ++i){
            gopherHoles[i] = new GopherHole(i);
        }
    }

    @Override
    public int getCount() {
        return gopherHoles.length;
    }

    @Override  // TODO: ???
    public long getItemId(int position) {
        return 0;
    }

    @Override  // Return GopherHole at position
    public Object getItem(int position) {
        return this.gopherHoles[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GopherHole gopherHole = this.gopherHoles[position];
        TextView textView = new TextView(mContext);
        textView.setText(String.valueOf(position));
        textView.setBackgroundColor(gopherHole.getColor());
        return textView;
    }
}
