package br.com.gpaengenharia.classes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Utilidades para o projeto
 */
public class Utils{
    public static Context contexto;

    public Utils(Context contexto){
        this.contexto = contexto;
    }

    /** Método sobrecarregado para mostrar/ocultar a barra de progresso
     * @param barraProgresso
     * @param mostra
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void barraProgresso(final ProgressBar barraProgresso, final boolean mostra) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int animacaoCurta = contexto.getResources().getInteger(android.R.integer.config_shortAnimTime);
            barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
            barraProgresso.animate().setDuration(animacaoCurta).alpha(
                    mostra ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
        }
    }

    /** Método sobrecarregado para mostrar/ocultar a barra de progresso
     * @param contexto
     * @param barraProgresso
     * @param mostra
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void barraProgresso(Context contexto, final ProgressBar barraProgresso, final boolean mostra) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int animacaoCurta = contexto.getResources().getInteger(android.R.integer.config_shortAnimTime);
            barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
            barraProgresso.animate().setDuration(animacaoCurta).alpha(
                    mostra ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            barraProgresso.setVisibility(mostra ? View.VISIBLE : View.GONE);
        }
    }

    /**método sobrecarregado para criar adaptador para Spinner
     * @param lista Array de strings
     * @return  adaptador de String para Spinner
     */
    public ArrayAdapter setAdaptador(String[] lista){
        ArrayAdapter adaptador = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, lista){
            @Override
            public boolean isEnabled(int posicao){
                return (posicao == 0) ? false : true;
            }
        };
        return adaptador;
    }

    /**método sobrecarregado para criar adaptador para Spinner
     * @param contexto
     * @param lista Array de strings
     * @return  adaptador de String para Spinner
     */
    public static ArrayAdapter setAdaptador(Context contexto, String[] lista){
        ArrayAdapter adaptador = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, lista){
            @Override
            public boolean isEnabled(int posicao){
                return (posicao == 0) ? false : true;
            }
        };
        return adaptador;
    }

    /** Cria cx de diálogo para DatePicker
     * implemente a interface para pegar a data escolhida
     */
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        Listener listener;

        public interface Listener{
            /** pega a data escolhida
             * @return data */
            public void getData(String data);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //pega data atual
            final Calendar calendario = Calendar.getInstance();
            int dia = calendario.get(Calendar.DAY_OF_MONTH);
            int mes = calendario.get(Calendar.MONTH);
            int ano = calendario.get(Calendar.YEAR);
            listener = (Listener) getActivity();
            return new DatePickerDialog(getActivity(), this, ano, mes, dia);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            //pega data escolhida no DatePicker
            Calendar calendario = Calendar.getInstance();
            calendario.set(year, month, day);
            //formata a data
            SimpleDateFormat dataFormatada = new SimpleDateFormat("dd/MM/yyyy");
            String data = dataFormatada.format(calendario.getTime());
            if (listener != null)
                listener.getData(data);
        }
    }
}