package br.com.gpaengenharia.classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.List;
import java.util.TreeMap;
import br.com.gpaengenharia.R;

/**
Adaptador do listView expansível
 */
public class Adaptador extends BaseExpandableListAdapter {
    private Context contexto;
    private TreeMap<String, List<String>> projetosTreeMap;
    private List<String> tarefasProjetos;

    public Adaptador(Context contexto,TreeMap<String, List<String>> projetosTreeMap, List<String> tarefasProjetos) {
        this.projetosTreeMap = projetosTreeMap;
        this.contexto = contexto;
        this.projetosTreeMap = projetosTreeMap;
        this.tarefasProjetos = tarefasProjetos;
    }

    @Override
    public int getGroupCount() {
        return this.projetosTreeMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.projetosTreeMap.get(this.tarefasProjetos.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.tarefasProjetos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.projetosTreeMap.get(this.tarefasProjetos.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        String projetoTitulo = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) this.contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent_layout, parent, false);
        }
        TextView parentTextView = (TextView) convertView.findViewById(R.id.textViewParent);
        parentTextView.setText(projetoTitulo);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Log.i("test", "parent view: " + parent.getTag());

        String tarefaTitulo = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) this.contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_layout, parent, false);
        }
        TextView childTextView = (TextView) convertView.findViewById(R.id.textViewChild);
        childTextView.setText(tarefaTitulo);
        //convertView.setVisibility(View.INVISIBLE);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}