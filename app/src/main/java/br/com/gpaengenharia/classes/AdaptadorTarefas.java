package br.com.gpaengenharia.classes;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.CaptioningManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import br.com.gpaengenharia.R;
import br.com.gpaengenharia.beans.Projeto;
import br.com.gpaengenharia.beans.Tarefa;

/**
Adaptador do listView expansível
 */
public class AdaptadorTarefas extends BaseExpandableListAdapter {
    private Context contexto;
    private TreeMap<Tarefa, List<Projeto>> tarefasTreeMap;
    private Object[] tarefasArray;
    private Tarefa tarefa;
    //private List<Tarefa> tarefasProjetos;

    public AdaptadorTarefas(Context contexto, TreeMap<Tarefa, List<Projeto>> tarefasTreeMap) {
        this.contexto = contexto;
        this.tarefasTreeMap = tarefasTreeMap;
        /**
         *  keyset:pega as chaves do TreeMap(no caso objetos Projeto),
         *  transforma em array e pega na posiçao correta
         */
        this.tarefasArray = this.tarefasTreeMap.keySet().toArray();
        //this.tarefasProjetos = tarefasProjetos;
    }

    @Override
    public int getGroupCount() {
        return this.tarefasTreeMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        this.tarefa = (Tarefa) tarefasArray[groupPosition];
        /*Log.i("getChildrenCount",this.tarefa.getNome());
        Log.i("getChildrenCount", String.valueOf(this.tarefasTreeMap.get(this.tarefa)));*/
        return this.tarefasTreeMap.get(this.tarefa).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        this.tarefa = (Tarefa) tarefasArray[groupPosition];
        return this.tarefa;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        this.tarefa = (Tarefa) tarefasArray[groupPosition];
        return this.tarefasTreeMap.get(this.tarefa).get(childPosition);
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
        Projeto projeto = (Projeto) getChild(groupPosition, 0);
        Tarefa tarefa = (Tarefa) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) this.contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parent_layout, parent, false);
        }
        TextView parentTextView = (TextView) convertView.findViewById(R.id.textViewParent);
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        String data = formatoData.format(tarefa.getVencimento());//seta data
        Spanned projetoString = Html.fromHtml(tarefa.getNome()+"<br><small><font color='gray'>" + projeto.getNome()+" ["+data+"]</font></small>");
        parentTextView.setText(projetoString);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) this.contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_layout, parent, false);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}