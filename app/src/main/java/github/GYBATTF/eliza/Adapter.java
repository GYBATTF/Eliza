package github.GYBATTF.eliza;

import android.content.Context;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Random;

import github.GYBATTF.eliza.eliza.Eliza;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

import static android.view.View.GONE;

/**
 * adapter for RecyclerView
 */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private ArrayList<Message> c;
    private Talker e;
    private RecyclerView r;
    private EditText et;
    private MainActivity ma;
    private Bots talker;


    Adapter(final MainActivity ma) {
        this.ma = ma;
        et = ma.findViewById(R.id.message);
        r = ma.findViewById(R.id.messageThread);
        r.setHasFixedSize(false);
        LinearLayoutManager l = new LinearLayoutManager(ma.getApplicationContext());
        l.setReverseLayout(true);
        r.setLayoutManager(l);
        r.setAdapter(this);
        r.setItemAnimator(new FadeInAnimator());
        r.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                InputMethodManager imm = (InputMethodManager) ma.getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                return false;
            }
        });
        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    return Adapter.this.send();
                }
                return false;
            }
        });
        ma.findViewById(R.id.reset_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Adapter.this.reset();
            }
        });
        reset();
    }

    boolean send() {
        String m = et.getText().toString();
        if (!m.equals("")) {
            if (lastWasEliza()) {
                add(m);
                et.setText("");
                return true;
            }
        }
        return false;
    }

    Adapter.Bots getTalker() {
        return talker;
    }

    void setTalker(Adapter.Bots t) {
        talker = t;
        reset();
    }

    private void add(final String m) {
        r.scrollToPosition(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                c.add(0, new Message(false, m));
                Adapter.this.notifyItemInserted(0);
                try {
                    int sleep = 0;
                    Random rand = new Random();
                    while (sleep < 750 || sleep > 1500) {
                        sleep = rand.nextInt(1500);
                    }
                    Thread.sleep(sleep);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                r.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            c.add(0, new Message(true, e.talk(m)));
                        } catch (Exception e) {
                            c.add(0, new Message(true, "Error!"));
                            StringWriter errors = new StringWriter();
                            e.printStackTrace(new PrintWriter(errors));
                            c.add(0, new Message(true, errors.toString()));
                        }
                        Adapter.this.notifyItemInserted(0);
                        r.scrollToPosition(0);
                        if (e.finished()) {
                            ma.findViewById(R.id.messageBox).setVisibility(GONE);
                            ma.findViewById(R.id.resetBox).setVisibility(View.VISIBLE);
                            InputMethodManager imm = (InputMethodManager) ma.getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert imm != null;
                            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        }
                    }
                });
            }
        }).start();
    }

    private boolean lastWasEliza() {
        return c.get(0).isEliza();
    }

    private void reset() {
        c = new ArrayList<>();
        ma.findViewById(R.id.messageBox).setVisibility(View.VISIBLE);
        ma.findViewById(R.id.resetBox).setVisibility(View.GONE);
        TextView tv = ma.findViewById(R.id.name);
        if (talker == Bots.ELIZA) {
            e = new Eliza(ma.getResources().openRawResource(R.raw.script));
            tv.setText(R.string.eliza);
        } else if (talker == Bots.DEBORAH) {
            /*try {

                e = new Deborah(et, ma);
            } catch (Exception e) {
                c.add(0, new Message(true, "Error creating Deborah!"));
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                c.add(0, new Message(true, errors.toString()));
            }
            tv.setText(R.string.deborah);*/
        }
        if (talker != null) {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            ma.findViewById(R.id.message).requestFocus();
            try {
                c.add(new Message(true, e.getInitial()));
            } catch (Exception e) {
                c.add(0, new Message(true, "Error!"));
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                c.add(0, new Message(true, errors.toString()));
            }
            notifyDataSetChanged();
            ma.findViewById(R.id.menu_opener).setVisibility(View.VISIBLE);
        } else {
            ma.findViewById(R.id.messageBox).setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder vh;
        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_her, parent, false);
            vh = new Adapter.ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_me, parent, false);
            vh = new Adapter.ViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.set(c.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return c.get(position).isEliza() ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return c.size();
    }

    /**
     * View holder for an item in the chat thread
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void set(Message m) {
            final TextView tv = itemView.findViewById(R.id.message);
            tv.setTextIsSelectable(true);
            tv.setText(m.getMessage());
        }
    }

    /**
     * Container for a message and who sent it
     */
    private class Message {
        private boolean eliza;
        private String message;

        Message(boolean isEliza, String message) {
            this.message = message;
            eliza = isEliza;
        }

        boolean isEliza() {
            return eliza;
        }

        String getMessage() {
            return message;
        }
    }

    /**
     * ENUM for types of bots we have
     */
    enum Bots {
        ELIZA, DEBORAH
    }
}