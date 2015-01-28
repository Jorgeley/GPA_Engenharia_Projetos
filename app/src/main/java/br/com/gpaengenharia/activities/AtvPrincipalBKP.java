package br.com.gpaengenharia.activities;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.gpaengenharia.R;

public class AtvPrincipalBKP extends ExpandableListActivity {
    public SimpleExpandableListAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_principal);
        /*try{
            adaptador = new SimpleExpandableListAdapter(
                    this,
                    createGroupList(),              // Creating group List.
                    android.R.layout.simple_expandable_list_item_1,             // Group item layout XML.
                    new String[]{"Projeto"},  // the key of group item.
                    new int[]{R.id.grupo},    // ID of each group item.-Data under the key goes into this TextView.
                    createChildList(),              // childData describes second-level entries.
                    android.R.layout.simple_expandable_list_item_1,             // Layout for sub-level entries(second level).
                    new String[]{"Projeto"},      // Keys in childData maps to display.
                    new int[]{R.id.item}     // Data under the keys above go into these TextViews.
            );
            setListAdapter(adaptador);       // setting the adaptador in the list.
        } catch (Exception e) {
            System.out.println("Errrr +++ " + e.getMessage());
        }*/
    }

    /* Cria HashMap dos projetos */
    private List createGroupList() {
        ArrayList result = new ArrayList();
        for (int i = 0; i < 10; ++i) {
            HashMap m = new HashMap();
            m.put("Projeto", "Projeto " + i);
            result.add(m);
        }
        return result;
    }

    /* Cria HashMap das tarefas */
    private List createChildList() {
        ArrayList result = new ArrayList();
        for (int i = 0; i < 10; ++i) {
            ArrayList secList = new ArrayList();
            for (int n = 0; n < 3; n++) {
                HashMap child = new HashMap();
                child.put("Projeto", "Projeto " + n);
                secList.add(child);
            }
            result.add(secList);
        }
        return result;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        Toast.makeText(this, (String) ((Map<String, String>)
                        adaptador.getChild(groupPosition, childPosition)).get("Projeto")+" selecionada...",
                Toast.LENGTH_LONG).show();
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.atv_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
