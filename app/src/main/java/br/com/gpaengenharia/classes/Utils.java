package br.com.gpaengenharia.classes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by root on 23/01/15.
 */
public class Utils {
    private Context contexto;

    public Utils(Context contexto){
        this.contexto = contexto;
    }

    /**
     * Mostra/Oculta a barra de progresso
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void barraProgresso(final ProgressBar barraProgresso, final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = contexto.getResources().getInteger(android.R.integer.config_shortAnimTime);

            barraProgresso.setVisibility(show ? View.VISIBLE : View.GONE);
            barraProgresso.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    barraProgresso.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            barraProgresso.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
