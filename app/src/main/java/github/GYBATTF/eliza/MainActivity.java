package github.GYBATTF.eliza;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

/**
 * Main activity of app
 */
public class MainActivity extends AppCompatActivity {
    private Adapter a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        a = new Adapter(this);
        a.setTalker(Adapter.Bots.ELIZA);

        //BotSelectDialog.show(a, getSupportFragmentManager());
    }

    /**
     * Send the input from the text input to the bot
     * @param v
     * Required parameter in order to use this method in the xml
     */
    public void send(View v) {
        a.send();
    }

    /**
     * DialogFragment for the options menu
     * @param v
     * Required parameter in order to use this method in the xml
     */
    public void openChoiceDialog(View v) {
        ChoiceDialog.show(a, getSupportFragmentManager());
    }

    /**
     * DialogFragment for the options menu
     */
    public static class ChoiceDialog extends DialogFragment {
        Adapter a;

        ChoiceDialog(Adapter a) {
            this.a = a;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setCancelable(false);
            Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View view = inflater.inflate(R.layout.options_menu, container);
            view.findViewById(R.id.reset_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    a.setTalker(a.getTalker());
                }
            });
            /*view.findViewById(R.id.choose_bot).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    BotSelectDialog.show(a, getFragmentManager());
                }
            });*/
            view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            return view;
        }

        static void show(Adapter a, FragmentManager fm) {
            new ChoiceDialog(a).show(fm, "");
        }
    }

    /*public static class BotSelectDialog extends DialogFragment {
        Adapter a;

        BotSelectDialog(Adapter a) {
            this.a = a;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setCancelable(false);
            Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            View view = inflater.inflate(R.layout.bot_choice, container);
            view.findViewById(R.id.eliza_choice).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    a.setTalker(Adapter.Bots.ELIZA);
                }
            });
            view.findViewById(R.id.deborah_choice).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    a.setTalker(Adapter.Bots.DEBORAH);
                }
            });
            if (a.getTalker() == null) {
                view.findViewById(R.id.back_button).setVisibility(View.GONE);
            }
            view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChoiceDialog.show(a, getFragmentManager());
                    dismiss();
                }
            });
            return view;
        }

        static void show(Adapter a, FragmentManager fm) {
            new BotSelectDialog(a).show(fm, "");
        }
    }*/
}